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
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.canopy.utils.viewModelFactories.TripViewModelFactory
import fi.metropolia.canopy.viewmodels.TripViewModel
import kotlin.math.roundToInt

@Composable
fun OverviewScreen(navController: NavController) {

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
                "petrol", "diesel", "hybrid", "electric", "car unknown", "car" -> "Car"
                "bus", "car/bus" -> "Bus"
                "metro" -> "Metro"
                "train", "train/high-speed" -> "Train"
                "moped" -> "Moped"
                "walking" -> "Walking"
                "cycling" -> "Cycling"
                else -> mode.replaceFirstChar { it.uppercase() }
            }
            grouped[category] = (grouped[category] ?: 0.0) + value
        }
        grouped
    }

    val totalEmission = emissions.values.sum()
    val hasData = totalEmission > 0
    val total = if (totalEmission == 0.0) 1.0 else totalEmission

    val showInfo = remember { mutableStateOf(false) }

    val ecoMode = (emissions["Car"] ?: 0.0) == 0.0 &&
            (emissions["Moped"] ?: 0.0) == 0.0

    val bgColor = if (ecoMode) Color(0xFF81C784) else OverviewColors.BgGreen

    val emissionsWithDistance = emissions.toMutableMap()
    emissionsWithDistance["Walking"] = walkingDist / 1000
    emissionsWithDistance["Cycling"] = cyclingDist / 1000

    val orderedModes = listOf(
        "Car", "Moped",
        "Bus", "Train", "Metro",
        "Walking", "Cycling"
    )

    val slices =
        if (!hasData) {
            listOf(EmissionSlice("No data", 1.0, Color(0xFF6FCF97)))
        } else {
            orderedModes
                .filter { (emissionsWithDistance[it] ?: 0.0) > 0 }
                .map {
                    EmissionSlice(
                        it,
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
            text = "Overview",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(start = 20.dp)
        )

        Spacer(Modifier.height(12.dp))

        if (!hasData) {
            Text(
                text = "Start a trip to see your emissions",
                color = Color.White,
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
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center
            ) {

                DonutChart(
                    centerText = "",
                    slices = slices
                )

                val centerText = if (hasData) {
                    val parts = formatEmission(totalEmission).split(" ")
                    "${parts[0]} ${parts[1]}\nCO₂"
                } else {
                    "No Data"
                }

                Text(
                    text = centerText,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(48.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                orderedModes.forEach { category ->

                    val icon = iconForLabel(category)
                    val value = emissions[category] ?: 0.0

                    val textValue =
                        if (category == "Walking" || category == "Cycling") {
                            ""
                        } else {
                            val pct = if (hasData) (value / total * 100).roundToInt() else 0
                            "$pct%"
                        }

                    if ((emissionsWithDistance[category] ?: 0.0) > 0 || category == "Metro") {

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Icon(icon, null, tint = colorForMode(category))

                            Spacer(Modifier.width(8.dp))

                            Text(
                                text = "$category $textValue",
                                color = Color.White,
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
                .background(Color(0xFFF5F5F5))
                .padding(20.dp)
        ) {

            Text(
                text = "${formatEmission(totalEmission)} total",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(20.dp))

            val visibleModes = orderedModes.filter {
                (emissionsWithDistance[it] ?: 0.0) > 0 || it == "Metro"
            }

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
                            tint = Color.Black
                        )

                        Spacer(Modifier.width(10.dp))

                        Text(category, style = MaterialTheme.typography.titleLarge)
                    }

                    Text(
                        text = when (category) {
                            "Walking" -> formatDistance(walkingDist)
                            "Cycling" -> formatDistance(cyclingDist)
                            else -> formatEmission(value)
                        },
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Spacer(Modifier.height(12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                    onClick = { viewModel.exportData(context) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Darkbutton,
                        contentColor = Color.White
                    )
                ) {
                    Text("Export Data to CSV")
                }
            }

            Spacer(Modifier.height(16.dp))

            if (showInfo.value) {
                AlertDialog(
                    onDismissRequest = { showInfo.value = false },
                    title = { Text("Emissions") },
                    text = {
                        Text("Each mode produces different emissions. Walking and cycling are the best.")
                    },
                    confirmButton = {
                        Button(
                            onClick = { showInfo.value = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Darkbutton,
                                contentColor = Color.White
                            )
                        ) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}


fun formatDistance(meters: Double): String =
    if (meters >= 1000) "%.2f km".format(meters / 1000)
    else "%.0f m".format(meters)

fun formatEmission(kg: Double): String =
    if (kg >= 1) "%.2f kg CO₂".format(kg)
    else "%.0f g CO₂".format(kg * 1000)

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
    "car", "moped" -> Color(0xFFD32F2F)
    "bus", "train", "metro" -> Color(0xFFFFEB3B)
    "walking", "cycling" -> Color(0xFF2E7D32)
    else -> Color.Gray
}