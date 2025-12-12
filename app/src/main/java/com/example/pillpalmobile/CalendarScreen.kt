package com.example.pillpalmobile

import android.content.Context
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavHostController
import com.example.pillpalmobile.data.AuthStore
import com.example.pillpalmobile.model.CalendarDose
import com.example.pillpalmobile.model.Medication
import com.example.pillpalmobile.model.MedicationDisplay
import com.example.pillpalmobile.navigation.BottomNavBar
import com.example.pillpalmobile.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

// ---------------------------------------------------------
// DISPLAY MODEL
// ---------------------------------------------------------
data class MedicationDisplay(
    val instanceId: Int,
    val name: String,
    val time: String,
    val isTaken: Boolean
)

// ---------------------------------------------------------
// MAIN SCREEN
// ---------------------------------------------------------
@Composable
fun CalendarScreen(
    navController: NavHostController,
    medications: List<Medication> = emptyList(),
    onAddMedication: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val currentDate = remember { LocalDate.now() }
    val selectedDate = remember { mutableStateOf(currentDate) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var dayDoses by remember { mutableStateOf<List<CalendarDose>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

// LOAD DOSES FOR SELECTED DAY
    LaunchedEffect(selectedDate.value) {
        loading = true
        try {
            val token = AuthStore.getToken(context)
            if (token != null) {
                dayDoses = RetrofitClient.calendarService.getDosesForDay(
                    selectedDate.value.toString()
                )
            }
        } catch (e: Exception) {
            Log.e("CALENDAR", "Failed to load doses", e)
        }
        loading = false
    }


    val reloadDayDoses: () -> Unit = {
        scope.launch {
            try {
                val token = AuthStore.getToken(context)
                if (token != null) {
                    dayDoses = RetrofitClient.calendarService.getDosesForDay(
                        selectedDate.value.toString()
                    )
                }
            } catch (e: Exception) {
                Log.e("CALENDAR", "Reload failed", e)
            }
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
                .padding(top = 234.dp, bottom = 175.dp)
        ) {

            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                PlanCards(
                    dayDoses = dayDoses,
                    selectedDate = selectedDate.value,
                    onMarkTaken = { dose, newState ->
                        updateDoseStatus(
                            instanceId = dose.instanceId,
                            newState = newState,
                            context = context
                        ) {
                            reloadDayDoses()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // TOP SECTION: Header + Dates
        CalendarTopSection(
            currentDate = currentDate,
            selectedDate = selectedDate.value,
            onSelect = { selectedDate.value = it }
        )

        // BOTTOM ADD BUTTON
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
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFDFAE7)
                )
            }
        }

        // NAV BAR
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        ) {
            BottomNavBar(navController, current = "calendar")
        }
    }
}

// ---------------------------------------------------------
// MARK TAKEN -> API CALL
// ---------------------------------------------------------

fun updateDoseStatus(
    instanceId: Int,
    newState: Boolean,
    context: Context,
    onComplete: () -> Unit
) {

    // Run everything inside IO coroutine
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // getToken is suspend → MUST run inside coroutine
            val token = AuthStore.getToken(context) ?: return@launch

            val status = if (newState) "taken" else "missed"
            Log.d("CALENDAR", "Updating instance $instanceId → $status")

            RetrofitClient.calendarService.updateDoseStatus(
                mapOf(
                    "instance_id" to instanceId,
                    "status" to status
                )
            )


            // Switch back to Main for UI
            withContext(Dispatchers.Main) {
                onComplete()
            }

        } catch (e: Exception) {
            Log.e("CALENDAR", "Status update failed", e)
        }
    }
}



// ---------------------------------------------------------
// TOP DATE SECTION
// ---------------------------------------------------------
@Composable
fun CalendarTopSection(
    currentDate: LocalDate,
    selectedDate: LocalDate,
    onSelect: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        CalendarHeader(currentDate)

        Spacer(modifier = Modifier.height(30.dp))
        WeekdayLabels()

        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color(0xFFE6E6E6))

        Spacer(modifier = Modifier.height(10.dp))

        DateNumbersRow(
            currentDate = currentDate,
            selectedDate = selectedDate,
            onDateSelected = onSelect
        )
    }
}

// ---------------------------------------------------------
// HEADER
// ---------------------------------------------------------
@Composable
fun CalendarHeader(currentDate: LocalDate) {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)

    Text(
        text = currentDate.format(formatter),
        fontSize = 30.sp,
        fontFamily = PixelifySans,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

// ---------------------------------------------------------
// WEEKDAY LABELS
// ---------------------------------------------------------
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
                modifier = Modifier.width(48.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ---------------------------------------------------------
// DATE SELECTOR
// ---------------------------------------------------------
@Composable
fun DateNumbersRow(
    currentDate: LocalDate,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
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

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isSelected) Color(0xFFFCE2A9) else Color.Transparent,
                        CircleShape
                    )
                    .border(
                        width = if (isToday && !isSelected) 2.dp else 0.dp,
                        color = if (isToday) Color(0xFFF16F33) else Color.Transparent,
                        shape = CircleShape
                    )
                    .clickable { onDateSelected(date) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    fontSize = 22.sp,
                    color = when {
                        isSelected -> Color.White
                        isPast -> Color.Gray
                        else -> Color.Black
                    }
                )
            }
        }
    }
}

// ---------------------------------------------------------
// DAY PLAN CARDS
// ---------------------------------------------------------
@Composable
fun PlanCards(
    dayDoses: List<CalendarDose>,
    selectedDate: LocalDate,
    onMarkTaken: (CalendarDose, Boolean) -> Unit
) {
    val displayMeds = dayDoses.map {
        MedicationDisplay(
            name = it.name,
            time = it.time,
            isTaken = it.status == "taken",
            instanceId = it.instanceId
        )
    }


    if (displayMeds.isEmpty()) {
        EmptyCalendarState()
        return
    }

    val morning = displayMeds.filter { LocalTime.parse(it.time).hour in 5..11 }
    val afternoon = displayMeds.filter { LocalTime.parse(it.time).hour in 12..16 }
    val evening = displayMeds.filter {
        val h = LocalTime.parse(it.time).hour
        h in 17..23 || h in 0..4
    }

    Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
        if (morning.isNotEmpty())
            PlanCard("☼", "Morning Plan", "Rise and Shine", morning, onMarkTaken, dayDoses)

        if (afternoon.isNotEmpty())
            PlanCard(null, "Afternoon Plan", "Keep Going", afternoon, onMarkTaken, dayDoses)

        if (evening.isNotEmpty())
            PlanCard("☾", "Evening Plan", "Wind Down", evening, onMarkTaken, dayDoses)
    }
}

// ---------------------------------------------------------
// EMPTY STATE
// ---------------------------------------------------------
@Composable
fun EmptyCalendarState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(R.drawable.pills),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(64.dp)
            )
            Text("No medications today", fontSize = 20.sp)
            Text("Enjoy your day!", fontSize = 16.sp, color = Color.Gray)
        }
    }
}

// ---------------------------------------------------------
// CARD WITH CHECKBOXES
// ---------------------------------------------------------
@Composable
fun PlanCard(
    icon: String?,
    title: String,
    subtitle: String,
    medications: List<MedicationDisplay>,
    onMarkTaken: (CalendarDose, Boolean) -> Unit,
    allDoses: List<CalendarDose>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(40.dp)),
        shape = RoundedCornerShape(40.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.pills),
                contentDescription = null,
                modifier = Modifier.size(70.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (icon != null)
                        Text(text = icon, fontSize = 28.sp)

                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = subtitle,
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                medications.forEach { med ->
                    val dose = allDoses.first { it.instanceId == med.instanceId }

                    MedicationCheckboxRow(
                        med = med,
                        onToggle = { checked -> onMarkTaken(dose, checked) }
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}

// ---------------------------------------------------------
// CHECKBOX ROW
// ---------------------------------------------------------
@Composable
fun MedicationCheckboxRow(
    med: MedicationDisplay,
    onToggle: (Boolean) -> Unit
) {
    var checked by remember { mutableStateOf(med.isTaken) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                checked = !checked
                onToggle(checked)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = {
                checked = it
                onToggle(it)
            }
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(med.time, fontSize = 18.sp)
            Text(med.name, fontSize = 20.sp)
        }
    }
}
