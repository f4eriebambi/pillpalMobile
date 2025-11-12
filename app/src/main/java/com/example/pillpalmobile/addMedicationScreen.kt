package com.example.pillpalmobile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.pillpalmobile.data.DataSource
import com.example.pillpalmobile.model.Medication
import java.text.SimpleDateFormat
import java.util.*

// Old Cream color for general UI elements (backgrounds, switches)
val CreamColor = Color(0xFFFFC46E)
// New color for the Continue/Save button
val ContinueButtonColor = Color(0xFFF16F33)
// Color for medication input and underline (#595880)
val MedicationInputColor = Color(0xFF595880)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationScreen(navController: NavHostController) {
    var currentStep by remember { mutableStateOf(1) }

    var medicationName by remember { mutableStateOf("") }
    var selectedTimes by remember { mutableStateOf(listOf<String>()) }
    var selectedDate by remember { mutableStateOf(getCurrentDate()) }
    var repeatEnabled by remember { mutableStateOf(false) }
    var repeatFrequency by remember { mutableStateOf("Daily") }
    var startDate by remember { mutableStateOf(getCurrentDate()) }
    var endDate by remember { mutableStateOf(getCurrentDate()) }
    var notes by remember { mutableStateOf("") }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    var editingTimeIndex by remember { mutableStateOf<Int?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Decorative stars image in bottom right (300.dp size)
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
            // --- START: LAYERED HEADER AND INPUT SECTION WITH IMAGE BACKGROUND ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // 1. Background Image (header_rec.png)
                Image(
                    painter = painterResource(id = R.drawable.header_rec),
                    contentDescription = "Header background rectangle",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )

                // 2. Content (Layered on top of the image)
                Column(modifier = Modifier.fillMaxSize()) {
                    // Status Bar / Header (Cancel button ONLY)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.cancel),
                            contentDescription = "Cancel Button",
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .size(130.dp, 80.dp)
                                .clickable { navController.popBackStack() },
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Medication Name Input (Underlined BasicTextField)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        if (medicationName.isEmpty()) {
                            Text(
                                text = "enter medication name",
                                color = MedicationInputColor.copy(alpha = 0.8f),
                                fontSize = 19.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 15.dp)
                                    .align(Alignment.Center)
                            )
                        }
                        BasicTextField(
                            value = medicationName,
                            onValueChange = { medicationName = it },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(
                                color = Color.Black,
                                fontSize = 17.sp,
                                textAlign = TextAlign.Center
                            ),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    innerTextField()
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(MedicationInputColor)
                                    )
                                }
                            }
                        )
                    }
                }
            }
            // --- END: LAYERED HEADER AND INPUT SECTION ---

            when (currentStep) {
                1 -> FirstScreen(
                    medicationName = medicationName,
                    selectedTimes = selectedTimes,
                    selectedDate = selectedDate,
                    onMedicationNameChange = { medicationName = it },
                    onTimesChange = { selectedTimes = it },
                    onDateChange = { selectedDate = it },
                    onContinue = {
                        if (medicationName.isNotEmpty() && selectedTimes.isNotEmpty()) {
                            currentStep = 2
                        }
                    },
                    onAddTimeClicked = {
                        editingTimeIndex = null
                        showTimePickerDialog = true
                    },
                    onEditTime = { index ->
                        editingTimeIndex = index
                        showTimePickerDialog = true
                    },
                    modifier = Modifier.weight(1f)
                )
                2 -> SecondScreen(
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
                        saveMedication(
                            medicationName = medicationName,
                            selectedTimes = selectedTimes,
                            selectedDate = selectedDate,
                            repeatEnabled = repeatEnabled,
                            repeatFrequency = repeatFrequency,
                            startDate = startDate,
                            endDate = endDate,
                            notes = notes,
                            navController = navController
                        )
                        showSuccessMessage = true
                    }
                )
            }
        }

        // Success message overlay
        if (showSuccessMessage) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth(0.85f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "✓",
                            fontSize = 48.sp,
                            color = Color(0xFF34C759),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            text = "Medication has been added successfully",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                showSuccessMessage = false
                navController.popBackStack()
            }
        }
    }

    // Time Picker Dialog
    if (showTimePickerDialog) {
        TimePickerDialog(
            onDismiss = { showTimePickerDialog = false },
            onTimeSelected = { time ->
                editingTimeIndex?.let { index ->
                    selectedTimes = selectedTimes.toMutableList().apply {
                        set(index, time)
                    }
                } ?: run {
                    if (selectedTimes.size < 4 && !selectedTimes.contains(time)) {
                        selectedTimes = selectedTimes + time
                    }
                }
                showTimePickerDialog = false
            }
        )
    }
}

// -----------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstScreen(
    medicationName: String,
    selectedTimes: List<String>,
    selectedDate: String,
    onMedicationNameChange: (String) -> Unit,
    onTimesChange: (List<String>) -> Unit,
    onDateChange: (String) -> Unit,
    onContinue: () -> Unit,
    onAddTimeClicked: () -> Unit,
    onEditTime: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Set Reminder Time Section
            SetReminderTimeSection(
                selectedTimes = selectedTimes,
                onTimeSelected = { time ->
                    if (selectedTimes.size < 4 && !selectedTimes.contains(time)) {
                        onTimesChange(selectedTimes + time)
                    }
                },
                onTimeRemoved = { index ->
                    onTimesChange(selectedTimes.filterIndexed { i, _ -> i != index })
                },
                onEditTime = onEditTime,
                onAddTimeClicked = onAddTimeClicked
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Date Section
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

        // Continue Button
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            enabled = medicationName.isNotEmpty() && selectedTimes.isNotEmpty(),
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
        DatePickerDialog(
            currentDate = selectedDate,
            onDateSelected = { date ->
                onDateChange(date)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

// -----------------------------------------------------------------------------------

@Composable
fun SetReminderTimeSection(
    selectedTimes: List<String>,
    onTimeSelected: (String) -> Unit,
    onTimeRemoved: (Int) -> Unit,
    onEditTime: (Int) -> Unit,
    onAddTimeClicked: () -> Unit
) {
    // Generate time options from 00:00 to 23:00
    val allTimes = (0..23).map { hour ->
        String.format("%02d:00", hour)
    }

    // Display time (largest or first selected)
    val displayTime = selectedTimes.maxByOrNull { it } ?: "10:00"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Set Reminder Time",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Large display time
        Text(
            text = displayTime,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Scrollable time list
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allTimes.forEach { time ->
                    TimeOptionItem(
                        time = time,
                        isSelected = selectedTimes.contains(time),
                        onSingleClick = {
                            if (!selectedTimes.contains(time)) {
                                onTimeSelected(time)
                            }
                        },
                        onDoubleClick = {
                            onAddTimeClicked()
                        }
                    )
                }
            }

            // Scrollbar indicator
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
                    .align(Alignment.CenterEnd)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected times with X buttons
        selectedTimes.forEachIndexed { index, time ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = time,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .weight(1f)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = { onEditTime(index) }
                            )
                        }
                )

                Text(
                    text = "x",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onTimeRemoved(index) }
                        .padding(horizontal = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add Time button
        if (selectedTimes.size < 4) {
            Button(
                onClick = onAddTimeClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
            ) {
                Text(
                    text = "+ Add Time",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You can add up to 4 reminder times.",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TimeOptionItem(
    time: String,
    isSelected: Boolean,
    onSingleClick: () -> Unit,
    onDoubleClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) Color(0xFFF0F0F0) else Color.White,
                RoundedCornerShape(8.dp)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFF638097) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(8.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onSingleClick() },
                    onDoubleTap = { onDoubleClick() }
                )
            }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time,
            fontSize = 18.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    var hour by remember { mutableStateOf(10) }
    var minute by remember { mutableStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Enter Time",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Hour input
                    OutlinedTextField(
                        value = String.format("%02d", hour),
                        onValueChange = {
                            val newHour = it.toIntOrNull()
                            if (newHour != null && newHour in 0..23) {
                                hour = newHour
                            }
                        },
                        modifier = Modifier.width(80.dp),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        )
                    )

                    Text(
                        text = " : ",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Minute input
                    OutlinedTextField(
                        value = String.format("%02d", minute),
                        onValueChange = {
                            val newMinute = it.toIntOrNull()
                            if (newMinute != null && newMinute in 0..59) {
                                minute = newMinute
                            }
                        },
                        modifier = Modifier.width(80.dp),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val time = String.format("%02d:%02d", hour, minute)
                            onTimeSelected(time)
                        }
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------

@Composable
fun SecondScreen(
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
    val frequencyOptions = listOf("Daily", "Weekly", "Monthly")
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Medication Name Display
            Text(
                text = medicationName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Repeat Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(10.dp))
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

            if (repeatEnabled) {
                Spacer(modifier = Modifier.height(24.dp))

                // How Often Section
                Text(
                    text = "How Often",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    frequencyOptions.forEach { frequency ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(45.dp)
                                .background(
                                    color = if (frequency == repeatFrequency) CreamColor else Color.White,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (frequency == repeatFrequency) CreamColor else Color(0xFFE5E5EA),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { onRepeatFrequencyChange(frequency) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = frequency,
                                fontSize = 16.sp,
                                color = if (frequency == repeatFrequency) Color.White else Color.Black,
                                fontWeight = if (frequency == repeatFrequency) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Start Date
                Text(
                    text = "Start Date",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(10.dp))
                        .clickable { showStartDatePicker = true }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = startDate, fontSize = 17.sp, color = Color.Black)
                    Image(
                        painter = painterResource(id = R.drawable.date),
                        contentDescription = "Date icon",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // End Date
                Text(
                    text = "End Date",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(10.dp))
                        .clickable { showEndDatePicker = true }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = endDate, fontSize = 17.sp, color = Color.Black)
                    Image(
                        painter = painterResource(id = R.drawable.date),
                        contentDescription = "Date icon",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            Spacer(modifier = Modifier.height(if (!repeatEnabled) 24.dp else 0.dp))

            // Notes Section
            Text(
                text = "Notes (Optional)",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                modifier = Modifier.fillMaxWidth().height(140.dp),
                placeholder = {
                    Text(
                        "(e.g. \"Take with water\", \"Don't take with dairy\", etc.)",
                        color = Color(0xFF8E8E93),
                        fontSize = 15.sp
                    )
                },
                maxLines = 6,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE5E5EA),
                    unfocusedBorderColor = Color(0xFFE5E5EA),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            )
        }

        // Save Button
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().padding(20.dp).height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ContinueButtonColor)
        ) {
            Text(
                text = "Save",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }

    if (showStartDatePicker) {
        DatePickerDialog(
            currentDate = startDate,
            onDateSelected = { date ->
                onStartDateChange(date)
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            currentDate = endDate,
            onDateSelected = { date ->
                onEndDateChange(date)
                showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false }
        )
    }
}

// -----------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
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

// -----------------------------------------------------------------------------------

private fun saveMedication(
    medicationName: String,
    selectedTimes: List<String>,
    selectedDate: String,
    repeatEnabled: Boolean,
    repeatFrequency: String,
    startDate: String,
    endDate: String,
    notes: String,
    navController: NavHostController
) {
    val newId = DataSource.medications.size + 1

    val timesText = selectedTimes.joinToString(", ")

    val frequencyText = if (repeatEnabled) {
        "$repeatFrequency from $startDate to $endDate"
    } else {
        "One time on $selectedDate"
    }

    val newMedication = Medication(
        id = newId,
        name = medicationName,
        dosage = "",
        frequency = "$timesText - $frequencyText",
        notes = notes
    )

    DataSource.medications.add(newMedication)
}

// -----------------------------------------------------------------------------------

private fun getCurrentDate(): String {
    val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return formatter.format(Date())
}