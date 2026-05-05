package fi.metropolia.canopy.utils

import android.location.Location
import kotlin.math.roundToInt

/**
 * Result of a campus resolution attempt.
 * @param name Name of the matched campus.
 * @param distanceMeters Distance from the coordinates to the campus center.
 * @param withinThreshold Whether the distance is within the geofencing limit.
 */
data class CampusMatch(
    val name: String,
    val distanceMeters: Int,
    val withinThreshold: Boolean
)

/**
 * Utility for mapping geographical coordinates to Metropolia campus locations.
 * Used for geofencing and associating trips with specific campuses.
 */
object CampusResolver {
    private const val CAMPUS_THRESHOLD_METERS = 250f

    private data class Campus(val name: String, val latitude: Double, val longitude: Double)

    private val campuses = listOf(
        Campus("Myllypuro", 60.22306, 25.07822),
        Campus("Karamalmi", 60.22392, 24.75852),
        Campus("Arabia", 60.21083, 24.97647),
        Campus("Myyrmaki", 60.25889, 24.84445)
    )

    /**
     * Finds the nearest campus to the given coordinates and determines if it's within range.
     */
    fun resolveCampus(latitude: Double?, longitude: Double?): CampusMatch? {
        if (latitude == null || longitude == null) return null
        if (latitude == 0.0 && longitude == 0.0) return null

        val nearest = campuses.minByOrNull { campus ->
            distanceMeters(latitude, longitude, campus.latitude, campus.longitude)
        } ?: return null

        val distance = distanceMeters(latitude, longitude, nearest.latitude, nearest.longitude)
        return CampusMatch(
            name = nearest.name,
            distanceMeters = distance.roundToInt(),
            withinThreshold = distance <= CAMPUS_THRESHOLD_METERS
        )
    }

    /**
     * Returns the static coordinates for a campus identified by its name.
     */
    fun getCampusCoordinates(name: String): Pair<Double, Double>? {
        val campus = campuses.find { it.name.equals(name, ignoreCase = true) }
        return campus?.let { Pair(it.latitude, it.longitude) }
    }

    private fun distanceMeters(
        latitude1: Double,
        longitude1: Double,
        latitude2: Double,
        longitude2: Double
    ): Float {
        val result = FloatArray(1)
        Location.distanceBetween(latitude1, longitude1, latitude2, longitude2, result)
        return result[0]
    }
}
