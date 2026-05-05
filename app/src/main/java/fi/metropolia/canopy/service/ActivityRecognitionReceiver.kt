package fi.metropolia.canopy.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import fi.metropolia.canopy.domain.model.TrackingState

class ActivityRecognitionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (!ActivityRecognitionResult.hasResult(intent)) return

        val result = ActivityRecognitionResult.extractResult(intent)
        val activity = result?.mostProbableActivity ?: return

        val confidence = activity.confidence
        val activityType = activity.type

        // Map to internal signal names using constants from TrackingState
        val signalName = when (activityType) {
            DetectedActivity.IN_VEHICLE -> TrackingState.SIGNAL_IN_VEHICLE
            DetectedActivity.ON_BICYCLE -> TrackingState.MODE_BICYCLE
            DetectedActivity.WALKING -> TrackingState.MODE_WALKING
            DetectedActivity.RUNNING -> TrackingState.MODE_RUNNING
            DetectedActivity.ON_FOOT -> TrackingState.MODE_WALKING
            DetectedActivity.STILL -> TrackingState.MODE_STILL
            else -> TrackingState.MODE_UNKNOWN
        }

        Log.d("ActivityRecognition", "Signal: $signalName ($confidence%)")

        // Update raw signals in TrackingState
        TrackingState.currentActivityByConfidence = signalName
        TrackingState.currentConfidence = confidence
    }
}
