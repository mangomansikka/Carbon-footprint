package fi.metropolia.canopy.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.google.android.gms.location.FusedLocationProviderClient
import fi.metropolia.canopy.location.LocationTracker
import fi.metropolia.canopy.activityrecognition.ActivityRecognitionManager
import fi.metropolia.canopy.permissions.rememberPermissionLauncher
import fi.metropolia.canopy.data.TrackingState

@Composable
fun LocationScreen(fusedLocationClient: FusedLocationProviderClient) {

    val context = LocalContext.current
    val locationTracker = remember { LocationTracker(fusedLocationClient) }
    val activityRecognitionManager = remember { ActivityRecognitionManager(context) }

    var locationText by remember { mutableStateOf("No location yet") }
    var isTracking by remember { mutableStateOf(false) }

    val startTrackingWithPermissions = rememberPermissionLauncher(
        onPermissionsGranted = {
            TrackingState.reset()

            locationTracker.start { locationText = it }

            try {
                activityRecognitionManager.start()
            } catch (e: SecurityException) {
                locationText = "Activity recognition error"
            }

            isTracking = true
        },
        onPermissionsDenied = {
            locationText = "Permissions denied"
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { startTrackingWithPermissions() },
                enabled = !isTracking,
                modifier = Modifier.weight(1f)
            ) {
                Text("Start Trip")
            }

            Button(
                onClick = {
                    locationTracker.stop()
                    try { activityRecognitionManager.stop() } catch (_: SecurityException) {}
                    isTracking = false
                },
                enabled = isTracking,
                modifier = Modifier.weight(1f)
            ) {
                Text("End Trip")
            }
        }

        // Live Debug Info Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Live Activity Detection", style = MaterialTheme.typography.titleSmall)
                Text(
                    "Activity: ${TrackingState.currentActivityByConfidence}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    "Confidence: ${TrackingState.currentConfidence}%",
                    style = MaterialTheme.typography.bodyLarge
                )
                LinearProgressIndicator(
                    progress = { TrackingState.currentConfidence / 100f },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                )
            }
        }

        Text(locationText, style = MaterialTheme.typography.bodyLarge)

        HorizontalDivider()

        Text("Trip Distance by Mode:", style = MaterialTheme.typography.titleMedium)

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Sort by distance (highest first) and exclude 'still' for cleaner UI
            val sortedModes = TrackingState.modeDistances.entries
                .filter { it.key != "still" && it.value > 0 }
                .sortedByDescending { it.value }

            items(sortedModes) { entry ->
                val mode = entry.key
                val distance = entry.value
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = mode.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (distance < 1000) "%.0f m".format(distance) 
                                   else "%.2f km".format(distance / 1000.0),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            
            if (sortedModes.isEmpty()) {
                item {
                    Text("Start moving to see distances per mode.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        
        Text(
            text = "Total: %.2f km".format(TrackingState.totalDistanceMeters / 1000.0),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
