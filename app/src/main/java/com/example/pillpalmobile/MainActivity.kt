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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            // Show only the NotificationsScreen
            NotificationScreen()
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
