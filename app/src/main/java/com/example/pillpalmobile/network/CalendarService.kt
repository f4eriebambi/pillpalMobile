package com.example.pillpalmobile.network

import com.example.pillpalmobile.model.CalendarDose
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface CalendarService {

    @GET("api/calendar/day")
    suspend fun getDosesForDay(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ): List<CalendarDose>

    @POST("api/dose/update")
    suspend fun updateDoseStatus(
        @Header("Authorization") token: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    )
}