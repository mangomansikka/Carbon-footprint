package fi.metropolia.canopy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import fi.metropolia.canopy.LocationScreen
import fi.metropolia.canopy.ui.overview.OverviewScreen
import fi.metropolia.canopy.viewmodels.TripViewModel

object Routes {
    const val LOCATION = "location"
    const val OVERVIEW = "overview"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    fusedLocationClient: FusedLocationProviderClient,
    setCallback: (LocationCallback) -> Unit,
    viewModel: TripViewModel,
    startDestination: String = Routes.LOCATION
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOCATION) {
            LocationScreen(
                fusedLocationClient = fusedLocationClient,
                setCallback = setCallback,
                viewModel = viewModel,
                onGoOverview = { navController.navigate(Routes.OVERVIEW) }
            )
        }

        composable(Routes.OVERVIEW) {
            OverviewScreen()
        }
    }
}