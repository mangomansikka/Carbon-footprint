package fi.metropolia.canopy

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.*
import fi.metropolia.canopy.data.DAO
import fi.metropolia.canopy.data.CanopyDatabase
import fi.metropolia.canopy.data.LocationEntity
import fi.metropolia.canopy.ui.theme.CanopyMinnoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch




class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Set the content of the activity
        setContent {
            CanopyMinnoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LocationScreen(
                        fusedLocationClient = fusedLocationClient,
                        setCallback = { callback -> locationCallback = callback }
                    )
                }
            }
        }
    }

    // Stop location updates when the activity is paused
    override fun onStop() {
        super.onStop()
        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}

// LocationScreen composable UI
@Composable
fun LocationScreen(
    fusedLocationClient: FusedLocationProviderClient,
    setCallback: (LocationCallback) -> Unit
) {
    var locationText by remember { mutableStateOf("No location yet") }
    var isTracking by remember { mutableStateOf(false) }
    var totalDistance by remember { mutableDoubleStateOf(0.0) }
    val locations = remember { mutableStateListOf<LocationEntity>() }
    var dbDistanceMeters by remember { mutableDoubleStateOf(0.0) }
    var dbEmissionKg by remember { mutableDoubleStateOf(0.0) }

    val context = LocalContext.current
    val locationDao = CanopyDatabase.getInstance(context).locationDao()

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                locations.clear()
                totalDistance = 0.0
                startLocationUpdates(
                    fusedLocationClient,
                    onLocationUpdate = { locationText = it },
                    setCallback = setCallback,
                    locationDao = locationDao,
                    locations = locations
                )
                isTracking = true
            } else {
                locationText = "Permission denied"
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Button(
            onClick = {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            },
            enabled = !isTracking
        ) {
            Text("Start")
        }

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val distance = locationDao.getTotalDistanceMeters()
                    val emission = locationDao.getTotalEmissionKg()
                    kotlinx.coroutines.withContext(Dispatchers.Main) {
                        dbDistanceMeters = distance
                        dbEmissionKg = emission
                        locationText = "Tracking stopped. DB distance: %.2f m, DB emission: %.5f kgCO2e"
                            .format(distance, emission)
                    }
                }
            },
            enabled = isTracking
        ) {
            Text("End")
        }

        Text(locationText)
        if (totalDistance > 0) {
            Text("Total distance traveled: %.2f meters".format(totalDistance))
        }

        Text("DB total distance: %.2f m".format(dbDistanceMeters))
        Text("DB total emissions: %.5f kgCO2e".format(dbEmissionKg))
    }
}

// Location updates
@SuppressLint("MissingPermission")
fun startLocationUpdates(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationUpdate: (String) -> Unit,
    setCallback: (LocationCallback) -> Unit,
    locationDao: DAO,
    locations: MutableList<LocationEntity>
): LocationCallback {

    onLocationUpdate("Waiting for location updates…")

    // Configure location request
    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, // Do it outside of Wi-Fi
        2000L
    ).build()

    var previousLocation: android.location.Location? = null

    // Configure location callback
    val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation
            if (location != null) {
                onLocationUpdate(
                    "Lat: ${location.latitude}, Lng: ${location.longitude}"
                )

                val distanceMeters = previousLocation?.distanceTo(location)?.toDouble() ?: 0.0
                val PETROL_CAR_KG_CO2E_PER_KM = 0.17048
                val emissionKg = (distanceMeters / 1000.0) * PETROL_CAR_KG_CO2E_PER_KM

                // Save location to database
                val entity = LocationEntity(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    segmentDistanceMeters = distanceMeters,
                    segmentEmissionKg = emissionKg
                )

                // Launch coroutine to save to database
                CoroutineScope(Dispatchers.IO).launch {
                    locationDao.insertLocation(entity)
                }
                previousLocation = location
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




// Stop location updates
fun stopLocationUpdates(
    fusedLocationClient: FusedLocationProviderClient,
    callback: LocationCallback?
) {
    callback?.let { fusedLocationClient.removeLocationUpdates(it) }
}