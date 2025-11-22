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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pillpalmobile.model.Medication
import com.example.pillpalmobile.ui.theme.Inter
import com.example.pillpalmobile.ui.theme.Montserrat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// --------------------------- DATA MODELS ------------------------------

data class MedicationDisplay(
    val name: String,
    val time: String,
    val isTaken: Boolean,
    val medication: Medication
)

private fun List<Medication>.toCalendarDisplay(): List<MedicationDisplay> {
    return this.flatMap { medication ->
        medication.reminderTimes.map { time ->
            MedicationDisplay(
                name = medication.name,
                time = time,
                isTaken = false,
                medication = medication
            )
        }
    }
}

private fun List<Medication>.filterForDate(selectedDate: LocalDate): List<Medication> {
    val formatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy", Locale.ENGLISH)
    val selectedDateString = selectedDate.format(formatter)

    return this.filter { medication ->

        if (medication.repeatEnabled) {

            when (medication.repeatFrequency) {

                "Daily" -> true

                "Weekly" -> {
                    val selectedDay = selectedDate.dayOfWeek
                        .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                        .take(3)
                    medication.repeatDays.any {
                        it.take(3).equals(selectedDay, ignoreCase = true)
                    }
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
                if (medication.medicationDate.equals(selectedDateString, ignoreCase = true))
                    return@filter true

                val medDate = LocalDate.parse(medication.medicationDate, formatter)
                medDate == selectedDate

            } catch (e: Exception) { false }
        }
    }
}

// --------------------------- MAIN SCREEN ------------------------------

@Composable
fun CalendarScreen(
    medications: List<Medication> = emptyList(),
    onAddMedication: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val currentDate = LocalDate.now()
    var selectedDate by remember { mutableStateOf(currentDate) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // MAIN CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(top = 234.dp, bottom = 175.dp)
        ) {
            PlanCards(
                selectedDate = selectedDate,
                medications = medications
            )

            Spacer(modifier = Modifier.height(30.dp))
        }

        // HEADER
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(Color.White)
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            CalendarHeader(currentDate)
            Spacer(modifier = Modifier.height(30.dp))
            WeekdayLabels()
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE6E6E6))
            )
            Spacer(modifier = Modifier.height(10.dp))
            DateNumbersRow(
                currentDate = currentDate,
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it }
            )
        }

        // ADD MED BUTTON
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
                )
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

        // NAV BAR
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        ) {
            CalendarNavigationBar()
        }
    }
}

// --------------------------- HEADER ------------------------------

@Composable
fun CalendarHeader(currentDate: LocalDate) {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)

    Text(
        text = currentDate.format(formatter),
        fontSize = 30.sp,
        fontFamily = Montserrat,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )
}

// --------------------------- WEEKDAY LABELS ------------------------------

@Composable
fun WeekdayLabels() {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN").forEach {
            Text(
                text = it,
                fontSize = 18.sp,
                fontFamily = Inter,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(48.dp),
                color = Color.Black
            )
        }
    }
}

// --------------------------- DATE NUMBERS ------------------------------

@Composable
fun DateNumbersRow(
    currentDate: LocalDate,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val startOfWeek = selectedDate.minusDays(selectedDate.dayOfWeek.value.toLong() - 1)
    val weekDates = (0..6).map { startOfWeek.plusDays(it.toLong()) }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        weekDates.forEach { date ->

            val isSelected = date == selectedDate
            val isToday = date == currentDate
            val isPast = date.isBefore(currentDate)

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isSelected) Color(0xFFFCE2A9) else Color.Transparent,
                        CircleShape
                    )
                    .border(
                        if (isToday && !isSelected) 2.dp else 0.dp,
                        if (isToday) Color(0xFFF16F33) else Color.Transparent,
                        CircleShape
                    )
                    .clickable { onDateSelected(date) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    fontSize = 22.sp,
                    fontFamily = Inter,
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

// --------------------------- PLAN CARDS ------------------------------

@Composable
fun PlanCards(
    selectedDate: LocalDate,
    medications: List<Medication>
) {
    val filtered = medications.filterForDate(selectedDate)
    val display = filtered.toCalendarDisplay()

    val morning = display.filter {
        val h = LocalTime.parse(it.time).hour
        h in 5..11
    }

    val afternoon = display.filter {
        val h = LocalTime.parse(it.time).hour
        h in 12..16
    }

    val evening = display.filter {
        val h = LocalTime.parse(it.time).hour
        h >= 17 || h <= 4
    }

    Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {

        if (display.isEmpty()) {
            EmptyState()
        } else {

            if (morning.isNotEmpty())
                PlanCard("☼", "Morning Plan", "Rise and Shine", morning)

            if (afternoon.isNotEmpty())
                PlanCard(null, "Afternoon Plan", "Keep Going", afternoon)

            if (evening.isNotEmpty())
                PlanCard("☾", "Evening Plan", "Wind Down", evening)
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(400.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(R.drawable.pills),
                contentDescription = null,
                tint = Color(0xFFA1A1A1),
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "No medications today",
                fontFamily = Montserrat,
                fontSize = 20.sp,
                color = Color(0xFF666666)
            )
            Text(
                text = "Enjoy your day off!",
                fontFamily = Montserrat,
                fontSize = 16.sp,
                color = Color(0xFFA1A1A1)
            )
        }
    }
}

// --------------------------- PLAN CARD ------------------------------

@Composable
fun PlanCard(
    icon: String?,
    title: String,
    subtitle: String,
    medications: List<MedicationDisplay>
) {
    Card(
        Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(40.dp)),
        shape = RoundedCornerShape(40.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(R.drawable.pills),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )

            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (icon != null) {
                        Text(text = "$icon ", fontSize = 28.sp)
                    }
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontFamily = Montserrat
                    )
                }

                Text(
                    text = subtitle,
                    fontSize = 18.sp,
                    fontFamily = Montserrat
                )

                Spacer(Modifier.height(16.dp))

                medications.forEachIndexed { index, med ->

                    Text(
                        text = med.time,
                        fontSize = 18.sp,
                        fontFamily = Montserrat
                    )

                    Text(
                        text = if (med.isTaken) "「✓」${med.name}" else "「⊕」${med.name}",
                        fontSize = 20.sp,
                        fontFamily = Montserrat
                    )

                    if (index != medications.lastIndex)
                        Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

// --------------------------- NAV BAR ------------------------------

@Composable
fun CalendarNavigationBar() {

    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        NavigationButton(R.drawable.home, "home", false) {}
        NavigationButton(R.drawable.history, "history", false) {}

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier
                    .size(72.dp)
                    .background(Color(0xFFCBCBE7), CircleShape)
                    .border(2.dp, Color(0xFF595880), CircleShape)
                    .clickable {}
            ) {
                Icon(
                    painter = painterResource(R.drawable.add_calendar),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                )
            }
        }

        NavigationButton(R.drawable.bell, "alerts", false) {}
        NavigationButton(R.drawable.user_settings, "settings", false) {}
    }
}

@Composable
fun NavigationButton(
    iconRes: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(
                    color = if (isSelected) Color(0xFFCBCBE7) else Color.White,
                    shape = CircleShape
                )
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) Color(0xFF595880) else Color(0xFF7C8081),
                    shape = CircleShape
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = label,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }
        }
    }
}

