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
        val activity = result?.mostProbableActivity
        
        val confidence = activity?.confidence ?: 0
        val type = activity?.type

        val movement = when (type) {
            DetectedActivity.WALKING -> "walking"
            DetectedActivity.ON_BICYCLE -> "biking"
            DetectedActivity.IN_VEHICLE -> "in_vehicle"
            DetectedActivity.RUNNING -> "running"
            DetectedActivity.STILL -> "still"
            DetectedActivity.TILTING -> "tilting"
            DetectedActivity.ON_FOOT -> "on_foot"
            else -> "unknown ($type)"
        }

        // Update live debug info
        TrackingState.currentActivityByConfidence = movement
        TrackingState.currentConfidence = confidence
        
        Log.d("ActivityRecognition", "Detected: $movement with $confidence% confidence")

        // Add to the list of unique modes used if confidence is decent
        if (confidence > 30 && movement != "still" && movement != "tilting") {
            if (!TrackingState.usedTransportModes.contains(movement)) {
                TrackingState.usedTransportModes.add(movement)
            }
        }
    }
}
