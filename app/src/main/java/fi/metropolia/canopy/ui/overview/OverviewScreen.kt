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
import androidx.navigation.NavController
import kotlin.math.roundToInt

@Composable
fun OverviewScreen(navController: NavController) {

    val data = remember { OverviewFakeData.myAverage() }
    val total = data.breakdown.totalTonsPerYear.coerceAtLeast(0.000001)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OverviewColors.BgGreen)
    ) {

        /* STATUS BAR SPACING */
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )

        Spacer(Modifier.height(24.dp))

        /* DONUT + PROSENTIT */

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            DonutChart(
                centerText = "${data.breakdown.totalTonsPerYear.roundTo1()} ton",
                slices = data.breakdown.slices
            )

            Spacer(Modifier.width(24.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                data.breakdown.slices.forEach { s ->

                    val pct = (s.value / total * 100).roundToInt()

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = iconForLabel(s.label),
                            contentDescription = null,
                            tint = Color.Black
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = "$pct%",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(30.dp))

        /* VALKOINEN CARD */

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color(0xFFEFEFEF))
                .padding(20.dp)
        ) {

            Text(
                text = "${data.breakdown.totalTonsPerYear.roundTo1()} ton CO2/yr",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(24.dp))

            data.breakdown.slices.forEach { s ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Icon(
                            imageVector = iconForLabel(s.label),
                            contentDescription = null,
                            tint = Color.Black
                        )

                        Spacer(Modifier.width(12.dp))

                        Text(
                            text = s.label,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    Text(
                        text = "${s.value.roundTo2()} ton",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

/* ICONS */

private fun iconForLabel(label: String) = when (label) {
    "Bus" -> Icons.Filled.DirectionsBus
    "Petrol" -> Icons.Filled.DirectionsCar
    "Train" -> Icons.Filled.Train
    "Metro" -> Icons.Filled.DirectionsSubway
    else -> Icons.Filled.DirectionsCar
}

/* FORMAT */

private fun Double.roundTo1(): String =
    ((this * 10.0).roundToInt() / 10.0).toString()

private fun Double.roundTo2(): String =
    ((this * 100.0).roundToInt() / 100.0).toString()