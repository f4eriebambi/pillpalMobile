package com.example.pillpalmobile.data

import com.example.pillpalmobile.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("medications")
    suspend fun getMedications(
        @Query("user_id") userId: Int
    ): Response<List<MedicationResponse>>
}
