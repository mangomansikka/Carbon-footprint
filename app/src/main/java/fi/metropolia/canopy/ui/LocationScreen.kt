package fi.metropolia.canopy.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
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

        Button(
            onClick = { startTrackingWithPermissions() },
            enabled = !isTracking
        ) {
            Text("Start")
        }

        Button(
            onClick = {
            locationTracker.stop()
            try { activityRecognitionManager.stop() } catch (_: SecurityException) {}

            val distanceKm = TrackingState.totalDistanceMeters / 1000

            locationText =
                "Trip ended\n" +
                        "Distance: %.2f km\n".format(distanceKm) +
                        "Transport modes: ${TrackingState.usedTransportModes.joinToString()}"

            isTracking = false
        }) {
            Text("End")
        }

        Text(locationText)
    }
}