package fi.metropolia.canopy.viewmodels

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.metropolia.canopy.domain.model.TrackingState
import fi.metropolia.canopy.data.source.CanopyDatabase
import fi.metropolia.canopy.data.source.LocationEntity
import fi.metropolia.canopy.service.TrackingService
import fi.metropolia.canopy.utils.CarbonHelper
import kotlinx.coroutines.launch

class TripViewModel(context: Context) : ViewModel() {

    private val db = CanopyDatabase.getInstance(context)

    val isTracking get() = TrackingState.isTracking
    val totalDistanceMeters get() = TrackingState.totalDistanceMeters
    val currentSpeedMps get() = TrackingState.currentSpeedMps
    val modeDistances get() = TrackingState.modeDistances

    /* 🔥 START TRACKING */
    fun startTracking(context: Context) {
        prepareForNewTrip()

        val intent = Intent(context, TrackingService::class.java).apply {
            action = TrackingService.ACTION_START
        }

        ContextCompat.startForegroundService(context, intent)
    }


    fun endTrip(context: Context) {

        val intent = Intent(context, TrackingService::class.java).apply {
            action = TrackingService.ACTION_STOP
        }

        context.startService(intent)

        stopTracking()
    }

    private fun prepareForNewTrip() {
        TrackingState.reset()
    }


    private fun stopTracking() {

        val modesList = TrackingState.usedTransportModes.toList()
        val modesString = modesList.joinToString(",")

        var totalEmissionsGrams = 0.0

        TrackingState.modeDistances.forEach { (mode, distance) ->
            totalEmissionsGrams += CarbonHelper.calculate(distance, mode) * 1000
        }

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