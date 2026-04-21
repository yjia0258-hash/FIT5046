package com.example.dailywell.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // user name
    val name: String,

    // email
    val email: String,

    // password）
    val password: String,

    // reminder switch
    val reminderEnabled: Boolean = false,

    // Remind me of the time, for example, "20:00".
    val reminderTime: String = "20:00",

    // Reminder frequency: "Daily", "Weekdays", "Twice a Week"
    val reminderFrequency: String = "Daily",

    // Primary wellbeing goals: "Reduce Stress", "Improve Sleep"
    val wellbeingGoal: String = "Reduce Stress",

    // Support preferences, stored separated by commas.
    // For example: "Breathing exercise suggestions,Sleep improvement tips"
    val supportPreferences: String = ""
)