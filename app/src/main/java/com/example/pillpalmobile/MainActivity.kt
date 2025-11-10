package com.example.pillpalmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pillpalmobile.data.DataSource
import com.example.pillpalmobile.model.Medication
import com.example.pillpalmobile.model.User
import com.example.pillpalmobile.ui.theme.PillPalMobileTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PillPalMobileTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    var showSplash by remember { mutableStateOf(true) }
    val navController = rememberNavController()

    if (showSplash) {
        SplashScreen {
            showSplash = false
        }
    } else {
        PillPalMobileTheme {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeScreen(navController = navController)
                }
                composable("add_medication") {
                    AddMedicationScreen(navController = navController)
                }
            }
        }
    }
}

// FIXED: Remove default parameter to avoid overload ambiguity
@Composable
fun AddMedicationScreen(navController: NavHostController) {
    var currentStep by remember { mutableStateOf(1) } // 1: Basic info, 2: Time & Repeat, 3: Review

    var medicationName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("08:00") }
    var repeatOption by remember { mutableStateOf("Daily") }
    var selectedDays by remember { mutableStateOf<List<String>>(emptyList()) }
    var startDate by remember { mutableStateOf(getCurrentDate()) }
    var endDate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        if (currentStep > 1) {
                            currentStep--
                        } else {
                            navController.popBackStack()
                        }
                    },
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = when (currentStep) {
                    1 -> "Add Medication"
                    2 -> "Set Reminder"
                    3 -> "Review"
                    else -> "Add Medication"
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        when (currentStep) {
            1 -> BasicInfoStep(
                medicationName = medicationName,
                dosage = dosage,
                onMedicationNameChange = { medicationName = it },
                onDosageChange = { dosage = it },
                onContinue = {
                    if (medicationName.isNotEmpty() && dosage.isNotEmpty()) {
                        currentStep = 2
                    }
                }
            )
            2 -> TimeAndRepeatStep(
                selectedTime = selectedTime,
                repeatOption = repeatOption,
                selectedDays = selectedDays,
                startDate = startDate,
                endDate = endDate,
                onTimeChange = { selectedTime = it },
                onRepeatOptionChange = { repeatOption = it },
                onDaysChange = { selectedDays = it },
                onStartDateChange = { startDate = it },
                onEndDateChange = { endDate = it },
                onContinue = { currentStep = 3 }
            )
            3 -> ReviewStep(
                medicationName = medicationName,
                dosage = dosage,
                selectedTime = selectedTime,
                repeatOption = repeatOption,
                selectedDays = selectedDays,
                startDate = startDate,
                endDate = endDate,
                notes = notes,
                onNotesChange = { notes = it },
                onSave = {
                    saveMedication(
                        medicationName = medicationName,
                        dosage = dosage,
                        selectedTime = selectedTime,
                        repeatOption = repeatOption,
                        selectedDays = selectedDays,
                        startDate = startDate,
                        endDate = endDate,
                        notes = notes,
                        navController = navController
                    )
                }
            )
        }
    }
}

@Composable
fun BasicInfoStep(
    medicationName: String,
    dosage: String,
    onMedicationNameChange: (String) -> Unit,
    onDosageChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Medication Name
        Text(
            text = "Medication Name",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = medicationName,
            onValueChange = onMedicationNameChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            placeholder = { Text("Enter medication name") },
            singleLine = true
        )

        // Dosage
        Text(
            text = "Dosage",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = dosage,
            onValueChange = onDosageChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            placeholder = { Text("e.g., 10mg, 1 tablet") },
            singleLine = true
        )

        // Continue Button
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = medicationName.isNotEmpty() && dosage.isNotEmpty()
        ) {
            Text(
                text = "Continue",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TimeAndRepeatStep(
    selectedTime: String,
    repeatOption: String,
    selectedDays: List<String>,
    startDate: String,
    endDate: String,
    onTimeChange: (String) -> Unit,
    onRepeatOptionChange: (String) -> Unit,
    onDaysChange: (List<String>) -> Unit,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    val timeOptions = listOf("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00")
    val repeatOptions = listOf("Daily", "Weekly", "Custom", "No Repeat")
    val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Set Reminder Time
        Text(
            text = "Set Reminder Time",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Time Selection
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            timeOptions.forEach { time ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTimeChange(time) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = time == selectedTime,
                        onClick = { onTimeChange(time) }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = time,
                        fontSize = 16.sp,
                        fontWeight = if (time == selectedTime) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Repeat Section
        Text(
            text = "Repeat",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Repeat Options
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            repeatOptions.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRepeatOptionChange(option) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = option == repeatOption,
                        onClick = { onRepeatOptionChange(option) }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = option,
                        fontSize = 16.sp,
                        fontWeight = if (option == repeatOption) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Conditional Fields based on Repeat Option
        when (repeatOption) {
            "Weekly" -> {
                Text(
                    text = "Which Day",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    daysOfWeek.forEach { day ->
                        val isSelected = selectedDays.contains(day)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = if (isSelected) Color(0xFF638097) else Color.Transparent,
                                    shape = CircleShape
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color(0xFF638097) else Color.Gray,
                                    shape = CircleShape
                                )
                                .clickable {
                                    val newDays = if (isSelected) {
                                        selectedDays - day
                                    } else {
                                        selectedDays + day
                                    }
                                    onDaysChange(newDays)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day,
                                color = if (isSelected) Color.White else Color.Black,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            "Custom" -> {
                // Start Date
                Text(
                    text = "Start Date",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = startDate,
                    onValueChange = onStartDateChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    placeholder = { Text("Select start date") },
                    singleLine = true
                )

                // End Date
                Text(
                    text = "End Date",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = endDate,
                    onValueChange = onEndDateChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    placeholder = { Text("Select end date") },
                    singleLine = true
                )
            }
        }

        // Continue Button
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Continue",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ReviewStep(
    medicationName: String,
    dosage: String,
    selectedTime: String,
    repeatOption: String,
    selectedDays: List<String>,
    startDate: String,
    endDate: String,
    notes: String,
    onNotesChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Review Summary
        Text(
            text = "Review Medication",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Medication Details
        ReviewItem("Name", medicationName)
        ReviewItem("Dosage", dosage)
        ReviewItem("Time", selectedTime)
        ReviewItem("Repeat", repeatOption)

        if (repeatOption == "Weekly" && selectedDays.isNotEmpty()) {
            ReviewItem("Days", selectedDays.joinToString(", "))
        }
        if (repeatOption == "Custom") {
            ReviewItem("Start Date", startDate)
            if (endDate.isNotEmpty()) {
                ReviewItem("End Date", endDate)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Notes
        Text(
            text = "Notes (Optional)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(bottom = 32.dp),
            placeholder = { Text("e.g., \"Take with water\", \"Don't take with dairy\", etc.") },
            maxLines = 5
        )

        // Save Button
        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Save Medication",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ReviewItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

private fun saveMedication(
    medicationName: String,
    dosage: String,
    selectedTime: String,
    repeatOption: String,
    selectedDays: List<String>,
    startDate: String,
    endDate: String,
    notes: String,
    navController: NavHostController
) {
    val newId = DataSource.medications.size + 1

    val newMedication = Medication(
        id = newId,
        name = medicationName,
        dosage = dosage,
        frequency = "$selectedTime - $repeatOption", // Combine time and repeat info
        notes = notes
    )

    DataSource.medications.add(newMedication)

    // Navigate back to home
    navController.popBackStack()
}

private fun getCurrentDate(): String {
    val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return formatter.format(Date())
}

@Composable
fun SplashScreen(onLoadingComplete: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onLoadingComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(R.drawable.deco_stars),
            contentDescription = "Decorative Stars",
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 50.dp, y = (-20).dp)
        )

        Image(
            painter = painterResource(R.drawable.pillpal_logo),
            contentDescription = "PillPal Logo",
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.Center)
                .offset(y = (-50).dp),
            contentScale = ContentScale.Fit
        )

        CircularProgressIndicator(
            color = Color(0xFF638097),
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}

// FIXED: Remove default parameter
@Composable
fun HomeScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(25.dp)
                .padding(bottom = 8.dp)
        ) {
            GreetingSection(user = DataSource.user)
            DateSection()
            Spacer(modifier = Modifier.height(16.dp))
            ProfileCard(user = DataSource.user)
            Spacer(modifier = Modifier.height(24.dp))
            MedicationSection(medications = DataSource.medications)
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            NavigationBar(navController = navController)
        }
    }
}

@Composable
fun GreetingSection(user: User) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Hello, ",
                fontSize = 32.sp,
                fontWeight = FontWeight.Normal,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.DarkGray.copy(alpha = 0.5f),
                        offset = Offset(5f, 5f),
                        blurRadius = 2f
                    )
                )
            )
            Box(
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(y = 2.dp)
                        .graphicsLayer {
                            scaleY = 0.5f
                        }
                        .background(
                            color = Color(0xFFFDED24),
                            shape = RoundedCornerShape(percent = 60)
                        )
                        .border(
                            width = 0.5.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(percent = 60)
                        )
                )
                Text(
                    text = "[ ${user.name} ]",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Gray.copy(alpha = 0.5f),
                            offset = Offset(5f, 5f),
                            blurRadius = 2f
                        )
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }
        }
        Image(
            painter = painterResource(R.drawable.pillpal_icon),
            contentDescription = "PillPal Icon",
            modifier = Modifier
                .size(90.dp)
                .padding(top = 8.dp)
        )
    }
}

// FIXED: Use old Java Date API instead of java.time for Android < 26
@Composable
fun DateSection() {
    val currentDate = Calendar.getInstance().time
    val formatter = SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH)
    val formattedDate = formatter.format(currentDate)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formattedDate,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .clickable { /* navigate to calendar */ }
        )
        Text(
            text = "»",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { /* navigate to calendar */ }
                .padding(start = 8.dp, bottom = 4.dp),
            color = Color.Black
        )
    }
}

@Composable
fun ProfileCard(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border( width = 1.dp, color = Color.Black, shape = RoundedCornerShape(10.dp) ),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE9F5FF)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮",
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(user.avatarRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(110.dp)
                            .border(1.5.dp, Color.Black),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = ".☆ ˖ִ ࣪⚝₊ ⊹˚",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Left
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PillPal ID",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "--------------------------",
                        fontSize = 20.sp,
                        color = Color(0xFF638097),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Row {
                                Text("Name: ", fontSize = 13.sp, color = Color.Gray)
                                Text(
                                    user.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "......................",
                                color = Color(0xFF638097),
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Column {
                            Row {
                                Text("Nickname: ", fontSize = 13.sp, color = Color.Gray)
                                Text(
                                    user.nickname ?: "N/A",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "......................",
                                color = Color(0xFF638097),
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Column {
                            Row {
                                Text("Birthday: ", fontSize = 13.sp, color = Color.Gray)
                                Text(
                                    user.birthday,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "......................",
                                color = Color(0xFF638097),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = user.dateJoined,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "--------------------------",
                fontSize = 18.sp,
                color = Color(0xFF638097),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Date of Issue (Joined)",
                fontSize = 13.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = " ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮",
                fontSize = 16.sp,
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// FIXED: Use Calendar instead of java.time for week calculation
@Composable
fun MedicationSection(medications: List<Medication>) {
    val calendar = Calendar.getInstance()
    val weekNumber = calendar.get(Calendar.WEEK_OF_YEAR)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "My Medication",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Week $weekNumber",
            fontSize = 14.sp,
            color = Color.Black
        )
    }

    HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        thickness = 1.dp,
        color = Color.Black
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF9F8F1),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFF918C84),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(8.dp)
                ),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                medications.forEach { medication ->
                    MedicationItem(medication = medication)
                    if (medication != medications.last()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            thickness = 1.dp,
                            color = Color(0xFFE8E8E8)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun MedicationItem(medication: Medication) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Text(
                text = medication.name,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable { /* navigate to edit medication */ }
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier
                .size(28.dp)
                .clickable { /* navigate */ },
            tint = Color.Black
        )
    }
}

// FIXED: Remove default parameter
@Composable
fun NavigationBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 50.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigationButton(
            iconRes = R.drawable.home,
            contentDescription = null,
            modifier = Modifier.clickable { /* navigate to home */ }
        )

        NavigationButton(
            iconRes = R.drawable.history,
            contentDescription = null,
            modifier = Modifier.clickable { /* navigate to history */ }
        )

        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = Color(0xFFF5F0ED),
                    shape = CircleShape
                )
                .clickable {
                    navController.navigate("add_medication")
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.add_calendar),
                    contentDescription = "Add Medication",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "add",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }

        NavigationButton(
            iconRes = R.drawable.bell,
            contentDescription = null,
            modifier = Modifier.clickable { /* navigate to notifs */ }
        )

        NavigationButton(
            iconRes = R.drawable.user_settings,
            contentDescription = null,
            modifier = Modifier.clickable { /* navigate to settings */ }
        )
    }
}

@Composable
fun NavigationButton(iconRes: Int, contentDescription: String?, modifier: Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(45.dp)
                .background(
                    color = Color(0xFFD7D4CF),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = contentDescription,
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
        }
    }
}