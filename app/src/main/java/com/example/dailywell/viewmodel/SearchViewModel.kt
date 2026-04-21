package com.example.dailywell.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailywell.data.local.AppDatabase
import com.example.dailywell.data.model.WellbeingEntry
import com.example.dailywell.data.remote.RetrofitInstance
import com.example.dailywell.data.remote.TipResponse
import com.example.dailywell.data.repository.WellbeingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// External API loading status
sealed class ApiState {
    object Idle : ApiState()
    object Loading : ApiState()
    object Success : ApiState()
    data class Error(val message: String) : ApiState()
}

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WellbeingRepository(
        AppDatabase.getDatabase(application).wellbeingDao()
    )

    // Search Keywords
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Local search results
    private val _localResults = MutableStateFlow<List<WellbeingEntry>>(emptyList())
    val localResults: StateFlow<List<WellbeingEntry>> = _localResults

    // External API tips Result
    private val _apiResults = MutableStateFlow<List<TipResponse>>(emptyList())
    val apiResults: StateFlow<List<TipResponse>> = _apiResults

    // API loading status (used by Progress Indicator)
    private val _apiState = MutableStateFlow<ApiState>(ApiState.Idle)
    val apiState: StateFlow<ApiState> = _apiState

    // Mood Filter
    private val _selectedMood = MutableStateFlow("All")
    val selectedMood: StateFlow<String> = _selectedMood

    //Activity Filter
    private val _selectedActivity = MutableStateFlow("All")
    val selectedActivity: StateFlow<String> = _selectedActivity

    //  Search keyword update
    fun onQueryChange(query: String) {
        _searchQuery.value = query
    }

    // Local Search (Search Local button)
    fun searchLocal() {
        viewModelScope.launch {
            val query = _searchQuery.value.trim()

            if (query.isBlank()) {
                // Show all when no keywords are found
                repository.getAllEntries().collectLatest { entries ->
                    _localResults.value = applyLocalFilters(entries)
                }
            } else {
                repository.searchEntries(query).collectLatest { entries ->
                    _localResults.value = applyLocalFilters(entries)
                }
            }
        }
    }

    //  Local Filtering (Mood + Activity)
    private fun applyLocalFilters(entries: List<WellbeingEntry>): List<WellbeingEntry> {
        var result = entries
        if (_selectedMood.value != "All") {
            result = result.filter { it.mood == _selectedMood.value }
        }
        if (_selectedActivity.value != "All") {
            result = result.filter { it.activityType == _selectedActivity.value }
        }
        return result
    }

    // Mood filter update
    fun onMoodFilterChange(mood: String) {
        _selectedMood.value = mood
        searchLocal()
    }

    // Activity Filtering and Update
    fun onActivityFilterChange(activity: String) {
        _selectedActivity.value = activity
        searchLocal()
    }

    //External API Search (Search API button)
    // The Progress Indicator is displayed when _apiState == Loading.
    fun searchApi() {
        viewModelScope.launch {
            _apiState.value = ApiState.Loading
            try {
                val results = RetrofitInstance.api.getDailyQuotes()
                _apiResults.value = results
                _apiState.value = ApiState.Success
            } catch (e: Exception) {
                _apiState.value = ApiState.Error("Failed to load external content. Please try again.")
                _apiResults.value = emptyList()
            }
        }
    }

    // Reset Search
    fun resetSearch() {
        _searchQuery.value = ""
        _localResults.value = emptyList()
        _apiResults.value = emptyList()
        _apiState.value = ApiState.Idle
        _selectedMood.value = "All"
        _selectedActivity.value = "All"
    }
}