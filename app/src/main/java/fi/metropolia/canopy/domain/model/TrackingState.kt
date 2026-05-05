package fi.metropolia.canopy.domain.model

import androidx.compose.runtime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder
import kotlin.math.*

/**
 * Global state for active trip tracking.
 * Standardizes transport mode identifiers and manages in-memory accumulation.
 */
object TrackingState {
    // Standardized internal keys for transport modes
    const val MODE_STILL = "still"
    const val MODE_WALKING = "walking"
    const val MODE_RUNNING = "running"
    const val MODE_BICYCLE = "bicycle"
    const val MODE_CAR = "car"
    const val MODE_BUS = "bus"
    const val MODE_TRAIN = "train"
    const val MODE_METRO = "metro"
    const val MODE_TRAM = "tram"
    const val MODE_MOPED = "moped"
    const val MODE_UNKNOWN = "unknown"
    
    // Activity Recognition signals
    const val SIGNAL_NONE = "None"
    const val SIGNAL_IN_VEHICLE = "in_vehicle"

    var isTracking by mutableStateOf(false)
    var lastUpdateTime: Long = 0L

    // Metrics for current session
    var totalDistanceMeters by mutableDoubleStateOf(0.0)
    var currentSpeedMps by mutableFloatStateOf(0f)
    var averageSpeedMps by mutableFloatStateOf(0f)

    private val speedHistory = mutableListOf<Float>()
    val speedHistorySize: Int get() = speedHistory.size

    val usedTransportModes = mutableStateListOf<String>()
    val modeDistances = mutableStateMapOf<String, Double>()
    val modeEmissions = mutableStateMapOf<String, Double>()

    var currentConfirmedMode by mutableStateOf(MODE_STILL)

    var currentActivityByConfidence by mutableStateOf(SIGNAL_NONE)
    var currentConfidence by mutableIntStateOf(0)

    var lastLatitude: Double? = null
    var lastLongitude: Double? = null
    
    // Session boundaries
    var tripStartLatitude: Double? = null
    var tripStartLongitude: Double? = null
    var tripEndLatitude: Double? = null
    var tripEndLongitude: Double? = null

    val totalEmissionKg: Double
        get() = modeEmissions.values.sum()

    // Rail Caching
    data class RailPoint(val lat: Double, val lon: Double, val type: String)
    private val _cachedRailPoints = mutableListOf<RailPoint>()
    var lastRailFetchLat: Double? = null
    var lastRailFetchLon: Double? = null
    var lastRailFetchTime: Long = 0L

    // Motion Pattern Analysis
    data class TrackPoint(val lat: Double, val lon: Double, val speedKmh: Double, val timestamp: Long, val bearing: Float)
    private val recentPoints = mutableListOf<TrackPoint>()
    
    data class MotionFeatures(
        val meanSpeedKmh: Double,
        val maxSpeedKmh: Double,
        val speedStd: Double,
        val accelerationStd: Double,
        val maxAcceleration: Double,
        val stopCount: Int,
        val turnStd: Double,
        val straightness: Double
    )

    // Temporal Smoothing
    private val modeHistory = ArrayDeque<String>()

    /**
     * Resets all tracking state for a new session.
     */
    fun reset() {
        isTracking = false
        totalDistanceMeters = 0.0
        currentSpeedMps = 0f
        averageSpeedMps = 0f
        speedHistory.clear()
        usedTransportModes.clear()
        modeDistances.clear()
        modeEmissions.clear()
        currentConfirmedMode = MODE_STILL
        currentActivityByConfidence = SIGNAL_NONE
        currentConfidence = 0
        lastLatitude = null
        lastLongitude = null
        tripStartLatitude = null
        tripStartLongitude = null
        tripEndLatitude = null
        tripEndLongitude = null
        lastUpdateTime = 0L
        
        synchronized(_cachedRailPoints) { _cachedRailPoints.clear() }
        lastRailFetchLat = null
        lastRailFetchLon = null
        lastRailFetchTime = 0L

        recentPoints.clear()
        modeHistory.clear()
    }

    /**
     * Accumulates distance and calculated emissions for the current transport mode.
     */
    fun addDistanceToMode(mode: String, distance: Double, emission: Double) {
        val currentDist = modeDistances[mode] ?: 0.0
        modeDistances[mode] = currentDist + distance
        
        val currentEmission = modeEmissions[mode] ?: 0.0
        modeEmissions[mode] = currentEmission + emission

        if (mode != MODE_STILL && !usedTransportModes.contains(mode)) {
            usedTransportModes.add(mode)
        }
    }

    fun updateRollingAverage(newSpeed: Float) {
        speedHistory.add(newSpeed)
        if (speedHistory.size > 10) {
            speedHistory.removeAt(0)
        }
        averageSpeedMps = speedHistory.average().toFloat()
    }

    fun addTrackPoint(lat: Double, lon: Double, speedMps: Float, bearing: Float) {
        if (tripStartLatitude == null) {
            tripStartLatitude = lat
            tripStartLongitude = lon
        }
        recentPoints.add(TrackPoint(lat, lon, speedMps.toDouble() * 3.6, System.currentTimeMillis(), bearing))
        if (recentPoints.size > 20) {
            recentPoints.removeAt(0)
        }
    }

    fun computeMotionFeatures(): MotionFeatures? {
        if (recentPoints.size < 5) return null

        val speeds = recentPoints.map { it.speedKmh }
        val meanSpeed = speeds.average()
        val maxSpeed = speeds.maxOrNull() ?: 0.0
        val speedStd = if (speeds.size > 1) sqrt(speeds.map { (it - meanSpeed).pow(2) }.average()) else 0.0

        val accels = mutableListOf<Double>()
        for (i in 1 until recentPoints.size) {
            val dv = recentPoints[i].speedKmh - recentPoints[i-1].speedKmh
            val dt = (recentPoints[i].timestamp - recentPoints[i-1].timestamp) / 1000.0
            if (dt > 0) {
                accels.add(abs(dv / dt))
            }
        }
        val meanAccel = if (accels.isNotEmpty()) accels.average() else 0.0
        val accelStd = if (accels.size > 1) sqrt(accels.map { (it - meanAccel).pow(2) }.average()) else 0.0
        val maxAccel = accels.maxOrNull() ?: 0.0

        val stopCount = recentPoints.count { it.speedKmh < 2.0 }

        val turnDiffs = mutableListOf<Double>()
        for (i in 1 until recentPoints.size) {
            var diff = abs(recentPoints[i].bearing - recentPoints[i-1].bearing).toDouble()
            if (diff > 180) diff = 360 - diff
            turnDiffs.add(diff)
        }
        val turnStd = if (turnDiffs.size > 1) sqrt(turnDiffs.map { (it - turnDiffs.average()).pow(2) }.average()) else 0.0

        val displacement = calculateDistance(recentPoints.first().lat, recentPoints.first().lon, recentPoints.last().lat, recentPoints.last().lon)
        var pathLength = 0.0
        for (i in 1 until recentPoints.size) {
            pathLength += calculateDistance(recentPoints[i-1].lat, recentPoints[i-1].lon, recentPoints[i].lat, recentPoints[i].lon)
        }
        val straightness = if (pathLength > 0) (displacement / pathLength).coerceIn(0.0, 1.0) else 1.0

        return MotionFeatures(meanSpeed, maxSpeed, speedStd, accelStd, maxAccel, stopCount, turnStd, straightness)
    }

    fun smoothMode(newMode: String): String {
        modeHistory.addLast(newMode)
        if (modeHistory.size > 5) {
            modeHistory.removeFirst()
        }
        return modeHistory.groupBy { it }
            .maxByOrNull { it.value.size }?.key ?: newMode
    }

    fun shouldFetchRailData(currentLat: Double, currentLon: Double): Boolean {
        val lastLat = lastRailFetchLat ?: return true
        val lastLon = lastRailFetchLon ?: return true
        val distance = calculateDistance(currentLat, currentLon, lastLat, lastLon)
        val timeElapsed = System.currentTimeMillis() - lastRailFetchTime
        return distance > 800.0 || timeElapsed > 30 * 60 * 1000L || synchronized(_cachedRailPoints) { _cachedRailPoints.isEmpty() }
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    suspend fun fetchRailData(lat: Double, lon: Double): List<RailPoint> = withContext(Dispatchers.IO) {
        try {
            val query = "[out:json];(way[\"railway\"=\"rail\"](around:1000, $lat, $lon);way[\"railway\"=\"tram\"](around:1000, $lat, $lon);way[\"railway\"=\"subway\"](around:1000, $lat, $lon););out geom;"
            val response = URL("https://overpass-api.de/api/interpreter?data=${URLEncoder.encode(query, "UTF-8")}").readText()
            val elements = JSONObject(response).getJSONArray("elements")
            val newList = mutableListOf<RailPoint>()
            for (i in 0 until elements.length()) {
                val element = elements.getJSONObject(i)
                val type = element.getJSONObject("tags").optString("railway", MODE_UNKNOWN)
                val geometry = element.optJSONArray("geometry") ?: continue
                for (j in 0 until geometry.length() step 2) {
                    val p = geometry.getJSONObject(j)
                    newList.add(RailPoint(p.getDouble("lat"), p.getDouble("lon"), type))
                }
            }
            newList
        } catch (e: Exception) { emptyList() }
    }

    fun updateRailCache(points: List<RailPoint>, lat: Double, lon: Double) {
        synchronized(_cachedRailPoints) {
            _cachedRailPoints.clear()
            _cachedRailPoints.addAll(points)
        }
        lastRailFetchLat = lat
        lastRailFetchLon = lon
        lastRailFetchTime = System.currentTimeMillis()
    }

    fun getNearestRailProximity(lat: Double, lon: Double): Pair<String?, Double> {
        var minDistance = Double.MAX_VALUE
        var closestType: String? = null
        val latBound = 0.01 
        val lonBound = 0.01
        synchronized(_cachedRailPoints) {
            for (point in _cachedRailPoints) {
                if (abs(point.lat - lat) < latBound && abs(point.lon - lon) < lonBound) {
                    val dist = calculateDistance(lat, lon, point.lat, point.lon)
                    if (dist < minDistance) {
                        minDistance = dist
                        closestType = point.type
                    }
                }
            }
        }
        return Pair(closestType, minDistance)
    }
}
