package fi.metropolia.canopy.data.repository

import fi.metropolia.canopy.data.source.LocationDAO
import fi.metropolia.canopy.data.source.LocationEntity
import fi.metropolia.canopy.domain.model.TrackingState
import fi.metropolia.canopy.utils.CarbonHelper
import fi.metropolia.canopy.utils.CampusResolver
import kotlinx.coroutines.flow.Flow

/**
 * Manages the persistence and retrieval of trip data.
 * Acts as the single source of truth for location and emission records.
 */
class TripRepository(private val dao: LocationDAO) {

    /**
     * Processes and persists the current active tracking session.
     * Aggregates distances and emissions across all used transport modes before saving.
     */
    suspend fun saveTripSummary() {
        val modesString = TrackingState.usedTransportModes.joinToString(",")

        var totalEmissionsKg = 0.0

        // Prepare individual mode totals
        var busKg = 0.0
        var metroKg = 0.0
        var unknownCarKg = 0.0
        var mopedKg = 0.0
        var trainKg = 0.0

        //Walking and cycling distances in meters
        var walkingDistanceM = 0.0
        var cyclingDistanceM = 0.0


        TrackingState.modeDistances.forEach { (mode, distance) ->
            val emissionKg = CarbonHelper.calculate(distance, mode)
            totalEmissionsKg += emissionKg

            // Update walking and cycling distances
            if (mode.lowercase().trim() == "walking") {
                walkingDistanceM += distance
            } else if (mode.lowercase().trim() == "cycling") {
                cyclingDistanceM += distance
            }
            
            when (mode.lowercase().trim()) {
                "bus", "car/bus" -> busKg += emissionKg
                "car", "in vehicle" -> unknownCarKg += emissionKg
                "metro" -> metroKg += emissionKg
                "moped" -> mopedKg += emissionKg
                "train", "train/high-speed" -> trainKg += emissionKg
            }
        }

            val entity = LocationEntity(
                latitude = TrackingState.tripEndLatitude ?: TrackingState.lastLatitude ?: 0.0,
                longitude = TrackingState.tripEndLongitude ?: TrackingState.lastLongitude ?: 0.0,
                startLatitude = TrackingState.tripStartLatitude,
                startLongitude = TrackingState.tripStartLongitude,
                endLatitude = TrackingState.tripEndLatitude ?: TrackingState.lastLatitude,
                endLongitude = TrackingState.tripEndLongitude ?: TrackingState.lastLongitude,
                transportModes = modesString,
                carbonEmissionGrams = (totalEmissionsKg * 1000).toFloat(),
                emissionBussKg = busKg,
                emissionMetroKg = metroKg,
                emissionTrainKg = trainKg,
                emissionUnknownCarKg = unknownCarKg,
                emissionMopedKg = mopedKg,
                walkingDistanceM = walkingDistanceM,
                cyclingDistanceM = cyclingDistanceM,
                timestampMillis = System.currentTimeMillis()
            )
            dao.insertLocation(entity)
    }

    suspend fun getAllTrips(): List<LocationEntity> {
        return dao.getAllLocations()
    }

    /**
     * Retrieves accumulated emission data grouped by transport category.
     */
    suspend fun getEmissionsByMode(): Map<String, Double> {
        val summary = dao.getEmissionsSummary()
        // Convert Kg from DB to Grams for the Overview UI which expects grams
        return mapOf(
            "bus" to summary.bus * 1000,
            "metro" to summary.metro * 1000,
            "petrol" to summary.petrol * 1000,
            "diesel" to summary.diesel * 1000,
            "hybrid" to summary.hybrid * 1000,
            "electric" to summary.electric * 1000,
            "car unknown" to summary.unknown * 1000,
            "moped" to summary.moped * 1000,
            "train" to summary.train * 1000
        )
    }

    suspend fun getEmissionsByMonth(): Map<String, Double> {
        return dao.getMonthlyEmissions().associate { it.month to it.totalEmissionsGrams }
    }

    /**
     * Creates a trip record from manual user input.
     * Optionally resolves campus names to geographical coordinates.
     */
    suspend fun saveManualTrip(
        distance: Double,
        mode: String,
        selectedTripTimeMillis: Long,
        assignedCampusName: String? = null
    ) {
        val emissionKg = CarbonHelper.calculate(distance, mode)

        var busKg = 0.0
        var metroKg = 0.0
        var trainKg = 0.0
        var unknownCarKg = 0.0
        var mopedKg = 0.0
        var walkingDistanceM = 0.0
        var cyclingDistanceM = 0.0

        when (mode.lowercase().trim()) {
            "bus", "car/bus" -> busKg = emissionKg
            "metro" -> metroKg = emissionKg
            "car", "in vehicle" -> unknownCarKg = emissionKg
            "moped" -> mopedKg = emissionKg
            "train", "train/high-speed" -> trainKg = emissionKg
        }

        if (mode.lowercase().trim() == "walking") {
            walkingDistanceM = distance
        } else if (mode.lowercase().trim() == "cycling") {
            cyclingDistanceM = distance
        }

        var startLat: Double? = null
        var startLon: Double? = null
        var endLat: Double? = null
        var endLon: Double? = null

        if (!assignedCampusName.isNullOrBlank() && !assignedCampusName.equals("Non campus trip", ignoreCase = true)) {
            val coords = CampusResolver.getCampusCoordinates(assignedCampusName)
            if (coords != null) {
                val (lat, lon) = coords
                startLat = lat
                startLon = lon
                endLat = lat
                endLon = lon
            }
        }

        val entity = LocationEntity(
            latitude = 0.0,
            longitude = 0.0,
            startLatitude = startLat,
            startLongitude = startLon,
            endLatitude = endLat,
            endLongitude = endLon,
            transportModes = mode,
            carbonEmissionGrams = (emissionKg * 1000).toFloat(),
            emissionBussKg = busKg,
            emissionMetroKg = metroKg,
            emissionTrainKg = trainKg,
            emissionUnknownCarKg = unknownCarKg,
            emissionMopedKg = mopedKg,
            walkingDistanceM = walkingDistanceM,
            cyclingDistanceM = cyclingDistanceM,
            timestampMillis = selectedTripTimeMillis
        )
        dao.insertLocation(entity)
    }

    /** Total walking distance across all trips in meters. */
    suspend fun getTotalWalkingDistance(): Double = dao.getTotalWalkingDistance()
    suspend fun getTotalCyclingDistance(): Double = dao.getTotalCyclingDistance()

    /** Returns a flow of trips within a specific time range. */
    fun getLocationsByDate(startDate: Long, endDate: Long): Flow<List<LocationEntity>> {
        return dao.getLocationsByDate(startDate, endDate)
    }

    /** Deletes a trip by its unique identifier. */
    suspend fun deleteLocationsById(id: Int) {
        dao.deleteLocationsById(id)
    }

    /** Returns a list of dates (YYYY-MM-DD) that have recorded data. */
    suspend fun getDaysWithData(): List<String> = dao.getDaysWithData()
    
    /** Locks all existing data, preventing it from being modified by background processes. */
    suspend fun lockAllCurrentData() {
        dao.lockAllCurrentData()
    }
    
    /** Flow monitoring whether any data in the database is currently locked. */
    fun checkIfDataIsLockedFlow(): Flow<Boolean> {
        return dao.hasAnyLockedDataFlow()
    }

}
