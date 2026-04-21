package com.example.dailywell.data.local

import androidx.room.*
import com.example.dailywell.data.model.WellbeingEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface WellbeingDao {

    //Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: WellbeingEntry)

    // Update
    @Update
    suspend fun updateEntry(entry: WellbeingEntry)

    // delete
    @Delete
    suspend fun deleteEntry(entry: WellbeingEntry)

    // Search all (sorted by date in descending order)
    // History page LazyColumn uses
    @Query("SELECT * FROM wellbeing_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<WellbeingEntry>>

    // Search for a single item by ID
    // Used when editing
    @Query("SELECT * FROM wellbeing_entries WHERE id = :id")
    suspend fun getEntryById(id: Int): WellbeingEntry?

    // Search by date
    // Check the CheckIn page to see if you have filled it out for the day.
    @Query("SELECT * FROM wellbeing_entries WHERE date = :date LIMIT 1")
    suspend fun getEntryByDate(date: String): WellbeingEntry?

    //Search (for page search)
    // Supports searching by keywords: mood, activityType, and date.
    @Query("""
        SELECT * FROM wellbeing_entries 
        WHERE mood LIKE '%' || :keyword || '%'
        OR activityType LIKE '%' || :keyword || '%'
        OR date LIKE '%' || :keyword || '%'
        ORDER BY date DESC
    """)
    fun searchEntries(keyword: String): Flow<List<WellbeingEntry>>

    // Filter by mood
    @Query("SELECT * FROM wellbeing_entries WHERE mood = :mood ORDER BY date DESC")
    fun getEntriesByMood(mood: String): Flow<List<WellbeingEntry>>

    // Filter by activity
    @Query("SELECT * FROM wellbeing_entries WHERE activityType = :activityType ORDER BY date DESC")
    fun getEntriesByActivity(activityType: String): Flow<List<WellbeingEntry>>

    // Last 7 days (for Weekly Report)
    @Query("""
        SELECT * FROM wellbeing_entries 
        WHERE date >= :startDate AND date <= :endDate 
        ORDER BY date ASC
    """)
    fun getEntriesForWeek(startDate: String, endDate: String): Flow<List<WellbeingEntry>>

    // Recent N items (for context-aware use on the Tips page)
    @Query("SELECT * FROM wellbeing_entries ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentEntries(limit: Int): List<WellbeingEntry>
}