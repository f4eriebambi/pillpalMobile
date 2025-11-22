package com.example.pillpalmobile.model

data class LoginResponse(
    val user_id: Int? = null,
    val full_name: String? = null,
    val username: String? = null,
    val email: String? = null,
    val timezone: String? = null,
    val error: String? = null
)
