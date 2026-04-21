package com.example.dailywell.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailywell.data.local.AppDatabase
import com.example.dailywell.data.model.WellbeingEntry
import com.example.dailywell.data.repository.WellbeingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WellbeingRepository(
        AppDatabase.getDatabase(application).wellbeingDao()
    )

    // All records(for display using LazyColumn)
    private val _allEntries = MutableStateFlow<List<WellbeingEntry>>(emptyList())
    val allEntries: StateFlow<List<WellbeingEntry>> = _allEntries

    // Filtered Records
    private val _filteredEntries = MutableStateFlow<List<WellbeingEntry>>(emptyList())
    val filteredEntries: StateFlow<List<WellbeingEntry>> = _filteredEntries

    //  Filtering criteria
    private val _selectedMoodFilter = MutableStateFlow("All")
    val selectedMoodFilter: StateFlow<String> = _selectedMoodFilter

    private val _selectedActivityFilter = MutableStateFlow("All")
    val selectedActivityFilter: StateFlow<String> = _selectedActivityFilter

    // Delete confirmation pop-up status
    private val _entryToDelete = MutableStateFlow<WellbeingEntry?>(null)
    val entryToDelete: StateFlow<WellbeingEntry?> = _entryToDelete

    init {
        loadAllEntries()
    }

    // Load all records
    private fun loadAllEntries() {
        viewModelScope.launch {
            repository.getAllEntries().collectLatest { entries ->
                _allEntries.value = entries
                applyFilters()
            }
        }
    }

    //Application Filtering
    private fun applyFilters() {
        var result = _allEntries.value

        if (_selectedMoodFilter.value != "All") {
            result = result.filter { it.mood == _selectedMoodFilter.value }
        }
        if (_selectedActivityFilter.value != "All") {
            result = result.filter { it.activityType == _selectedActivityFilter.value }
        }

        _filteredEntries.value = result
    }

    //  Update mood filter
    fun onMoodFilterChange(mood: String) {
        _selectedMoodFilter.value = mood
        applyFilters()
    }

    // Update activity filter
    fun onActivityFilterChange(activity: String) {
        _selectedActivityFilter.value = activity
        applyFilters()
    }

    //Reset Filter
    fun resetFilters() {
        _selectedMoodFilter.value = "All"
        _selectedActivityFilter.value = "All"
        applyFilters()
    }

    //Delete record
    fun deleteEntry(entry: WellbeingEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
            _entryToDelete.value = null
        }
    }

    //Display the deletion confirmation pop-up
    fun onDeleteClick(entry: WellbeingEntry) {
        _entryToDelete.value = entry
    }

    // Cancel Delete
    fun onDeleteCancel() {
        _entryToDelete.value = null
    }
}