package fi.metropolia.canopy.location

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import fi.metropolia.canopy.data.TrackingState
import fi.metropolia.canopy.data.source.LocationDAO
import fi.metropolia.canopy.data.source.LocationEntity
import fi.metropolia.canopy.viewmodels.TripViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationTracker(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val locationDao: LocationDAO,
    private val viewModel: TripViewModel
) {

    private var locationCallback: LocationCallback? = null

    fun getCallback(): LocationCallback? = locationCallback

    @SuppressLint("MissingPermission")
    fun start(onLocationUpdate: (String) -> Unit) {

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000L
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                
                // Update ViewModel and Database (Replacing LocationHelper's role)
                viewModel.onNewLocation(location)
                val entity = LocationEntity(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
                CoroutineScope(Dispatchers.IO).launch {
                    locationDao.insertLocation(entity)
                }

                val lastLat = TrackingState.lastLatitude
                val lastLon = TrackingState.lastLongitude

                // 1. Determine current mode
                val speedKmh = location.speed * 3.6
                val mode = when {
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

                // 2. Calculate distance delta and attribute it to the mode
                if (lastLat != null && lastLon != null) {
                    val results = FloatArray(1)
                    Location.distanceBetween(
                        lastLat, lastLon,
                        location.latitude, location.longitude,
                        results
                    )
                    val deltaDistance = results[0].toDouble()
                    
                    if (deltaDistance > 0) {
                        TrackingState.totalDistanceMeters += deltaDistance
                        TrackingState.addDistanceToMode(mode, deltaDistance)
                    }
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
