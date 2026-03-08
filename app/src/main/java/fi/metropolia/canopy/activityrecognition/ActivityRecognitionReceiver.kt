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
            DetectedActivity.IN_VEHICLE -> "In Vehicle"
            DetectedActivity.ON_BICYCLE -> "On Bicycle"
            DetectedActivity.ON_FOOT, 
            DetectedActivity.WALKING, 
            DetectedActivity.RUNNING -> "On Foot"
            DetectedActivity.STILL -> "Still"
            DetectedActivity.TILTING -> "Tilting"
            else -> "Unknown"
        }

        Log.d("ActivityRecognition", "Detected: $activityName with $confidence% confidence")

        // Update live debug info
        TrackingState.currentActivityByConfidence = activityName
        TrackingState.currentConfidence = confidence

        // If confidence is high, we can treat this as the "official" current mode for distance tracking
        if (confidence >= 30) {
            val modeKey = activityName.lowercase()
            TrackingState.currentConfirmedMode = modeKey
            
            if (modeKey != "still" && !TrackingState.usedTransportModes.contains(modeKey)) {
                TrackingState.usedTransportModes.add(modeKey)
            }
        }
    }
}
