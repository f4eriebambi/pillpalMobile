package com.example.pillpalmobile

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.pillpalmobile.data.AuthStore
import com.example.pillpalmobile.network.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// -----------------------------------------------------------------------------
//  Colors and fonts
// -----------------------------------------------------------------------------

private val AddSFPro = FontFamily.Default
private val AddInter = FontFamily.Default
private val AddMontserrat = FontFamily.Default

private val CreamColor = Color(0xFFFFC46E)
private val ContinueButtonColor = Color(0xFFF16F33)
private val MedicationInputColor = Color(0xFF595880)

// -----------------------------------------------------------------------------
// Helpers
// -----------------------------------------------------------------------------

private fun getCurrentDate(): String {
    val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return formatter.format(Date())
}

/**
 * Construye el day_mask que espera tu backend.
 * Por ahora:
 *  - si repeatEnabled = false -> null
 *  - "daily" o "weekly" -> "1111111" (todos los días)
 *  - "custom" -> null (lo podrías extender después)
 */
private fun buildDayMask(
    repeatFrequency: String,
    repeatEnabled: Boolean
): String? {
    if (!repeatEnabled) return null

    return when (repeatFrequency.lowercase()) {
        "daily" -> "1111111"
        "weekly" -> "1111111"
        else -> null
    }
}


suspend fun saveMedicationToBackend(
    context: Context,
    name: String,
    notes: String,
    startDate: String,
    endDate: String?,
    times: List<String>,
    repeatEnabled: Boolean,
    repeatType: String?,
    dayMask: String?
): Boolean {
    val token = AuthStore.getToken(context) ?: return false

    val body = mapOf(
        "name" to name,
        "notes" to notes,
        "start_date" to startDate,
        "end_date" to endDate,
        "times" to times,
        "repeat_enabled" to repeatEnabled,
        "repeat_type" to repeatType,
        "day_mask" to dayMask
    )

    val response = RetrofitClient.medicationService.createMedication(
        "Bearer $token",
        body
    )

    return if (response.isSuccessful) {
        true
    } else {
        println("Medication save error: ${response.errorBody()?.string()}")
        false
    }
}

// -----------------------------------------------------------------------------
//
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationScreen(navController: NavHostController) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var currentStep by remember { mutableStateOf(1) }

    var medicationName by remember { mutableStateOf("") }


    val times: SnapshotStateList<String> = remember {
        mutableStateListOf("10:00", "15:00")
    }

    var activeTimeIndex by remember { mutableStateOf(0) }
    var selectedDate by remember { mutableStateOf(getCurrentDate()) }
    var repeatEnabled by remember { mutableStateOf(false) }
    var repeatFrequency by remember { mutableStateOf("Daily") }
    var startDate by remember { mutableStateOf(getCurrentDate()) }
    var endDate by remember { mutableStateOf(getCurrentDate()) }
    var notes by remember { mutableStateOf("") }

    var showSuccessMessage by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showRemoveTimeDialog by remember { mutableStateOf(-1) }
    var showCancelDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Image(
            painter = painterResource(id = R.drawable.deco_stars),
            contentDescription = "Decorative stars",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(300.dp)
                .padding(16.dp),
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (showSuccessMessage) Modifier.blur(8.dp) else Modifier)
        ) {
            // -----------------------------------------------------------------
            // HEADER
            // -----------------------------------------------------------------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        spotColor = Color.Black,
                        ambientColor = Color.Black,
                        clip = false
                    )
                    .background(Color(0xFFFFFDF4))
                    .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 40.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    onClick = { showCancelDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 16.sp,
                        fontFamily = AddSFPro,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xff333333),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                //  medicación
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* clickeable*/ }
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (medicationName.isEmpty()) {
                            Text(
                                text = "enter medication name",
                                fontSize = 24.sp,
                                fontFamily = AddSFPro,
                                fontWeight = FontWeight.Normal,
                                color = MedicationInputColor.copy(alpha = 0.6f)
                            )
                        } else {
                            Text(
                                text = medicationName,
                                fontSize = 24.sp,
                                fontFamily = AddSFPro,
                                fontWeight = FontWeight.Normal,
                                color = MedicationInputColor
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Box(
                            modifier = Modifier
                                .width(250.dp)
                                .height(1.5.dp)
                                .background(MedicationInputColor)
                        )
                    }

                    BasicTextField(
                        value = medicationName,
                        onValueChange = { medicationName = it },
                        modifier = Modifier
                            .width(250.dp)
                            .height(35.dp)
                            .alpha(0.01f),
                        textStyle = TextStyle(
                            fontSize = 24.sp,
                            fontFamily = AddSFPro,
                            fontWeight = FontWeight.Normal,
                            color = MedicationInputColor,
                            textAlign = TextAlign.Center
                        ),
                        singleLine = true
                    )
                }
            }

            // -----------------------------------------------------------------
            //
            // -----------------------------------------------------------------
            when (currentStep) {
                1 -> AddMedicationFirstStep(
                    medicationName = medicationName,
                    times = times,
                    activeTimeIndex = activeTimeIndex,
                    selectedDate = selectedDate,
                    onMedicationNameChange = { medicationName = it },
                    onTimesChange = { newTimes ->
                        times.clear()
                        times.addAll(newTimes)
                    },
                    onActiveTimeIndexChange = { activeTimeIndex = it },
                    onDateChange = { selectedDate = it },
                    onContinue = {
                        if (medicationName.isNotEmpty() && times.isNotEmpty()) {
                            currentStep = 2
                        }
                    },
                    onShowTimePicker = { showTimePicker = true },
                    onShowRemoveTimeDialog = { showRemoveTimeDialog = it },
                    modifier = Modifier.weight(1f)
                )

                2 -> AddMedicationSecondStep(
                    medicationName = medicationName,
                    repeatEnabled = repeatEnabled,
                    repeatFrequency = repeatFrequency,
                    startDate = startDate,
                    endDate = endDate,
                    notes = notes,
                    onRepeatEnabledChange = { repeatEnabled = it },
                    onRepeatFrequencyChange = { repeatFrequency = it },
                    onStartDateChange = { startDate = it },
                    onEndDateChange = { endDate = it },
                    onNotesChange = { notes = it },
                    onSave = {
                        val ctx = context
                        scope.launch {
                            val success = saveMedicationToBackend(
                                context = ctx,
                                name = medicationName,
                                notes = notes,
                                startDate = startDate,
                                endDate = endDate,
                                times = times,
                                repeatEnabled = repeatEnabled,
                                repeatType = repeatFrequency.lowercase(),   // "daily", "weekly", "custom"
                                dayMask = buildDayMask(repeatFrequency, repeatEnabled)
                            )

                            if (success) {
                                showSuccessMessage = true
                            } else {
                                println("FAILED TO SAVE MEDICATION")
                            }
                        }
                    }
                )
            }

            // -----------------------------------------------------------------
            // Dialog
            // -----------------------------------------------------------------
            if (showSuccessMessage) {
                Dialog(onDismissRequest = { showSuccessMessage = false }) {
                    Card(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Medication Added:",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Text(
                                text = "You have successfully added a new medication",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF666666),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            Button(
                                onClick = {
                                    showSuccessMessage = false
                                    navController.popBackStack()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .border(
                                        width = 1.dp,
                                        color = Color.Black,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "OK",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }

            // -----------------------------------------------------------------
            // Time Picker
            // -----------------------------------------------------------------
            if (showTimePicker) {
                val timePickerState = rememberTimePickerState(
                    initialHour = 8,
                    initialMinute = 0,
                    is24Hour = true
                )

                AlertDialog(
                    onDismissRequest = { showTimePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val formattedTime = String.format(
                                    "%02d:%02d",
                                    timePickerState.hour,
                                    timePickerState.minute
                                )

                                if (activeTimeIndex < times.size) {
                                    times[activeTimeIndex] = formattedTime
                                }

                                showTimePicker = false
                            }
                        ) {
                            Text(
                                text = "OK",
                                fontFamily = AddSFPro,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFACBD6F),
                                fontSize = 20.sp,
                                modifier = Modifier.padding(horizontal = 42.dp)
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text(
                                text = "Cancel",
                                fontFamily = AddSFPro,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF595880),
                                fontSize = 20.sp
                            )
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Select Time",
                                fontSize = 20.sp,
                                fontFamily = AddSFPro,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF595880),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            TimePicker(
                                state = timePickerState,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    },
                    containerColor = Color(0xFFFFFDF4),
                    shape = RoundedCornerShape(16.dp)
                )
            }

            // -----------------------------------------------------------------
            //  horario
            // -----------------------------------------------------------------
            if (showRemoveTimeDialog >= 0) {
                AlertDialog(
                    onDismissRequest = { showRemoveTimeDialog = -1 },
                    confirmButton = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TextButton(
                                onClick = {
                                    val indexToRemove = showRemoveTimeDialog
                                    if (indexToRemove in times.indices) {
                                        times.removeAt(indexToRemove)
                                        if (activeTimeIndex >= times.size) {
                                            activeTimeIndex =
                                                (times.size - 1).coerceAtLeast(0)
                                        }
                                    }
                                    showRemoveTimeDialog = -1
                                },
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = Color.Black,
                                        shape = RoundedCornerShape(15.dp)
                                    )
                                    .background(Color.White, RoundedCornerShape(15.dp))
                            ) {
                                Text(
                                    text = "yes",
                                    fontFamily = AddMontserrat,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 20.sp
                                )
                            }

                            TextButton(
                                onClick = { showRemoveTimeDialog = -1 },
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = Color.Black,
                                        shape = RoundedCornerShape(15.dp)
                                    )
                                    .background(Color.White, RoundedCornerShape(15.dp))
                            ) {
                                Text(
                                    text = "cancel",
                                    fontFamily = AddMontserrat,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    },
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Remove Time?",
                                fontFamily = AddMontserrat,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    },
                    text = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Are you sure you want to remove this\nreminder time?",
                                textAlign = TextAlign.Center,
                                fontFamily = AddMontserrat,
                                fontWeight = FontWeight.Medium,
                                fontSize = 22.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                )
            }

            // -----------------------------------------------------------------
            // Diálogo
            // -----------------------------------------------------------------
            if (showCancelDialog) {
                AlertDialog(
                    onDismissRequest = { showCancelDialog = false },
                    confirmButton = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TextButton(
                                onClick = {
                                    showCancelDialog = false
                                    navController.popBackStack()
                                },
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = Color.Black,
                                        shape = RoundedCornerShape(15.dp)
                                    )
                                    .background(Color.White, RoundedCornerShape(15.dp))
                            ) {
                                Text(
                                    text = "yes",
                                    fontFamily = AddMontserrat,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 20.sp
                                )
                            }

                            TextButton(
                                onClick = { showCancelDialog = false },
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = Color.Black,
                                        shape = RoundedCornerShape(15.dp)
                                    )
                                    .background(Color.White, RoundedCornerShape(15.dp))
                            ) {
                                Text(
                                    text = "cancel",
                                    fontFamily = AddMontserrat,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    },
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Discard changes?",
                                fontFamily = AddMontserrat,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    },
                    text = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Are you sure you want to leave without \nsaving?",
                                textAlign = TextAlign.Center,
                                fontFamily = AddMontserrat,
                                fontWeight = FontWeight.Medium,
                                fontSize = 22.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
//
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMedicationFirstStep(
    medicationName: String,
    times: List<String>,
    activeTimeIndex: Int,
    selectedDate: String,
    onMedicationNameChange: (String) -> Unit,
    onTimesChange: (List<String>) -> Unit,
    onActiveTimeIndexChange: (Int) -> Unit,
    onDateChange: (String) -> Unit,
    onContinue: () -> Unit,
    onShowTimePicker: () -> Unit,
    onShowRemoveTimeDialog: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Set Reminder Time",
                fontSize = 23.sp,
                fontFamily = AddSFPro,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .padding(horizontal = 44.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 42.dp)
                    .border(1.dp, Color(0xffb7b7b7), RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Button(
                        onClick = onShowTimePicker,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFFFFF)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Text(
                            text = if (times.isNotEmpty() && activeTimeIndex < times.size)
                                times[activeTimeIndex] else "Select Time",
                            fontSize = 32.sp,
                            fontFamily = AddInter,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        times.forEachIndexed { index, time ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (index == activeTimeIndex) Color(0xFFACBD6F) else Color.White,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        2.dp,
                                        if (index == activeTimeIndex) Color.Black else Color(
                                            0xFFACBD6F
                                        ),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onActiveTimeIndexChange(index) }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = time,
                                    fontSize = 18.sp,
                                    fontFamily = AddInter,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black,
                                    modifier = Modifier.weight(1f)
                                )

                                if (times.size > 1) {
                                    Text(
                                        text = "✕",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        modifier = Modifier
                                            .clickable { onShowRemoveTimeDialog(index) }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (times.size < 4) {
                                onTimesChange(times + "10:00")
                                onActiveTimeIndexChange(times.size)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .border(2.dp, Color.Black, RoundedCornerShape(15.dp)),
                        enabled = times.size < 4,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "+ Add Time",
                            fontSize = 18.sp,
                            fontFamily = AddMontserrat,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "You can add up to 4 reminder times.",
                        fontSize = 16.sp,
                        fontFamily = AddSFPro,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFA1A1A1),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Date",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(10.dp))
                    .clickable { showDatePicker = true }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedDate,
                    fontSize = 17.sp,
                    color = Color.Black
                )
                Image(
                    painter = painterResource(id = R.drawable.date),
                    contentDescription = "Date icon",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            enabled = medicationName.isNotEmpty() && times.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = ContinueButtonColor,
                disabledContainerColor = ContinueButtonColor.copy(alpha = 0.5f)
            )
        ) {
            Text(
                text = "Continue",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }

    if (showDatePicker) {
        AddMedicationDatePickerDialog(
            currentDate = selectedDate,
            onDateSelected = { date ->
                onDateChange(date)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

// -----------------------------------------------------------------------------
// step 2
// -----------------------------------------------------------------------------

@Composable
private fun AddMedicationSecondStep(
    medicationName: String,
    repeatEnabled: Boolean,
    repeatFrequency: String,
    startDate: String,
    endDate: String,
    notes: String,
    onRepeatEnabledChange: (Boolean) -> Unit,
    onRepeatFrequencyChange: (String) -> Unit,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onSave: () -> Unit
) {
    val frequencyOptions = listOf("Daily", "Weekly", "Custom")
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var selectedDays by remember { mutableStateOf<List<String>>(emptyList()) }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Repeat",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(
                        checked = repeatEnabled,
                        onCheckedChange = onRepeatEnabledChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF34C759),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFE5E5EA)
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 200.dp, end = 16.dp)
                        .height(1.dp)
                        .background(Color(0xFFE5E5EA))
                )
            }

            if (repeatEnabled) {
                Spacer(modifier = Modifier.height(24.dp))

                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "How Often",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier
                                .width(150.dp)
                                .background(Color(0xFFD8D8D8), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = repeatFrequency,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF757575)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(1.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable {
                                            val currentIndex =
                                                frequencyOptions.indexOf(repeatFrequency)
                                            val newIndex =
                                                (currentIndex - 1 + frequencyOptions.size) % frequencyOptions.size
                                            onRepeatFrequencyChange(frequencyOptions[newIndex])
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "↓",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF757575)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable {
                                            val currentIndex =
                                                frequencyOptions.indexOf(repeatFrequency)
                                            val newIndex =
                                                (currentIndex + 1) % frequencyOptions.size
                                            onRepeatFrequencyChange(frequencyOptions[newIndex])
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "↑",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF757575)
                                    )
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 200.dp, end = 16.dp)
                            .height(1.dp)
                            .background(Color(0xFFE5E5EA))
                    )
                }

                if (repeatFrequency == "Weekly") {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column {
                        AddMedicationWhichDaySection(
                            selectedDays = selectedDays,
                            onDaysChanged = { selectedDays = it }
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 200.dp, end = 16.dp)
                                .height(1.dp)
                                .background(Color(0xFFE5E5EA))
                        )
                    }
                }

                if (repeatFrequency == "Custom") {
                    AddMedicationCustomDateSection(
                        startDate = startDate,
                        endDate = endDate,
                        onStartDateChange = onStartDateChange,
                        onEndDateChange = onEndDateChange,
                        showStartDatePicker = { showStartDatePicker = true },
                        showEndDatePicker = { showEndDatePicker = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (!repeatEnabled) 24.dp else 0.dp))

            Text(
                text = "Notes (Optional)",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp),
                placeholder = {
                    Text(
                        "(e.g. \"Take with water\", \"Don't take with dairy\", etc.)",
                        color = Color(0xFF8E8E93),
                        fontSize = 15.sp
                    )
                },
                maxLines = 6,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF8E8E93),
                    unfocusedBorderColor = Color(0xFF8E8E93),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            )
        }

        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(50.dp)
                .border(2.dp, Color(0xFFF16F33), RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFCE2A9),
                contentColor = Color(0xFFF16F33)
            )
        ) {
            Text(
                text = "Save",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFF16F33)
            )
        }
    }

    if (showStartDatePicker) {
        AddMedicationDatePickerDialog(
            currentDate = startDate,
            onDateSelected = { date ->
                onStartDateChange(date)
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }

    if (showEndDatePicker) {
        AddMedicationDatePickerDialog(
            currentDate = endDate,
            onDateSelected = { date ->
                onEndDateChange(date)
                showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false }
        )
    }
}

// -----------------------------------------------------------------------------
// days of the week
// -----------------------------------------------------------------------------

@Composable
private fun AddMedicationWhichDaySection(
    selectedDays: List<String>,
    onDaysChanged: (List<String>) -> Unit
) {
    val days = listOf(
        "M" to "Monday",
        "T" to "Tuesday",
        "W" to "Wednesday",
        "T" to "Thursday",
        "F" to "Friday",
        "S" to "Saturday",
        "S" to "Sunday"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Which Day",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(end = 16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                days.forEach { (abbreviation, fullName) ->
                    val isSelected = selectedDays.contains(fullName)

                    Text(
                        text = "[$abbreviation]",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Normal,
                        color = if (isSelected) Color(0xFF4A86E8) else Color.Black,
                        modifier = Modifier.clickable {
                            val isCurrentlySelected = selectedDays.contains(fullName)
                            val newSelectedDays = if (isCurrentlySelected) {
                                selectedDays - fullName
                            } else {
                                selectedDays + fullName
                            }
                            onDaysChanged(newSelectedDays)
                        }
                    )
                }
            }
        }

        if (selectedDays.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Selected: ${selectedDays.joinToString(", ")}",
                fontSize = 14.sp,
                color = Color(0xFF666666),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// -----------------------------------------------------------------------------
// custom dates
// -----------------------------------------------------------------------------

@Composable
private fun AddMedicationCustomDateSection(
    startDate: String,
    endDate: String,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    showStartDatePicker: () -> Unit,
    showEndDatePicker: () -> Unit
) {
    Column {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Start Date",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier
                        .clickable { showStartDatePicker() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = startDate,
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Normal
                    )
                    Image(
                        painter = painterResource(id = R.drawable.date),
                        contentDescription = "Calendar icon",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 200.dp, end = 16.dp)
                    .height(1.dp)
                    .background(Color(0xFFE5E5EA))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "End Date",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier
                        .clickable { showEndDatePicker() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = endDate,
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Normal
                    )
                    Image(
                        painter = painterResource(id = R.drawable.date),
                        contentDescription = "Calendar icon",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 200.dp, end = 16.dp)
                    .height(1.dp)
                    .background(Color(0xFFE5E5EA))
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// -----------------------------------------------------------------------------
// DatePicker dialog
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMedicationDatePickerDialog(
    currentDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                    val date = formatter.format(Date(millis))
                    onDateSelected(date)
                }
            }) {
                Text("OK", color = CreamColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CreamColor)
            }
        },
        text = {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = CreamColor,
                    todayContentColor = CreamColor,
                    todayDateBorderColor = CreamColor
                )
            )
        }
    )
}
