package fi.metropolia.canopy.utils.viewModelFactories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fi.metropolia.canopy.viewmodels.TripViewModel

/**
 * Factory class to provide the Context dependency to TripViewModel.
 */
class TripViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripViewModel::class.java)) {
            return TripViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
