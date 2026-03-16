package fi.metropolia.canopy.activityrecognition

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import fi.metropolia.canopy.data.TrackingState

class ActivityRecognitionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (!ActivityRecognitionResult.hasResult(intent)) return

        val result = ActivityRecognitionResult.extractResult(intent)
        val activity = result?.mostProbableActivity ?: return
        
        val confidence = activity.confidence
        val activityType = activity.type

        val activityName = when (activityType) {
            // Use averageSpeedMps for vehicle classification
            DetectedActivity.IN_VEHICLE -> classifyVehicleType(TrackingState.averageSpeedMps)
            DetectedActivity.ON_BICYCLE -> "bicycle"
            DetectedActivity.WALKING -> "walking"
            DetectedActivity.RUNNING -> "running"
            DetectedActivity.ON_FOOT -> "walking" 
            DetectedActivity.STILL -> "still"
            DetectedActivity.TILTING -> "tilting"
            else -> "unknown"
        }

        Log.d("ActivityRecognition", "Detected: $activityName with $confidence% confidence")

        // Update live debug info
        TrackingState.currentActivityByConfidence = activityName
        TrackingState.currentConfidence = confidence

        if (confidence >= 30) {
            val modeKey = activityName.lowercase()
            TrackingState.currentConfirmedMode = modeKey
            
            if (modeKey != "still" && modeKey != "tilting" && modeKey != "unknown") {
                if (!TrackingState.usedTransportModes.contains(modeKey)) {
                    TrackingState.usedTransportModes.add(modeKey)
                }
            }
        }
    }

    private fun classifyVehicleType(speed: Float): String {
        return when {
            speed < 12f -> "bus"
            speed <= 25f -> "car"
            else -> "train"
        }
    }
}
