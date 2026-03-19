package fi.metropolia.canopy.data.repository

import fi.metropolia.canopy.data.source.LocationDAO
import fi.metropolia.canopy.data.source.LocationEntity
import fi.metropolia.canopy.domain.model.TrackingState
import fi.metropolia.canopy.utils.CarbonHelper

class TripRepository(private val dao: LocationDAO) {

    suspend fun saveTripSummary() {


        val modesString = TrackingState.usedTransportModes.joinToString(",")

        var totalEmissionsGrams = 0.0

        TrackingState.modeDistances.forEach { (mode, distance) ->
            totalEmissionsGrams +=
                CarbonHelper.calculate(distance, mode) * 1000
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

    suspend fun getEmissionsByMode(): Map<String, Double> {
        val summary = dao.getEmissionsSummary()
        return mapOf(
            "bus" to summary.bus,
            "metro" to summary.metro,
            "petrol" to summary.petrol,
            "diesel" to summary.diesel,
            "hybrid" to summary.hybrid,
            "electric" to summary.electric,
            "car unknown" to summary.unknown,
            "moped" to summary.moped
        )
    }
}