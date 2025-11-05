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
import com.example.pillpalmobile.ui.theme.PillPalMobileTheme
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

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
//            LoginScreen(
//                onNavigateToSignUp = { /* */ },
//                onNavigateToHome = { /* */ },
//                onForgotPassword = { /* */ }
//            )
            CreateAccountScreen(
                onNavigateToLogin = { /* */ },
                onAccountCreated = { /* */ }
            )
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
            colorFilter = ColorFilter.tint(Color.Gray),
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
        Image(
            painter = painterResource(R.drawable.deco_stars),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.Gray),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(380.dp)
                .offset(x = 40.dp, y = 54.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(25.dp)
                .padding(bottom = 8.dp)
        ) {
            GreetingSection(user = DataSource.user)

//            Spacer(modifier = Modifier.height(16.dp))

            DateSection()

            Spacer(modifier = Modifier.height(16.dp))

            ProfileCard(user = DataSource.user)

            Spacer(modifier = Modifier.height(24.dp))

            MedicationSection(medications = DataSource.medications)
        }

        // nav abr
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
//                .background(Color.White)
                .padding(bottom = 4.dp)
        ) {
            NavigationBar()
        }
        Image(
            painter = painterResource(R.drawable.deco_starspiral2),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(115.dp)
                .offset(x = (-25).dp, y = (-618).dp)
        )
        Image(
            painter = painterResource(R.drawable.deco_symbol),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(150.dp)
                .offset(x = (-3).dp, y = (-380).dp)
        )
        Image(
            painter = painterResource(R.drawable.pillpal_team),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .rotate(-90f)
                .size(310.dp)
                .offset(x = (110).dp, y = (-175).dp)
        )
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
//                        .fillMaxWidth(fraction = 0.3f)
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
//                        .padding(horizontal = 9.dp, vertical = 7.dp)
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
    // get current date
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)
    val formattedDate = currentDate.format(formatter)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$formattedDate",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .clickable { /* navigate to calendar */ }
        )
//        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "»",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold, // optional
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
                spotColor = Color(0x55000000),
                ambientColor = Color(0x33000000),
                shape = RoundedCornerShape(10.dp)
            )
            .border(1.dp, Color.Black, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE9F5FF))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
        ) {
            // star border
            Text(
                text = "✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Image(
//                        painter = painterResource(user.avatarRes),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .size(100.dp)
//                            .border(1.5.dp, Color.Black, padding ),
//                        contentScale = ContentScale.Crop
//                    )
                    Box(
                        modifier = Modifier
                            .size(width = 110.dp, height = 109.dp)
                            .border(2.dp, Color.Black)
                            .background(Color.White)
                    ) {
                        Image(
                            painter = painterResource(user.avatarRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(width = 84.dp, height = 84.dp)
                                .align(Alignment.Center),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = ".☆ ˖ִ ࣪⚝₊ ⊹˚",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(x = (-30).dp, y = (-19).dp)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // user info
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PillPal ID",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "----------------------------",
                        color = Color(0xFF638097),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    // name
                    ProfileField(label = "Name:     ", value = user.name)
                    Spacer(modifier = Modifier.height(1.dp))

                    // nickname
                    ProfileField(label = "Nickname:     ", value = user.nickname ?: "N/A")
                    Spacer(modifier = Modifier.height(1.dp))

                    // birthday
                    ProfileField(label = "Birthday:     ", value = user.birthday)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // date joined section + stars
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = user.dateJoined,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 13.sp
                )
                Text(
                    text = "----------------------------",
                    color = Color(0xFF638097),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 12.sp
                )

                // stars + joined
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = " ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮  ✮",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 10.sp
                    )
                    Text(
                        text = "Date of Issue (Joined)",
                        fontSize = 10.sp,
                        textAlign = TextAlign.Right,
                        lineHeight = 10.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Black,
                lineHeight = 10.sp
            )
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                lineHeight = 13.sp
            )
        }
        Text(
            text = ".....................................................",
            color = Color(0xFF638097),
            fontSize = 8.sp,
            lineHeight = 8.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun MedicationSection(medications: List<Medication>) {
    val currentDate = LocalDate.now()
    val weekFields = WeekFields.of(Locale.getDefault())
    val weekNumber = currentDate.get(weekFields.weekOfWeekBasedYear())

    Row(
        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "My Medication",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "    Week $weekNumber",
            fontSize = 15.sp,
//            color = Color.Gray
            color = Color.Black
        )
    }

    // divider
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        thickness = 1.dp,
//        color = Color(0xFFE8E8E8)
        color = Color.Black
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 390.dp)
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 60.dp)
                                .padding(vertical = 8.dp),
                            thickness = 1.dp,
                            color = Color(0xFFE8E8E8)
                        )
                    }
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            thickness = 0.7.dp,
            color = Color(0xFF918C84)
        )

        Text(
            text = "✮⋆˙",
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(40.dp))
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
                fontSize = 18.sp,
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
fun NavigationBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 50.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // home
        NavigationButton(
            iconRes = R.drawable.home,
            contentDescription = null,
            modifier = Modifier
                .clickable { /* navigate to home */ },
        )

        // history
        NavigationButton(
            iconRes = R.drawable.history,
            contentDescription = null,
            modifier = Modifier
                .clickable { /* navigate to history */ },
        )

        // add/calendar
        Box(
            modifier = Modifier
                .size(62.dp)
                .background(
                    color = Color(0xFFF5F0ED), // circle color
                    shape = CircleShape
                )
                .clickable { /* navigation to add/calendar */ },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.add_calendar),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = "add",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }

        // notifs
        NavigationButton(
            iconRes = R.drawable.bell,
            contentDescription = null,
            modifier = Modifier
                .clickable { /* navigate to notifs */ },
        )

        // settings
        NavigationButton(
            iconRes = R.drawable.user_settings,
            contentDescription = null,
            modifier = Modifier
                .clickable { /* navigate to settings */ },
        )
    }
}

@Composable
fun NavigationButton(iconRes: Int, contentDescription: String?, modifier: Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color(0xFFD7D4CF),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(20.dp),
                tint = Color.Unspecified
            )
        }
    }
}