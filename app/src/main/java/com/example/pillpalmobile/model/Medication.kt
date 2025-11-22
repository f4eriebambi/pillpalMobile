package com.example.pillpalmobile.model

data class Medication(
    val id: Int,
    val name: String,
    val reminderTimes: List<String> = listOf("10:00"),
    val medicationDate: String = "Fri, Nov 7, 2025",
    val repeatEnabled: Boolean = false,
    val repeatFrequency: String = "Daily",
    val repeatDays: List<String> = emptyList(),
    val repeatStartDate: Long? = null,
    val repeatEndDate: Long? = null,
    val notes: String = ""
)
