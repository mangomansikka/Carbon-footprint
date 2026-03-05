package fi.metropolia.canopy.viewmodels

import android.content.Context
import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class TripState(
    val lastLocation: Location? = null,
    val totalDistanceMeters: Double = 0.0,
    val currentSpeedMps: Float = 0f,
    val isTracking: Boolean = false,
)

class TripViewModel(context: Context) : ViewModel() {

    private val _tripState = mutableStateOf(TripState())
    val tripState: State<TripState> = _tripState

    fun onNewLocation(location: Location?) {
        location ?: return

        val previous = _tripState.value.lastLocation

        val distance = previous?.distanceTo(location)?.toDouble() ?: 0.0

        val totalDistance = _tripState.value.totalDistanceMeters + distance

        _tripState.value = _tripState.value.copy(
            lastLocation = location,
            totalDistanceMeters = totalDistance,
            currentSpeedMps = location.speed
        )
    }

    fun startTracking() {
        _tripState.value = _tripState.value.copy(isTracking = true)
    }

    fun stopTracking() {
        _tripState.value = _tripState.value.copy(isTracking = false)
    }
}

