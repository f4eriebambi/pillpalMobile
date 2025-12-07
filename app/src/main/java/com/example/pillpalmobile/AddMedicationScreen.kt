package com.example.pillpalmobile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pillpalmobile.data.AuthStore
import com.example.pillpalmobile.network.AddMedicationRequest
import com.example.pillpalmobile.network.RetrofitClient
import com.example.pillpalmobile.network.ScheduleRequest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // medication name
    var name by remember { mutableStateOf("") }
    var showCancelDialog by remember { mutableStateOf(false) }

    // time picker section
    var times by remember { mutableStateOf(mutableStateListOf("10:00")) }
    var activeTimeIndex by remember { mutableStateOf(0) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showRemoveTimeDialog by remember { mutableStateOf(-1) }

    // repeat section
    var repeatEnabled by remember { mutableStateOf(false) }
    var howOften by remember { mutableStateOf("Daily") }
    val selectedDays = remember { mutableStateListOf(false, false, false, false, false, false, false) }
    var startDate by remember { mutableStateOf("Dec 7, 2025") }
    var endDate by remember { mutableStateOf("Dec 21, 2025") }
    var showHowOftenDropdown by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val startDatePickerState = rememberDatePickerState()
    val endDatePickerState = rememberDatePickerState()
    var startDateValue by remember { mutableStateOf<Long?>(null) }
    var endDateValue by remember { mutableStateOf<Long?>(null) }

    // date section (REPEAT OFF)
    var medicationDate by remember { mutableStateOf(SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).format(Date())) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // notes
    var notes by remember { mutableStateOf("") }

    // save dialogs and error handling
    var showSaveConfirmDialog by remember { mutableStateOf(false) }
    var showSaveSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // validation - name must not be empty
    val canSave = name.isNotBlank()

    // function to save medication to backend
    fun saveMedicationToBackend() {
        scope.launch {
            try {
                val token = AuthStore.getToken(context)
                if (token == null) {
                    errorMessage = "Not authenticated. Please log in again."
                    showErrorDialog = true
                    return@launch
                }

                // prepare day mask for weekly repeat
                val dayMask = if (howOften == "Weekly") {
                    selectedDays.joinToString("") { if (it) "1" else "0" }
                } else null

                // convert dates to yyyy-MM-dd format for backend
                val customStart = if (howOften == "Custom" && startDateValue != null) {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(startDateValue!!))
                } else null

                val customEnd = if (howOften == "Custom" && endDateValue != null) {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(endDateValue!!))
                } else null

                val scheduleRequest = ScheduleRequest(
                    repeat_type = when {
                        !repeatEnabled -> "once"
                        howOften == "Daily" -> "daily"
                        howOften == "Weekly" -> "weekly"
                        howOften == "Custom" -> "custom"
                        else -> "daily"
                    },
                    day_mask = dayMask,
                    times = times.toList(),
                    custom_start = customStart,
                    custom_end = customEnd
                )

                val request = AddMedicationRequest(
                    name = name,
                    notes = notes.ifBlank { null },
                    schedule = scheduleRequest
                )

                val response = RetrofitClient.medicationService.addMedication(
                    token = "Bearer $token",
                    medication = request
                )

                if (response.isSuccessful) {
                    showSaveSuccessDialog = true
                } else {
                    errorMessage = "Failed to add medication: ${response.code()}"
                    showErrorDialog = true
                }

            } catch (e: Exception) {
                errorMessage = "Network error: ${e.message}"
                showErrorDialog = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(R.drawable.deco_stars),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.LightGray),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(350.dp)
                .offset(x = -(20).dp, y = 475.dp)
        )

        // main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 300.dp, bottom = 80.dp)
        ) {
            TimePickerSection(
                times = times,
                activeTimeIndex = activeTimeIndex,
                onActiveTimeIndexChange = { activeTimeIndex = it },
                onShowTimePicker = { showTimePicker = true },
                onAddTime = {
                    if (times.size < 4) {
                        times.add("10:00")
                        activeTimeIndex = times.size - 1
                    }
                },
                onRemoveTime = { showRemoveTimeDialog = it }
            )

            TimePickerModal(
                showTimePicker = showTimePicker,
                onDismiss = { showTimePicker = false },
                onConfirm = { hour, minute ->
                    val formattedTime = String.format("%02d:%02d", hour, minute)
                    if (activeTimeIndex < times.size) {
                        times[activeTimeIndex] = formattedTime
                    }
                    showTimePicker = false
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            RepeatSection(
                repeatEnabled = repeatEnabled,
                onRepeatEnabledChange = { repeatEnabled = it },
                howOften = howOften,
                showHowOftenDropdown = showHowOftenDropdown,
                onShowHowOftenDropdown = { showHowOftenDropdown = it },
                onHowOftenChange = { newValue ->
                    howOften = newValue
                    showHowOftenDropdown = false
                    if (newValue != "Weekly") selectedDays.clear()
                },
                selectedDays = selectedDays,
                startDate = startDate,
                endDate = endDate,
                onShowStartDatePicker = { showStartDatePicker = true },
                onShowEndDatePicker = { showEndDatePicker = true },
                startDateValue = startDateValue,
                endDateValue = endDateValue
            )

            CustomDatePickers(
                showStartDatePicker = showStartDatePicker,
                showEndDatePicker = showEndDatePicker,
                startDatePickerState = startDatePickerState,
                endDatePickerState = endDatePickerState,
                onDismissStartDatePicker = { showStartDatePicker = false },
                onDismissEndDatePicker = { showEndDatePicker = false },
                onStartDateConfirm = { millis ->
                    startDateValue = millis
                    val df = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                    startDate = df.format(Date(millis))
                    if (endDateValue != null && millis > endDateValue!!) {
                        endDateValue = millis
                        endDate = df.format(Date(millis))
                    }
                    showStartDatePicker = false
                },
                onEndDateConfirm = { millis ->
                    endDateValue = millis
                    val df = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                    endDate = df.format(Date(millis))
                    if (startDateValue != null && millis < startDateValue!!) {
                        startDateValue = millis
                        startDate = df.format(Date(millis))
                    }
                    showEndDatePicker = false
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!repeatEnabled) {
                DateSection(
                    medicationDate = medicationDate,
                    onShowDatePicker = { showDatePicker = true }
                )

                DatePickerModal(
                    showDatePicker = showDatePicker,
                    datePickerState = datePickerState,
                    onDismiss = { showDatePicker = false },
                    onConfirm = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
                            medicationDate = dateFormat.format(Date(millis))
                        }
                        showDatePicker = false
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            NotesSection(
                notes = notes,
                onNotesChange = { notes = it }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // sticky header at top
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        ) {
            AddHeaderSection(
                name = name,
                onNameChange = { name = it },
                onCancelClick = { showCancelDialog = true }
            )
        }

        // sticky save button at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            AddSaveButton(
                enabled = canSave,
                onClick = { showSaveConfirmDialog = true }
            )
        }

        // cancel dialog
        AddCancelDialog(
            showDialog = showCancelDialog,
            onDismiss = { showCancelDialog = false },
            onConfirm = {
                showCancelDialog = false
                onNavigateBack()
            }
        )

        // remove time dialog
        AddRemoveTimeDialog(
            showDialog = showRemoveTimeDialog >= 0,
            onDismiss = { showRemoveTimeDialog = -1 },
            onConfirm = {
                val indexToRemove = showRemoveTimeDialog
                times.removeAt(indexToRemove)
                if (activeTimeIndex >= times.size) {
                    activeTimeIndex = times.size - 1
                }
                showRemoveTimeDialog = -1
            }
        )

        // save confirmation dialog
        AddSaveConfirmDialog(
            showDialog = showSaveConfirmDialog,
            onDismiss = { showSaveConfirmDialog = false },
            onConfirm = {
                showSaveConfirmDialog = false
                saveMedicationToBackend()
            }
        )

        // save success dialog
        AddSaveSuccessDialog(
            showDialog = showSaveSuccessDialog,
            onDismiss = { showSaveSuccessDialog = false },
            onConfirm = {
                showSaveSuccessDialog = false
                onNavigateBack()
            }
        )

        // error dialog
        AddErrorDialog(
            showDialog = showErrorDialog,
            errorMessage = errorMessage,
            onDismiss = { showErrorDialog = false }
        )
    }
}

@Composable
fun AddHeaderSection(
    name: String,
    onNameChange: (String) -> Unit,
    onCancelClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                spotColor = Color.Black,
                ambientColor = Color.Black,
                clip = false
            )
            .background(Color(0xFFFFFDF4))
            .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 60.dp)
    ) {
        Spacer(modifier = Modifier.height(22.dp))

        Surface(
            onClick = onCancelClick,
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 6.dp,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text(
                text = "Cancel",
                fontSize = 19.sp,
                fontFamily = SFPro,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xff333333),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = name,
                onValueChange = onNameChange,
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = SFPro,
                    fontWeight = FontWeight.Normal,
                    fontSize = 28.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xff595880)
                ),
                modifier = Modifier
                    .width(275.dp)
                    .wrapContentHeight(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color(0xFF595880)
                ),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "medication name",
                        fontFamily = SFPro,
                        fontWeight = FontWeight.Normal,
                        fontSize = 28.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .width(275.dp)
                    .height(2.dp)
                    .background(Color(0xFF595880))
            )
        }
    }
}

@Composable
fun AddSaveButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 38.dp)
            .height(56.dp)
            .border(2.5.dp, Color(0xFFFCE2A9), RoundedCornerShape(15.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(15.dp)
    ) {
        Text(
            text = "Add Medication",
            fontSize = 24.sp,
            fontFamily = Montserrat,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFF16F33)
        )
    }
}

@Composable
fun AddCancelDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(
                        onClick = onConfirm,
                        modifier = Modifier
                            .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                            .background(Color.White, RoundedCornerShape(15.dp))
                    ) {
                        Text(
                            text = "yes",
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    }

                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                            .background(Color.White, RoundedCornerShape(15.dp))
                    ) {
                        Text(
                            text = "cancel",
                            fontFamily = Montserrat,
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
                        text = "Discard medication?",
                        fontFamily = Montserrat,
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
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }
}

@Composable
fun AddRemoveTimeDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(
                        onClick = onConfirm,
                        modifier = Modifier
                            .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                            .background(Color.White, RoundedCornerShape(15.dp))
                    ) {
                        Text(
                            text = "yes",
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    }

                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                            .background(Color.White, RoundedCornerShape(15.dp))
                    ) {
                        Text(
                            text = "cancel",
                            fontFamily = Montserrat,
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
                        fontFamily = Montserrat,
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
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }
}

@Composable
fun AddSaveConfirmDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(
                        onClick = onConfirm,
                        modifier = Modifier
                            .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                            .background(Color.White, RoundedCornerShape(15.dp))
                    ) {
                        Text(
                            text = "yes",
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    }

                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                            .background(Color.White, RoundedCornerShape(15.dp))
                    ) {
                        Text(
                            text = "cancel",
                            fontFamily = Montserrat,
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
                        text = "Add medication?",
                        fontFamily = Montserrat,
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
                        text = "Are you sure you want to add \nthis medication?",
                        textAlign = TextAlign.Center,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }
}

@Composable
fun AddSaveSuccessDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = onConfirm,
                        modifier = Modifier
                            .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                            .background(Color.White, RoundedCornerShape(15.dp))
                    ) {
                        Text(
                            text = "ok",
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    }
                }
            },
            title = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Text(
                        text = "Medication Added!",
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Text(
                        text = "Your medication has been\nsuccessfully added.",
                        textAlign = TextAlign.Center,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp
                    )
                }
            }
        )
    }
}

@Composable
fun AddErrorDialog(
    showDialog: Boolean,
    errorMessage: String,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                            .background(Color.White, RoundedCornerShape(15.dp))
                    ) {
                        Text(
                            text = "ok",
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    }
                }
            },
            title = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Text(
                        text = "Error",
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Red
                    )
                }
            },
            text = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Text(
                        text = errorMessage,
                        textAlign = TextAlign.Center,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    )
                }
            }
        )
    }
}