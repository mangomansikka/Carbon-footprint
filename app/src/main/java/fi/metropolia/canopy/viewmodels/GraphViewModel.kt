package fi.metropolia.canopy.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.metropolia.canopy.data.repository.TripRepository
import fi.metropolia.canopy.data.source.CanopyDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GraphViewModel(context: Context) : ViewModel() {
    private val repository: TripRepository

    // Use StateFlow so the UI can observe these
    private val _monthlyEmissions = MutableStateFlow<Map<String, Double>>(emptyMap())
    val monthlyEmissions: StateFlow<Map<String, Double>> = _monthlyEmissions

    private val _yearlyTotalTon = MutableStateFlow(0.0)
    val yearlyTotalTon: StateFlow<Double> = _yearlyTotalTon

    private val _percentageChange = MutableStateFlow(0.0)
    val percentageChange: StateFlow<Double> = _percentageChange

    init {
        val db = CanopyDatabase.getInstance(context)
        repository = TripRepository(db.locationDao())
        loadMonthlyEmissions()
    }

    fun loadMonthlyEmissions() {
        viewModelScope.launch {
            val data = repository.getEmissionsByMonth()
            _monthlyEmissions.value = data

            // 1. Calculate Yearly Total (Convert grams to tons for the UI)
            val totalGrams = data.values.sum()
            _yearlyTotalTon.value = totalGrams / 1_000_000.0 // 1 million grams = 1 ton

            // 2. Extract Current & Previous Month
            val calendar = java.util.Calendar.getInstance()
            val currentMonthKey = String.format("%02d", calendar.get(java.util.Calendar.MONTH) + 1)

            calendar.add(java.util.Calendar.MONTH, -1)
            val prevMonthKey = String.format("%02d", calendar.get(java.util.Calendar.MONTH) + 1)

            val current = data[currentMonthKey] ?: 0.0
            val previous = data[prevMonthKey] ?: 0.0

            // 3. Calculate Percentage Change (Handle division by zero!)
            if (previous > 0) {
                _percentageChange.value = ((current - previous) / previous) * 100
            } else {
                _percentageChange.value = if (current > 0) 100.0 else 0.0
            }
        }
    }
}