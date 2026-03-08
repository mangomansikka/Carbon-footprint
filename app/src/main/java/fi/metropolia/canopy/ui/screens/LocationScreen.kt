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
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import fi.metropolia.canopy.activityrecognition.ActivityRecognitionManager
import fi.metropolia.canopy.data.TrackingState
import fi.metropolia.canopy.data.source.CanopyDatabase
import fi.metropolia.canopy.location.LocationTracker
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
    
    val modeDistances = TrackingState.modeDistances

    val locationDao = CanopyDatabase.getInstance(context).locationDao()
    
    // We use the LocationTracker class which contains our distance-per-mode logic
    val locationTracker = remember { LocationTracker(fusedLocationClient, locationDao, viewModel) }
    val activityRecognitionManager = remember { ActivityRecognitionManager(context) }

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
                // START tracking using our custom LocationTracker
                locationTracker.start { 
                    locationText = it 
                    // Link the callback back to the activity if needed for background persistence
                    locationTracker.getCallback()?.let { cb -> setCallback(cb) }
                }
                
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

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        permissions.add(Manifest.permission.ACTIVITY_RECOGNITION)
                    }
                    permissionLauncher.launch(permissions.toTypedArray())
                },
                enabled = !state.isTracking,
                modifier = Modifier.weight(1f)
            ) {
                Text("Start Trip")
            }

            Button(
                onClick = {
                    // STOP tracking using our custom LocationTracker
                    locationTracker.stop()
                    try {
                        activityRecognitionManager.stop()
                    } catch (e: SecurityException) {}
                    viewModel.stopTracking()
                    locationText = "Tracking stopped"
                },
                enabled = state.isTracking,
                modifier = Modifier.weight(1f)
            ) {
                Text("End Trip")
            }
        }

        // Live Debug Section
        if (state.isTracking) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Live Detection", style = MaterialTheme.typography.titleSmall)
                    Text("Current: ${TrackingState.currentActivityByConfidence}")
                    LinearProgressIndicator(
                        progress = { TrackingState.currentConfidence / 100f },
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    )
                }
            }
        }

        Text(locationText)
        Text("Total Distance: ${"%.2f".format(TrackingState.totalDistanceMeters)} m")
        Text("Current Speed: ${"%.2f".format(state.currentSpeedMps)} m/s")

        HorizontalDivider()

        Text("Distance per Mode:", style = MaterialTheme.typography.titleMedium)
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(modeDistances.entries.toList()) { entry ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = entry.key.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${"%.1f".format(entry.value)} m",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            if (modeDistances.isEmpty()) {
                item {
                    Text("No distance recorded yet", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Button(
            onClick = {
                navController.navigate("overviewScreen")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go to Overview")
        }
    }
}
