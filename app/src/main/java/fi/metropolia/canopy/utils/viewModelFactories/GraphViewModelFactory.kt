package fi.metropolia.canopy.utils.viewModelFactories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fi.metropolia.canopy.viewmodels.GraphViewModel

class GraphViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GraphViewModel::class.java)) {
            return GraphViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
