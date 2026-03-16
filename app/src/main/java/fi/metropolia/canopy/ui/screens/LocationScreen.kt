package fi.metropolia.canopy.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.canopy.data.TrackingState
import fi.metropolia.canopy.location.TrackingService
import fi.metropolia.canopy.utils.viewModelFactories.TripViewModelFactory
import fi.metropolia.canopy.viewmodels.TripViewModel

@Composable
fun LocationScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: TripViewModel = viewModel(
        factory = TripViewModelFactory(context)
    )

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
                startTrackingService(context)
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
                    val permissions = mutableListOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        permissions.add(Manifest.permission.ACTIVITY_RECOGNITION)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    permissionLauncher.launch(permissions.toTypedArray())
                },
                enabled = !TrackingState.isTracking,
                modifier = Modifier.weight(1f)
            ) {
                Text("Start Trip")
            }

            Button(
                onClick = {
                    stopTrackingService(context)
                    // Trigger the persistence logic in the ViewModel
                    viewModel.stopTracking()
                },
                enabled = TrackingState.isTracking,
                modifier = Modifier.weight(1f)
            ) {
                Text("End Trip")
            }
        }

        // Live Debug & Detection Section
        if (TrackingState.isTracking) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Live Detection", style = MaterialTheme.typography.titleSmall)
                    Text("Current: ${TrackingState.currentActivityByConfidence}")
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // PART 4: UI Debug Display
                    Text(
                        text = "Average speed (last ${TrackingState.speedHistorySize} points): ${"%.1f".format(TrackingState.averageSpeedMps)} m/s",
                        style = MaterialTheme.typography.bodySmall
                    )
                    
                    LinearProgressIndicator(
                        progress = { TrackingState.currentConfidence / 100f },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
            }
        }

        Text("Total Distance: ${"%.2f".format(TrackingState.totalDistanceMeters)} m")
        Text("Current Speed: ${"%.2f".format(TrackingState.currentSpeedMps)} m/s")

        HorizontalDivider()

        Text("Distance per Mode:", style = MaterialTheme.typography.titleMedium)
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(TrackingState.modeDistances.entries.toList()) { entry ->
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
            if (TrackingState.modeDistances.isEmpty()) {
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

private fun startTrackingService(context: Context) {
    val intent = Intent(context, TrackingService::class.java).apply {
        action = TrackingService.ACTION_START
    }
    ContextCompat.startForegroundService(context, intent)
}

private fun stopTrackingService(context: Context) {
    val intent = Intent(context, TrackingService::class.java).apply {
        action = TrackingService.ACTION_STOP
    }
    context.startService(intent)
}
