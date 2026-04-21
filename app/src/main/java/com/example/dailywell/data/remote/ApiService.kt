package com.example.dailywell.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

// Get wellbeing-related tips and quotes from the public API

// This uses the ZenQuotes API (free, no API key required)

// Documentation: https://zenquotes.io/

data class TipResponse(
    val q: String,  // quote content
    val a: String,  // author
    val h: String   // HTML
)

interface ApiService {

    //Get a daily inspirational quote
    // Used on Tips / Support pages
    @GET("quotes")
    suspend fun getDailyQuotes(): List<TipResponse>

    // Get a random quote
    @GET("random")
    suspend fun getRandomQuote(): List<TipResponse>
}
