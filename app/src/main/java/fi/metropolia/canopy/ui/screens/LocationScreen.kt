package fi.metropolia.canopy.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import fi.metropolia.canopy.activityrecognition.ActivityRecognitionManager
import fi.metropolia.canopy.data.TrackingState
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
    val context = LocalContext.current
    val viewModel: TripViewModel = viewModel(
        factory = TripViewModelFactory(context)
    )
    var locationText by remember { mutableStateOf("No location yet") }
    val state by viewModel.tripState
    
    // Observed from TrackingState
    val activities = TrackingState.usedTransportModes

    val activityRecognitionManager = remember { ActivityRecognitionManager(context) }
    var locationCallback by remember { mutableStateOf<LocationCallback?>(null) }

    val locationDao = CanopyDatabase.getInstance(context).locationDao()

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val activityGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissions[Manifest.permission.ACTIVITY_RECOGNITION] == true
            } else {
                true 
            }

            if (locationGranted && activityGranted) {
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
                try {
                    activityRecognitionManager.start()
                } catch (e: SecurityException) {
                    locationText = "Activity recognition error"
                }
                viewModel.startTracking()
            } else {
                locationText = "Permissions denied"
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
                val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    permissions.add(Manifest.permission.ACTIVITY_RECOGNITION)
                }
                permissionLauncher.launch(permissions.toTypedArray())
            },
            enabled = !state.isTracking
        ) {
            Text("Start Trip")
        }

        Button(
            onClick = {
                LocationHelper.stopLocationUpdates(fusedLocationClient, locationCallback)
                try {
                    activityRecognitionManager.stop()
                } catch (e: SecurityException) {}
                viewModel.stopTracking()
                locationText = "Tracking stopped"
            },
            enabled = state.isTracking
        ) {
            Text("End Trip")
        }
        
        Button(
            onClick = {
                navController.navigate("overviewScreen")
            }
        ) {
            Text("Overview")
        }

        Text(locationText)
        Text("Distance: ${"%.2f".format(state.totalDistanceMeters)} m")
        Text("Speed: ${"%.2f".format(state.currentSpeedMps)} m/s")

        HorizontalDivider()

        Text("Detected Activities:", style = MaterialTheme.typography.titleMedium)
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(activities.distinct()) { activity ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = activity.replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            if (activities.isEmpty()) {
                item {
                    Text("No activities detected", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
