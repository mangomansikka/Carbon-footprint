package fi.metropolia.canopy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import fi.metropolia.canopy.ui.theme.CanopyMinnoTheme
import androidx.navigation.compose.composable
import fi.metropolia.canopy.ui.homeview.HomeScreen
import fi.metropolia.canopy.ui.homeview.LandingScreen
import fi.metropolia.canopy.ui.overview.OverviewScreen
import fi.metropolia.canopy.ui.screens.LocationScreen

class CanopyActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CanopyMinnoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph()
                }
            }
        }
    }
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

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
