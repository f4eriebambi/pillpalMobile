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
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pillpalmobile.data.DataSource
import com.example.pillpalmobile.model.Medication
import com.example.pillpalmobile.model.User
import com.example.pillpalmobile.screens.SettingsScreen
import com.example.pillpalmobile.screens.WelcomeScreen
import com.example.pillpalmobile.ui.theme.PillPalMobileTheme
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale


// https://developer.android.com/develop/ui/views/text-and-emoji/fonts-in-xml
// FONTS USED FROM : https://fonts.google.com/ (montserrat, inter, pixelify sans) and https://developer.apple.com/fonts//https://github.com/ravijoon/SF-Pro-Expanded-Font/blob/main/SF-Pro.ttf (sf pro)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PillPalMobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    MainApp()
//                    HomeScreen()
//                    WelcomeScreen(
//                        onNavigateToLogin = { /* need to do login screen */ },
//                        onNavigateToSignUp = { /* need to do signup screen */ }
//                    )
//                    LoginScreen(
//                        onNavigateToSignUp = { /* */ },
//                        onNavigateToHome = { /* */ },
//                        onForgotPassword = { /* */ }
//                    )
                }
            }
        }
    }
}

@Composable
fun MainApp() {
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        SplashScreen {
            showSplash = false
        }
    } else {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
//            SplashScreen (
//                onLoadingComplete = {
//                    showSplash = false
//                }
//            )
//            HomeScreen()
            SettingsScreen()
//            WelcomeScreen(
//                onNavigateToLogin = { },
//                onNavigateToSignUp = { },
//            )
//            LoginScreen(
//                onNavigateToSignUp = { /* */ },
//                onNavigateToHome = { /* */ },
//                onForgotPassword = { /* */ }
//            )
//            CreateAccountScreen(
//                onNavigateToLogin = { /* */ },
//                onAccountCreated = { /* */ }
//            )
            EditMedicationScreen {  }
        }
    }
}

@Composable
fun SplashScreen(onLoadingComplete: () -> Unit) {

    LaunchedEffect(Unit) {
        delay(3000)
        onLoadingComplete()
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
                .align(Alignment.BottomEnd)
                .size(380.dp)
                .offset(x = 40.dp, y = 54.dp)
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
    }
}

@Composable
fun HomeScreen() {
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

            GreetingSection(user = DataSource.user)

            Spacer(modifier = Modifier.height(8.dp))

            DateSection()

            Spacer(modifier = Modifier.height(12.dp))

            ProfileCard(user = DataSource.user)

            Spacer(modifier = Modifier.height(28.dp))

            MedicationSection(medications = DataSource.medications)
        }

        // nav abr
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
//                .background(Color.White)
                .padding(bottom = 20.dp)
        ) {
            NavigationBar()
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
                fontFamily = Montserrat,
                fontWeight = FontWeight.Normal,
            )
            Box(
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
//                        .fillMaxWidth(fraction = 0.3f)
                        .matchParentSize()
                        .offset(y = 2.dp)
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
//                        .padding(horizontal = 9.dp, vertical = 7.dp)
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
    // get current date
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)
    val formattedDate = currentDate.format(formatter)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$formattedDate",
            fontSize = 30.sp,
            fontFamily = PixelifySans,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .clickable { /* navigate to calendar */ }
        )
//        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "»",
            fontSize = 24.sp,
            fontFamily = PixelifySans,
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
                Column(horizontalAlignment = Alignment.CenterHorizontally)
                {
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
//                    Spacer(modifier = Modifier.height(6.dp))
                }

                Spacer(modifier = Modifier.width(20.dp))

                // user info
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

//                    Spacer(modifier = Modifier.height(3.dp))

                    // name
                    ProfileField(label = "Name:     ", value = user.name)
                    Spacer(modifier = Modifier.height(1.dp))

                    // birthday
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
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
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

@Composable
fun MedicationSection(medications: List<Medication>) {
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
                    medications.forEach { medication ->
                        MedicationItem(medication = medication)
                        if (medication != medications.last()) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .padding(top = 8.dp),
                                thickness = 0.5.dp,
                                color = Color(0xFF918C84)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


@Composable
fun MedicationItem(medication: Medication) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Text(
                text = medication.name,
                fontSize = 22.sp,
                fontFamily = PixelifySans,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable { /* navigate to edit medication */ }
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clickable { /* navigate to edit medication */ },
            tint = Color.Black
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
        // home (enabled/current page)
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
                Icon(
                    painter = painterResource(R.drawable.home),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.Unspecified
                )
            }
        }

        // history
        NavigationButton(
            iconRes = R.drawable.history,
            label = "history",
            modifier = Modifier
                .clickable { /* navigate to history */ },
        )

        // add/calendar
        NavigationButton(
            iconRes = R.drawable.add_calendar,
            label = "add",
            modifier = Modifier
                .clickable { /* navigation to add/calendar */ },
        )

        // notifs
        NavigationButton(
            iconRes = R.drawable.bell,
            label = "alerts",
            modifier = Modifier
                .clickable { /* navigate to notifs */ },
        )

        // settings
        NavigationButton(
            iconRes = R.drawable.user_settings,
            label = "settings",
            modifier = Modifier
                .clickable { /* navigate to settings */ },
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
                        modifier = Modifier.clickable { /* navigation will be implemented */ }
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp).padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = label,
                    fontSize = 11.sp, // or can change to 15 and then history->log and settings->prefs
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }
        }
    }
}