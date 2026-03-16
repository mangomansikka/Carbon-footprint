package fi.metropolia.canopy.location

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import fi.metropolia.canopy.CanopyActivity
import fi.metropolia.canopy.activityrecognition.ActivityRecognitionManager
import fi.metropolia.canopy.data.TrackingState
import fi.metropolia.canopy.data.source.CanopyDatabase
import fi.metropolia.canopy.data.source.LocationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class TrackingService : Service() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var activityRecognitionManager: ActivityRecognitionManager
    private lateinit var locationCallback: LocationCallback

    companion object {
        private const val CHANNEL_ID = "tracking_channel"
        private const val NOTIFICATION_ID = 1
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        activityRecognitionManager = ActivityRecognitionManager(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTracking()
            ACTION_STOP -> stopTracking()
        }
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun startTracking() {
        if (TrackingState.isTracking) return
        
        TrackingState.reset()
        TrackingState.isTracking = true
        
        val notification = createNotification("Trip tracking active")
        startForeground(NOTIFICATION_ID, notification)

        // Start Activity Recognition
        try {
            activityRecognitionManager.start()
        } catch (e: SecurityException) {
            Log.e("TrackingService", "Activity Recognition permission missing", e)
        }

        // Start Location Updates
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L)
            .setMinUpdateDistanceMeters(1f)
            .build()

        val db = CanopyDatabase.getInstance(this)
        
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                processLocationUpdate(location, db)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun processLocationUpdate(location: Location, db: CanopyDatabase) {
        val lastLat = TrackingState.lastLatitude
        val lastLon = TrackingState.lastLongitude

        TrackingState.currentSpeedMps = location.speed
        
        // Update rolling average speed
        TrackingState.updateRollingAverage(location.speed)
        
        val speedKmh = location.speed * 3.6
        
        // Mode logic
        val mode = determineTransportMode(speedKmh)

        if (lastLat != null && lastLon != null) {
            val results = FloatArray(1)
            Location.distanceBetween(lastLat, lastLon, location.latitude, location.longitude, results)
            val deltaDistance = results[0].toDouble()
            
            // GPS Drift Filtering
            // distance > 8 meters AND accuracy < 15 meters AND speed > 0.5 m/s
            if (deltaDistance > 8.0 && location.accuracy < 15f && location.speed > 0.5f) {
                TrackingState.addDistanceToMode(mode, deltaDistance)
                TrackingState.totalDistanceMeters += deltaDistance
            }
        }

        TrackingState.lastLatitude = location.latitude
        TrackingState.lastLongitude = location.longitude

        // Database persistence
        // TODO add mode to database and Co2 footprint
        serviceScope.launch {
            db.locationDao().insertLocation(
                LocationEntity(latitude = location.latitude, longitude = location.longitude)
            )
        }
    }

    private fun determineTransportMode(speedKmh: Double): String {
        return when {
            TrackingState.currentConfirmedMode != "still" && 
            TrackingState.currentConfirmedMode != "unknown" &&
            TrackingState.currentConfirmedMode != "none" &&
            TrackingState.currentConfirmedMode != "Tilting" -> {
                TrackingState.currentConfirmedMode.lowercase()
            }
            speedKmh < 3.0 -> "still"
            speedKmh < 10.0 -> "on foot"
            speedKmh < 25.0 -> "cycling"
            speedKmh < 120.0 -> "car/bus"
            else -> "train/high-speed"
        }
    }

    private fun stopTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        try {
            activityRecognitionManager.stop()
        } catch (e: SecurityException) {}
        
        TrackingState.isTracking = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotification(content: String): Notification {
        val intent = Intent(this, CanopyActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Canopy Tracker")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Trip Tracking", NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}
