package com.example.pillpalmobile.model

data class MedicationResponse(
    val med_id: Int,
    val name: String,
    val notes: String?,
    val active_start_date: String?,
    val active_end_date: String?,
    val schedule: MedicationSchedule?
)

data class MedicationSchedule(
    val repeat_type: String?,
    val day_mask: String?,
    val times: List<String>,
    val custom_start: String?,
    val custom_end: String?
)
