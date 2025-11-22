@file:RequiresApi(Build.VERSION_CODES.O)

package com.example.pillpalmobile.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pillpalmobile.R
import com.example.pillpalmobile.model.Medication
import com.example.pillpalmobile.ui.theme.Montserrat
import com.example.pillpalmobile.ui.theme.PixelifySans
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

// -------------------------------------------------------------------------
//                               DATA CLASSES
// -------------------------------------------------------------------------

data class MedicationHistoryEntry(
    val medicationName: String,
    val scheduledTime: String,
    val status: HistoryStatus
)

data class DayHistory(
    val date: LocalDate,
    val entries: List<MedicationHistoryEntry>,
    val allTaken: Boolean
)

enum class HistoryStatus {
    TAKEN, MISSED, UPCOMING
}

// -------------------------------------------------------------------------
//                               STREAK CARD
// -------------------------------------------------------------------------

@Composable
fun StreakCard(currentStreak: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

            Image(
                painter = painterResource(R.drawable.streak_border),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(275.dp)
                    .offset(x = 20.dp, y = 10.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentStreak.toString(),
                        fontSize = 120.sp,
                        fontFamily = PixelifySans,
                        color = Color.Black
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = -25.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "day streak! 🔥",
                        fontSize = 24.sp,
                        fontFamily = PixelifySans
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
//                               MAIN SCREEN
// -------------------------------------------------------------------------

@Composable
fun HistoryScreen(
    medications: List<Medication> = emptyList(),
    currentStreak: Int = 0,
    onNavigateHome: () -> Unit = {},
    onNavigateCalendar: () -> Unit = {},
    onNavigateNotifications: () -> Unit = {},
    onNavigateSettings: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    val historyData = remember(medications) {
        generateHistoryFromMedications(medications)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {

            Text(
                text = "My History",
                fontSize = 32.sp,
                fontFamily = Montserrat,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            StreakCard(currentStreak)

            Spacer(modifier = Modifier.height(20.dp))

            MedicationHistoryLog(historyData)
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        ) {
            HistoryNavigationBar(
                onHome = onNavigateHome,
                onCalendar = onNavigateCalendar,
                onNotifications = onNavigateNotifications,
                onSettings = onNavigateSettings
            )
        }
    }
}

// -------------------------------------------------------------------------
//                       GENERATE HISTORY – API 24 SAFE
// -------------------------------------------------------------------------

private fun generateHistoryFromMedications(medications: List<Medication>): List<DayHistory> {
    val today = LocalDate.now()

    return (0..29).map { daysAgo ->
        val date = today.minusDays(daysAgo.toLong())

        val entries = medications.flatMap { med ->
            med.reminderTimes.map {
                MedicationHistoryEntry(
                    medicationName = med.name,
                    scheduledTime = it,
                    status = if (daysAgo == 0) HistoryStatus.UPCOMING else HistoryStatus.TAKEN
                )
            }
        }

        DayHistory(
            date = date,
            entries = entries,
            allTaken = entries.all { it.status == HistoryStatus.TAKEN }
        )
    }
}

// -------------------------------------------------------------------------
//                     HISTORY DATE SECTION
// -------------------------------------------------------------------------

@Composable
fun HistoryDateSection(dayHistory: DayHistory) {
    val formatter = DateTimeFormatter.ofPattern("EEE MMM d", Locale.ENGLISH)
    val dateLabel = dayHistory.date.format(formatter)

    Column(modifier = Modifier.fillMaxWidth()) {

        Text(
            text = dateLabel,
            fontSize = 20.sp,
            fontFamily = Montserrat,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        dayHistory.entries.forEachIndexed { index, entry ->
            HistoryEntryCard(entry)
            if (index != dayHistory.entries.lastIndex)
                Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// -------------------------------------------------------------------------
//                        LOG LIST
// -------------------------------------------------------------------------

@Composable
fun MedicationHistoryLog(historyData: List<DayHistory>) {
    Column(verticalArrangement = Arrangement.spacedBy(28.dp)) {
        historyData.forEach { day ->
            HistoryDateSection(day)
        }
    }
}

// -------------------------------------------------------------------------
//                         ENTRY CARD
// -------------------------------------------------------------------------

@Composable
fun HistoryEntryCard(entry: MedicationHistoryEntry) {
    val (badgeText, badgeColor) = when (entry.status) {
        HistoryStatus.TAKEN -> "Taken" to Color(0xFFE8F5E9)
        HistoryStatus.MISSED -> "Missed" to Color(0xFFF5E8E8)
        HistoryStatus.UPCOMING -> "Upcoming" to Color.White
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF7C8081), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "${entry.medicationName} — ${entry.scheduledTime}",
                fontSize = 18.sp,
                fontFamily = Montserrat,
                color = Color.Black
            )

            Surface(
                color = badgeColor,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = badgeText,
                    modifier = Modifier.padding(8.dp),
                    fontFamily = Montserrat,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// -------------------------------------------------------------------------
//                      SIMPLE NAVIGATION BAR
// -------------------------------------------------------------------------

@Composable
fun HistoryNavigationBar(
    onHome: () -> Unit,
    onCalendar: () -> Unit,
    onNotifications: () -> Unit,
    onSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavIcon(R.drawable.home, "Home", onHome)

        // Calendar icon corrected (your project uses add_calendar)
        NavIcon(R.drawable.add_calendar, "Calendar", onCalendar)

        NavIcon(R.drawable.bell, "Alerts", onNotifications)
        NavIcon(R.drawable.user_settings, "Settings", onSettings)
    }
}

@Composable
fun NavIcon(icon: Int, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(icon),
            contentDescription = label,
            modifier = Modifier
                .size(36.dp)
                .clickable { onClick() },
            tint = Color.Unspecified
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = Montserrat,
            color = Color.Black
        )
    }
}
