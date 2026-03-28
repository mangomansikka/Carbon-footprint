package fi.metropolia.canopy.ui.overview


import androidx.compose.foundation.background
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

    LaunchedEffect(Unit) {
        viewModel.loadEmissions()
    }

    /* GROUP DATA */
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

    /* DONUT */
    val slices: List<EmissionSlice> =
        if (!hasData) {
            listOf(
                EmissionSlice("No data", 1.0, Color(0xFF6FCF97))
            )
        } else {
            emissions
                .filter { it.value > 0 && it.key != "Walking" && it.key != "Cycling" }
                .map { (category, value) ->
                    EmissionSlice(category, value, colorForMode(category))
                }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OverviewColors.BgGreen)
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(start = 20.dp)
        )

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            DonutChart(
                centerText = if (hasData)
                    "${totalEmission.roundTo1()} g"
                else
                    "No Data",
                slices = slices
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                val summaryModes = listOf(
                    "Car" to Icons.Default.DirectionsCar,
                    "Bus" to Icons.Default.DirectionsBus,
                    "Train" to Icons.Default.Train,
                    "Metro" to Icons.Default.DirectionsSubway,
                    "Moped" to Icons.Default.TwoWheeler,
                    "Walking" to Icons.Default.DirectionsWalk,
                    "Cycling" to Icons.Default.DirectionsBike
                )

                summaryModes.forEach { (category, icon) ->

                    val value = emissions[category] ?: 0.0

                    val textValue =
                        if (category == "Walking" || category == "Cycling") {
                            ""
                        } else {
                            val pct = if (hasData) (value / total * 100).roundToInt() else 0
                            "$pct%"
                        }

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Icon(icon, null, tint = Color.White)

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

        Spacer(Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color(0xFFF5F5F5))
                .padding(20.dp)
        ) {

            Text(
                text = "${totalEmission.roundTo1()} g CO2 total",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(20.dp))

            val allModes = listOf(
                "Car", "Bus", "Train", "Metro", "Moped", "Walking", "Cycling"
            )

            allModes.forEach { category ->

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
                            "Walking", "Cycling" -> "No emissions"
                            else -> "${value.roundTo1()} g"
                        },
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Spacer(Modifier.height(12.dp))
            }

            if (!hasData) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Start a trip to see your emissions",
                    color = Color.Gray
                )
            }
        }
    }
}

/* ICONS */
private fun iconForLabel(label: String) = when (label.lowercase()) {
    "bus" -> Icons.Filled.DirectionsBus
    "metro" -> Icons.Filled.DirectionsSubway
    "train" -> Icons.Filled.Train
    "walking" -> Icons.Filled.DirectionsWalk
    "cycling" -> Icons.Filled.DirectionsBike
    "moped" -> Icons.Filled.TwoWheeler
    else -> Icons.Filled.DirectionsCar
}

/* COLORS */
private fun colorForMode(mode: String) = when (mode.lowercase()) {
    "car" -> Color(0xFF6FCF97)
    "bus" -> Color(0xFF27AE60)
    "train" -> Color(0xFF1B5E20)
    "metro" -> Color(0xFFA8E6CF)
    "moped" -> Color(0xFF2F4F2F)
    else -> Color.Gray
}

/* FORMAT */
private fun Double.roundTo1(): String =
    ((this * 10.0).roundToInt() / 10.0).toString()