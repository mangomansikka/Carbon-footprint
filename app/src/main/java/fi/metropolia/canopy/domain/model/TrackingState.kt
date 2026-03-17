package fi.metropolia.canopy.domain.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object TrackingState {

    var isTracking by mutableStateOf(false)
    var totalDistanceMeters by mutableStateOf(0.0)
    var currentSpeedMps by mutableStateOf(0f)

    // Rolling average speed state
    var averageSpeedMps by mutableStateOf(0f)
    private val speedHistory = mutableListOf<Float>()
    val speedHistorySize: Int get() = speedHistory.size

    // Keeps unique transport modes used during trip
    val usedTransportModes = mutableStateListOf<String>()

    // Track distance per mode
    val modeDistances = mutableStateMapOf<String, Double>()

    var currentConfirmedMode by mutableStateOf("still")

    // Debug live info
    var currentActivityByConfidence by mutableStateOf("None")
    var currentConfidence by mutableStateOf(0)

    var lastLatitude: Double? = null
    var lastLongitude: Double? = null

    fun reset() {
        isTracking = false
        totalDistanceMeters = 0.0
        currentSpeedMps = 0f
        averageSpeedMps = 0f
        speedHistory.clear()
        usedTransportModes.clear()
        modeDistances.clear()
        currentConfirmedMode = "still"
        currentActivityByConfidence = "None"
        currentConfidence = 0
        lastLatitude = null
        lastLongitude = null
    }

    fun addDistanceToMode(mode: String, distance: Double) {
        val currentDist = modeDistances[mode] ?: 0.0
        modeDistances[mode] = currentDist + distance

        if (mode != "still" && !usedTransportModes.contains(mode)) {
            usedTransportModes.add(mode)
        }
    }

    // update rolling average based on 10 most recent points
    fun updateRollingAverage(newSpeed: Float) {
        speedHistory.add(newSpeed)
        if (speedHistory.size > 10) {
            speedHistory.removeAt(0)
        }
        averageSpeedMps = speedHistory.average().toFloat()
    }
}