package com.example.pillpalmobile.network

data class ScheduleRequest(
    val repeat_type: String,          // "once" | "daily" | "weekly" | "custom"
    val day_mask: String? = null,     // e.g. "0110010" or null
    val times: List<String>,          // ["08:00", "15:30"]
    val custom_start: String? = null, // "2025-12-07"
    val custom_end: String? = null    // "2025-12-21"
)
