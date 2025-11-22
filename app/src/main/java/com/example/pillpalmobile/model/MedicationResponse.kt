package com.example.pillpalmobile.model

data class MedicationScheduleResponse(
    val repeat_type: String?,
    val day_mask: String?,
    val times: List<String>

)

data class MedicationResponse(
    val med_id: Int,
    val name: String,
    val active_start_date: String?,
    val active_end_date: String?,
    val notes: String?,
    val schedule: MedicationScheduleResponse?
)
