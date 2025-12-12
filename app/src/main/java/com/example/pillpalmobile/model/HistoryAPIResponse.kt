package com.example.pillpalmobile.model

import com.example.pillpalmobile.DayHistory
import com.example.pillpalmobile.HistoryStatus
import com.example.pillpalmobile.MedicationHistoryEntry
import java.time.LocalDate

data class HistoryAPIResponse(
    val date: String,
    val medications: List<HistoryMedAPI>
)

fun List<HistoryAPIResponse>.toDayHistoryList(): List<DayHistory> {
    return this.map { day ->
        DayHistory(
            date = LocalDate.parse(day.date),
            entries = day.medications.map { med ->
                MedicationHistoryEntry(
                    medicationName = med.name,
                    scheduledTime = med.scheduledTime,
                    status = when (med.status.lowercase()) {
                        "taken" -> HistoryStatus.TAKEN
                        "missed" -> HistoryStatus.MISSED
                        else -> HistoryStatus.UPCOMING
                    }
                )
            },
            allTaken = day.medications.all { it.status == "taken" }
        )
    }
}
