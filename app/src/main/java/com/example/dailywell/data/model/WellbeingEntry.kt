package com.example.dailywell.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wellbeing_entries")
data class WellbeingEntry(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // date
    val date: String,

    // Emotions wellbeing
    // For example: "Happy", "Calm", "Neutral", "Anxious", "Sad"
    val mood: String,
    val moodScore: Int = 0,

    // Pressure level 1–5
    // For example: "Low", "Moderate", "High"
    val stressLevel: String,
    val stressScore: Int = 0,

    // Sleep duration (hours)
    // For example: 7.5
    val sleepDuration: Float,

    // activity level
    // For example: "Low", "Moderate", "High"
    val activityLevel: String,

    // Activity type
    // For example: "Walking", "Gym", "Yoga", "Rest Day"
    val activityType: String,

    // notes
    val notes: String = "",

    // Record creation time ）
    val createdAt: Long = System.currentTimeMillis()
)