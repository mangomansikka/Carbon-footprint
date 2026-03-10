package fi.metropolia.canopy.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import fi.metropolia.canopy.data.TrackingState

class TripViewModel(context: Context) : ViewModel() {

    // Exposure of global state to the UI
    val isTracking get() = TrackingState.isTracking
    val totalDistanceMeters get() = TrackingState.totalDistanceMeters
    val currentSpeedMps get() = TrackingState.currentSpeedMps
    val modeDistances get() = TrackingState.modeDistances


    fun prepareForNewTrip() {
        TrackingState.reset()
    }
}
