package fi.metropolia.canopy.data.repository

import fi.metropolia.canopy.data.source.LocationDAO
import fi.metropolia.canopy.data.source.LocationEntity
import fi.metropolia.canopy.domain.model.TrackingState

class TripRepository(private val dao: LocationDAO) {

    suspend fun saveTripSummary() {
        val modesString = TrackingState.usedTransportModes.joinToString(",")

        // Calculate total trip emissions in grams for the database
        var totalEmissionsGrams = 0.0
        TrackingState.modeEmissions.forEach { (_, emissionKg) ->
            totalEmissionsGrams += emissionKg * 1000
        }

        dao.insertLocation(
            LocationEntity(
                latitude = TrackingState.lastLatitude ?: 0.0,
                longitude = TrackingState.lastLongitude ?: 0.0,
                transportModes = modesString,
                carbonEmissionGrams = totalEmissionsGrams.toFloat()
            )
        )
    }

    suspend fun getAllTrips(): List<LocationEntity> {
        return dao.getAllLocations()
    }
}
