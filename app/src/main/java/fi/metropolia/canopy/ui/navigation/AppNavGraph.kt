package fi.metropolia.canopy.ui.navigation

import androidx.compose.runtime.Composable
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
    const val LOCATION = "location"
    const val OVERVIEW = "overview"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    fusedLocationClient: FusedLocationProviderClient,
    setCallback: (LocationCallback) -> Unit,
    viewModel: TripViewModel,
    startDestination: String = Routes.HOME
) {

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // HOME SCREEN
        composable(Routes.HOME) {
            HomeScreen(
                onGoLocation = { navController.navigate(Routes.LOCATION) },
                onGoOverview = { navController.navigate(Routes.OVERVIEW) }
            )
        }

        // LOCATION SCREEN
        composable(Routes.LOCATION) {
            LocationScreen(
                fusedLocationClient = fusedLocationClient,
                setCallback = setCallback,
                viewModel = viewModel,
                onGoOverview = { navController.navigate(Routes.OVERVIEW) }
            )
        }

        // OVERVIEW SCREEN
        composable(Routes.OVERVIEW) {
            OverviewScreen()
        }
    }
}