package fi.metropolia.canopy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import fi.metropolia.canopy.LocationScreen
import fi.metropolia.canopy.ui.home.HomeScreen
import fi.metropolia.canopy.ui.overview.OverviewScreen
import fi.metropolia.canopy.viewmodels.TripViewModel

object Routes {
    const val HOME = "home"
    const val FOOTPRINT = "footprint"
    const val LOCATION = "location"
    const val OVERVIEW = "overview"
    const val ECO = "eco"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    fusedLocationClient: FusedLocationProviderClient,
    setCallback: (LocationCallback) -> Unit,
    viewModel: TripViewModel
) {

    NavHost(
        navController = navController,
        startDestination = Routes.FOOTPRINT
    ) {

        /* CHART / FOOTPRINT PAGE */

        composable(Routes.FOOTPRINT) {

            HomeScreen(
                onGoLocation = { navController.navigate(Routes.LOCATION) },
                onGoOverview = { navController.navigate(Routes.OVERVIEW) }
            )
        }

        /* TROPHY PAGE */

        composable(Routes.OVERVIEW) {
            OverviewScreen(navController = navController)
        }

        /* LOCATION TRACKING */

        composable(Routes.LOCATION) {

            LocationScreen(
                fusedLocationClient = fusedLocationClient,
                setCallback = setCallback,
                viewModel = viewModel,
                onGoOverview = { navController.navigate(Routes.OVERVIEW) }
            )
        }

        /* HOME PLACEHOLDER */

        composable(Routes.HOME) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Home page coming later")
            }
        }

        /* ECO PLACEHOLDER */

        composable(Routes.ECO) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Eco page coming later")
            }
        }
    }
}