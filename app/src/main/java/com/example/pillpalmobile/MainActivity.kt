package com.example.pillpalmobile

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Notifications
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.pillpalmobile.screens.CalendarScreen
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pillpalmobile.data.DataSource
import com.example.pillpalmobile.model.Medication
import com.example.pillpalmobile.model.User

import com.example.pillpalmobile.screens.SettingsScreen
import com.example.pillpalmobile.screens.WelcomeScreen
import com.example.pillpalmobile.ui.screens.NotificationScreen
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
        PillPalMobileTheme {
            val navController = rememberNavController()

            // For now, always show the main app without login
            val isLoggedIn = true

            if (isLoggedIn) {
                // Main app with bottom navigation
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen()
                        }
                        composable("history") {
                            CalendarScreen() // Link to your CalendarScreen
                        }
                        composable("add") {
                            AddMedicationScreen(navController = navController)
                        }
                        composable("notifications") {
                            NotificationScreen(navController = navController)
                        }
                        composable("settings") {
                            SettingsScreen(navController = navController)
                        }
                        composable("edit-medication") {
                            EditMedicationScreen(navController = navController)
                        }
                    }
                }
            } else {
                // Auth flow - start with welcome screen
                NavHost(
                    navController = navController,
                    startDestination = "welcome"
                ) {
                    composable("welcome") {
                        WelcomeScreen(navController = navController)
                    }
                    composable("login") {
                        LoginScreen(
                            navController = navController,
                            onNavigateToHome = {
                                navController.navigate("home") {
                                    popUpTo("welcome") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("create-account") {
                        CreateAccountScreen(
                            navController = navController,
                            onAccountCreated = { email ->
                                navController.navigate("home") {
                                    popUpTo("welcome") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

// Data class for bottom navigation items
data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(
            route = "home",
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavItem(
            route = "history",
            title = "History",
            selectedIcon = Icons.Filled.History,
            unselectedIcon = Icons.Outlined.History
        ),
        BottomNavItem(
            route = "add",
            title = "Add",
            selectedIcon = Icons.Filled.Add,
            unselectedIcon = Icons.Outlined.Add
        ),
        BottomNavItem(
            route = "notifications",
            title = "Alerts",
            selectedIcon = Icons.Filled.Notifications,
            unselectedIcon = Icons.Outlined.Notifications
        ),
        BottomNavItem(
            route = "settings",
            title = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        )
    )

    val currentRoute = currentRoute(navController)

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Black,
        modifier = Modifier.shadow(elevation = 8.dp)
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (currentRoute == item.route) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

// Your existing HomeScreen code
@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
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
            text = "$formattedDate",
            fontSize = 30.sp,
            fontFamily = PixelifySans,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .clickable { /* navigate to calendar */ }
        )
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
        modifier = Modifier
            .fillMaxWidth()
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

// Placeholder screens for navigation
@Composable
fun AddMedicationScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Add Medication", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Add new medications here", fontSize = 16.sp, color = Color.Gray)
    }
}

@Composable
fun EditMedicationScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Edit Medication", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Edit medication details here", fontSize = 16.sp, color = Color.Gray)
    }
}

@Composable
fun LoginScreen(navController: NavController, onNavigateToHome: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login Screen", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Button(onClick = onNavigateToHome) {
            Text("Login")
        }
    }
}

@Composable
fun CreateAccountScreen(navController: NavController, onAccountCreated: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create Account", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Button(onClick = { onAccountCreated("user@example.com") }) {
            Text("Create Account")
        }
    }
}