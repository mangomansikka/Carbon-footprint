package fi.metropolia.canopy.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.metropolia.canopy.data.TrackingState
import fi.metropolia.canopy.data.source.CanopyDatabase
import fi.metropolia.canopy.data.source.LocationEntity
import fi.metropolia.canopy.utils.CarbonHelper
import kotlinx.coroutines.launch


class TripViewModel(context: Context) : ViewModel() {

    private val db = CanopyDatabase.getInstance(context)

    // Exposure of global state to the UI
    val isTracking get() = TrackingState.isTracking
    val totalDistanceMeters get() = TrackingState.totalDistanceMeters
    val currentSpeedMps get() = TrackingState.currentSpeedMps
    val modeDistances get() = TrackingState.modeDistances


    fun prepareForNewTrip() {
        TrackingState.reset()
    }


    fun stopTracking() {
        val distance = TrackingState.totalDistanceMeters
        val modesList = TrackingState.usedTransportModes.toList()
        val modesString = modesList.joinToString(",")

        // Calculate total carbon footprint using the helper
        var totalEmissionsGrams = 0.0

        // Calculate per-mode emissions using the map of distances
        TrackingState.modeDistances.forEach { (mode, modeDistance) ->
            totalEmissionsGrams += CarbonHelper.calculate(modeDistance, mode) * 1000 // Convert kg to grams
        }

        // Save trip summary to database
        viewModelScope.launch {
            db.locationDao().insertLocation(
                LocationEntity(
                    latitude = TrackingState.lastLatitude ?: 0.0,
                    longitude = TrackingState.lastLongitude ?: 0.0,
                    transportModes = modesString,
                    carbonEmissionGrams = totalEmissionsGrams.toFloat()
                )
            )
        }

        TrackingState.isTracking = false
    }
}
