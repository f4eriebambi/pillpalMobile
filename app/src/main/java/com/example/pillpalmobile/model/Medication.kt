package com.example.pillpalmobile.model

import androidx.annotation.DrawableRes

data class Medication(
    val id: Int = 0,
    val name: String,
    val dosage: String = "",
    val frequency: String = "",
    val notes: String = "",
    val isActive: Boolean = true
)