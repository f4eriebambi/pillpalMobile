package com.example.pillpalmobile

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pillpalmobile.model.Medication
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import androidx.navigation.NavHostController
import com.example.pillpalmobile.data.AuthStore
import com.example.pillpalmobile.navigation.BottomNavBar
import com.example.pillpalmobile.network.HistoryAPIResponse
import com.example.pillpalmobile.network.RetrofitClient
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * BACKEND will dp:
 * - API endpoint: GET /medications/history?from={startDate}&to={endDate}
 * - Response should include:
 *   {
 *     "date": "2025-11-07",
 *     "medications": [
 *       {
 *         "id": 1,
 *         "name": "vitamin C",
 *         "scheduledTime": "08:00",
 *         "status": "taken" | "missed" | "upcoming",
 *         "takenAt": timestamp (if taken)
 *       }
 *     ]
 *   }
 * - calculate streak count based on consecutive days where ALL MEDS were taken
 */

// history entry data model
data class MedicationHistoryEntry(
    val medicationName: String,
    val scheduledTime: String,
    val status: HistoryStatus // taken, missed, upcoming, completed
)

enum class HistoryStatus {
    TAKEN,
    MISSED,
    UPCOMING
}

// day summary for grouping
data class DayHistory(
    val date: LocalDate,
    val entries: List<MedicationHistoryEntry>,
    val allTaken: Boolean
)

@Composable
fun HistoryScreen(
    navController: NavHostController,
    medications: List<Medication> = emptyList(),
    currentStreak: Int = 0, // for BACKEND: Calculate from API
    onNavigateHome: () -> Unit = {},
    onNavigateCalendar: () -> Unit = {},
    onNavigateNotifications: () -> Unit = {},
    onNavigateSettings: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    val context = LocalContext.current
    var historyData by remember { mutableStateOf<List<DayHistory>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

// FIXED: Added import + no duplicate variable
    val navBackStackEntry = navController.currentBackStackEntryAsState()

    LaunchedEffect(navBackStackEntry.value) {
        // only refresh when user navigates to history
        if (navBackStackEntry.value?.destination?.route == "history") {
            isLoading = true
            try {
                val token = AuthStore.getToken(context)
                if (token != null) {
                    val response = RetrofitClient.historyService.getHistory("Bearer $token")
                    historyData = response.toDayHistoryList()
                }
            } catch (e: Exception) {
                println("Error loading history: $e")
            }
            isLoading = false
        }
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
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 100.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // title
            Text(
                text = "My History",
                fontSize = 32.sp,
                fontFamily = Montserrat,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            // day streak counter, changed from testStreak as finished testing
            StreakCard(currentStreak = currentStreak)

            Spacer(modifier = Modifier.height(20.dp))

            // history log
            MedicationHistoryLog(historyData = historyData)

            Spacer(modifier = Modifier.height(8.dp))
        }

        // nav bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            BottomNavBar(navController, current = "history")
        }
    }
}

/**
 * for the frontend i generated history from meds
 * simulates what BACKEND would return
 *
 * for BACKEND: replace this with actual API call to get history data
 * including "taken" status, timestamps, etc.
 */
private fun generateHistoryFromMedications(medications: List<Medication>): List<DayHistory> {
    // HARDCODED TEST DATA - showing all badge types
    // commenting once i check that the display is correct
//    val today = LocalDate.now()
//    return listOf(
//        // TODAY - mix of statuses to show all badges
//        DayHistory(
//            date = today,
//            entries = listOf(
//                MedicationHistoryEntry("vitamin C", "08:00", HistoryStatus.TAKEN),
//                MedicationHistoryEntry("omega-3", "14:00", HistoryStatus.MISSED),
//                MedicationHistoryEntry("medication 2", "20:00", HistoryStatus.UPCOMING)
//            ),
//            allTaken = false
//        ),
//        // YESTERDAY - all taken (should show "all medications" card)
//        DayHistory(
//            date = today.minusDays(1),
//            entries = listOf(
//                MedicationHistoryEntry("vitamin C", "08:00", HistoryStatus.TAKEN),
//                MedicationHistoryEntry("omega-3", "14:00", HistoryStatus.TAKEN),
//                MedicationHistoryEntry("medication 3", "20:00", HistoryStatus.TAKEN)
//            ),
//            allTaken = true
//        ),
//        // 2 DAYS AGO - mix of taken and missed
//        DayHistory(
//            date = today.minusDays(2),
//            entries = listOf(
//                MedicationHistoryEntry("vitamin C", "08:00", HistoryStatus.TAKEN),
//                MedicationHistoryEntry("omega-3", "14:00", HistoryStatus.MISSED),
//                MedicationHistoryEntry("medication 3", "20:00", HistoryStatus.TAKEN)
//            ),
//            allTaken = false
//        ),
//        // 3 DAYS AGO - all taken (should show "all medications" card)
//        DayHistory(
//            date = today.minusDays(3),
//            entries = listOf(
//                MedicationHistoryEntry("vitamin C", "08:00", HistoryStatus.TAKEN),
//                MedicationHistoryEntry("medication 2", "12:00", HistoryStatus.TAKEN)
//            ),
//            allTaken = true
//        ),
//        // 4 DAYS AGO - all missed (worst case scenario)
//        DayHistory(
//            date = today.minusDays(4),
//            entries = listOf(
//                MedicationHistoryEntry("vitamin C", "08:00", HistoryStatus.MISSED),
//                MedicationHistoryEntry("omega-3", "14:00", HistoryStatus.MISSED),
//                MedicationHistoryEntry("medication 3", "20:00", HistoryStatus.MISSED)
//            ),
//            allTaken = false
//        )
//    )

    // REAL IMPLEMENTATION - ucommenting after testing after testing
//    /*
    val today = LocalDate.now()
    val historyDays = mutableListOf<DayHistory>()

    // generating history for last 30 days (or since user started)
    // for BACKEND: Get actual date range from user's join date
    for (daysAgo in 0..29) {
        val date = today.minusDays(daysAgo.toLong())
        val medsForDay = medications.filterForDate(date)

        if (medsForDay.isNotEmpty()) {
            val entries = medsForDay.flatMap { med ->
                med.reminderTimes.map { time ->
                    MedicationHistoryEntry(
                        medicationName = med.name,
                        scheduledTime = time,
                        // for BACKEND: Get actual status from API
                        status = when {
                            daysAgo == 0 -> HistoryStatus.UPCOMING // today - upcoming
                            else -> HistoryStatus.TAKEN // past - assume taken for demo
                        }
                    )
                }
            }.sortedBy { it.scheduledTime }

            val allTaken = entries.all { it.status == HistoryStatus.TAKEN }

            historyDays.add(
                DayHistory(
                    date = date,
                    entries = entries,
                    allTaken = allTaken && daysAgo > 0 // only past days can be "all taken"
                )
            )
        }
    }

    return historyDays
//    */
}

// filtering meds for specific date (reusing logic from calendar screen)
private fun List<Medication>.filterForDate(selectedDate: LocalDate): List<Medication> {
    val formatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy", Locale.ENGLISH)
    val selectedDateString = selectedDate.format(formatter)

    return this.filter { medication ->
        if (medication.repeatEnabled) {
            when (medication.repeatFrequency) {
                "Daily" -> true
                "Weekly" -> {
                    val selectedDay = selectedDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    val normalized = selectedDay.take(3)
                    medication.repeatDays.any { it.take(3).equals(normalized, ignoreCase = true) }
                }
                "Custom" -> {
                    if (medication.repeatStartDate != null && medication.repeatEndDate != null) {
                        val start = Instant.ofEpochMilli(medication.repeatStartDate)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                        val end = Instant.ofEpochMilli(medication.repeatEndDate)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                        !selectedDate.isBefore(start) && !selectedDate.isAfter(end)
                    } else false
                }
                else -> false
            }
        } else {
            try {
                if (medication.medicationDate.equals(selectedDateString, ignoreCase = true)) {
                    return@filter true
                }
                val medDate = LocalDate.parse(medication.medicationDate, formatter)
                medDate == selectedDate
            } catch (e: Exception) {
                false
            }
        }
    }
}

// day streak counter card
@Composable
fun StreakCard(currentStreak: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.streak_border),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(275.dp)
                    .offset(x = 20.dp, y = (10).dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // streak number display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentStreak.toString(),
                        fontSize = 120.sp,
                        fontFamily = PixelifySans,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                }

//                Spacer(modifier = Modifier.height(1.dp))
//                Spacer(modifier = Modifier.height((-80).dp))

                // streak banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-25).dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "day streak! 🔥",
                        fontSize = 24.sp,
                        fontFamily = PixelifySans,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// medication history log
@Composable
fun MedicationHistoryLog(historyData: List<DayHistory>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        if (historyData.isEmpty()) {
            // empty state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(R.drawable.pills),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFFA1A1A1)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No medication history yet",
                        fontSize = 20.sp,
                        fontFamily = Montserrat,
                        color = Color(0xFF666666)
                    )
                }
            }
        } else {
            historyData.forEach { dayHistory ->
                HistoryDateSection(dayHistory = dayHistory)
            }
        }
    }
}

// history section for each date
@Composable
fun HistoryDateSection(dayHistory: DayHistory) {
    val today = LocalDate.now()
    val dateString = when (dayHistory.date) {
        today -> {
            val formatter = DateTimeFormatter.ofPattern("EEE MMM d", Locale.ENGLISH)
            "Today, ${dayHistory.date.format(formatter)}"
        }
        else -> {
            val formatter = DateTimeFormatter.ofPattern("EEE MMM d", Locale.ENGLISH)
            dayHistory.date.format(formatter)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // date header
        Text(
            text = dateString,
            fontSize = 20.sp,
            fontFamily = Montserrat,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        // show entries based on logic
        if (dayHistory.allTaken && dayHistory.date != LocalDate.now()) {
            // show one "all medications" card for past days where everything was taken
            HistoryEntryCard(
                entry = MedicationHistoryEntry(
                    medicationName = "All Medications Taken",
                    scheduledTime = "",
                    status = HistoryStatus.TAKEN
                ),
                isAllMedicationsCard = true
            )
        } else {
            // show individual history cards
            dayHistory.entries.forEach { entry ->
                HistoryEntryCard(entry = entry, isAllMedicationsCard = false)
                if (entry != dayHistory.entries.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

// individual history entry card
@Composable
fun HistoryEntryCard(
    entry: MedicationHistoryEntry,
    isAllMedicationsCard: Boolean = false
) {
    val backgroundColor = when {
        isAllMedicationsCard -> Color.White
        entry.status == HistoryStatus.TAKEN -> Color.White
        entry.status == HistoryStatus.MISSED -> Color.White
        else -> Color.White
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF7C8081), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // med name with formatting
                Text(
                    text = if (entry.scheduledTime.isNotEmpty()) {
                        "${entry.medicationName}: ${entry.scheduledTime}"
                    } else {
                        entry.medicationName
                    },
                    fontSize = 18.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
            }

            // status badge with proper styling
            StatusBadge(status = entry.status, isAllMedicationsCard = isAllMedicationsCard)
        }
    }
}

// status badge component
@Composable
fun StatusBadge(status: HistoryStatus, isAllMedicationsCard: Boolean) {
    val (backgroundColor, borderColor, text) = when {
        isAllMedicationsCard -> Triple(Color.White, Color.Black, "Completed")
        status == HistoryStatus.TAKEN -> Triple(Color(0xFFE8F5E9), Color(0xFFACBD6F), "Taken")
        status == HistoryStatus.MISSED -> Triple(Color(0xFFF5E8E8), Color(0xFFBD6F6F), "Missed")
        else -> Triple(Color.White, Color.Black, "Upcoming")
    }

    Surface(
        shape = RoundedCornerShape(5.dp),
        color = backgroundColor,
        modifier = Modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(5.dp),
                spotColor = Color.Black,
                ambientColor = Color.Black,
                clip = false
            )
            .border(1.dp, borderColor, RoundedCornerShape(5.dp))
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontFamily = Montserrat,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

//// nav bar
//@Composable
//fun HistoryNavigationBar(
//    onNavigateHome: () -> Unit,
//    onNavigateCalendar: () -> Unit,
//    onNavigateNotifications: () -> Unit,
//    onNavigateSettings: () -> Unit
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 4.dp),
//        horizontalArrangement = Arrangement.SpaceEvenly,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        // home
//        HistoryNavigationButton(
//            iconRes = R.drawable.home,
//            label = "home",
//            isSelected = false,
//            onClick = onNavigateHome
//        )
//
//        // history
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            Box(
//                modifier = Modifier
//                    .size(72.dp)
//                    .shadow(
//                        elevation = 4.dp,
//                        shape = CircleShape,
//                        spotColor = Color.Black,
//                        ambientColor = Color.Black,
//                        clip = false
//                    )
//                    .background(
//                        color = Color(0xFFCBCBE7),
//                        shape = CircleShape
//                    )
//                    .border(
//                        width = 2.dp,
//                        color = Color(0xFF595880),
//                        shape = CircleShape
//                    )
//                    .clickable { /* already on history */ },
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    painter = painterResource(R.drawable.history),
//                    contentDescription = null,
//                    modifier = Modifier.size(48.dp),
//                    tint = Color.Unspecified
//                )
//            }
//        }
//
//        // calendar
//        HistoryNavigationButton(
//            iconRes = R.drawable.add_calendar,
//            label = "add",
//            isSelected = false,
//            onClick = onNavigateCalendar
//        )
//
//        // notifications
//        HistoryNavigationButton(
//            iconRes = R.drawable.bell,
//            label = "alerts",
//            isSelected = false,
//            onClick = onNavigateNotifications
//        )
//
//        // settings
//        HistoryNavigationButton(
//            iconRes = R.drawable.user_settings,
//            label = "settings",
//            isSelected = false,
//            onClick = onNavigateSettings
//        )
//    }
//}

fun List<HistoryAPIResponse>.toDayHistoryList(): List<DayHistory> {
    return this.map { day ->
        val date = LocalDate.parse(day.date)

        val entries = day.medications.map { med ->
            MedicationHistoryEntry(
                medicationName = med.name,
                scheduledTime = med.scheduledTime,
                status = when (med.status.lowercase()) {
                    "taken" -> HistoryStatus.TAKEN
                    "missed" -> HistoryStatus.MISSED
                    else -> HistoryStatus.UPCOMING
                }
            )
        }

        DayHistory(
            date = date,
            entries = entries,
            allTaken = entries.all { it.status == HistoryStatus.TAKEN }
        )
    }
}

