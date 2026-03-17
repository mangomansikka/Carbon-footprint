package fi.metropolia.canopy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.core.view.WindowCompat
import fi.metropolia.canopy.ui.theme.CanopyMinnoTheme
import fi.metropolia.canopy.ui.home.HomeScreen
import fi.metropolia.canopy.ui.homeview.LandingScreen
import fi.metropolia.canopy.ui.overview.OverviewScreen
import fi.metropolia.canopy.ui.screens.LocationScreen

class CanopyActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            CanopyMinnoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    AppNavGraph()
                }
            }
        }
    }
}

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val AccentGreen = Color(0xFF58F0B1)

    Scaffold(
        bottomBar = {

            Box {

                /*  NAV BAR */
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .background(Color(0xFF3A2F2F)),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val iconColor = { route: String ->
                        if (currentRoute == route) AccentGreen else Color.Gray
                    }

                    /*  HOME */
                    IconButton(onClick = {
                        navController.navigate("landingScreen") {
                            popUpTo("landingScreen")
                            launchSingleTop = true
                        }
                    }) {
                        Icon(Icons.Default.Home, null, tint = iconColor("landingScreen"))
                    }

                    /*  OVERVIEW */
                    IconButton(onClick = {
                        navController.navigate("overviewScreen") {
                            launchSingleTop = true
                        }
                    }) {
                        Icon(Icons.Default.EmojiEvents, null, tint = iconColor("overviewScreen"))
                    }

                    Spacer(modifier = Modifier.width(60.dp))

                    /*  FOOTPRINT */
                    IconButton(onClick = {
                        navController.navigate("homeScreen") {
                            launchSingleTop = true
                        }
                    }) {
                        Icon(Icons.Default.ShowChart, null, tint = iconColor("homeScreen"))
                    }

                    /* 🌱 ECO */
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Eco, null, tint = Color.Gray)
                    }
                }

                /*  KESKINAPPI (GLOW) */
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-25).dp)
                        .size(70.dp),
                    contentAlignment = Alignment.Center
                ) {

                    /* glow efekti */
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(
                                color = AccentGreen.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                    )

                    /* nappi */
                    FloatingActionButton(
                        onClick = {
                            //  tänne myöhemmin uusi screen
                        },
                        containerColor = Color(0xFF4E7D5A)
                    ) {
                        Icon(Icons.Default.Search, null, tint = Color.White)
                    }
                }
            }
        }
    ) { _ ->

        NavHost(
            navController = navController,
            startDestination = "landingScreen"
        ) {

            composable("landingScreen") {
                LandingScreen(navController)
            }

            composable("locationScreen") {
                LocationScreen(navController)
            }

            composable("overviewScreen") {
                OverviewScreen(navController)
            }

            composable("homeScreen") {
                HomeScreen(navController)
            }
        }
    }
}