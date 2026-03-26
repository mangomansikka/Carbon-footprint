package fi.metropolia.canopy.ui.homeview

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.canopy.R
import fi.metropolia.canopy.utils.viewModelFactories.GraphViewModelFactory
import fi.metropolia.canopy.viewmodels.GraphViewModel
import java.util.Calendar
import java.util.Locale

private val BgGreen = Color(0xFF6F9C73)
private val LightGreen = Color(0xFFAED3B0)
private val AccentGreen = Color(0xFF58F0B1)

@Composable
fun HomeScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: GraphViewModel = viewModel(
        factory = GraphViewModelFactory(context)
    )
    
    val monthlyEmissions by viewModel.monthlyEmissions.collectAsState()
    val yearlyEmissions by viewModel.yearlyTotalTon.collectAsState()
    val percentageChange by viewModel.percentageChange.collectAsState()

    // Calculate last 4 months dynamically
    val last4Months = remember(monthlyEmissions) {
        val calendar = Calendar.getInstance()
        (0..3).map { i ->
            val cal = calendar.clone() as Calendar
            cal.add(Calendar.MONTH, -3 + i)
            val monthKey = String.format("%02d", cal.get(Calendar.MONTH) + 1)
            val monthName = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) ?: ""
            val value = (monthlyEmissions[monthKey] ?: 0.0).toFloat()
            monthName to value
        }
    }

    // Scale points to fit the 150dp height
    val maxVal = last4Months.maxOf { it.second }.coerceAtLeast(1f)
    val chartPoints = last4Months.map { (it.second / maxVal) * 100f + 20f }

    val animatedValue by animateFloatAsState(
        targetValue = yearlyEmissions.toFloat(),
        label = "co2Animation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(24.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            Text(
                text = "My Footprint",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.padding(start = 20.dp)
            )

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = String.format("%.3f", animatedValue),
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "ton CO₂/year",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (percentageChange >= 0) Icons.Default.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                    contentDescription = null,
                    tint = if (percentageChange <= 0) AccentGreen else Color(0xFFFF6B6B)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = String.format("%.1f", Math.abs(percentageChange)) + "% " + 
                           (if (percentageChange >= 0) "increase" else "decrease") + " since last month",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(40.dp))

            LineChart(chartPoints)

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                last4Months.forEach { (name, _) ->
                    Text(name, color = Color.White, style = MaterialTheme.typography.labelMedium)
                }
            }

            Spacer(Modifier.height(30.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val path = Path().apply {
                        moveTo(0f, size.height * 0.3f)
                        quadraticBezierTo(
                            size.width * 0.25f,
                            size.height * 0.1f,
                            size.width * 0.5f,
                            size.height * 0.35f
                        )
                        quadraticBezierTo(
                            size.width * 0.75f,
                            size.height * 0.6f,
                            size.width,
                            size.height * 0.3f
                        )
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                        close()
                    }
                    drawPath(
                        path = path,
                        color = LightGreen
                    )
                }

                Image(
                    painter = painterResource(R.drawable.plant_girl),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .height(260.dp)
                )
            }
        }
    }
}

@Composable
fun LineChart(points: List<Float>) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        if (points.size < 2) return@Canvas

        val space = size.width / (points.size - 1)

        for (i in 0 until points.size - 1) {
            val start = Offset(space * i, size.height - points[i])
            val end = Offset(space * (i + 1), size.height - points[i + 1])

            drawLine(
                color = AccentGreen.copy(alpha = 0.3f),
                start = start,
                end = end,
                strokeWidth = 16f
            )

            drawLine(
                color = AccentGreen,
                start = start,
                end = end,
                strokeWidth = 6f
            )

            drawCircle(
                color = AccentGreen,
                radius = 8f,
                center = start
            )
            
            if (i == points.size - 2) {
                drawCircle(
                    color = AccentGreen,
                    radius = 8f,
                    center = end
                )
            }
        }
    }
}
