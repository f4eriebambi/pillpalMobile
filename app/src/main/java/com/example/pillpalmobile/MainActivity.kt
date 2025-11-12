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