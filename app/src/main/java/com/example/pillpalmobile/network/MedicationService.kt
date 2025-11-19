package com.example.pillpalmobile.network

import com.example.pillpalmobile.model.MedicationResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface MedicationService {

    @GET("api/medications")
    suspend fun getMedications(
        @Header("Authorization") token: String
    ): List<MedicationResponse>

    @GET("api/medications/{id}")
    suspend fun getMedicationById(
        @Header("Authorization") token: String,
        @Path("id") medId: Int
    ): MedicationResponse
}
