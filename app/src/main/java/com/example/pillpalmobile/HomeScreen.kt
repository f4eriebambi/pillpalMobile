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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.pillpalmobile.model.Medication
import com.example.pillpalmobile.model.MedicationResponse
import com.example.pillpalmobile.model.MedicationViewModel
import com.example.pillpalmobile.model.UserUI
import com.example.pillpalmobile.ui.theme.Inter
import com.example.pillpalmobile.ui.theme.Montserrat
import com.example.pillpalmobile.ui.theme.PixelifySans
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

// =============================================
// HOME SCREEN (ViewModel + Navegación)
// =============================================
@Composable
fun HomeScreen(
    user: UserUI,
    navController: NavHostController,
    viewModel: MedicationViewModel
) {
    val medications by viewModel.medications.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.error.collectAsState()


    LaunchedEffect(user.id) {
        viewModel.loadMedications(userId = user.id)
    }

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {

        // ---------- CONTENIDO PRINCIPAL ----------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(25.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            GreetingSection(user)
            Spacer(modifier = Modifier.height(8.dp))
            DateSection()
            Spacer(modifier = Modifier.height(12.dp))
            ProfileCard(user)
            Spacer(modifier = Modifier.height(28.dp))

            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }

                errorMsg != null -> {
                    Text(
                        text = "Error: $errorMsg",
                        color = Color.Red,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                else -> {
                    MedicationSectionFromNetwork(
                        medications = medications,
                        navController = navController
                    )
                }
            }
        }

        // ---------- BOTTOM NAV BAR ----------
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            NavigationBar(navController)
        }
    }
}

// ----------------------------------------------------
// GREETING SECTION
// ----------------------------------------------------
@Composable
fun GreetingSection(user: UserUI) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Hello, ",
            fontSize = 32.sp,
            fontFamily = Montserrat
        )

        Box(contentAlignment = Alignment.Center) {

            Box(
                modifier = Modifier
                    .graphicsLayer { scaleY = 0.5f }
                    .background(Color(0xFFFCE2A9), RoundedCornerShape(60))
                    .border(1.dp, Color(0xFFF16F33), RoundedCornerShape(60))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )

            Text(
                text = user.name,
                fontSize = 32.sp,
                fontFamily = Montserrat,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}

// ----------------------------------------------------
// DATE SECTION
// ----------------------------------------------------
@Composable
fun DateSection() {
    val today = LocalDate.now()
    val formatted = today.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(formatted, fontSize = 30.sp, fontFamily = PixelifySans)

        Text(
            "»",
            fontSize = 24.sp,
            fontFamily = PixelifySans,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

// ----------------------------------------------------
// PROFILE CARD
// ----------------------------------------------------
@Composable
fun ProfileCard(user: UserUI) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(10.dp))
            .border(2.dp, Color(0xFFFDFAE7), RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFACBD6F))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Image(
                    painter = painterResource(user.avatarRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .border(1.dp, Color.Black)
                        .background(Color.White),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(20.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        "PillPal ID",
                        fontSize = 28.sp,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        "-------------------",
                        fontSize = 24.sp,
                        color = Color.White
                    )

                    ProfileField("Name:", user.name)
                    ProfileField("Birthday:", user.birthday)
                }
            }
        }
    }
}

@Composable
fun ProfileField(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 16.sp, fontFamily = Inter, color = Color.White)
        Text(value, fontSize = 16.sp, fontFamily = Inter, color = Color.White)
    }

    Text(
        "...................................",
        fontSize = 16.sp,
        color = Color.White,
        textAlign = TextAlign.Center,
        fontFamily = Inter
    )
}

// ----------------------------------------------------
// MEDICATION LIST (desde backend)
// ----------------------------------------------------
@Composable
fun MedicationSectionFromNetwork(
    medications: List<MedicationResponse>,
    navController: NavHostController
) {
    val mapped = medications.map { med ->
        val schedule = med.schedule

        Medication(
            id = med.med_id,
            name = med.name,
            reminderTimes = schedule?.times ?: emptyList(),
            repeatEnabled = schedule?.repeat_type != null,
            repeatFrequency = schedule?.repeat_type ?: "daily",
            repeatDays = decodeDayMask(schedule?.day_mask),
            notes = med.notes ?: ""
        )
    }

    MedicationSection(mapped, navController)
}

fun decodeDayMask(mask: String?): List<String> {
    if (mask.isNullOrEmpty()) return emptyList()
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    return mask.mapIndexedNotNull { i, c -> if (c == '1') days[i] else null }
}

@Composable
fun MedicationSection(
    medications: List<Medication>,
    navController: NavHostController
) {
    Column {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("   My Medication", fontSize = 22.sp, fontFamily = Montserrat)

            val week = LocalDate.now().get(
                WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()
            )

            Text(" Week $week", fontSize = 18.sp, fontFamily = Montserrat)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 420.dp)
                .background(Color(0xFFFDFAE7))
                .border(2.dp, Color(0xFF595880)),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // Rellenamos hasta 6 filas para que se vea como el diseño
                val padded = medications + List(maxOf(0, 6 - medications.size)) {
                    Medication(id = -1, name = "Medication")
                }

                padded.forEach { med ->
                    MedicationRowUnified(
                        name = med.name,
                        isPlaceholder = (med.id == -1)
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// ROW ITEM
// ----------------------------------------------------
@Composable
fun MedicationRowUnified(
    name: String,
    isPlaceholder: Boolean,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isPlaceholder) { onClick?.invoke() }
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = name,
            fontSize = 22.sp,
            fontFamily = PixelifySans,
            color = if (isPlaceholder) Color.Gray else Color.Black,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = if (isPlaceholder) Color.Gray else Color.Black
        )
    }
}

// ----------------------------------------------------
// BOTTOM NAV BAR (FUNCIONAL)
// ----------------------------------------------------
@Composable
fun NavigationBar(navController: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        NavigationButton(
            iconRes = R.drawable.home,
            label = "Home",
            onClick = { navController.navigate("home") }
        )

        NavigationButton(
            iconRes = R.drawable.history,
            label = "History",
            onClick = { /* TODO */ }
        )

        NavigationButton(
            iconRes = R.drawable.add_calendar,
            label = "Add",
            onClick = { navController.navigate("calendar") }
        )

        NavigationButton(
            iconRes = R.drawable.bell,
            label = "Alerts",
            onClick = { /* TODO */ }
        )

        NavigationButton(
            iconRes = R.drawable.user_settings,
            label = "Settings",
            onClick = { /* TODO */ }
        )
    }
}

@Composable
fun NavigationButton(
    iconRes: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Box(
            modifier = Modifier
                .size(70.dp)
                .background(Color.White, CircleShape)
                .border(1.dp, Color(0xFF7C8081), CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
        }

        Text(label, fontSize = 11.sp, fontFamily = Montserrat)
    }
}
