package com.example.pillpalmobile.model

enum class NotificationType {
    UPCOMING_DOSE,
    REFILL_REMINDER,
    STREAK_MILESTONE,
    MISSED_DOSE
}

data class Notification(
    val id: Int,
    val type: NotificationType,
    val title: String,
    val subtitle: String,
    val timeAgo: String,
    val medicationName: String? = null,
    val time: String? = null
)