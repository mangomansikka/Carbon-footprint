package fi.metropolia.canopy.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import fi.metropolia.canopy.CanopyActivity
import fi.metropolia.canopy.utils.ActivityRecognitionManager
import fi.metropolia.canopy.domain.model.TrackingState
import fi.metropolia.canopy.data.source.CanopyDatabase
import fi.metropolia.canopy.data.source.LocationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import fi.metropolia.canopy.utils.CarbonHelper



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
        val currentTime = System.currentTimeMillis()

        TrackingState.currentSpeedMps = location.speed
        TrackingState.updateRollingAverage(location.speed)

        val speedKmh = location.speed * 3.6
        val mode = determineTransportMode(speedKmh)

        var deltaEmission = 0.0

        //Calculate time gap
        val timeGapMs = if (TrackingState.lastUpdateTime > 0L) {
            currentTime - TrackingState.lastUpdateTime
        } else 0L

        if (lastLat != null && lastLon != null) {
            val results = FloatArray(1)
            Location.distanceBetween(lastLat, lastLon, location.latitude, location.longitude, results)
            val deltaDistance = results[0].toDouble()

            // Improved GPS Drift Filtering
            // If activity is not STILL, we trust the movement and remove the filtering.
            // If activity is STILL, we keep strict filters to prevent distance accumulation from GPS noise.
            val isConfirmedMoving = TrackingState.currentConfirmedMode != "still" && 
                                    TrackingState.currentConfirmedMode != "unknown" &&
                                    TrackingState.currentConfirmedMode != "none"

            //Detect a "tunnel gap" (more than 15 seconds since last GPS fix)
            val isGapRecovery = timeGapMs > 15000L && isConfirmedMoving

            val shouldAccumulate = if (isGapRecovery) {
                // TUNNEL LOGIC: If we just regained signal after a gap and were moving,
                // we accept the distance even if accuracy is slightly lower (up to 50m)
                // because we need to "bridge" the tunnel entrance and exit.
                location.accuracy < 50f && deltaDistance > 0.0
            } else if (isConfirmedMoving) {
                location.accuracy < 25f && deltaDistance > 0.0
            } else {
                deltaDistance > 8.0 && location.accuracy < 15f && location.speed > 0.5f
            }

            if (shouldAccumulate) {
                deltaEmission = CarbonHelper.calculate(deltaDistance, mode)
                TrackingState.addDistanceToMode(mode, deltaDistance, deltaEmission)
                TrackingState.totalDistanceMeters += deltaDistance

                if (isGapRecovery) {
                    Log.d("TrackingService", "Bridged gap: ${deltaDistance}m over ${timeGapMs/1000}s")
                }
            }
        }

        TrackingState.lastLatitude = location.latitude
        TrackingState.lastLongitude = location.longitude
        TrackingState.lastUpdateTime = currentTime


        // Database persistence
        serviceScope.launch {
            db.locationDao().insertLocation(
                LocationEntity(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    // Use a simple mapping to fill specific columns for the Overview summation
                    emissionBussKg = if (mode == "bus") deltaEmission else 0.0,
                    emissionMopedKg = if (mode == "moped_scooter") deltaEmission else 0.0,
                    emissionUnknownCarKg = if (mode == "car") deltaEmission else 0.0,
                    // Note: Activity recognition doesn't distinguish fuel type yet, defaulting to unknown
                )
            )
        }
    }

    private fun determineTransportMode(speedKmh: Double): String {
        return when {
            TrackingState.currentConfirmedMode != "still" &&
                    TrackingState.currentConfirmedMode != "unknown" &&
                    TrackingState.currentConfirmedMode != "none" -> {
                TrackingState.currentConfirmedMode.lowercase()
            }
            speedKmh < 3.0 -> "still"
            speedKmh < 10.0 -> "walking"     // Fixed from "on foot"
            speedKmh < 120.0 -> "car"        // Fixed from "car/bus"
            else -> "train"                  // Fixed from "train/high-speed"
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
        val channel = NotificationChannel(
            CHANNEL_ID, "Trip Tracking", NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}
