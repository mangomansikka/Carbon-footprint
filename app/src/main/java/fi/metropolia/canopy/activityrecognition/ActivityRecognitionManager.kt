package fi.metropolia.canopy.activityrecognition

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.ActivityRecognition

class ActivityRecognitionManager(private val context: Context) {

    private val client = ActivityRecognition.getClient(context)

    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, ActivityRecognitionReceiver::class.java)
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    fun start() {
        client.requestActivityUpdates(
            3000,
            pendingIntent
        ).addOnSuccessListener {
            Log.d("ActivityRecognition", "Activity updates started")
        }.addOnFailureListener {
            Log.e("ActivityRecognition", "Failed to start updates", it)
        }
    }

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    fun stop() {
        client.removeActivityUpdates(pendingIntent)
    }
}