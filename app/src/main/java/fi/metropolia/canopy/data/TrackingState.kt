package fi.metropolia.canopy.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object TrackingState {

    var totalDistanceMeters: Double = 0.0

    // Keeps unique transport modes used during trip
    val usedTransportModes = mutableStateListOf<String>()
    
    // Track distance per mode: Mode Name -> Distance in Meters
    val modeDistances = mutableStateMapOf<String, Double>()
    
    // For debugging: live update of current activity and confidence
    var currentActivityByConfidence by mutableStateOf("None")
    var currentConfidence by mutableStateOf(0)

    var lastLatitude: Double? = null
    var lastLongitude: Double? = null

    fun reset() {
        totalDistanceMeters = 0.0
        usedTransportModes.clear()
        modeDistances.clear()
        currentActivityByConfidence = "None"
        currentConfidence = 0
        lastLatitude = null
        lastLongitude = null
    }
    
    fun addDistanceToMode(mode: String, distance: Double) {
        val currentDist = modeDistances[mode] ?: 0.0
        modeDistances[mode] = currentDist + distance
        
        if (!usedTransportModes.contains(mode) && mode != "still") {
            usedTransportModes.add(mode)
        }
    }
}
