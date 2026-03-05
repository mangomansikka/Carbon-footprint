package fi.metropolia.canopy.utils

import android.annotation.SuppressLint
import android.os.Looper
import com.google.android.gms.location.*
import fi.metropolia.canopy.data.source.LocationDAO
import fi.metropolia.canopy.data.source.LocationEntity
import fi.metropolia.canopy.viewmodels.TripViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object LocationHelper {

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(
        fusedLocationClient: FusedLocationProviderClient,
        onLocationUpdate: (String) -> Unit,
        setCallback: (LocationCallback) -> Unit,
        locationDao: LocationDAO,
        viewModel: TripViewModel
    ): LocationCallback {

        onLocationUpdate("Waiting for location updates…")

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000L
        ).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation
                if (location != null) {
                    onLocationUpdate(
                        "Lat: ${location.latitude}, Lng: ${location.longitude}"
                    )

                    viewModel.onNewLocation(location)

                    val entity = LocationEntity(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        locationDao.insertLocation(entity)
                    }
                }
            }
        }

        setCallback(callback)

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        )

        return callback
    }

    fun stopLocationUpdates(
        fusedLocationClient: FusedLocationProviderClient,
        callback: LocationCallback?
    ) {
        callback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }
}
