package fi.metropolia.canopy.domain.model

import androidx.compose.runtime.*
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf

/**
 * Singleton state holder for real-time trip tracking.
 * Uses Compose state to allow UI components to observe and react to tracking updates.
 */
object TrackingState {

    var isTracking by mutableStateOf(false)
    var lastUpdateTime: Long = 0L

    // Real-time motion metrics
    var totalDistanceMeters by mutableDoubleStateOf(0.0)
    var currentSpeedMps by mutableFloatStateOf(0f)
    var averageSpeedMps by mutableFloatStateOf(0f)

    private val speedHistory = mutableListOf<Float>()
    val speedHistorySize: Int get() = speedHistory.size

    // Aggregated session data per transport mode
    val usedTransportModes = mutableStateListOf<String>()
    val modeDistances = mutableStateMapOf<String, Double>()
    
    val modeEmissions = mutableStateMapOf<String, Double>()
    
    val totalEmissionKg: Double get() = modeEmissions.values.sum()

    /**
     * The transport mode currently identified for the user (e.g., "bus", "walking", "still").
     */
    var currentConfirmedMode by mutableStateOf("still")

    /**
     * Activity recognition data from Google Play Services.
     */
    var currentActivityByConfidence by mutableStateOf("None")
    var currentConfidence by mutableIntStateOf(0)

    // Location coordinates for the current tracking session
    var lastLatitude: Double? = null
    var lastLongitude: Double? = null
    var tripStartLatitude: Double? = null
    var tripStartLongitude: Double? = null
    var tripEndLatitude: Double? = null
    var tripEndLongitude: Double? = null

    /**
     * Resets the tracking state to initial values, clearing all session data.
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
        currentConfirmedMode = "still"
        currentActivityByConfidence = "None"
        currentConfidence = 0
        lastLatitude = null
        lastLongitude = null
        tripStartLatitude = null
        tripStartLongitude = null
        tripEndLatitude = null
        tripEndLongitude = null
    }

    /**
     * Records distance and calculated emissions for a specific mode.
     * Modes other than "still" are added to the list of used modes for the session.
     */
    fun addDistanceToMode(mode: String, distance: Double, emission: Double) {
        val currentDist = modeDistances[mode] ?: 0.0
        modeDistances[mode] = currentDist + distance
        
        val currentEmission = modeEmissions[mode] ?: 0.0
        modeEmissions[mode] = currentEmission + emission

        if (mode != "still" && !usedTransportModes.contains(mode)) {
            usedTransportModes.add(mode)
        }
    }

    /**
     * Updates the rolling average speed using a window of the last 10 speed readings.
     */
    fun updateRollingAverage(newSpeed: Float) {
        speedHistory.add(newSpeed)
        if (speedHistory.size > 10) {
            speedHistory.removeAt(0)
        }
        averageSpeedMps = speedHistory.average().toFloat()
    }
}
