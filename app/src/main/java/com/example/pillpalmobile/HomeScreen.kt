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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import com.example.pillpalmobile.model.UserUI
import com.example.pillpalmobile.ui.theme.Inter
import com.example.pillpalmobile.ui.theme.Montserrat
import com.example.pillpalmobile.ui.theme.PixelifySans
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

// ------------- HOME SCREEN PRINCIPAL -------------

@Composable
fun HomeScreen(
    user: UserUI,
    medications: List<MedicationResponse>,
    isMedicationLoading: Boolean,
    navController: NavHostController,
    onLogout: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(25.dp)
                .padding(bottom = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            GreetingSection(user = user)

            Spacer(modifier = Modifier.height(8.dp))

            DateSection()

            Spacer(modifier = Modifier.height(12.dp))

            ProfileCard(user = user)

            Spacer(modifier = Modifier.height(28.dp))

            if (isMedicationLoading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            } else {
                MedicationSectionFromNetwork(
                    medications = medications,
                    navController = navController
                )
            }
        }

        // Barra de navegación inferior
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            NavigationBar()
        }
    }
}

// ------------- SECCIONES DE LA UI -------------

@Composable
fun GreetingSection(user: UserUI) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Hello, ",
                fontSize = 32.sp,
                fontFamily = Montserrat,
                fontWeight = FontWeight.Normal,
            )
            Box(
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer {
                            scaleY = 0.5f
                        }
                        .background(
                            color = Color(0xFFFCE2A9),
                            shape = RoundedCornerShape(percent = 60)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFFF16F33),
                            shape = RoundedCornerShape(percent = 60)
                        )
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
}

@Composable
fun DateSection() {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)
    val formattedDate = currentDate.format(formatter)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = formattedDate,
            fontSize = 30.sp,
            fontFamily = PixelifySans,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.clickable { /* calendario en el futuro */ }
        )
        Text(
            text = "»",
            fontSize = 24.sp,
            fontFamily = PixelifySans,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { /* calendario */ }
                .padding(start = 8.dp, bottom = 4.dp),
            color = Color.Black
        )
    }
}

@Composable
fun ProfileCard(user: UserUI) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                spotColor = Color.Black,
                ambientColor = Color.Black,
                shape = RoundedCornerShape(10.dp),
                clip = false
            )
            .border(2.dp, Color(0xFFFDFAE7), RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFACBD6F))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(0.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(width = 102.dp, height = 100.dp)
                            .border(1.dp, Color.Black)
                            .background(Color.White)
                    ) {
                        Image(
                            painter = painterResource(user.avatarRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(width = 90.dp, height = 88.dp)
                                .align(Alignment.Center),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PillPal ID",
                        fontSize = 28.sp,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                    Text(
                        text = "-------------------",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.offset(y = (-16).dp)
                    )

                    ProfileField(label = "Name:     ", value = user.name)
                    Spacer(modifier = Modifier.height(1.dp))

                    ProfileField(label = "Birthday:     ", value = user.birthday)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "--------------------------------",
                color = Color.White,
                fontSize = 24.sp,
                fontFamily = Montserrat,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ProfileField(label: String, value: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = label,
                fontSize = 16.sp,
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                lineHeight = 10.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontFamily = Inter,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                lineHeight = 13.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f),
            )
        }
        Text(
            text = "...................................",
            color = Color.White,
            fontSize = 16.sp,
            fontFamily = Inter,
            fontWeight = FontWeight.Bold,
            lineHeight = 4.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

// ----------- MEDICATION SECTION (usa datos del backend) ------------

@Composable
fun MedicationSection(
    medications: List<Medication>,
    navController: NavHostController
) {
    val currentDate = LocalDate.now()
    val weekFields = WeekFields.of(Locale.getDefault())
    val weekNumber = currentDate.get(weekFields.weekOfWeekBasedYear())

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "   My Medication",
                fontSize = 22.sp,
                fontFamily = Montserrat,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = " Week $weekNumber",
                fontSize = 18.sp,
                fontFamily = Montserrat,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 420.dp)
                .background(
                    color = Color(0xFFFDFAE7),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 2.dp,
                    color = Color(0xFF595880),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 195.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFF918C84),
                        shape = RoundedCornerShape(8.dp)
                    ),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val paddedList = remember(medications) {
                        medications + List(maxOf(0, 6 - medications.size)) {
                            Medication(
                                id = -1,
                                name = "Medication"
                            )
                        }
                    }

                    paddedList.forEach { med ->
                        MedicationRowUnified(
                            name = med.name,
                            isPlaceholder = (med.id == -1),
                            onClick = {
                                if (med.id != -1) {
                                    // en el futuro: navController.navigate("edit_med/${med.id}")
                                }
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun MedicationSectionFromNetwork(
    medications: List<MedicationResponse>,
    navController: NavHostController
) {
    val mappedMeds = remember(medications) {
        medications.map { med ->
            val schedule = med.schedule

            Medication(
                id = med.med_id,
                name = med.name,
                reminderTimes = schedule?.times ?: emptyList(),
                medicationDate = med.active_start_date ?: "",
                repeatEnabled = schedule?.repeat_type != null,
                repeatFrequency = when (schedule?.repeat_type) {
                    "daily" -> "Daily"
                    "weekly" -> "Weekly"
                    "custom" -> "Custom"
                    else -> "Daily"
                },
                repeatDays = decodeDayMask(schedule?.day_mask),
                repeatStartDate = null,
                repeatEndDate = null,
                notes = med.notes ?: ""
            )
        }
    }

    MedicationSection(
        medications = mappedMeds,
        navController = navController
    )
}

// day_mask tipo "1110000" -> ["Mon","Tue","Wed"]
fun decodeDayMask(mask: String?): List<String> {
    if (mask.isNullOrEmpty()) return emptyList()

    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    return mask.mapIndexedNotNull { index, c ->
        if (c == '1') days[index] else null
    }
}

// ------------- FILAS, ITEMS, NAV BAR -------------

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
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = name,
            fontSize = 22.sp,
            fontFamily = PixelifySans,
            fontWeight = if (isPlaceholder) FontWeight.Light else FontWeight.Normal,
            color = if (isPlaceholder) Color.Gray else Color.Black,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        androidx.compose.material3.Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = if (isPlaceholder) Color.Gray else Color.Black,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun NavigationBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // home activo
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        color = Color(0xFFCBCBE7),
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = Color(0xFF595880),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Icon(
                    painter = painterResource(R.drawable.home),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.Unspecified
                )
            }
        }

        NavigationButton(
            iconRes = R.drawable.history,
            label = "history",
            modifier = Modifier.clickable { }
        )

        NavigationButton(
            iconRes = R.drawable.add_calendar,
            label = "add",
            modifier = Modifier.clickable { }
        )

        NavigationButton(
            iconRes = R.drawable.bell,
            label = "alerts",
            modifier = Modifier.clickable { }
        )

        NavigationButton(
            iconRes = R.drawable.user_settings,
            label = "settings",
            modifier = Modifier.clickable { }
        )
    }
}

@Composable
fun NavigationButton(iconRes: Int, label: String, modifier: Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(
                    color = Color.White,
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFF7C8081),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier
            ) {
                androidx.compose.material3.Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(top = 4.dp)
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
