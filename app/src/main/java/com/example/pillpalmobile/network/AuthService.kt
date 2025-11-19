package com.example.pillpalmobile.network

import com.example.pillpalmobile.model.LoginRequest
import com.example.pillpalmobile.model.LoginResponse
import com.example.pillpalmobile.model.ProfileResponse
import com.example.pillpalmobile.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {

    @POST("/api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("/api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @GET("/api/auth/me")
    suspend fun getProfile(
        @Header("Authorization") authHeader: String
    ): Response<ProfileResponse>

}


data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String
)

data class AuthResponse(
    val token: String
)
