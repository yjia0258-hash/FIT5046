package com.example.dailywell.data.repository

import com.example.dailywell.data.local.WellbeingDao
import com.example.dailywell.data.model.WellbeingEntry
import kotlinx.coroutines.flow.Flow

class WellbeingRepository(private val wellbeingDao: WellbeingDao) {

    // insert
    suspend fun insertEntry(entry: WellbeingEntry) {
        wellbeingDao.insertEntry(entry)
    }

    // update
    suspend fun updateEntry(entry: WellbeingEntry) {
        wellbeingDao.updateEntry(entry)
    }

    //delete
    suspend fun deleteEntry(entry: WellbeingEntry) {
        wellbeingDao.deleteEntry(entry)
    }

    // View all
    fun getAllEntries(): Flow<List<WellbeingEntry>> {
        return wellbeingDao.getAllEntries()
    }

    // Search by ID
    suspend fun getEntryById(id: Int): WellbeingEntry? {
        return wellbeingDao.getEntryById(id)
    }

    // Search by date
    suspend fun getEntryByDate(date: String): WellbeingEntry? {
        return wellbeingDao.getEntryByDate(date)
    }

    // Keyword Search
    fun searchEntries(keyword: String): Flow<List<WellbeingEntry>> {
        return wellbeingDao.searchEntries(keyword)
    }

    //Filter by mood
    fun getEntriesByMood(mood: String): Flow<List<WellbeingEntry>> {
        return wellbeingDao.getEntriesByMood(mood)
    }

    //  Filter by activity
    fun getEntriesByActivity(activityType: String): Flow<List<WellbeingEntry>> {
        return wellbeingDao.getEntriesByActivity(activityType)
    }

    // Last 7 days (for Weekly Report)
    fun getEntriesForWeek(startDate: String, endDate: String): Flow<List<WellbeingEntry>> {
        return wellbeingDao.getEntriesForWeek(startDate, endDate)
    }

    // Recent N items (for context-aware use on the Tips page)
    suspend fun getRecentEntries(limit: Int): List<WellbeingEntry> {
        return wellbeingDao.getRecentEntries(limit)
    }
}