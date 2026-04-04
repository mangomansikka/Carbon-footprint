package fi.metropolia.canopy.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.canopy.domain.model.TrackingState
import fi.metropolia.canopy.ui.overview.OverviewColors
import fi.metropolia.canopy.ui.theme.Darkbutton
import fi.metropolia.canopy.utils.viewModelFactories.TripViewModelFactory
import fi.metropolia.canopy.viewmodels.TripViewModel

@Composable
fun LocationScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: TripViewModel = viewModel(
        factory = TripViewModelFactory(context)
    )

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
            .background(Color.White)
            .navigationBarsPadding()
    ) {
        /* HEADER - Matching Green Theme */
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(OverviewColors.BgGreen)
                .padding(top = 48.dp, start = 20.dp, bottom = 24.dp)
        ) {
            Text(
                text = "Live Tracking",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }

        /* CONTENT */
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // Action Buttons
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Darkbutton),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Start Trip")
                    }

                    Button(
                        onClick = { viewModel.endTrip(context) },
                        enabled = viewModel.isTracking,
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.isTracking) Color(0xFFE57373) else Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("End Trip")
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = { navController.navigate("manualScreen") },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add Manual Entry")
                }
            }

            // Live Debug & Detection Section
            if (viewModel.isTracking) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = OverviewColors.BgGreen.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Live Detection", style = MaterialTheme.typography.titleSmall, color = OverviewColors.BgGreen)
                            Text("Mode: ${TrackingState.currentActivityByConfidence}", fontWeight = FontWeight.Bold)
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Avg Speed: ${"%.1f".format(TrackingState.averageSpeedMps)} m/s",
                                style = MaterialTheme.typography.bodySmall
                            )
                            
                            LinearProgressIndicator(
                                progress = { TrackingState.currentConfidence / 100f },
                                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                color = OverviewColors.BgGreen,
                                trackColor = Color.LightGray
                            )
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Total Distance: ${"%.2f".format(viewModel.totalDistanceMeters)} m", style = MaterialTheme.typography.titleMedium)
                    Text("Current Speed: ${"%.2f".format(viewModel.currentSpeedMps)} m/s", style = MaterialTheme.typography.bodyMedium)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Distance per Mode:", style = MaterialTheme.typography.titleSmall, color = Color.Gray)
                }
            }

            val distances = viewModel.modeDistances.entries.filter { it.key != "still" }.toList()
            if (distances.isEmpty()) {
                item {
                    Text("No data yet", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            } else {
                items(distances) { entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(entry.key.replaceFirstChar { it.uppercase() })
                        Text("${"%.1f".format(entry.value)} m", fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Emissions (Current Trip):", style = MaterialTheme.typography.titleSmall, color = Color.Gray)
            }

            val currentEmissions = viewModel.modeEmissions.entries.filter { it.key != "still" }.toList()
            if (currentEmissions.isEmpty()) {
                item {
                    Text("Emissions will appear once you start moving", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            } else {
                items(currentEmissions) { entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8F1)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(entry.key.replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.Medium)
                            Text(text = "${"%.4f".format(entry.value)} kg CO₂", color = OverviewColors.BgGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Visit the Overview screen for your full history.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
