package fi.metropolia.canopy.ui.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import fi.metropolia.canopy.ui.theme.Darkbutton
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import fi.metropolia.canopy.R
import fi.metropolia.canopy.utils.viewModelFactories.TripViewModelFactory
import fi.metropolia.canopy.viewmodels.TripViewModel
import kotlin.math.roundToInt
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import fi.metropolia.canopy.ui.screens.getModeDisplayName

@Composable
fun OverviewScreen() {

    val context = LocalContext.current
    val viewModel: TripViewModel = viewModel(
        factory = TripViewModelFactory(context)
    )

    val rawEmissions by viewModel.emissions.collectAsState()
    val walkingDist by viewModel.walkingDistance.collectAsState()
    val cyclingDist by viewModel.cyclingDistance.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadEmissions()
    }

    val emissions = remember(rawEmissions) {
        val grouped = mutableMapOf<String, Double>()
        rawEmissions.forEach { (mode, value) ->
            val category = when (mode.lowercase().trim()) {
                "petrol", "diesel", "hybrid", "electric", "car unknown", "car" -> "car"
                "bus", "car/bus" -> "bus"
                "metro" -> "metro"
                "train", "train/high-speed" -> "train"
                "moped", "moped_scooter" -> "moped"
                "walking" -> "walking"
                "cycling" -> "cycling"
                else -> mode.lowercase().trim()
            }
            grouped[category] = (grouped[category] ?: 0.0) + value
        }
        grouped
    }

    val totalEmission = emissions.values.sum()
    val hasData = totalEmission > 0
    val total = if (totalEmission == 0.0) 1.0 else totalEmission

    val showInfo = remember { mutableStateOf(false) }
    val showConfirm = remember { mutableStateOf(false) }

    val bgColor = OverviewColors.BgGreen

    val emissionsWithDistance = emissions.toMutableMap()
    emissionsWithDistance["walking"] = walkingDist / 1000
    emissionsWithDistance["cycling"] = cyclingDist / 1000

    val orderedModes = listOf(
        "car", "moped",
        "bus", "train", "metro",
        "walking", "cycling"
    )

    val slices =
        if (!hasData) {
            listOf(EmissionSlice(stringResource(R.string.no_data_yet), 1.0, Color(0xFFB2DFDB)))
        } else {
            orderedModes
                .filter { it != "walking" && it != "cycling" }
                .filter { (emissionsWithDistance[it] ?: 0.0) > 0 }
                .map {
                    EmissionSlice(
                        getModeDisplayName(it),
                        emissionsWithDistance[it] ?: 0.0,
                        colorForMode(it)
                    )
                }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .verticalScroll(scrollState)
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.overview_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(start = 20.dp)
        )

        Spacer(Modifier.height(12.dp))

        if (!hasData) {
            Text(
                text = stringResource(R.string.no_trips_hint),
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 20.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                val animatedProgress by animateFloatAsState(
                    targetValue = if (hasData) 1f else 0f,
                    animationSpec = tween(durationMillis = 800),
                    label = "donutAnimation"
                )

                val centerText = if (hasData) {
                    when {
                        totalEmission >= 1000 -> stringResource(R.string.t_co2_unit, (totalEmission / 1000).toInt())
                        totalEmission >= 100 -> stringResource(R.string.kg_co2_multi_line, totalEmission.toInt())
                        else -> stringResource(R.string.kg_co2_unit, totalEmission.toInt())
                    }
                } else {
                    stringResource(R.string.no_data)
                }

                DonutChart(
                    centerText = "",
                    slices = slices.map {
                        it.copy(value = it.value * animatedProgress)
                    }
                )
                Text(
                    text = centerText,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(48.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                orderedModes.forEach { category ->

                    val icon = iconForLabel(category)
                    val value = emissions[category] ?: 0.0

                    val textValue =
                        if (category == "walking" || category == "cycling") ""
                        else {
                            val pct = if (hasData) (value / total * 100).roundToInt() else 0
                            "$pct%"
                        }

                    if ((emissionsWithDistance[category] ?: 0.0) > 0) {

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Icon(icon, null, tint = colorForMode(category))

                            Spacer(Modifier.width(8.dp))

                            Text(
                                text = "${getModeDisplayName(category)} $textValue",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {

            Text(
                text = stringResource(R.string.total_suffix, if (totalEmission > 1000)
                    stringResource(R.string.t_co2_unit, (totalEmission / 1000).toInt())
                else
                    stringResource(R.string.kg_co2_unit, totalEmission.toInt())),
                style = MaterialTheme.typography.headlineMedium
            )

            if (!hasData) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.no_emissions_recorded),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.height(20.dp))

            val visibleModes = orderedModes.filter {
                (emissionsWithDistance[it] ?: 0.0) > 0
            }

            if (hasData) {
                visibleModes.forEach { category ->

                    val value = emissions[category] ?: 0.0

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Icon(
                                imageVector = iconForLabel(category),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(Modifier.width(10.dp))

                            Text(
                                getModeDisplayName(category),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = when (category) {
                                "walking" -> formatDistance(walkingDist)
                                "cycling" -> formatDistance(cyclingDist)
                                else -> {
                                    if (value > 1000)
                                        stringResource(R.string.kg_co2_int_format, value)
                                    else
                                        formatEmission(value)
                                }
                            },
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { showInfo.value = true },
                    modifier = Modifier.height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Darkbutton,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Info, contentDescription = null)
                }

                Button(
                    onClick = { showConfirm.value = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Darkbutton,
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(R.string.export_csv))
                }
            }

            Spacer(Modifier.height(120.dp))

            if (showInfo.value) {
                AlertDialog(
                    onDismissRequest = { showInfo.value = false },
                    title = { Text(stringResource(R.string.emissions_info_title)) },
                    text = {
                        Text(stringResource(R.string.emissions_info_text))
                    },
                    confirmButton = {
                        Button(
                            onClick = { showInfo.value = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Darkbutton,
                                contentColor = Color.White
                            )
                        ) {
                            Text(stringResource(R.string.ok))
                        }
                    }
                )
            }

            if (showConfirm.value) {
                AlertDialog(
                    onDismissRequest = { showConfirm.value = false },
                    title = { Text(stringResource(R.string.confirm_export_title)) },
                    text = {
                        Text(stringResource(R.string.confirm_export_text))
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.exportData(context)
                                showConfirm.value = false
                            }) {
                            Text(stringResource(R.string.yes))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showConfirm.value = false }
                        ) {
                            Text(stringResource(R.string.no))
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun formatDistance(meters: Double): String =
    if (meters >= 1000) stringResource(R.string.km_unit, meters / 1000)
    else stringResource(R.string.m_unit, meters)

@Composable
fun formatEmission(kg: Double): String =
    if (kg >= 1) stringResource(R.string.kg_co2_format, kg)
    else stringResource(R.string.g_co2_unit, kg * 1000)

private fun iconForLabel(label: String) = when (label.lowercase()) {
    "bus" -> Icons.Filled.DirectionsBus
    "metro" -> Icons.Filled.DirectionsSubway
    "train" -> Icons.Filled.Train
    "walking" -> Icons.Filled.DirectionsWalk
    "cycling" -> Icons.Filled.DirectionsBike
    "moped" -> Icons.Filled.TwoWheeler
    else -> Icons.Filled.DirectionsCar
}

private fun colorForMode(mode: String) = when (mode.lowercase()) {
    "car" -> Color(0xFFD32F2F)
    "moped" -> Color(0xFFFF6B6B)
    "bus" -> Color(0xFFFFC107)
    "train" -> Color(0xFFFFE082)
    "walking" -> Color(0xFF1B5E20)
    "cycling" -> Color(0xFF2E7D32)
    "metro" -> Color(0xFFA5D6A7)
    else -> Color.Gray
}
