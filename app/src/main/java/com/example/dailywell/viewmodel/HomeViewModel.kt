package com.example.dailywell.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailywell.data.local.AppDatabase
import com.example.dailywell.data.model.User
import com.example.dailywell.data.model.WellbeingEntry
import com.example.dailywell.data.repository.WellbeingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WellbeingRepository(
        AppDatabase.getDatabase(application).wellbeingDao()
    )
    private val userDao = AppDatabase.getDatabase(application).userDao()

    // Current User
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // Today’s entry
    private val _todayEntry = MutableStateFlow<WellbeingEntry?>(null)
    val todayEntry: StateFlow<WellbeingEntry?> = _todayEntry

    // Check if today's check-in was completed.
    private val _isTodayCompleted = MutableStateFlow(false)
    val isTodayCompleted: StateFlow<Boolean> = _isTodayCompleted

    // Recent Log (for context-aware support preview)
    private val _recentEntries = MutableStateFlow<List<WellbeingEntry>>(emptyList())
    val recentEntries: StateFlow<List<WellbeingEntry>> = _recentEntries

    // Personalized prompt text
    private val _supportPreview = MutableStateFlow("")
    val supportPreview: StateFlow<String> = _supportPreview

    // Today's date string
    val todayDate: String = LocalDate.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    // Greetings (depending on time)
    val greeting: String
        get() {
            val hour = java.time.LocalTime.now().hour
            return when {
                hour < 12 -> "Good morning"
                hour < 17 -> "Good afternoon"
                else -> "Good evening"
            }
        }

    //Loading user data
    fun loadUser(userId: Int) {
        viewModelScope.launch {
            userDao.getUserById(userId).collect { user ->
                _currentUser.value = user
            }
        }
    }

    //Loading homepage data
    fun loadHomeData() {
        viewModelScope.launch {
            // Check if today's check-in was completed.
            val todayEntry = repository.getEntryByDate(todayDate)
            _todayEntry.value = todayEntry
            _isTodayCompleted.value = todayEntry != null

            // Load the 7 most recent records
            val recent = repository.getRecentEntries(7)
            _recentEntries.value = recent

            // Generate context-aware support preview
            _supportPreview.value = generateSupportPreview(recent)
        }
    }

    // Generate personalized tips (for Home page Support Preview)
    private fun generateSupportPreview(entries: List<WellbeingEntry>): String {
        if (entries.isEmpty()) return "Start your first check-in to get personalised support."

        val avgSleep = entries.map { it.sleepDuration }.average()
        val avgStress = entries.map { it.stressScore }.average()
        val avgMood = entries.map { it.moodScore }.average()

        return when {
            avgSleep < 6 && avgStress > 3 ->
                "You had lower sleep and higher stress recently. Try a short break tonight."
            avgSleep < 6 ->
                "You had lower sleep recently. A short evening walk may help."
            avgStress > 3 ->
                "Stress has been higher this week. A breathing exercise may help."
            avgMood < 3 ->
                "Your mood has been lower recently. Try something you enjoy today."
            else ->
                "You're doing well! Keep up your healthy routine."
        }
    }
}