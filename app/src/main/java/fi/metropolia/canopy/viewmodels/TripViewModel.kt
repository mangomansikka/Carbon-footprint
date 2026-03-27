package fi.metropolia.canopy.viewmodels

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.metropolia.canopy.data.repository.TripRepository
import fi.metropolia.canopy.data.source.CanopyDatabase
import fi.metropolia.canopy.data.source.LocationEntity
import fi.metropolia.canopy.domain.model.TrackingState
import fi.metropolia.canopy.service.TrackingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TripViewModel(context: Context) : ViewModel() {

    private val repository: TripRepository

    private val _trips = MutableStateFlow<List<LocationEntity>>(emptyList())
    private val _emissions = MutableStateFlow<Map<String, Double>>(emptyMap())

    val trips: StateFlow<List<LocationEntity>> = _trips
    val emissions: StateFlow<Map<String, Double>> = _emissions

    init {
        val db = CanopyDatabase.getInstance(context)
        repository = TripRepository(db.locationDao())
        loadEmissions()
    }

    val isTracking get() = TrackingState.isTracking
    val totalDistanceMeters get() = TrackingState.totalDistanceMeters
    val currentSpeedMps get() = TrackingState.currentSpeedMps
    val modeDistances get() = TrackingState.modeDistances
    val modeEmissions get() = TrackingState.modeEmissions

    /* 🔹 START TRACKING */
    fun startTracking(context: Context) {
        TrackingState.reset()
        val intent = Intent(context, TrackingService::class.java).apply {
            action = TrackingService.ACTION_START
        }
        ContextCompat.startForegroundService(context, intent)
    }

    fun loadTrips() {
        viewModelScope.launch {
            _trips.value = repository.getAllTrips()
        }
    }

    fun loadEmissions() {
        viewModelScope.launch {
            _emissions.value = repository.getEmissionsByMode()
        }
    }

    fun saveManualTrip(distance: Double, mode: String) {
        viewModelScope.launch {
            repository.saveManualTrip(distance, mode)
            loadEmissions()
        }
    }

    fun endTrip(context: Context) {
        val intent = Intent(context, TrackingService::class.java).apply {
            action = TrackingService.ACTION_STOP
        }
        context.startService(intent)

        viewModelScope.launch {
            repository.saveTripSummary()
            TrackingState.isTracking = false
            loadEmissions()
        }
    }
}