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
import androidx.compose.ui.res.stringResource
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

object Routes {
    const val LANDING = "landingScreen"
    const val LOCATION = "locationScreen"
    const val OVERVIEW = "overviewScreen"
    const val HOME = "homeScreen"
    const val ECO = "ecoScreen"
    const val MANUAL = "manualScreen"
}

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

                    /* HOME */
                    IconButton(onClick = {
                        navController.navigate(Routes.LANDING) {
                            popUpTo(Routes.LANDING)
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            Icons.Default.Home, 
                            contentDescription = stringResource(R.string.nav_home), 
                            tint = iconColor(Routes.LANDING)
                        )
                    }

                    /* ECO 🌱 */
                    IconButton(onClick = {
                        navController.navigate(Routes.ECO)
                    }) {
                        Icon(
                            Icons.Default.Eco, 
                            contentDescription = stringResource(R.string.nav_eco), 
                            tint = iconColor(Routes.ECO)
                        )
                    }

                    /* SPACE for center button */
                    Spacer(modifier = Modifier.width(40.dp))

                    /* OVERVIEW 🏆 */
                    IconButton(onClick = {
                        navController.navigate(Routes.OVERVIEW)
                    }) {
                        Icon(
                            Icons.Default.PieChart, 
                            contentDescription = stringResource(R.string.nav_overview), 
                            tint = iconColor(Routes.OVERVIEW)
                        )
                    }

                    /* STATS 📈 */
                    IconButton(onClick = {
                        navController.navigate(Routes.HOME)
                    }) {
                        Icon(
                            Icons.Default.ShowChart, 
                            contentDescription = stringResource(R.string.nav_stats), 
                            tint = iconColor(Routes.HOME)
                        )
                    }
                }

                /* CENTER BUTTON (TRACKING) */
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Routes.LOCATION)
                    },
                    containerColor = Color(0xFF4E7D5A),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-25).dp)
                ) {
                    Icon(
                        Icons.Default.Add, 
                        contentDescription = stringResource(R.string.live_tracking), 
                        tint = Color.White
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.LANDING,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Routes.LANDING) {
                LandingScreen()
            }

            composable(Routes.LOCATION) {
                LocationScreen(navController)
            }

            composable(Routes.OVERVIEW) {
                OverviewScreen()
            }

            composable(Routes.HOME) {
                HomeScreen()
            }

            composable(Routes.ECO) {
                TreeScreen()
            }

            composable(Routes.MANUAL) {
                ManualInputScreen()
            }
        }
    }
}
