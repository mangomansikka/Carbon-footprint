package fi.metropolia.canopy.utils.viewModelFactories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fi.metropolia.canopy.viewmodels.TripViewModel

/**
 * ViewModelFactory is for injecting parameters into the ViewModel.
 * It is used to create an instance of the ViewModel with the required parameters.
 *
 * @param context The context of the application.
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