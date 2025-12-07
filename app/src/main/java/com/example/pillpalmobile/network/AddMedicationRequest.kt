package com.example.pillpalmobile.network

data class AddMedicationRequest(
    val name: String,
    val notes: String? = null,
    val schedule: ScheduleRequest
)
