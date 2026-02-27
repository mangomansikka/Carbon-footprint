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
import fi.metropolia.canopy.data.source.LocationDAO
import fi.metropolia.canopy.data.source.CanopyDatabase
import fi.metropolia.canopy.data.source.LocationEntity
import fi.metropolia.canopy.ui.theme.CanopyMinnoTheme
import fi.metropolia.canopy.viewmodels.TripViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CanopyActivity : ComponentActivity() {

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
                        setCallback = { callback -> locationCallback = callback },
                        viewModel = TripViewModel()
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
    setCallback: (LocationCallback) -> Unit,
    viewModel: TripViewModel
) {
    var locationText by remember { mutableStateOf("No location yet") }
    val state by viewModel.tripState
    var isTracking by remember { mutableStateOf(false) }
    var locationCallback by remember { mutableStateOf<LocationCallback?>(null) }

    val context = LocalContext.current
    val locationDao = CanopyDatabase.getInstance(context).locationDao()

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                locationCallback = startLocationUpdates(
                    fusedLocationClient,
                    onLocationUpdate = { locationText = it },
                    setCallback = setCallback,
                    locationDao = locationDao,
                    viewModel = viewModel
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
                stopLocationUpdates(fusedLocationClient, locationCallback)
                isTracking = false
                locationText = "Tracking stopped"
            },
            enabled = isTracking
        ) {
            Text("End")
        }

        Text(locationText)
        Text("Distance: ${state.totalDistanceMeters} m")
        Text("Speed: ${state.currentSpeedMps} m/s")
    }
}

// Location updates
@SuppressLint("MissingPermission")
fun startLocationUpdates(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationUpdate: (String) -> Unit,
    setCallback: (LocationCallback) -> Unit,
    locationDao: LocationDAO,
    viewModel: TripViewModel
): LocationCallback {

    onLocationUpdate("Waiting for location updates…")

    // Configure location request
    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, // Do it outside of Wi-Fi
        2000L
    ).build()

    // Configure location callback
    val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation
            if (location != null) {
                onLocationUpdate(
                    "Lat: ${location.latitude}, Lng: ${location.longitude}"
                )

                // Update ViewModel
                viewModel.onNewLocation(location)

                // Save location to database
                val entity = LocationEntity(
                    latitude = location.latitude,
                    longitude = location.longitude
                )

                // Launch coroutine to save to database
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

// Stop location updates
fun stopLocationUpdates(
    fusedLocationClient: FusedLocationProviderClient,
    callback: LocationCallback?
) {
    callback?.let { fusedLocationClient.removeLocationUpdates(it) }
}