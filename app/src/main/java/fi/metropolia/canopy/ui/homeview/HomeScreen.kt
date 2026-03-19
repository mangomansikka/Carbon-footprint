package fi.metropolia.canopy.ui.homeview

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fi.metropolia.canopy.R

private val BgGreen = Color(0xFF6F9C73)
private val LightGreen = Color(0xFFAED3B0)
private val AccentGreen = Color(0xFF58F0B1)

@Composable
fun HomeScreen(
    navController: NavController
) {

    val animatedValue by animateFloatAsState(
        targetValue = 17.4f,
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
                    text = String.format("%.1f", animatedValue),
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
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = AccentGreen
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "+5% since last month",
                    color = Color.White
                )
            }

            Spacer(Modifier.height(40.dp))

            LineChart()

            Spacer(Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Jan", color = Color.White)
                Text("Feb", color = Color.White)
                Text("Mar", color = Color.White)
                Text("Apr", color = Color.White)
            }

            Spacer(Modifier.height(30.dp))

            /*  AALTO + KUVA */

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

            Spacer(Modifier.height(12.dp))

        }
    }
}

/* -------- CHART -------- */

@Composable
fun LineChart() {

    val points = listOf(
        80f, 120f, 60f, 140f, 100f, 160f, 130f, 180f
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {

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
        }
    }
}