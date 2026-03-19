package fi.metropolia.canopy.ui.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
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
import fi.metropolia.canopy.ui.overview.DonutChart
import fi.metropolia.canopy.ui.overview.EmissionSlice
import kotlin.math.roundToInt

@Composable
fun OverviewScreen(navController: NavController) {

    val context = LocalContext.current

    val viewModel: TripViewModel = viewModel(
        factory = TripViewModelFactory(context)
    )

    val trips by viewModel.trips.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTrips()
    }


    val totalEmission = trips.sumOf { it.carbonEmissionGrams.toDouble() }
    val total = totalEmission.coerceAtLeast(0.000001)


    val slices: List<EmissionSlice> = listOf(
        EmissionSlice(
            label = "Total",
            value = totalEmission,
            color = Color(0xFF6FCF97)
        )
    )

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

        Spacer(Modifier.height(20.dp))

        /*  DONUT + TEKSTI */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {

            DonutChart(
                centerText = "${totalEmission.roundTo1()} g",
                slices = slices
            )

            Spacer(Modifier.width(32.dp))

            Column {
                Text(
                    text = "Total 100%",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(Modifier.height(30.dp))

        /*  LISTA */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color(0xFFEFEFEF))
                .padding(20.dp)
        ) {

            Text(
                text = "${totalEmission.roundTo1()} g CO2 total",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(24.dp))

            trips
                .filter { it.carbonEmissionGrams > 0 }
                .forEachIndexed { index, trip ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = "Trip #${index + 1}",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = "${trip.carbonEmissionGrams} g",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                }
        }
    }
}

/* FORMAT */

private fun Double.roundTo1(): String =
    ((this * 10.0).roundToInt() / 10.0).toString()