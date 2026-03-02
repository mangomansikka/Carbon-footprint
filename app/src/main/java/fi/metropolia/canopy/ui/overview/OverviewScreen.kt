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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlin.math.roundToInt

@Composable
fun OverviewScreen(
    navController: NavController
) {
    var dimension by remember { mutableStateOf(CompareDimension.ME) }

    val me = remember { OverviewFakeData.myAverage() }
    val campuses = remember { OverviewFakeData.campuses() }
    val roles = remember { OverviewFakeData.roles() }

    val targets: List<CompareTarget> = when (dimension) {
        CompareDimension.ME -> listOf(me)
        CompareDimension.CAMPUS -> campuses
        CompareDimension.ROLE -> roles
    }

    var selectedId by remember(dimension) { mutableStateOf(targets.first().id) }
    val selected = targets.first { it.id == selectedId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OverviewColors.BgGreen)
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Overview",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Button(
            onClick = { navController.navigate("locationScreen") },
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3F6F4F),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Go back",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DonutChart(
                centerText = "${selected.breakdown.totalTonsPerYear.roundTo1()} ton",
                slices = selected.breakdown.slices
            )

            Column(
                modifier = Modifier.padding(start = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val total = selected.breakdown.totalTonsPerYear.coerceAtLeast(0.000001)
                selected.breakdown.slices.forEach { s ->
                    val pct = (s.value / total * 100).roundToInt()

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = iconForLabel(s.label),
                            contentDescription = s.label,
                            tint = s.color
                        )
                        Text(
                            text = "${s.label}  $pct%",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(OverviewColors.CardWhite)
                .padding(20.dp)
        ) {
            TabRow(selectedTabIndex = dimension.ordinal) {
                CompareDimension.entries.forEachIndexed { index, dim ->
                    Tab(
                        selected = dimension.ordinal == index,
                        onClick = { dimension = dim },
                        text = { Text(dim.title) }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (dimension != CompareDimension.ME) {
                CompareTargetDropdown(
                    targets = targets,
                    selectedId = selectedId,
                    onSelected = { selectedId = it }
                )
                Spacer(Modifier.height(10.dp))
            }

            Text(
                text = "${selected.breakdown.totalTonsPerYear.roundTo1()} ton CO2/yr",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            Spacer(Modifier.height(8.dp))

            selected.breakdown.slices.forEach { s ->
                BreakdownRow(
                    label = s.label,
                    valueTons = s.value,
                    color = s.color
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun CompareTargetDropdown(
    targets: List<CompareTarget>,
    selectedId: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = targets.first { it.id == selectedId }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selected.name)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            targets.forEach { t ->
                DropdownMenuItem(
                    text = { Text(t.name) },
                    onClick = {
                        onSelected(t.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    valueTons: Double,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(
                imageVector = iconForLabel(label),
                contentDescription = label,
                tint = color
            )
            Text(text = label, style = MaterialTheme.typography.titleLarge)
        }

        Text(text = "${valueTons.roundTo2()} ton", style = MaterialTheme.typography.titleLarge)
    }
}

private fun iconForLabel(label: String) = when (label) {
    "Bus" -> Icons.Filled.DirectionsBus
    "Petrol" -> Icons.Filled.DirectionsCar
    "Train" -> Icons.Filled.Train
    "Metro" -> Icons.Filled.DirectionsSubway
    else -> Icons.Filled.DirectionsCar
}

private fun Double.roundTo1(): String = ((this * 10.0).roundToInt() / 10.0).toString()
private fun Double.roundTo2(): String = ((this * 100.0).roundToInt() / 100.0).toString()

@Preview(showBackground = true)
@Composable
fun OverviewPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        OverviewScreen(navController = navController)
    }
}