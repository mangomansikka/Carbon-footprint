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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.canopy.R
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
                text = stringResource(R.string.live_tracking),
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
                        Text(stringResource(R.string.start_trip))
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
                        Text(stringResource(R.string.end_trip))
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = { navController.navigate("manualScreen") },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.add_manual_entry))
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
                            Text(stringResource(R.string.live_detection), style = MaterialTheme.typography.titleSmall, color = OverviewColors.BgGreen)
                            Text(stringResource(R.string.mode_label, getModeDisplayName(TrackingState.currentConfirmedMode)), fontWeight = FontWeight.Bold)
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = stringResource(R.string.avg_speed_label, TrackingState.averageSpeedMps),
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
                    Text(stringResource(R.string.total_distance_label, viewModel.totalDistanceMeters), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.current_speed_label, viewModel.currentSpeedMps), style = MaterialTheme.typography.bodyMedium)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(stringResource(R.string.distance_per_mode), style = MaterialTheme.typography.titleSmall, color = Color.Gray)
                }
            }

            val distances = viewModel.modeDistances.entries.filter { it.key != "still" }.toList()
            if (distances.isEmpty()) {
                item {
                    Text(stringResource(R.string.no_data_yet), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            } else {
                items(distances) { entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(getModeDisplayName(entry.key))
                        Text(stringResource(R.string.distance_m_label, entry.value), fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(stringResource(R.string.emissions_current_trip), style = MaterialTheme.typography.titleSmall, color = Color.Gray)
            }

            val currentEmissions = viewModel.modeEmissions.entries.filter { it.key != "still" }.toList()
            if (currentEmissions.isEmpty()) {
                item {
                    Text(stringResource(R.string.emissions_hint), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
                            Text(getModeDisplayName(entry.key), fontWeight = FontWeight.Medium)
                            Text(text = stringResource(R.string.kg_co2_label, entry.value), color = OverviewColors.BgGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.overview_history_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun getModeDisplayName(mode: String): String {
    return when (mode.lowercase()) {
        "car" -> stringResource(R.string.mode_car)
        "bus" -> stringResource(R.string.mode_bus)
        "train" -> stringResource(R.string.mode_train)
        "metro" -> stringResource(R.string.mode_metro)
        "moped", "moped_scooter" -> stringResource(R.string.mode_moped)
        "walking", "on_foot" -> stringResource(R.string.mode_walking)
        "cycling", "bicycle", "on_bicycle" -> stringResource(R.string.mode_bicycle)
        "tram" -> stringResource(R.string.mode_tram)
        "still" -> stringResource(R.string.mode_still)
        else -> stringResource(R.string.mode_none)
    }
}
