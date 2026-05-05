package fi.metropolia.canopy.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.metropolia.canopy.data.repository.TripRepository
import fi.metropolia.canopy.data.source.CanopyDatabase
import fi.metropolia.canopy.data.source.LocationEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * GraphViewModel class for managing data for the graphs
 */
class GraphViewModel(context: Context) : ViewModel() {
    private val repository: TripRepository

    private val _monthlyEmissions = MutableStateFlow<Map<String, Double>>(emptyMap())
    val monthlyEmissions: StateFlow<Map<String, Double>> = _monthlyEmissions

    private val _totalEmissionsKg = MutableStateFlow(0.0)
    val totalEmissionsKg: StateFlow<Double> = _totalEmissionsKg

    private val _percentageChange = MutableStateFlow(0.0)
    val percentageChange: StateFlow<Double> = _percentageChange

    private val _calenderData = MutableStateFlow<List<LocationEntity>>(emptyList())
    val calenderData: StateFlow<List<LocationEntity>> = _calenderData

    private val _daysWithData = MutableStateFlow<Set<String>>(emptySet())
    val daysWithData: StateFlow<Set<String>> = _daysWithData

    private var calendarJob: Job? = null

    init {
        val db = CanopyDatabase.getInstance(context)
        repository = TripRepository(db.locationDao())
        loadMonthlyEmissions()
        loadDaysWithData()
    }

    // Loads monthly emissions data from the repository
    fun loadMonthlyEmissions() {
        viewModelScope.launch {
            val data = repository.getEmissionsByMonth()
            _monthlyEmissions.value = data

            val calendar = Calendar.getInstance()
            val currentMonthKey = String.format("%02d", calendar.get(Calendar.MONTH) + 1)
            
            calendar.add(Calendar.MONTH, -1)
            val prevMonthKey = String.format("%02d", calendar.get(Calendar.MONTH) + 1)

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

    // Loads calendar data from the repository
    fun loadCalenderData(startMillis: Long, endMillis: Long) {
        calendarJob?.cancel()
        calendarJob = viewModelScope.launch {
            repository.getLocationsByDate(startMillis, endMillis).collect { data ->
                _calenderData.value = data
            }
        }
    }

    // Loads days with data from the repository
    fun loadDaysWithData() {
        viewModelScope.launch {
            _daysWithData.value = repository.getDaysWithData().toSet()
        }
    }

    // Deletes locations by ID from the repository
    fun deleteLocationsById(id: Int, startMillis: Long, endMillis: Long) {
        viewModelScope.launch {
            repository.deleteLocationsById(id)
            // Note: calendarData will update automatically due to Flow collection in loadCalenderData
            loadMonthlyEmissions() 
            loadDaysWithData()
        }
    }
}