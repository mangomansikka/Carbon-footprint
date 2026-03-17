package fi.metropolia.canopy.viewmodels

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.metropolia.canopy.data.repository.TripRepository
import fi.metropolia.canopy.data.source.CanopyDatabase
import fi.metropolia.canopy.domain.model.TrackingState
import fi.metropolia.canopy.service.TrackingService
import kotlinx.coroutines.launch

class TripViewModel(context: Context) : ViewModel() {

    private val repository: TripRepository

    init {
        val db = CanopyDatabase.getInstance(context)
        repository = TripRepository(db.locationDao())
    }

    // Expose live tracking data to the UI
    val isTracking get() = TrackingState.isTracking
    val totalDistanceMeters get() = TrackingState.totalDistanceMeters
    val currentSpeedMps get() = TrackingState.currentSpeedMps
    val modeDistances get() = TrackingState.modeDistances
    val modeEmissions get() = TrackingState.modeEmissions

    fun startTracking(context: Context) {
        TrackingState.reset()
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
        
        viewModelScope.launch {
            repository.saveTripSummary()
            TrackingState.isTracking = false
        }
    }
}
