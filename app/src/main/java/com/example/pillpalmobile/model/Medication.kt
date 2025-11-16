package com.example.pillpalmobile.model

data class Medication(
    val id: Int,
    val name: String,
    val reminderTimes: List<String> = listOf("10:00"), // the list of times
    val medicationDate: String = "Fri, Nov 7, 2025", // date for when taking one-time meds (repeat OFF)
    val repeatEnabled: Boolean = false, // repeat toggle
    val repeatFrequency: String = "Daily", // daily,weekly,custom
    val repeatDays: List<String> = emptyList(), // for weekly = ["Mon", "Wed", "Fri"]
    val repeatStartDate: Long? = null, // for custom
    val repeatEndDate: Long? = null, // for custom
    val notes: String = "" // optional notes
)