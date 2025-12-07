package com.example.pillpalmobile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
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
import com.example.pillpalmobile.model.Medication
import com.example.pillpalmobile.model.UpdateMedicationRequest
import com.example.pillpalmobile.network.RetrofitClient
import com.example.pillpalmobile.network.ScheduleRequest

import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// https://developer.android.com/develop/ui/compose/components/time-pickers
// https://developer.android.com/develop/ui/compose/components/time-pickers-dialogs#:~:text=The%20AdvancedTimePickerExample%20composable%20creates%20a,between%20dial%20and%20input%20modes.
// https://www.youtube.com/watch?v=Ndp6RyDwPYs
// https://developer.android.com/reference/kotlin/androidx/compose/runtime/package-summary#mutableStateListOf()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMedicationScreen(
    medication: Medication? = null,
    onNavigateBack: () -> Unit = {},
    onDelete: (Int) -> Unit = {},
    onSave: (Medication) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val scrollState = rememberScrollState()
    // header section
    var name by remember { mutableStateOf(medication?.name ?: "") }
    var medicationId by remember { mutableStateOf(medication?.id ?: 0) }
    var showCancelDialog by remember { mutableStateOf(false) }
    // time picker section
    var times by remember { mutableStateOf(mutableStateListOf("10:00", "15:00")) }
    var activeTimeIndex by remember { mutableStateOf(0) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDeleteMedicationDialog by remember { mutableStateOf(false) }
    var showRemoveTimeDialog by remember { mutableStateOf(-1) }
    // repeat section + selection of how often
    var repeatEnabled by remember { mutableStateOf(false) }
    var howOften by remember { mutableStateOf("Daily") }
    val selectedDays =
        remember { mutableStateListOf(false, false, false, false, false, false, false) }
    var startDate by remember { mutableStateOf("Nov 7, 2025") }
    var endDate by remember { mutableStateOf("Nov 21, 2025") }
    var showHowOftenDropdown by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val startDatePickerState = rememberDatePickerState()
    val endDatePickerState = rememberDatePickerState()
    var startDateValue by remember { mutableStateOf<Long?>(null) }
    var endDateValue by remember { mutableStateOf<Long?>(null) }
    // date section ( REPEAT OFF )
    var medicationDate by remember {
        mutableStateOf(
            SimpleDateFormat(
                "EEE, MMM d, yyyy",
                Locale.getDefault()
            ).format(Date())
        )
    }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    // notes
    var notes by remember { mutableStateOf("") }
    // save
    var showSaveConfirmDialog by remember { mutableStateOf(false) }
    var showSaveSuccessDialog by remember { mutableStateOf(false) }
    // check if any field is changed
    val hasChanges =
        name != medication?.name ||
                times.toList() != medication?.reminderTimes ||
                repeatEnabled != medication?.repeatEnabled ||
                howOften != medication?.repeatFrequency ||
                selectedDays.toList() != medication?.repeatDays ||
                startDateValue != medication?.repeatStartDate ||
                endDateValue != medication?.repeatEndDate ||
                medicationDate != medication?.medicationDate ||
                notes != medication?.notes

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
                .padding(top = 290.dp, bottom = 80.dp) // space for header and save button
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
                            val dateFormat =
                                SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
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
            HeaderSection(
                name = name,
                onNameChange = { name = it },
                onCancelClick = { showCancelDialog = true },
                onDeleteClick = { showDeleteMedicationDialog = true }
            )
        }

        // sticky save button at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            SaveButton(
                hasChanges = hasChanges,
                onClick = { showSaveConfirmDialog = true }
            )
        }

        // all modals
        CancelDialog(
            showDialog = showCancelDialog,
            onDismiss = { showCancelDialog = false },
            onConfirm = {
                showCancelDialog = false
                onNavigateBack()
            }
        )

        DeleteMedicationDialog(
            showDialog = showDeleteMedicationDialog,
            onDismiss = { showDeleteMedicationDialog = false },
            onConfirm = {
                showDeleteMedicationDialog = false
                onDelete(medicationId)
            }
        )

        RemoveTimeDialog(
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

        SaveConfirmDialog(
            showDialog = showSaveConfirmDialog,
            onDismiss = { showSaveConfirmDialog = false },
            onConfirm = {
                showSaveConfirmDialog = false

                scope.launch {

                    val token = AuthStore.getToken(context) ?: return@launch

                    val request = UpdateMedicationRequest(
                        name = name,
                        notes = notes.ifBlank { null },
                        schedule = ScheduleRequest(
                            repeat_type = when {
                                !repeatEnabled -> "once"
                                howOften == "Daily" -> "daily"
                                howOften == "Weekly" -> "weekly"
                                howOften == "Custom" -> "custom"
                                else -> "daily"
                            },
                            day_mask = if (howOften == "Weekly") {
                                selectedDays.joinToString("") { if (it) "1" else "0" }
                            } else null,
                            times = times.toList(),
                            custom_start = if (howOften == "Custom") formatMillis(startDateValue) else null,
                            custom_end = if (howOften == "Custom") formatMillis(endDateValue) else null
                        )
                    )

                    val response = RetrofitClient.medicationService.updateMedication(
                        token = "Bearer $token",
                        medId = medicationId,
                        body = request
                    )

                    if (response.isSuccessful) {
                        showSaveSuccessDialog = true
                    } else {
                        // TODO: Show error dialog
                    }
                }
            }

        )

        SaveSuccessDialog(
            showDialog = showSaveSuccessDialog,
            onDismiss = { showSaveSuccessDialog = false },
            onConfirm = {
                showSaveSuccessDialog = false
                onNavigateBack()
            }
        )
    }
}


// header section
@Composable
fun HeaderSection(
    name: String,
    onNameChange: (String) -> Unit,
    onCancelClick: () -> Unit,
    onDeleteClick: () -> Unit
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

        // cancel button
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

        Spacer(modifier = Modifier.height(38.dp))

        // med name with delete icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete medication",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onDeleteClick() },
                tint = Color(0xFF4A4A4A)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // now editable med name
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

                // border line
                Box(
                    modifier = Modifier
                        .width(275.dp)
                        .height(2.dp)
                        .background(Color(0xFF595880))
                )
            }
        }
    }
}

// time picker card
@Composable
fun TimePickerSection(
    times: MutableList<String>,
    activeTimeIndex: Int,
    onActiveTimeIndexChange: (Int) -> Unit,
    onShowTimePicker: () -> Unit,
    onAddTime: () -> Unit,
    onRemoveTime: (Int) -> Unit
) {
    Text(
        text = "Edit Reminder Time",
        fontSize = 23.sp,
        fontFamily = SFPro,
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
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // big time display button
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
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // lsit of times
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
                                if (index == activeTimeIndex) Color.Black else Color(0xFFACBD6F),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onActiveTimeIndexChange(index) }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // time text
                        Text(
                            text = time,
                            fontSize = 18.sp,
                            fontFamily = Inter,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )

                        // show X button if more than 1 time
                        if (times.size > 1) {
                            IconButton(
                                onClick = { onRemoveTime(index) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .align(Alignment.CenterVertically)
                            ) {
                                Text(
                                    text = "✕",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // add time to list button
            Button(
                onClick = onAddTime,
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
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // limit reminder
            Text(
                text = "You can add up to 4 reminder times.",
                fontSize = 16.sp,
                fontFamily = SFPro,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFA1A1A1),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

// time picker modal
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModal(
    showTimePicker: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = 8,
            initialMinute = 0,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm(timePickerState.hour, timePickerState.minute)
                    }
                ) {
                    Text(
                        text = "OK",
                        fontFamily = SFPro,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFACBD6F),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(horizontal = 42.dp)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Cancel",
                        fontFamily = SFPro,
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
                        fontFamily = SFPro,
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
}

// repeat toggle and options (daily, weekly, custom)
@Composable
fun RepeatSection(
    repeatEnabled: Boolean,
    onRepeatEnabledChange: (Boolean) -> Unit,
    howOften: String,
    showHowOftenDropdown: Boolean,
    onShowHowOftenDropdown: (Boolean) -> Unit,
    onHowOftenChange: (String) -> Unit,
    selectedDays: MutableList<Boolean>,
    startDate: String,
    endDate: String,
    onShowStartDatePicker: () -> Unit,
    onShowEndDatePicker: () -> Unit,
    startDateValue: Long?,
    endDateValue: Long?
) {
    Text(
        text = "Repeat",
        fontSize = 23.sp,
        fontFamily = SFPro,
        fontWeight = FontWeight.Medium,
        color = Color.Black,
        modifier = Modifier
            .background(Color.White)
            .padding(bottom = 12.dp)
            .padding(horizontal = 20.dp)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .border(0.5.dp, Color.Black, RoundedCornerShape(5.dp)),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // repeat toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Repeat",
                    fontSize = 20.sp,
                    fontFamily = SFPro,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Switch(
                    checked = repeatEnabled,
                    onCheckedChange = onRepeatEnabledChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF34C759),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFE0E0E0)
                    )
                )
            }

            // show how often section of when to repeat when repeat ON
            if (repeatEnabled) {
                Spacer(modifier = Modifier.height(20.dp))

                // how often dropdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "How Often",
                        fontSize = 20.sp,
                        fontFamily = SFPro,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )

                    Box {
                        Button(
                            onClick = { onShowHowOftenDropdown(true) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF0F0F0)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp
                            )
                        ) {
                            Text(
                                text = howOften,
                                fontSize = 19.sp,
                                fontFamily = SFPro,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            Text(
                                text = " ⇅",
                                style = TextStyle(
                                    fontSize = 28.sp,
                                    fontFamily = SFPro,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black,
                                    baselineShift = BaselineShift(0.2f)
                                )
                            )
                        }

                        DropdownMenu(
                            expanded = showHowOftenDropdown,
                            onDismissRequest = { onShowHowOftenDropdown(false) },
                            modifier = Modifier.background(Color.White)
                        ) {
                            listOf("Daily", "Weekly", "Custom").forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            option,
                                            fontSize = 17.sp,
                                            fontFamily = SFPro,
                                            fontWeight = FontWeight.Medium
                                        )
                                    },
                                    onClick = { onHowOftenChange(option) }
                                )
                            }
                        }
                    }
                }

                // weekly (which days)
                if (howOften == "Weekly") {
                    Spacer(modifier = Modifier.height(20.dp))

                    Column {
                        Text(
                            text = "Which Day(s)",
                            fontSize = 18.sp,
                            fontFamily = SFPro,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val days = listOf("M","T","W","T","F","S","S")

                            days.forEachIndexed { i, day ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)                           // ⭐ equal-size buttons
                                        .height(42.dp)
                                        .background(
                                            if (selectedDays[i]) Color(0xFFACBD6F) else Color(0xFFF3F3F3),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedDays[i] = !selectedDays[i] },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        day,
                                        fontFamily = SFPro,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 18.sp,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }

                // custom (start + end dates)
                if (howOften == "Custom") {
                    Spacer(modifier = Modifier.height(20.dp))

                    // start date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Start Date",
                            fontSize = 18.sp,
                            fontFamily = SFPro,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )

                        Button(
                            onClick = onShowStartDatePicker,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF0F0F0)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = startDate,
                                fontSize = 18.sp,
                                fontFamily = SFPro,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // end date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "End Date",
                            fontSize = 18.sp,
                            fontFamily = SFPro,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )

                        Button(
                            onClick = onShowEndDatePicker,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF0F0F0)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = endDate,
                                fontSize = 18.sp,
                                fontFamily = SFPro,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black
                            )
                        }
                    }

                    // make sure time picked is valid
                    if (startDateValue != null && endDateValue != null &&
                        startDateValue!! > endDateValue!!
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Start date cannot be after end date.",
                            color = Color.Red,
                            fontFamily = SFPro,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// date pickers for custom repeat option
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickers(
    showStartDatePicker: Boolean,
    showEndDatePicker: Boolean,
    startDatePickerState: DatePickerState,
    endDatePickerState: DatePickerState,
    onDismissStartDatePicker: () -> Unit,
    onDismissEndDatePicker: () -> Unit,
    onStartDateConfirm: (Long) -> Unit,
    onEndDateConfirm: (Long) -> Unit
) {
    // date picker modal for custom repeat 1
    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = onDismissStartDatePicker,
            confirmButton = {
                TextButton(
                    onClick = {
                        startDatePickerState.selectedDateMillis?.let { millis ->
                            onStartDateConfirm(millis)
                        }
                    }
                ) {
                    Text(
                        "OK",
                        fontFamily = SFPro,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = Color(0xFFACBD6F)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissStartDatePicker) {
                    Text(
                        "Cancel",
                        fontFamily = SFPro,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = Color(0xFF595880)
                    )
                }
            }
        ) {
            DatePicker(state = startDatePickerState)
        }
    }

    // date picker modal for custom repeat 2
    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = onDismissEndDatePicker,
            confirmButton = {
                TextButton(
                    onClick = {
                        endDatePickerState.selectedDateMillis?.let { millis ->
                            onEndDateConfirm(millis)
                        }
                    }
                ) {
                    Text(
                        "OK",
                        fontFamily = SFPro,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = Color(0xFFACBD6F)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissEndDatePicker) {
                    Text(
                        "Cancel",
                        fontFamily = SFPro,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = Color(0xFF595880)
                    )
                }
            }
        ) {
            DatePicker(state = endDatePickerState)
        }
    }
}

// date section (shows when repeat is OFF)
@Composable
fun DateSection(
    medicationDate: String,
    onShowDatePicker: () -> Unit
) {
    Text(
        text = "Date",
        fontSize = 23.sp,
        fontFamily = SFPro,
        fontWeight = FontWeight.Medium,
        color = Color.Black,
        modifier = Modifier
            .padding(bottom = 12.dp)
            .padding(horizontal = 20.dp)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .border(0.5.dp, Color.Black, RoundedCornerShape(5.dp)),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onShowDatePicker() }
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = medicationDate,
                fontSize = 20.sp,
                fontFamily = SFPro,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )

            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Select date",
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF595880)
            )
        }
    }
}

// date picker modal for date section
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    showDatePicker: Boolean,
    datePickerState: DatePickerState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(
                        "OK",
                        fontFamily = SFPro,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = Color(0xFFACBD6F)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        "Cancel",
                        fontFamily = SFPro,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = Color(0xFF595880)
                    )
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// notes text field
@Composable
fun NotesSection(
    notes: String,
    onNotesChange: (String) -> Unit
) {
    Text(
        text = "Notes (Optional)",
        fontSize = 23.sp,
        fontFamily = SFPro,
        fontWeight = FontWeight.Medium,
        color = Color.Black,
        modifier = Modifier
            .background(Color.White)
            .padding(bottom = 12.dp)
            .padding(horizontal = 20.dp)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .border(1.dp, Color.Black),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            placeholder = {
                Text(
                    text = "(e.g. \"Take with water\", \"Don't take with dairy\", etc.)",
                    fontSize = 18.sp,
                    fontFamily = SFPro,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFA1A1A1)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            textStyle = TextStyle(
                fontSize = 18.sp,
                fontFamily = SFPro,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
        )
    }
}

// sticky save button at bottom
@Composable
fun SaveButton(
    hasChanges: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = hasChanges,
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
            text = "Save",
            fontSize = 24.sp,
            fontFamily = Montserrat,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFF16F33)
        )
    }
}

// cancel button modal
@Composable
fun CancelDialog(
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
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(15.dp)
                            )
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
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(15.dp)
                            )
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
                        text = "Discard changes?",
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

// delete med icon modal
@Composable
fun DeleteMedicationDialog(
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
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(15.dp)
                            )
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
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(15.dp)
                            )
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
                        text = "Delete Medication?",
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
                        text = "Are you sure you want to delete this\nmedication?",
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

// remove time X button modal
@Composable
fun RemoveTimeDialog(
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
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(15.dp)
                            )
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
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(15.dp)
                            )
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

// save changes confirmation modal
@Composable
fun SaveConfirmDialog(
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
                        text = "Save changes?",
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
                        text = "Are you sure you want to apply \nall changes to this medication?",
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

// save success modal
@Composable
fun SaveSuccessDialog(
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
                        text = "Medication Updated!",
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
                        text = "Your changes have been\nsuccessfully saved.",
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

private fun formatMillis(millis: Long?): String? {
    if (millis == null) return null
    val date = Date(millis)
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(date)
}
