package fi.metropolia.canopy.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fi.metropolia.canopy.R
import fi.metropolia.canopy.domain.model.TrackingState

object ModeUtils {
    @Composable
    fun getModeDisplayName(mode: String): String {
        return when (mode.lowercase().trim()) {
            TrackingState.MODE_CAR -> stringResource(R.string.mode_car)
            TrackingState.MODE_BUS -> stringResource(R.string.mode_bus)
            TrackingState.MODE_TRAIN -> stringResource(R.string.mode_train)
            TrackingState.MODE_METRO, "subway" -> stringResource(R.string.mode_metro)
            TrackingState.MODE_MOPED, "moped_scooter", "scooter" -> stringResource(R.string.mode_moped)
            TrackingState.MODE_WALKING, "on_foot" -> stringResource(R.string.mode_walking)
            TrackingState.MODE_RUNNING -> stringResource(R.string.mode_walking) // Group running with walking for display
            TrackingState.MODE_BICYCLE, "cycling", "on_bicycle" -> stringResource(R.string.mode_bicycle)
            TrackingState.MODE_TRAM -> stringResource(R.string.mode_tram)
            TrackingState.MODE_STILL -> stringResource(R.string.mode_still)
            else -> stringResource(R.string.mode_none)
        }
    }
}
