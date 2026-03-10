package fi.metropolia.canopy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import fi.metropolia.canopy.ui.theme.CanopyMinnoTheme
import androidx.navigation.compose.composable
import fi.metropolia.canopy.ui.homeview.HomeScreen
import fi.metropolia.canopy.ui.homeview.LandingScreen
import fi.metropolia.canopy.ui.overview.OverviewScreen
import fi.metropolia.canopy.ui.screens.LocationScreen

class CanopyActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            CanopyMinnoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph(fusedLocationClient)
                }
            }
        }
    }
}

@Composable
fun AppNavGraph(
    fusedLocationClient: FusedLocationProviderClient
) {
    val navController = rememberNavController()
    lateinit var locationCallback: LocationCallback

    val currentRoute = navController.currentBackStackEntryFlow
        .collectAsState(initial = navController.currentBackStackEntry)
        .value?.destination?.route
        ?: "locationScreen" // Default route if null

    NavHost(
        navController = navController,
        startDestination = "landingScreen"
    ) {
        composable("landingScreen") {
            LandingScreen(navController)
        }

        composable("locationScreen") {
            LocationScreen(navController, setCallback = { callback ->
                locationCallback = callback
            }, fusedLocationClient = fusedLocationClient)
        }

        composable("overviewScreen") {
            OverviewScreen(navController)
        }

        composable("homeScreen") {
            HomeScreen(navController)
        }

        //To add new screens into navigation graph, add them here
    }
}