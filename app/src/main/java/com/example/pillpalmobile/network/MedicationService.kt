package com.example.pillpalmobile.network
import com.example.pillpalmobile.model.MedicationResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface MedicationService {

    @GET("api/medications")
    suspend fun getMedications(
        @Header("Authorization") token: String
    ): List<MedicationResponse>
}
