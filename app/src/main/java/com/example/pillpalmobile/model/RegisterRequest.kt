package com.example.pillpalmobile.model

data class RegisterRequest(
    val full_name: String,
    val username: String,
    val email: String,
    val password: String,
    val birth_date: String,
    val timezone: String = "UTC"
)
