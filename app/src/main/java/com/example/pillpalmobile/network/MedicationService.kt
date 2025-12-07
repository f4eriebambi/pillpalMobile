package com.example.pillpalmobile.network

import com.example.pillpalmobile.model.MedicationResponse
import retrofit2.Response
import retrofit2.http.*

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

    @POST("api/medications")
    suspend fun addMedication(
        @Header("Authorization") token: String,
        @Body medication: AddMedicationRequest
    ): Response<MedicationResponse>
}

data class AddMedicationRequest(
    val name: String,
    val notes: String?,
    val schedule: ScheduleRequest
)

data class ScheduleRequest(
    val repeat_type: String,
    val day_mask: String?,
    val times: List<String>,
    val custom_start: String?,
    val custom_end: String?
)