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
import androidx.navigation.NavController
import com.example.pillpalmobile.model.Medication
import java.text.SimpleDateFormat
import java.util.*

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
private fun List<Medication>.filterForDate(selectedDate: Date): List<Medication> {
    val formatter = SimpleDateFormat("EEE, MMM d, yyyy", Locale.ENGLISH)
    val selectedDateString = formatter.format(selectedDate) // e.g. "Fri, Nov 7, 2025"

    return this.filter { medication ->
        // REPEAT ENABLED
        if (medication.repeatEnabled) {
            when (medication.repeatFrequency) {
                "Daily" -> true
                "Weekly" -> {
                    val selectedDay = SimpleDateFormat("EEE", Locale.ENGLISH).format(selectedDate)
                    val normalized = selectedDay.take(3)  // "Mon", "Tue", "Wed"
                    medication.repeatDays.any { it.take(3).equals(normalized, ignoreCase = true) }
                }
                "Custom" -> {
                    if (medication.repeatStartDate != null && medication.repeatEndDate != null) {
                        val start = Date(medication.repeatStartDate!!)
                        val end = Date(medication.repeatEndDate!!)
                        // inclusive range
                        !selectedDate.before(start) && !selectedDate.after(end)
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
                val medDate = formatter.parse(medication.medicationDate)
                val calendar1 = Calendar.getInstance().apply { time = medDate }
                val calendar2 = Calendar.getInstance().apply { time = selectedDate }
                calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                        calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                        calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)
            } catch (e: Exception) {
                false
            }
        }
    }
}

@Composable
fun CalendarScreen(
    navController: NavController? = null,
    medications: List<Medication> = emptyList(),
    onAddMedication: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val currentDate = remember { Date() }
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
                onClick = {
                    navController?.navigate("add")
                },
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
                    fontFamily = MontserratFont,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFDFAE7)
                )
            }
        }
    }
}

// calendar header
@Composable
fun CalendarHeader(currentDate: Date) {
    val formatter = SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = formatter.format(currentDate),
            fontSize = 30.sp,
//            fontFamily = PixelifySansFont,
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
                fontFamily = InterFont,
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
    currentDate: Date,
    selectedDate: Date,
    onDateSelected: (Date) -> Unit
) {
    val calendar = Calendar.getInstance().apply { time = selectedDate }
    // get start of week for current date
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
    val startOfWeek = calendar.time

    val weekDates = (0..6).map {
        Calendar.getInstance().apply {
            time = startOfWeek
            add(Calendar.DAY_OF_YEAR, it)
        }.time
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        weekDates.forEach { date ->
            val isSelected = isSameDay(date, selectedDate)
            val isToday = isSameDay(date, currentDate)
            val isPast = date.before(currentDate) && !isToday

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
                    text = SimpleDateFormat("d", Locale.ENGLISH).format(date),
                    fontSize = 22.sp,
                    fontFamily = InterFont,
                    fontWeight = FontWeight.Medium,
                    color = when {
                        isSelected -> Color.Black
                        isPast -> Color(0xFFA1A1A1)
                        else -> Color.Black
                    }
                )
            }
        }
    }
}

// Helper function to check if two dates are the same day
private fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}

// plan of the day
@Composable
fun PlanCards(
    selectedDate: Date,
    medications: List<Medication>
) {
    val filteredMeds = medications.filterForDate(selectedDate)
    val displayMeds = filteredMeds.toCalendarDisplay()

    // make sure meds into correct time section
    val morningMeds = displayMeds.filter { med ->
        val timeParts = med.time.split(":")
        val hour = timeParts[0].toInt()
        hour in 5..11
    }.sortedBy { it.time }

    val afternoonMeds = displayMeds.filter { med ->
        val timeParts = med.time.split(":")
        val hour = timeParts[0].toInt()
        hour in 12..16
    }.sortedBy { it.time }

    val eveningMeds = displayMeds.filter { med ->
        val timeParts = med.time.split(":")
        val hour = timeParts[0].toInt()
        hour in 17..23 || hour in 0..4
    }.sortedBy { it.time }

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
                        fontFamily = MontserratFont,
                        color = Color(0xFF666666)
                    )
                    Text(
                        text = "Enjoy your day off!",
                        fontSize = 16.sp,
                        fontFamily = SFProFont,
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
                        fontFamily = MontserratFont,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // subtitle
                Text(
                    text = subtitle,
                    fontSize = 18.sp,
                    fontFamily = SFProFont,
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
                            fontFamily = SFProFont,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // medication name with status
                        Text(
                            text = if (med.isTaken) "「✓」${med.name}" else "「⊕」${med.name}",
                            fontSize = 20.sp,
                            fontFamily = SFProFont,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )
                    }

                    if (med != medications.last()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}