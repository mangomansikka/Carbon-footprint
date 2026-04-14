package fi.metropolia.canopy.domain.model

import androidx.compose.runtime.*
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf

object TrackingState {

    var isTracking by mutableStateOf(false)
    var lastUpdateTime: Long = 0L

    var totalDistanceMeters by mutableDoubleStateOf(0.0)
    var currentSpeedMps by mutableFloatStateOf(0f)
    var averageSpeedMps by mutableFloatStateOf(0f)

    private val speedHistory = mutableListOf<Float>()
    val speedHistorySize: Int get() = speedHistory.size

    val usedTransportModes = mutableStateListOf<String>()
    val modeDistances = mutableStateMapOf<String, Double>()
    
    // Track emissions per mode
    val modeEmissions = mutableStateMapOf<String, Double>()
    
    // Total emissions for the current tracking session in Kg
    val totalEmissionKg: Double get() = modeEmissions.values.sum()

    var currentConfirmedMode by mutableStateOf("still")

    var currentActivityByConfidence by mutableStateOf("None")
    var currentConfidence by mutableIntStateOf(0)

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
        modeEmissions.clear()
        currentConfirmedMode = "still"
        currentActivityByConfidence = "None"
        currentConfidence = 0
        lastLatitude = null
        lastLongitude = null
    }

    fun addDistanceToMode(mode: String, distance: Double, emission: Double) {
        val currentDist = modeDistances[mode] ?: 0.0
        modeDistances[mode] = currentDist + distance
        
        val currentEmission = modeEmissions[mode] ?: 0.0
        modeEmissions[mode] = currentEmission + emission

        if (mode != "still" && !usedTransportModes.contains(mode)) {
            usedTransportModes.add(mode)
        }
    }

    fun updateRollingAverage(newSpeed: Float) {
        speedHistory.add(newSpeed)
        if (speedHistory.size > 10) {
            speedHistory.removeAt(0)
        }
        averageSpeedMps = speedHistory.average().toFloat()
    }
}