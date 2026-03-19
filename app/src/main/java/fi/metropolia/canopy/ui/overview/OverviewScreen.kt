package fi.metropolia.canopy.ui.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsSubway
import androidx.compose.material.icons.filled.Train
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

    val emissions by viewModel.emissions.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadEmissions()
    }

    val totalEmission = emissions.values.sum()
    val hasData = totalEmission > 0
    val total = if (totalEmission == 0.0) 1.0 else totalEmission

    val slices: List<EmissionSlice> =
        if (!hasData) {
            listOf(
                EmissionSlice(
                    label = "No data",
                    value = 1.0,
                    color = Color(0xFF6FCF97)
                )
            )
        } else {
            emissions
                .filter { it.value > 0 }
                .map { (mode, value) ->
                    EmissionSlice(
                        label = mode,
                        value = value,
                        color = colorForMode(mode)
                    )
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

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                val modes = listOf(
                    "petrol" to Icons.Default.DirectionsCar,
                    "bus" to Icons.Default.DirectionsBus,
                    "train" to Icons.Default.Train,
                    "metro" to Icons.Default.DirectionsSubway
                )

                modes.forEach { (mode, icon) ->

                    val value = emissions[mode] ?: 0.0
                    val pct = if (hasData) {
                        (value / total * 100).roundToInt()
                    } else 0

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = "${formatLabel(mode)} $pct%",
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

            emissions
                .filter { it.value > 0 }
                .forEach { (mode, value) ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Icon(
                                imageVector = iconForLabel(mode),
                                contentDescription = null,
                                tint = Color.Black
                            )

                            Spacer(Modifier.width(10.dp))

                            Text(
                                text = formatLabel(mode),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        Text(
                            text = "${value.roundTo1()} g",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                }

            if (!hasData) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Start a trip to see your emissions",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}


private fun iconForLabel(label: String) = when (label.lowercase()) {
    "bus" -> Icons.Filled.DirectionsBus
    "metro" -> Icons.Filled.DirectionsSubway
    "train" -> Icons.Filled.Train
    else -> Icons.Filled.DirectionsCar
}


private fun colorForMode(mode: String) = when (mode.lowercase()) {
    "bus" -> Color(0xFF27AE60)
    "metro" -> Color(0xFFA8E6CF)
    "petrol", "diesel", "hybrid", "electric", "car unknown" -> Color(0xFF6FCF97)
    "moped" -> Color(0xFF2F4F2F)
    else -> Color.Gray
}

private fun formatLabel(label: String): String = when (label.lowercase()) {
    "petrol", "diesel", "hybrid", "electric", "car unknown" -> "Car"
    else -> label.replaceFirstChar { it.uppercase() }
}


private fun Double.roundTo1(): String =
    ((this * 10.0).roundToInt() / 10.0).toString()