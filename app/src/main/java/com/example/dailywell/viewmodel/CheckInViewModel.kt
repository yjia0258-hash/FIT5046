package com.example.dailywell.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailywell.data.local.AppDatabase
import com.example.dailywell.data.model.WellbeingEntry
import com.example.dailywell.data.repository.WellbeingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Form save status
sealed class CheckInState {
    object Idle : CheckInState()
    object Loading : CheckInState()
    object Success : CheckInState()
    data class Error(val message: String) : CheckInState()
}

class CheckInViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WellbeingRepository(
        AppDatabase.getDatabase(application).wellbeingDao()
    )

    //data
    private val _selectedDate = MutableStateFlow(
        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    )
    val selectedDate: StateFlow<String> = _selectedDate

    // form
    private val _mood = MutableStateFlow("")
    val mood: StateFlow<String> = _mood

    private val _stressLevel = MutableStateFlow("")
    val stressLevel: StateFlow<String> = _stressLevel

    private val _sleepDuration = MutableStateFlow("")
    val sleepDuration: StateFlow<String> = _sleepDuration

    private val _activityLevel = MutableStateFlow("")
    val activityLevel: StateFlow<String> = _activityLevel

    private val _activityType = MutableStateFlow("")
    val activityType: StateFlow<String> = _activityType

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes

    //  Inline error message
    private val _moodError = MutableStateFlow<String?>(null)
    val moodError: StateFlow<String?> = _moodError

    private val _stressError = MutableStateFlow<String?>(null)
    val stressError: StateFlow<String?> = _stressError

    private val _sleepError = MutableStateFlow<String?>(null)
    val sleepError: StateFlow<String?> = _sleepError

    private val _activityLevelError = MutableStateFlow<String?>(null)
    val activityLevelError: StateFlow<String?> = _activityLevelError

    // Saved Status
    private val _checkInState = MutableStateFlow<CheckInState>(CheckInState.Idle)
    val checkInState: StateFlow<CheckInState> = _checkInState

    //Entry ID being edited (for edit mode)
    private var editingEntryId: Int? = null

    //Field Update Function
    fun onDateChange(date: String) { _selectedDate.value = date }
    fun onMoodChange(value: String) { _mood.value = value; _moodError.value = null }
    fun onStressChange(value: String) { _stressLevel.value = value; _stressError.value = null }
    fun onSleepChange(value: String) { _sleepDuration.value = value; _sleepError.value = null }
    fun onActivityLevelChange(value: String) { _activityLevel.value = value; _activityLevelError.value = null }
    fun onActivityTypeChange(value: String) { _activityType.value = value }
    fun onNotesChange(value: String) { _notes.value = value }

    // Form Validation
    private fun validate(): Boolean {
        var isValid = true

        if (_mood.value.isBlank()) {
            _moodError.value = "Please select a mood level."
            isValid = false
        }
        if (_stressLevel.value.isBlank()) {
            _stressError.value = "Please select a stress level."
            isValid = false
        }
        if (_sleepDuration.value.isBlank()) {
            _sleepError.value = "Please enter sleep duration."
            isValid = false
        } else {
            val sleep = _sleepDuration.value.toFloatOrNull()
            if (sleep == null || sleep < 0 || sleep > 24) {
                _sleepError.value = "Please enter a valid number (0–24)."
                isValid = false
            }
        }
        if (_activityLevel.value.isBlank()) {
            _activityLevelError.value = "Please select an activity level."
            isValid = false
        }

        return isValid
    }

    //Save Form
    fun saveEntry() {
        if (!validate()) return

        viewModelScope.launch {
            _checkInState.value = CheckInState.Loading

            val entry = WellbeingEntry(
                id = editingEntryId ?: 0,
                date = _selectedDate.value,
                mood = _mood.value,
                stressLevel = _stressLevel.value,
                sleepDuration = _sleepDuration.value.toFloat(),
                activityLevel = _activityLevel.value,
                activityType = _activityType.value,
                notes = _notes.value
            )

            if (editingEntryId != null) {
                repository.updateEntry(entry)
            } else {
                repository.insertEntry(entry)
            }

            _checkInState.value = CheckInState.Success
        }
    }

    // Load an existing entry (for edit mode)
    fun loadEntry(entryId: Int) {
        viewModelScope.launch {
            val entry = repository.getEntryById(entryId) ?: return@launch
            editingEntryId = entry.id
            _selectedDate.value = entry.date
            _mood.value = entry.mood
            _stressLevel.value = entry.stressLevel
            _sleepDuration.value = entry.sleepDuration.toString()
            _activityLevel.value = entry.activityLevel
            _activityType.value = entry.activityType
            _notes.value = entry.notes
        }
    }

    // Clear Form
    fun clearForm() {
        editingEntryId = null
        _selectedDate.value = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        _mood.value = ""
        _stressLevel.value = ""
        _sleepDuration.value = ""
        _activityLevel.value = ""
        _activityType.value = ""
        _notes.value = ""
        _moodError.value = null
        _stressError.value = null
        _sleepError.value = null
        _activityLevelError.value = null
        _checkInState.value = CheckInState.Idle
    }

    // Reset Status
    fun resetState() {
        _checkInState.value = CheckInState.Idle
    }
}