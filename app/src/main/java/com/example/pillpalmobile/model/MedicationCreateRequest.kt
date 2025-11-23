package com.example.pillpalmobile.model


data class MedicationCreateRequest(
    val user_id: Int,
    val name: String,
    val notes: String?,
    val start_date: String? = null,
    val end_date: String? = null,
    val times: List<String>,
    val repeat_type: String,
    val day_mask: String,
    val lead_minutes: Int = 0
)
