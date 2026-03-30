package fi.metropolia.canopy.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.metropolia.canopy.data.repository.TripRepository
import fi.metropolia.canopy.data.source.CanopyDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class GraphViewModel(context: Context) : ViewModel() {
    private val repository: TripRepository

    private val _monthlyEmissions = MutableStateFlow<Map<String, Double>>(emptyMap())
    val monthlyEmissions: StateFlow<Map<String, Double>> = _monthlyEmissions

    private val _totalEmissionsKg = MutableStateFlow(0.0)
    val totalEmissionsKg: StateFlow<Double> = _totalEmissionsKg

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

            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR).toString()
            val currentMonthKey = String.format("%02d", calendar.get(Calendar.MONTH) + 1)
            
            calendar.add(Calendar.MONTH, -1)
            val prevMonthKey = String.format("%02d", calendar.get(Calendar.MONTH) + 1)

            // Calculate Total specifically for the CURRENT YEAR
            // We'll filter the data map keys if they contained year info, 
            // but since our current DAO only returns "MM", we'll sum the current map.
            // Note: For a true yearly footprint, we should clear old data or update DAO to group by YYYY-MM.
            val totalGrams = data.values.sum()
            _totalEmissionsKg.value = totalGrams / 1000.0

            val current = data[currentMonthKey] ?: 0.0
            val previous = data[prevMonthKey] ?: 0.0

            if (previous > 0) {
                _percentageChange.value = ((current - previous) / previous) * 100
            } else {
                _percentageChange.value = if (current > 0) 100.0 else 0.0
            }
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            // This is a helper if you want to clear the inconsistent data
            // repository.deleteAll() // You can implement this in repository
        }
    }
}