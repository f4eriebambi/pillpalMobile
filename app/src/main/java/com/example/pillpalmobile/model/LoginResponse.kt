package com.example.pillpalmobile.model

data class LoginResponse(
    val user_id: Int?,
    val full_name: String?,
    val username: String?,
    val email: String?,
    val timezone: String?,
    val birth_date: String?,
    val error: String?
)

