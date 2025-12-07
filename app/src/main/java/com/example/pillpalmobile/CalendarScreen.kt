package com.example.pillpalmobile

import android.util.Log
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import java.util.Locale
import com.example.pillpalmobile.model.Medication
import androidx.navigation.NavHostController
import com.example.pillpalmobile.navigation.BottomNavBar


// https://www.youtube.com/watch?v=vL_3r9tz1gM
// https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/filter.html
// https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/sorted-by.html


// display model for calendar
data class MedicationDisplay(
    val name: String,
    val time: String,
    val isTaken: Boolean,
    val medication: Medication // ref to real data
)

// make sure med is converted to display format of figma
private fun List<Medication>.toCalendarDisplay(): List<MedicationDisplay> {
    return this.flatMap { medication ->
        medication.reminderTimes.map { time ->
            MedicationDisplay(
                name = medication.name,
                time = time,
                isTaken = false, // placeholder, tracking will be handled by backend
                medication = medication
            )
        }
    }
}

// filter meds for selected date
private fun List<Medication>.filterForDate(selectedDate: LocalDate): List<Medication> {
    val formatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy", Locale.ENGLISH)

    val selectedDateString = selectedDate.format(formatter) // e.g. "Fri, Nov 7, 2025"

    return this.filter { medication ->

        // REPEAT ENABLED
        if (medication.repeatEnabled) {
            when (medication.repeatFrequency) {

                "Daily" -> true

                "Weekly" -> {
                    val selectedDay = selectedDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    val normalized = selectedDay.take(3)  // "Mon", "Tue", "Wed"
                    medication.repeatDays.any { it.take(3).equals(normalized, ignoreCase = true) }
                }

                "Custom" -> {
                    if (medication.repeatStartDate != null && medication.repeatEndDate != null) {
                        val start = Instant.ofEpochMilli(medication.repeatStartDate).atZone(ZoneId.systemDefault()).toLocalDate()
                        val end = Instant.ofEpochMilli(medication.repeatEndDate).atZone(ZoneId.systemDefault()).toLocalDate()

                        // inclusive range
                        !selectedDate.isBefore(start) && !selectedDate.isAfter(end)
                    } else false
                }

                else -> false
            }

        } else {
            // ONE-TIME MED
            try {
                // quick match string → string
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

@Composable
fun CalendarScreen(
    navController: NavHostController,
    medications: List<Medication> = emptyList(),
    onAddMedication: () -> Unit
) {
    val scrollState = rememberScrollState()
    val currentDate = remember { LocalDate.now() }
    val selectedDate = remember { mutableStateOf(currentDate) }

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
                .padding(top = 234.dp, bottom = 175.dp)
        ) {
            // scrolling parts
            // plan of the day
            PlanCards(
                selectedDate = selectedDate.value,
                medications = medications
            )

            Spacer(modifier = Modifier.height(30.dp))

//            // add medication button
//            Button(
//                onClick = { /* navigate to add med page */ },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(56.dp)
//                    .border(2.dp, Color(0xFF595880), RoundedCornerShape(15.dp)),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFFACBD6F)
//                ),
//                shape = RoundedCornerShape(15.dp)
//            ) {
//                Text(
//                    text = "+ Add Medication",
//                    fontSize = 24.sp,
//                    fontFamily = Montserrat,
//                    fontWeight = FontWeight.SemiBold,
//                    color = Color(0xFFFDFAE7)
//                )
//            }
        }

        // header section at top
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
//                    .shadow(
//                        elevation = 6.dp,
//                        spotColor = Color(0xFFA1A1A1),
//                        ambientColor = Color(0xFFA1A1A1),
//                        clip = false
//                    )
                    .background(Color.White)
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                // header
                CalendarHeader(currentDate = currentDate)

                Spacer(modifier = Modifier.height(30.dp))

                // days of week
                WeekdayLabels()

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFE6E6E6))
                )

                Spacer(modifier = Modifier.height(10.dp))

                // dates number
                DateNumbersRow(
                    currentDate = currentDate,
                    selectedDate = selectedDate.value,
                    onDateSelected = { selectedDate.value = it }
                )
            }
        }

        // sticky add medication button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 120.dp)
        ) {
            Button(
                onClick = onAddMedication,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 38.dp)
                    .height(56.dp)
                    .border(2.dp, Color(0xFF595880), RoundedCornerShape(15.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFACBD6F)
                ),
                shape = RoundedCornerShape(15.dp)
            ) {
                Text(
                    text = "+ Add Medication",
                    fontSize = 24.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFDFAE7)
                )
            }
        }

//        Spacer(modifier = Modifier.height(40.dp))

        // nav bar at very bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            BottomNavBar(navController, current = "calendar")
        }
    }
}

// calendar header
@Composable
fun CalendarHeader(currentDate: LocalDate) {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = currentDate.format(formatter),
            fontSize = 30.sp,
            fontFamily = PixelifySans,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        )
    }
}

// days of week display
@Composable
fun WeekdayLabels() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN").forEach { day ->
            Text(
                text = day,
                fontSize = 18.sp,
                fontFamily = Inter,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                modifier = Modifier.width(48.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

// dates numbers
@Composable
fun DateNumbersRow(
    currentDate: LocalDate,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    // get start of week for current date
    val startOfWeek = selectedDate.minusDays(selectedDate.dayOfWeek.value.toLong() - 1)
    val weekDates = (0..6).map { startOfWeek.plusDays(it.toLong()) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        weekDates.forEach { date ->
            val isSelected = date == selectedDate
            val isToday = date == currentDate
            val isPast = date.isBefore(currentDate)
            val isFuture = date.isAfter(currentDate)

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isSelected) Color(0xFFFCE2A9) else Color.Transparent,
                        CircleShape
                    )
                    .border(
                        width = if (isToday && !isSelected) 2.dp else 0.dp,
                        color = if (isToday && !isSelected) Color(0xFFF16F33) else Color.Transparent,
                        shape = CircleShape
                    )
                    .clickable { onDateSelected(date) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    fontSize = 22.sp,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    color = when {
                        isSelected -> Color.White
                        isPast -> Color(0xFFA1A1A1)
                        else -> Color.Black
                    }
                )
            }
        }
    }
}

// plan of the day
@Composable
fun PlanCards(
    selectedDate: LocalDate,
    medications: List<Medication>
    ) {
    val filteredMeds = medications.filterForDate(selectedDate)
    val displayMeds = filteredMeds.toCalendarDisplay()

    // make sure meds into correct time section
    val morningMeds = displayMeds.filter { med ->
        val time = LocalTime.parse(med.time)
        time.hour in 5..11
    }.sortedBy { LocalTime.parse(it.time) } // sort by time

    val afternoonMeds = displayMeds.filter { med ->
        val time = LocalTime.parse(med.time)
        time.hour in 12..16
    }.sortedBy { LocalTime.parse(it.time) }

    val eveningMeds = displayMeds.filter { med ->
        val time = LocalTime.parse(med.time)
        time.hour in 17..23 || time.hour in 0..4
    }.sortedBy { LocalTime.parse(it.time) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // empty state when no meds for the day
        if (displayMeds.isEmpty()) {
            // Simple centered design
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
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
                        text = "No medications today",
                        fontSize = 20.sp,
                        fontFamily = Montserrat,
                        color = Color(0xFF666666)
                    )
                    Text(
                        text = "Enjoy your day off!",
                        fontSize = 16.sp,
                        fontFamily = SFPro,
                        color = Color(0xFFA1A1A1)
                    )
                }
            }
        } else {
            // morning plan - only show if has meds
            if (morningMeds.isNotEmpty()) {
                PlanCard(
                    icon = "☼",
                    title = "Morning Plan",
                    subtitle = "Rise and Shine",
                    medications = morningMeds
                )
            }

            // afternoon plan - only show if has meds
            if (afternoonMeds.isNotEmpty()) {
                PlanCard(
                    icon = null,
                    title = "Afternoon Plan",
                    subtitle = "Keep Going",
                    medications = afternoonMeds
                )
            }

            // evening plan - only show if has meds
            if (eveningMeds.isNotEmpty()) {
                PlanCard(
                    icon = "☾",
                    title = "Evening Plan",
                    subtitle = "Wind Down",
                    medications = eveningMeds
                )
            }
        }
    }
}

// plan cards
@Composable
fun PlanCard(
    icon: String?,
    title: String,
    subtitle: String,
    medications: List<MedicationDisplay>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                spotColor = Color.Black,
                ambientColor = Color.Black,
                shape = RoundedCornerShape(40.dp),
                clip = false
            ),
        shape = RoundedCornerShape(40.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // left side - title, subtitle, meds
            Image(
                painter = painterResource(R.drawable.pills),
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .padding(end = 16.dp)
                    .align(Alignment.CenterVertically)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // title with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (icon != null) {
                        Text(
                            text = "$icon ",
                            fontSize = 28.sp
                        )
                    }
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // subtitle
                Text(
                    text = subtitle,
                    fontSize = 18.sp,
                    fontFamily = SFPro,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                // medications list
                medications.forEach { med ->
                    Column(
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        // time
                        Text(
                            text = med.time,
                            fontSize = 18.sp,
                            fontFamily = SFPro,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // medication name with status
                        Text(
                            text = if (med.isTaken) "「✓」${med.name}" else "「⊕」${med.name}",
                            fontSize = 20.sp,
                            fontFamily = SFPro,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )
                    }

                    if (med != medications.last()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

//            Image(
//                painter = painterResource(R.drawable.pills),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(80.dp)
//                    .align(Alignment.CenterVertically)
//            )
        }
    }
}

//// nav bar
//@Composable
//fun CalendarNavigationBar() {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 4.dp),
//        horizontalArrangement = Arrangement.SpaceEvenly,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        // home
//        NavigationButton(
//            iconRes = R.drawable.home,
//            label = "home",
//            isSelected = false,
//            onClick = { /* navigate to home */ }
//        )
//
//        // history
//        NavigationButton(
//            iconRes = R.drawable.history,
//            label = "history",
//            isSelected = false,
//            onClick = { /* navigate to history */ }
//        )
//
//        // calendar
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            Box(
//                modifier = Modifier
//                    .size(72.dp)
//                    .background(
//                        color = Color(0xFFCBCBE7),
//                        shape = CircleShape
//                    )
//                    .border(
//                        width = 2.dp,
//                        color = Color(0xFF595880),
//                        shape = CircleShape
//                    )
//                    .clickable { /* already on calendar */ },
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    painter = painterResource(R.drawable.add_calendar),
//                    contentDescription = null,
//                    modifier = Modifier.size(48.dp),
//                    tint = Color.Unspecified
//                )
//            }
//        }
//
//        // notifications
//        NavigationButton(
//            iconRes = R.drawable.bell,
//            label = "alerts",
//            isSelected = false,
//            onClick = { /* navigate to notifications */ }
//        )
//
//        // settings
//        NavigationButton(
//            iconRes = R.drawable.user_settings,
//            label = "settings",
//            isSelected = false,
//            onClick = { /* navigate to settings */ }
//        )
//    }
//}
//
//@Composable
//fun NavigationButton(
//    iconRes: Int,
//    label: String,
//    isSelected: Boolean,
//    onClick: () -> Unit
//) {
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        Box(
//            modifier = Modifier
//                .size(70.dp)
//                .shadow(
//                    elevation = 4.dp,
//                    shape = CircleShape,
//                    spotColor = Color.Black,
//                    ambientColor = Color.Black,
//                    clip = false
//                )
//                .background(
//                    color = if (isSelected) Color(0xFFCBCBE7) else Color.White,
//                    shape = CircleShape
//                )
//                .border(
//                    width = if (isSelected) 2.dp else 1.dp,
//                    color = if (isSelected) Color(0xFF595880) else Color(0xFF7C8081),
//                    shape = CircleShape
//                )
//                .clickable(onClick = onClick),
//            contentAlignment = Alignment.Center
//        ) {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                Icon(
//                    painter = painterResource(iconRes),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(36.dp)
//                        .padding(top = 4.dp),
//                    tint = Color.Unspecified
//                )
//                Spacer(modifier = Modifier.height(2.dp))
//                Text(
//                    text = label,
//                    fontSize = 11.sp,
//                    fontFamily = Montserrat,
//                    fontWeight = FontWeight.SemiBold,
//                    color = Color.Black
//                )
//            }
//        }
//    }
//}