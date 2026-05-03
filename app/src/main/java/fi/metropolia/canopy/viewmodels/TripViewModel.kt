package fi.metropolia.canopy.viewmodels

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.metropolia.canopy.data.repository.TripRepository
import fi.metropolia.canopy.data.repository.UserRepository
import fi.metropolia.canopy.data.source.CanopyDatabase
import fi.metropolia.canopy.data.source.LocationEntity
import fi.metropolia.canopy.domain.model.TrackingState
import fi.metropolia.canopy.service.TrackingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import fi.metropolia.canopy.utils.ExportUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class TripViewModel(context: Context) : ViewModel() {

    private val repository: TripRepository
    private val userRepository: UserRepository

    private val _trips = MutableStateFlow<List<LocationEntity>>(emptyList())
    private val _emissions = MutableStateFlow<Map<String, Double>>(emptyMap())

    val trips: StateFlow<List<LocationEntity>> = _trips
    val emissions: StateFlow<Map<String, Double>> = _emissions

    private val _walkingDistance = MutableStateFlow(0.0)
    val walkingDistance: StateFlow<Double> = _walkingDistance

    private val _cyclingDistance = MutableStateFlow(0.0)
    val cyclingDistance: StateFlow<Double> = _cyclingDistance
    
    init {
        val db = CanopyDatabase.getInstance(context)
        repository = TripRepository(db.locationDao())
        userRepository = UserRepository(db.userDao())
        loadEmissions()
    }

    // Reactive "locked" state
    val isLocked: StateFlow<Boolean> = repository.checkIfDataIsLockedFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

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

    fun loadTrips() {
        viewModelScope.launch {
            _trips.value = repository.getAllTrips()
        }
    }
    
    fun loadEmissions() {
        viewModelScope.launch {
            _emissions.value = repository.getEmissionsByMode()
            _walkingDistance.value = repository.getTotalWalkingDistance()
            _cyclingDistance.value = repository.getTotalCyclingDistance()
        }
    }
    
    fun saveManualTrip(
        distance: Double,
        mode: String,
        selectedTripTimeMillis: Long,
        assignedCampusName: String? = null
    ) {
        viewModelScope.launch {
            repository.saveManualTrip(
                distance = distance,
                mode = mode,
                selectedTripTimeMillis = selectedTripTimeMillis,
                assignedCampusName = assignedCampusName
            )
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

    fun exportData(context: Context, recipientEmail: String? = null) {
        viewModelScope.launch {
            val trips = repository.getAllTrips()
            val userRole = userRepository.userRole.first()
            ExportUtils.exportAndEmailData(context, trips, userRole, recipientEmail)
            
            // Lock all existing data after export
            repository.lockAllCurrentData()
        }
    }
    
    // Removed redundant manual check as we now use StateFlow
    fun loadIsLocked() { }
}