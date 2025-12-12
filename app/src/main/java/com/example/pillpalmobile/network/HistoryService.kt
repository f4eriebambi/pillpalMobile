package com.example.pillpalmobile.network

import com.example.pillpalmobile.model.HistoryAPIResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface HistoryService {

    @GET("/api/medications/history")
    suspend fun getHistory(
        @Query("from") from: String? = null,
        @Query("to") to: String? = null
    ): List<HistoryAPIResponse>
}
