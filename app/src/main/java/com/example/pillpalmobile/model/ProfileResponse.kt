package com.example.pillpalmobile.model

data class ProfileResponse(
    val user_id: Int,
    val email: String,
    val full_name: String?,
    val birthday: String?
)
