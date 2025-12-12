package com.example.pillpalmobile.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val full_name: String,
    val birthday: String // "dd/MM/yyyy" btw
)
