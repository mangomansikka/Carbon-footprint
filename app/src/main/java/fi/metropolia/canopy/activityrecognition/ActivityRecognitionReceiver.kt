package fi.metropolia.canopy.activityrecognition

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import fi.metropolia.canopy.data.TrackingState

class ActivityRecognitionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (!ActivityRecognitionResult.hasResult(intent)) return

        val result = ActivityRecognitionResult.extractResult(intent)
        val activity = result?.mostProbableActivity

        val movement = when (activity?.type) {
            DetectedActivity.WALKING -> "walking"
            DetectedActivity.ON_BICYCLE -> "biking"
            DetectedActivity.IN_VEHICLE -> "in_vehicle"
            DetectedActivity.RUNNING -> "running"
            DetectedActivity.STILL -> "still"
            else -> "unknown"
        }

        TrackingState.usedTransportModes.add(movement)
    }
}