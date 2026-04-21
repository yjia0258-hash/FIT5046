package com.example.dailywell.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailywell.data.local.AppDatabase
import com.example.dailywell.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// save state
sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    object Success : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()

    //Current User
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // Form Fields
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _reminderTime = MutableStateFlow("20:00")
    val reminderTime: StateFlow<String> = _reminderTime

    // Switch: Reminder Switch
    private val _reminderEnabled = MutableStateFlow(false)
    val reminderEnabled: StateFlow<Boolean> = _reminderEnabled

    // RadioButton: Reminder Frequency
    // Options: "Daily", "Weekdays", "Twice a Week"
    private val _reminderFrequency = MutableStateFlow("Daily")
    val reminderFrequency: StateFlow<String> = _reminderFrequency

    // RadioButton: Primary wellbeing goal
    // Options: "Reduce Stress", "Improve Sleep"
    private val _wellbeingGoal = MutableStateFlow("Reduce Stress")
    val wellbeingGoal: StateFlow<String> = _wellbeingGoal

    //Checkbox: Support Preferences
    private val _breathingChecked = MutableStateFlow(false)
    val breathingChecked: StateFlow<Boolean> = _breathingChecked

    private val _sleepTipsChecked = MutableStateFlow(false)
    val sleepTipsChecked: StateFlow<Boolean> = _sleepTipsChecked

    private val _activityPromptsChecked = MutableStateFlow(false)
    val activityPromptsChecked: StateFlow<Boolean> = _activityPromptsChecked

    // Saved Status
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState

    //Loading user data
    fun loadUser(userId: Int) {
        viewModelScope.launch {
            userDao.getUserById(userId).collect { user ->
                user?.let {
                    _currentUser.value = it
                    _name.value = it.name
                    _reminderTime.value = it.reminderTime
                    _reminderEnabled.value = it.reminderEnabled
                    _reminderFrequency.value = it.reminderFrequency
                    _wellbeingGoal.value = it.wellbeingGoal

                    // Parsing the supportPreferences string
                    val prefs = it.supportPreferences.split(",")
                    _breathingChecked.value = prefs.contains("Breathing exercise suggestions")
                    _sleepTipsChecked.value = prefs.contains("Sleep improvement tips")
                    _activityPromptsChecked.value = prefs.contains("Physical activity prompts")
                }
            }
        }
    }

    //  Field Update Function
    fun onNameChange(value: String) { _name.value = value }
    fun onReminderTimeChange(value: String) { _reminderTime.value = value }
    fun onReminderEnabledChange(value: Boolean) { _reminderEnabled.value = value }
    fun onReminderFrequencyChange(value: String) { _reminderFrequency.value = value }
    fun onWellbeingGoalChange(value: String) { _wellbeingGoal.value = value }
    fun onBreathingCheckedChange(value: Boolean) { _breathingChecked.value = value }
    fun onSleepTipsCheckedChange(value: Boolean) { _sleepTipsChecked.value = value }
    fun onActivityPromptsCheckedChange(value: Boolean) { _activityPromptsChecked.value = value }

    //  Save Preferences
    fun savePreferences() {
        val user = _currentUser.value ?: return

        if (_name.value.isBlank()) {
            _profileState.value = ProfileState.Error("Name cannot be empty.")
            return
        }

        viewModelScope.launch {
            _profileState.value = ProfileState.Loading

            // Combine checkboxes into a string
            val prefs = mutableListOf<String>()
            if (_breathingChecked.value) prefs.add("Breathing exercise suggestions")
            if (_sleepTipsChecked.value) prefs.add("Sleep improvement tips")
            if (_activityPromptsChecked.value) prefs.add("Physical activity prompts")

            val updatedUser = user.copy(
                name = _name.value.trim(),
                reminderTime = _reminderTime.value,
                reminderEnabled = _reminderEnabled.value,
                reminderFrequency = _reminderFrequency.value,
                wellbeingGoal = _wellbeingGoal.value,
                supportPreferences = prefs.joinToString(",")
            )

            userDao.updateUser(updatedUser)
            _profileState.value = ProfileState.Success
        }
    }

    //Reset Status
    fun resetState() {
        _profileState.value = ProfileState.Idle
    }
}