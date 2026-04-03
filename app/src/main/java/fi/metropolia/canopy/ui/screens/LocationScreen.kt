package fi.metropolia.canopy.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.canopy.domain.model.TrackingState
import fi.metropolia.canopy.utils.viewModelFactories.TripViewModelFactory
import fi.metropolia.canopy.viewmodels.TripViewModel

@Composable
fun LocationScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: TripViewModel = viewModel(
        factory = TripViewModelFactory(context)
    )

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadEmissions()
    }

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
                viewModel.startTracking(context)
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp, 60.dp, 24.dp, 24.dp)
            .verticalScroll(scrollState)
            .navigationBarsPadding(),
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
                enabled = !viewModel.isTracking,
                modifier = Modifier.weight(1f)
            ) {
                Text("Start Trip")
            }

            Button(
                onClick = {
                    viewModel.endTrip(context)
                },
                enabled = viewModel.isTracking,
                modifier = Modifier.weight(1f)
            ) {
                Text("End Trip")
            }
        }
        Button(
            onClick = { navController.navigate("manualScreen") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add manually")
        }

        // Scrollable Content
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Debug & Detection Section
            if (viewModel.isTracking) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Live Detection", style = MaterialTheme.typography.titleSmall)
                            Text("Current: ${TrackingState.currentActivityByConfidence}")
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
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
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Total Distance: ${"%.2f".format(viewModel.totalDistanceMeters)} m")
                    Text("Current Speed: ${"%.2f".format(viewModel.currentSpeedMps)} m/s")
                    HorizontalDivider()
                    Text("Distance per Mode:", style = MaterialTheme.typography.titleMedium)
                }
            }


            val distances = viewModel.modeDistances.entries.filter { it.key != "still" }.toList()
            if (distances.isEmpty()) {
                item {
                    Text("No distance recorded yet", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                items(distances) { entry ->
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
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HorizontalDivider()
                    Text("Emissions per Mode (Current Trip):", style = MaterialTheme.typography.titleMedium)
                }
            }

            val currentEmissions = viewModel.modeEmissions.entries.filter { it.key != "still" }.toList()
            if (currentEmissions.isEmpty()) {
                item {
                    Text("Start moving to see emission data...",
                        style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                items(currentEmissions) { entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = entry.key.replaceFirstChar { it.uppercase() },
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "${"%.4f".format(entry.value)} kg CO₂")
                        }
                    }
                }
            }

            item {
                HorizontalDivider()
                Text("To see your total emissions, go to the Overview screen",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
