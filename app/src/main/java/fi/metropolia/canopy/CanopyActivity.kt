package fi.metropolia.canopy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import fi.metropolia.canopy.ui.treeview.TreeScreen
import fi.metropolia.canopy.ui.homeview.HomeScreen
import fi.metropolia.canopy.ui.homeview.LandingScreen
import fi.metropolia.canopy.ui.overview.OverviewScreen
import fi.metropolia.canopy.ui.screens.LocationScreen
import fi.metropolia.canopy.ui.screens.ManualInputScreen

/**
 * CanopyActivity class for the main activity of the application
 */
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

/**
 * AppNavGraph composable function for navigating between screens
 */
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val accentGreen = Color(0xFF58F0B1)

    Scaffold(
        bottomBar = {
            Box {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .navigationBarsPadding()
                        .background(Color(0xFF3A2F2F)),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val iconColor = { route: String ->
                        if (currentRoute == route) accentGreen else Color.Gray
                    }

                    /* Landing screen */
                    IconButton(onClick = {
                        navController.navigate("landingScreen") {
                            popUpTo("landingScreen")
                            launchSingleTop = true
                        }
                    }) {
                        Icon(Icons.Default.Home, null, tint = iconColor("landingScreen"))
                    }

                    /* Tree Screen */
                    IconButton(onClick = {
                        navController.navigate("ecoScreen")
                    }) {
                        Icon(Icons.Default.Eco, null, tint = iconColor("ecoScreen"))
                    }

                    Spacer(modifier = Modifier.width(40.dp))

                    /* Overview screen */
                    IconButton(onClick = {
                        navController.navigate("overviewScreen")
                    }) {
                        Icon(Icons.Default.PieChart, null, tint = iconColor("overviewScreen"))
                    }

                    /* Home screen */
                    IconButton(onClick = {
                        navController.navigate("homeScreen")
                    }) {
                        Icon(Icons.Default.ShowChart, null, tint = iconColor("homeScreen"))
                    }
                }

                /* Tracking screen */
                FloatingActionButton(
                    onClick = {
                        navController.navigate("locationScreen")
                    },
                    containerColor = Color(0xFF4E7D5A),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-25).dp)
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                }
            }
        }
    ) { innerPadding ->

        // NavHost for navigating between screens
        NavHost(
            navController = navController,
            startDestination = "landingScreen",
            modifier = Modifier.padding(innerPadding)
        ) {

            composable("landingScreen") {
                LandingScreen()
            }

            composable("locationScreen") {
                LocationScreen(navController)
            }

            composable("overviewScreen") {
                OverviewScreen()
            }

            composable("homeScreen") {
                HomeScreen()
            }

            composable("ecoScreen") {
                TreeScreen()
            }

            composable("manualScreen") {
                ManualInputScreen()
            }
        }
    }
}