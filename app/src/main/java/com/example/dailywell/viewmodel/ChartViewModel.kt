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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Data class for a single day's chart values
data class ChartDataPoint(
    val day: String,         // e.g. "Mon", "Tue"
    val moodScore: Float,    // 1-5
    val stressScore: Float,  // 1-5
    val sleepHours: Float,   // 0-24
    val activityLevel: Float // 0=none, 1=Low, 2=Moderate, 3=High
)

// Data class for the weekly summary cards
data class WeeklySummary(
    val avgMood: Float,
    val avgMoodLabel: String,
    val avgStress: Float,
    val avgStressLabel: String,
    val avgSleep: Float,
    val avgSleepLabel: String,
    val activeDays: Int
)

class ChartViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WellbeingRepository(
        AppDatabase.getDatabase(application).wellbeingDao()
    )

    private val _weekStartDate = MutableStateFlow(getStartOfWeek())
    val weekStartDate: StateFlow<String> = _weekStartDate

    private val _weekEndDate = MutableStateFlow(getEndOfWeek())
    val weekEndDate: StateFlow<String> = _weekEndDate

    private val _chartData = MutableStateFlow<List<ChartDataPoint>>(emptyList())
    val chartData: StateFlow<List<ChartDataPoint>> = _chartData

    private val _weeklySummary = MutableStateFlow<WeeklySummary?>(null)
    val weeklySummary: StateFlow<WeeklySummary?> = _weeklySummary

    private val _insights = MutableStateFlow<List<String>>(emptyList())
    val insights: StateFlow<List<String>> = _insights

    private val _dateRangeLabel = MutableStateFlow("")
    val dateRangeLabel: StateFlow<String> = _dateRangeLabel

    init {
        loadWeekData()
    }

    fun loadWeekData() {
        viewModelScope.launch {
            updateDateRangeLabel()
            repository.getEntriesForWeek(
                _weekStartDate.value,
                _weekEndDate.value
            ).collectLatest { entries ->
                _chartData.value = buildChartData(entries)
                _weeklySummary.value = buildWeeklySummary(entries)
                _insights.value = generateInsights(entries)
            }
        }
    }

    fun previousWeek() {
        val start = LocalDate.parse(_weekStartDate.value).minusWeeks(1)
        _weekStartDate.value = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        _weekEndDate.value = start.plusDays(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        loadWeekData()
    }

    fun nextWeek() {
        val start = LocalDate.parse(_weekStartDate.value).plusWeeks(1)
        _weekStartDate.value = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        _weekEndDate.value = start.plusDays(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        loadWeekData()
    }

    private fun buildChartData(entries: List<WellbeingEntry>): List<ChartDataPoint> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dayFormatter = DateTimeFormatter.ofPattern("EEE")
        val start = LocalDate.parse(_weekStartDate.value)

        return (0..6).map { offset ->
            val date = start.plusDays(offset.toLong())
            val dateStr = date.format(formatter)
            val dayLabel = date.format(dayFormatter)
            val entry = entries.find { it.date == dateStr }

            ChartDataPoint(
                day = dayLabel,
                moodScore = entry?.moodScore?.toFloat() ?: 0f,
                stressScore = entry?.stressScore?.toFloat() ?: 0f,
                sleepHours = entry?.sleepDuration ?: 0f,
                activityLevel = when (entry?.activityLevel) {
                    "High" -> 3f
                    "Moderate" -> 2f
                    "Low" -> 1f
                    else -> 0f
                }
            )
        }
    }

    private fun buildWeeklySummary(entries: List<WellbeingEntry>): WeeklySummary {
        if (entries.isEmpty()) {
            return WeeklySummary(0f, "-", 0f, "-", 0f, "-", 0)
        }

        val avgMood = entries.map { it.moodScore }.average().toFloat()
        val avgStress = entries.map { it.stressScore }.average().toFloat()
        val avgSleep = entries.map { it.sleepDuration }.average().toFloat()
        val activeDays = entries.count { it.activityLevel != "Low" }

        return WeeklySummary(
            avgMood = avgMood,
            avgMoodLabel = when {
                avgMood >= 4.5f -> "Excellent"
                avgMood >= 3.5f -> "Good"
                avgMood >= 2.5f -> "Neutral"
                avgMood >= 1.5f -> "Low"
                else -> "Very Low"
            },
            avgStress = avgStress,
            avgStressLabel = when {
                avgStress >= 4f -> "High"
                avgStress >= 2.5f -> "Moderate"
                else -> "Low"
            },
            avgSleep = avgSleep,
            avgSleepLabel = when {
                avgSleep >= 7f -> "Sufficient"
                avgSleep >= 5f -> "Below usual"
                else -> "Low"
            },
            activeDays = activeDays
        )
    }

    private fun generateInsights(entries: List<WellbeingEntry>): List<String> {
        if (entries.isEmpty()) return listOf("No data available for this week.")

        val insights = mutableListOf<String>()

        if (entries.size >= 3) {
            val firstHalf = entries.take(entries.size / 2).map { it.moodScore }.average()
            val secondHalf = entries.takeLast(entries.size / 2).map { it.moodScore }.average()
            when {
                secondHalf > firstHalf + 0.5 -> insights.add("Mood improved towards the weekend.")
                firstHalf > secondHalf + 0.5 -> insights.add("Mood was better earlier in the week.")
            }
        }

        val avgSleep = entries.map { it.sleepDuration }.average()
        when {
            avgSleep < 6 -> insights.add("Sleep was lower this week. Try to rest more.")
            avgSleep >= 7 -> insights.add("Sleep was consistent and sufficient this week.")
            else -> insights.add("Sleep was lower midweek.")
        }

        val avgStress = entries.map { it.stressScore }.average()
        if (avgStress > 3.5) insights.add("Stress was elevated this week.")

        if (insights.isEmpty()) insights.add("Your wellbeing was stable this week. Keep it up!")

        return insights
    }

    private fun updateDateRangeLabel() {
        val displayFormatter = DateTimeFormatter.ofPattern("d MMM")
        val yearFormatter = DateTimeFormatter.ofPattern("yyyy")
        val start = LocalDate.parse(_weekStartDate.value)
        val end = LocalDate.parse(_weekEndDate.value)
        _dateRangeLabel.value =
            "${start.format(displayFormatter)} - ${end.format(displayFormatter)} ${end.format(yearFormatter)}"
    }

    private fun getStartOfWeek(): String {
        val today = LocalDate.now()
        val monday = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        return monday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    private fun getEndOfWeek(): String {
        val today = LocalDate.now()
        val sunday = today.plusDays(7 - today.dayOfWeek.value.toLong())
        return sunday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }
}