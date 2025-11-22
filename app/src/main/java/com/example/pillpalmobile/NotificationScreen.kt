package com.example.pillpalmobile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pillpalmobile.R
import com.example.pillpalmobile.model.Notification
import com.example.pillpalmobile.model.NotificationType
import com.example.pillpalmobile.ui.components.NotificationCardsWithBorders

// --- Mock Data ---
val initialNotifications = listOf(
    Notification(1, NotificationType.UPCOMING_DOSE, "Upcoming Dose", "lexapro in 5 hours 19 mins", "1 min ago", "lexapro"),
    Notification(2, NotificationType.REFILL_REMINDER, "Refill Reminder", "vitamin C is running low", "1 hour ago", "vitamin C"),
    Notification(3, NotificationType.STREAK_MILESTONE, "Streak Milestone", "7 days in a row!", "1 day ago"),
    Notification(4, NotificationType.MISSED_DOSE, "Missed Dose", "vitamin C at 8:00 AM", "2 week(s) ago", "vitamin C"),
)

// --- Main Components ---

@Composable
fun NotificationScreen(navController: NavController? = null) {
    var notifications by remember { mutableStateOf(initialNotifications) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // White background for entire screen
    ) {
        // Deco stars moved to the left and zoomed in
        Image(
            painter = painterResource(R.drawable.deco_stars),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(240.dp) // Increased size for zoom effect
                .offset(x = (-20).dp, y = (-15).dp) // Moved to left with slight offset
                .alpha(0.15f) // Low opacity
        )

        // "Notifications" text at top left - with only 'N' uppercase
        Text(
            text = "Notifications",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Light,
                fontSize = 32.sp
            ),
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(36.dp)
        )

        // Content Area with bordered notifications
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 90.dp) // Adjusted padding for smaller text
        ) {
            // Use the bordered notification cards
            if (notifications.isEmpty()) {
                EmptyState(navController = navController)
            } else {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    NotificationCardsWithBorders(navController = navController) // Added navController parameter
                }
            }

            // Clear All Button - ensure it's visible
            ClearAllButton(
                onClear = { notifications = emptyList() },
                navController = navController
            )
        }
    }
}

@Composable
fun EmptyState(navController: NavController? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "All clear!",
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp)
        )
        Text(
            "You have no new notifications.",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
            color = Color.Gray
        )

        // Optional: Add navigation button when empty
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                navController?.navigate("add")
            },
            modifier = Modifier
                .padding(horizontal = 50.dp)
                .height(48.dp)
        ) {
            Text("Add Medication", fontSize = 16.sp)
        }
    }
}

@Composable
fun ClearAllButton(onClear: () -> Unit, navController: NavController? = null) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .clickable {
                onClear()
                // Optional: Navigate after clearing
                // navController?.navigate("home")
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.clear_notification),
            contentDescription = "Clear all notifications",
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewNotificationScreen() {
    NotificationScreen()
}