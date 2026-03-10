package fi.metropolia.canopy.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import fi.metropolia.canopy.data.source.CanopyDatabase
import fi.metropolia.canopy.utils.LocationHelper
import fi.metropolia.canopy.utils.viewModelFactories.TripViewModelFactory
import fi.metropolia.canopy.viewmodels.TripViewModel

@Composable
fun LocationScreen(
    navController: NavController,
    setCallback: (LocationCallback) -> Unit,
    fusedLocationClient: FusedLocationProviderClient,
) {
    val viewModel: TripViewModel = viewModel(
        factory = TripViewModelFactory(LocalContext.current)
    )
    var locationText by remember { mutableStateOf("No location yet") }
    val state by viewModel.tripState
    
    // Use a remembered variable to hold the callback locally for stopping updates
    var locationCallback by remember { mutableStateOf<LocationCallback?>(null) }

    val context = LocalContext.current
    val locationDao = CanopyDatabase.getInstance(context).locationDao()

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                locationCallback = LocationHelper.startLocationUpdates(
                    fusedLocationClient,
                    onLocationUpdate = { locationText = it },
                    setCallback = { 
                        locationCallback = it
                        setCallback(it) 
                    },
                    locationDao = locationDao,
                    viewModel = viewModel
                )
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
                LocationHelper.stopLocationUpdates(fusedLocationClient, locationCallback)
                viewModel.stopTracking()
                locationText = "Tracking stopped"
            },
            enabled = state.isTracking
        ) {
            Text("End")
        }
        
        Button(
            onClick = {
                navController.navigate("overviewScreen")
            }
        ) {
            Text("Overview")
        }

        Button(
            onClick = {
                navController.navigate("homeScreen")
            }
        ) {
            Text("Home")
        }

        Text(locationText)
        Text("Distance: ${"%.2f".format(state.totalDistanceMeters)} m")
        Text("Speed: ${"%.2f".format(state.currentSpeedMps)} m/s")
    }
}
