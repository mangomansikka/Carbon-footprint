package fi.metropolia.canopy.location

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import fi.metropolia.canopy.data.TrackingState

class LocationTracker(
    private val fusedLocationClient: FusedLocationProviderClient
) {

    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun start(onLocationUpdate: (String) -> Unit) {

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000L
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                
                val lastLat = TrackingState.lastLatitude
                val lastLon = TrackingState.lastLongitude

                // 1. Determine current mode based on speed
                val speedMps = location.speed
                val speedKmh = speedMps * 3.6
                
                val mode = when {
                    speedKmh < 3.0 -> "still"
                    speedKmh < 8.0 -> "walking"
                    speedKmh < 25.0 -> "cycling"
                    speedKmh < 120.0 -> "car/bus"
                    else -> "train/high-speed"
                }

                // 2. Calculate distance delta and attribute it to the mode
                if (lastLat != null && lastLon != null) {
                    val results = FloatArray(1)
                    Location.distanceBetween(
                        lastLat, lastLon,
                        location.latitude, location.longitude,
                        results
                    )
                    val deltaDistance = results[0].toDouble()
                    
                    TrackingState.totalDistanceMeters += deltaDistance
                    TrackingState.addDistanceToMode(mode, deltaDistance)
                }

                TrackingState.lastLatitude = location.latitude
                TrackingState.lastLongitude = location.longitude

                onLocationUpdate(
                    "Speed: %.1f km/h\nDistance: %.0f m".format(speedKmh, TrackingState.totalDistanceMeters)
                )
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    fun stop() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }
}
