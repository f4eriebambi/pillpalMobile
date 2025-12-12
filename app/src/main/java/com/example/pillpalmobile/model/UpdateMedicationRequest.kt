package com.example.pillpalmobile.model

import com.example.pillpalmobile.network.ScheduleRequest

data class UpdateMedicationRequest(
    val name: String,
    val notes: String?,
    val schedule: ScheduleRequest
)
