package com.example.pillpalmobile.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

data class HistoryMedAPI(
    val id: Int,
    val name: String,
    val scheduledTime: String,
    val status: String
)

data class HistoryAPIResponse(
    val date: String,
    val medications: List<HistoryMedAPI>
)

interface HistoryService {
    @GET("/api/medications/history")
    suspend fun getHistory(
        @Header("Authorization") authHeader: String,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null
    ): List<HistoryAPIResponse>
}
