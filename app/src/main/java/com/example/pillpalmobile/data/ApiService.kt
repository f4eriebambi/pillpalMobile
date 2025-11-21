package com.example.pillpalmobile.data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(
    val full_name: String,
    val username: String,
    val email: String,
    val password: String,
    val birth_date: String,
    val timezone: String = "UTC"
)

data class RegisterResponse(
    val user_id: Int,
    val full_name: String,
    val username: String,
    val email: String,
    val timezone: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val user_id: Int,
    val full_name: String,
    val username: String,
    val email: String,
    val timezone: String
)

interface ApiService {

    @POST("/auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}
