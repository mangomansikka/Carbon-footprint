package fi.metropolia.canopy.data.repository

import fi.metropolia.canopy.data.source.LocationDAO
import fi.metropolia.canopy.data.source.LocationEntity
import fi.metropolia.canopy.domain.model.TrackingState
import fi.metropolia.canopy.utils.CarbonHelper

class TripRepository(private val dao: LocationDAO) {

    suspend fun saveTripSummary() {
        val modesString = TrackingState.usedTransportModes.joinToString(",")

        var totalEmissionsKg = 0.0
        
        // Prepare individual mode totals
        var busKg = 0.0
        var metroKg = 0.0
        var unknownCarKg = 0.0
        var mopedKg = 0.0

        TrackingState.modeDistances.forEach { (mode, distance) ->
            val emissionKg = CarbonHelper.calculate(distance, mode)
            totalEmissionsKg += emissionKg
            
            when (mode.lowercase().trim()) {
                "bus", "car/bus" -> busKg += emissionKg
                "car", "in vehicle" -> unknownCarKg += emissionKg
                "metro" -> metroKg += emissionKg
                "moped" -> mopedKg += emissionKg
            }
        }

        dao.insertLocation(
            LocationEntity(
                latitude = TrackingState.lastLatitude ?: 0.0,
                longitude = TrackingState.lastLongitude ?: 0.0,
                transportModes = modesString,
                carbonEmissionGrams = (totalEmissionsKg * 1000).toFloat(),
                emissionBussKg = busKg,
                emissionMetroKg = metroKg,
                emissionUnknownCarKg = unknownCarKg,
                emissionMopedKg = mopedKg
            )
        )
    }

    suspend fun getAllTrips(): List<LocationEntity> {
        return dao.getAllLocations()
    }

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
            "moped" to summary.moped * 1000
        )
    }

    suspend fun getEmissionsByMonth(): Map<String, Double> {
        return dao.getMonthlyEmissions().associate { it.month to it.totalEmissionsGrams }
    }
}