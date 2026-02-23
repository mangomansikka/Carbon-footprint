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

                if (lastLat != null && lastLon != null) {
                    val results = FloatArray(1)
                    Location.distanceBetween(
                        lastLat, lastLon,
                        location.latitude, location.longitude,
                        results
                    )
                    TrackingState.totalDistanceMeters += results[0]
                }

                TrackingState.lastLatitude = location.latitude
                TrackingState.lastLongitude = location.longitude

                onLocationUpdate(
                    "Lat: ${location.latitude}, Lng: ${location.longitude}"
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