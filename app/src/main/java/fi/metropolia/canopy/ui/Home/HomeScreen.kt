package fi.metropolia.canopy.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fi.metropolia.canopy.R

private val BgGreen = Color(0xFF6F9C73)
private val AccentGreen = Color(0xFF58F0B1)

@Composable
fun HomeScreen(
    onGoLocation: () -> Unit,
    onGoOverview: () -> Unit
) {

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
                color = Color.White
            )

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.Bottom) {

                Text(
                    text = "17.4",
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

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = onGoLocation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Start tracking")
            }

            Spacer(Modifier.height(16.dp))

            Image(
                painter = painterResource(R.drawable.plant_girl),
                contentDescription = "Plant girl",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onGoOverview,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go to Overview")
            }
        }

        NavigationBar(
            containerColor = Color(0xFF3A2F2F)
        ) {

            NavigationBarItem(
                selected = true,
                onClick = { },
                icon = { Icon(Icons.Default.Home, contentDescription = "home") }
            )

            NavigationBarItem(
                selected = false,
                onClick = { },
                icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "trophy") }
            )

            NavigationBarItem(
                selected = false,
                onClick = { },
                icon = { Icon(Icons.Default.ShowChart, contentDescription = "stats") }
            )

            NavigationBarItem(
                selected = false,
                onClick = { },
                icon = { Icon(Icons.Default.Public, contentDescription = "globe") }
            )
        }
    }
}

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

            drawLine(
                color = AccentGreen,
                start = Offset(space * i, size.height - points[i]),
                end = Offset(space * (i + 1), size.height - points[i + 1]),
                strokeWidth = 6f
            )
        }
    }
}
