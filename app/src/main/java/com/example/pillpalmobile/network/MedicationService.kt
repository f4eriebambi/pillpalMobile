package com.example.pillpalmobile.network

import com.example.pillpalmobile.model.MedicationResponse
import com.example.pillpalmobile.model.UpdateMedicationRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MedicationService {

    @GET("api/medications")
    suspend fun getMedications(): List<MedicationResponse>

    @GET("api/medications/{id}")
    suspend fun getMedicationById(
        @Path("id") medId: Int
    ): MedicationResponse

    @POST("api/medications")
    suspend fun addMedication(
        @Body medication: AddMedicationRequest
    ): retrofit2.Response<Unit>

    @PUT("api/medications/{medId}")
    suspend fun updateMedication(
        @Path("medId") medId: Int,
        @Body body: UpdateMedicationRequest
    ): retrofit2.Response<Unit>

    @DELETE("api/medications/{medId}")
    suspend fun deleteMedication(
        @Path("medId") medId: Int
    ): retrofit2.Response<Unit>
}
