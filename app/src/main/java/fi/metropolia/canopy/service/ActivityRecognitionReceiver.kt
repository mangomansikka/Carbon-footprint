package fi.metropolia.canopy.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import fi.metropolia.canopy.domain.model.TrackingState

/**
 * Receiver that processes activity recognition updates from Google Play Services.
 * It translates physical movement into specific transport modes to track carbon emissions.
 */
class ActivityRecognitionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (!ActivityRecognitionResult.hasResult(intent)) return

        val result = ActivityRecognitionResult.extractResult(intent)
        val activity = result?.mostProbableActivity ?: return

        val confidence = activity.confidence
        val activityType = activity.type

        val activityName = when (activityType) {
            // General "vehicle" detections are further refined using speed data
            DetectedActivity.IN_VEHICLE -> classifyVehicleType(TrackingState.averageSpeedMps)
            DetectedActivity.ON_BICYCLE -> "bicycle"
            DetectedActivity.WALKING -> "walking"
            DetectedActivity.RUNNING -> "running"
            DetectedActivity.ON_FOOT -> "walking"
            DetectedActivity.STILL -> "still"
            else -> "unknown"
        }

        Log.d("ActivityRecognition", "Detected: $activityName with $confidence% confidence")

        // Update live debug info
        TrackingState.currentActivityByConfidence = activityName
        TrackingState.currentConfidence = confidence

        // Filter out low-confidence detections to prevent erratic mode switching
        if (confidence >= 30) {
            val modeKey = activityName.lowercase()
            TrackingState.currentConfirmedMode = modeKey

            // Track unique modes used during the current session
            if (modeKey != "still" && modeKey != "unknown") {
                if (!TrackingState.usedTransportModes.contains(modeKey)) {
                    TrackingState.usedTransportModes.add(modeKey)
                }
            }
        }
    }

    /**
     * Heuristic to distinguish vehicle types based on average speed in m/s.
     */
    private fun classifyVehicleType(speed: Float): String {
        return when {
            speed < 12f -> "bus"
            speed <= 25f -> "car"
            else -> "train"
        }
    }
}
