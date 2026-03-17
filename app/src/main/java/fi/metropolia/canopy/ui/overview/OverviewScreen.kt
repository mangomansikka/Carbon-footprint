package fi.metropolia.canopy.ui.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import kotlin.math.roundToInt

private val AccentGreen = Color(0xFF58F0B1)

@Composable
fun OverviewScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {

    val me = remember { OverviewFakeData.myAverage() }
    val selected = me

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OverviewColors.BgGreen)
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Overview",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(40.dp))

            val total = selected.breakdown.totalTonsPerYear.coerceAtLeast(0.000001)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                DonutChart(
                    centerText = "${selected.breakdown.totalTonsPerYear.roundTo1()} ton",
                    slices = selected.breakdown.slices
                )

                Spacer(Modifier.width(24.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    selected.breakdown.slices.forEach { s ->

                        val pct = (s.value / total * 100).roundToInt()

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Icon(
                                imageVector = iconForLabel(s.label),
                                contentDescription = s.label,
                                tint = Color.Black
                            )

                            Text(
                                text = "$pct%",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(30.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(OverviewColors.CardWhite)
                    .padding(20.dp)
            ) {

                Text(
                    text = "${selected.breakdown.totalTonsPerYear.roundTo1()} ton CO2/yr",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                Spacer(Modifier.height(8.dp))

                selected.breakdown.slices.forEach { s ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            Icon(
                                imageVector = iconForLabel(s.label),
                                contentDescription = s.label,
                                tint = Color.Black
                            )

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

                    Spacer(Modifier.height(12.dp))
                }
            }
        }

        /* -------- BOTTOM NAVIGATION -------- */

        NavigationBar(
            containerColor = Color(0xFF3A2F2F)
        ) {

            NavigationBarItem(
                selected = false,
                onClick = {
                    navController.navigate("home")
                },
                icon = {
                    Icon(Icons.Default.Home, contentDescription = "home", tint = AccentGreen)
                }
            )

            NavigationBarItem(
                selected = true,
                onClick = {
                    navController.navigate("overview") {
                        popUpTo("footprint")
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(Icons.Default.EmojiEvents, contentDescription = "trophy", tint = AccentGreen)
                }
            )

            NavigationBarItem(
                selected = false,
                onClick = {
                    navController.navigate("footprint")
                },
                icon = {
                    Icon(Icons.Default.ShowChart, contentDescription = "chart", tint = AccentGreen)
                }
            )

            NavigationBarItem(
                selected = false,
                onClick = {
                    navController.navigate("eco") {
                        popUpTo("footprint")
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(Icons.Default.Eco, contentDescription = "eco", tint = AccentGreen)
                }
            )
        }
    }
}

private fun iconForLabel(label: String) = when (label) {
    "Bus" -> Icons.Filled.DirectionsBus
    "Petrol" -> Icons.Filled.DirectionsCar
    "Train" -> Icons.Filled.Train
    "Metro" -> Icons.Filled.DirectionsSubway
    else -> Icons.Filled.DirectionsCar
}

private fun Double.roundTo1(): String =
    ((this * 10.0).roundToInt() / 10.0).toString()

private fun Double.roundTo2(): String =
    ((this * 100.0).roundToInt() / 100.0).toString()