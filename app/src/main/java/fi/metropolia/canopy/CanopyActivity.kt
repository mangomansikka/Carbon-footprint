package fi.metropolia.canopy

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.*
import fi.metropolia.canopy.ui.theme.CanopyMinnoTheme
import fi.metropolia.canopy.viewmodels.TripViewModel

class CanopyActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private val viewModel: TripViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            CanopyMinnoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LocationScreen(
                        fusedLocationClient = fusedLocationClient,
                        setCallback = { callback -> locationCallback = callback },
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }
}

@Composable
fun LocationScreen(
    fusedLocationClient: FusedLocationProviderClient,
    setCallback: (LocationCallback?) -> Unit,
    viewModel: TripViewModel
) {
    var locationText by remember { mutableStateOf("No location yet") }
    val state by viewModel.tripState

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val callback = startLocationUpdates(
                fusedLocationClient,
                onLocationUpdate = { locationText = it },
                setCallback = setCallback,
                viewModel = viewModel
            )
            setCallback(callback)
            viewModel.startTracking()
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
            enabled = !state.isTracking
        ) {
            Text("Start")
        }

        Button(
            onClick = {
                stopLocationUpdates(fusedLocationClient, setCallback)
                viewModel.stopTracking()
                locationText = "Tracking stopped"
            },
            enabled = state.isTracking
        ) {
            Text("End")
        }

        Text(locationText)
        Text("Distance: ${state.totalDistanceMeters} m")
        Text("Speed: ${state.currentSpeedMps} m/s")
    }
}

@SuppressLint("MissingPermission")
fun startLocationUpdates(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationUpdate: (String) -> Unit,
    setCallback: (LocationCallback) -> Unit,
    viewModel: TripViewModel
): LocationCallback {
    onLocationUpdate("Waiting for location updates…")

    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, // Do it outside of Wi-Fi
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
    setCallback: (LocationCallback?) -> Unit,
) {
    fusedLocationClient.removeLocationUpdates(object : LocationCallback() {})
    setCallback(null)
}
