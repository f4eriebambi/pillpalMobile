package com.example.pillpalmobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pillpalmobile.R
import com.example.pillpalmobile.model.Notification
import com.example.pillpalmobile.model.NotificationType

// --- Custom Colors (Simulating Figma Prototype Colors) ---
val ColorYellowBg = Color(0xFFFFF7E7) // Light Yellow/Orange
val ColorYellowBorder = Color(0xFFE6B400) // Darker Orange/Gold
val ColorYellowText = Color(0xFFCC8C00) // Darker Yellow

val ColorRedBg = Color(0xFFFFF0EC) // Light Red/Orange
val ColorRedBorder = Color(0xFFE64A19) // Darker Red/Orange
val ColorRedText = Color(0xFFD84315) // Dark Red

val ColorGreenBg = Color(0xFFEEFFEE) // Light Green
val ColorGreenBorder = Color(0xFF2E7D32) // Darker Green
val ColorGreenText = Color(0xFF387E38) // Dark Green

val ColorPinkBg = Color(0xFFFAEBEB) // Light Pink/Red
val ColorPinkBorder = Color(0xFFC62828) // Darker Red
val ColorPinkText = Color(0xFFB74949) // Dark Pink

// --- Notification Configuration Map ---
data class NotificationStyle(
    val bgColor: Color,
    val borderColor: Color,
    val timeColor: Color,
    val iconRes: Int // Added icon resource
)

@Composable
fun getNotificationStyle(type: NotificationType): NotificationStyle {
    return when (type) {
        NotificationType.UPCOMING_DOSE -> NotificationStyle(
            bgColor = ColorYellowBg,
            borderColor = ColorYellowBorder,
            timeColor = Color(0xFFA1A1A1), // Changed to A1A1A1
            iconRes = R.drawable.timer // Timer icon for upcoming dose
        )
        NotificationType.REFILL_REMINDER -> NotificationStyle(
            bgColor = ColorRedBg,
            borderColor = ColorRedBorder,
            timeColor = Color(0xFFA1A1A1), // Changed to A1A1A1
            iconRes = R.drawable.warning // Warning icon for refill reminder
        )
        NotificationType.STREAK_MILESTONE -> NotificationStyle(
            bgColor = ColorGreenBg,
            borderColor = ColorGreenBorder,
            timeColor = Color(0xFFA1A1A1), // Changed to A1A1A1
            iconRes = R.drawable.clover // Clover icon for streak milestone
        )
        NotificationType.MISSED_DOSE -> NotificationStyle(
            bgColor = ColorPinkBg,
            borderColor = ColorPinkBorder,
            timeColor = Color(0xFFA1A1A1), // Changed to A1A1A1
            iconRes = R.drawable.cross // Cross icon for missed dose
        )
    }
}

@Composable
fun NotificationCard(notification: Notification, modifier: Modifier = Modifier) {
    val style = getNotificationStyle(notification.type)

    // Create the formatted subtitle with bolded medication name
    val formattedSubtitle = buildAnnotatedString {
        val subtitleParts = notification.subtitle.split(" ")

        subtitleParts.forEachIndexed { index, part ->
            val isMedicationName = notification.medicationName != null &&
                    part.equals(notification.medicationName, ignoreCase = true)

            if (isMedicationName) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append(part)
                }
            } else {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, color = Color.DarkGray)) {
                    append(part)
                }
            }
            if (index < subtitleParts.lastIndex) {
                append(" ") // Re-add spaces between words
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp) // Increased from 110dp to 130dp
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .background(color = style.bgColor, shape = RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                color = style.borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(14.dp) // Slightly increased internal padding
    ) {
        // Main content - ALL TEXT ALIGNED TO LEFT
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Title with icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(style.iconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(22.dp) // Slightly increased icon size
                        .padding(end = 8.dp)
                )
                Text(
                    text = notification.title,
                    color = Color.Black,
                    fontSize = 17.sp, // Slightly increased font size
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(modifier = Modifier.height(8.dp)) // Increased spacing

            // Display the formatted subtitle
            Text(
                text = formattedSubtitle,
                color = Color.DarkGray,
                fontSize = 17.sp, // Slightly increased font size
                lineHeight = 22.sp, // Increased line height
                modifier = Modifier.align(Alignment.Start)
            )
        }

        // Time at the bottom, aligned to the LEFT - now using A1A1A1 color
        Text(
            text = notification.timeAgo,
            color = style.timeColor, // This now uses A1A1A1 for all notification types
            fontSize = 15.sp, // Slightly increased font size
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Start)
        )
    }
}

@Composable
fun NotificationCardsWithBorders(navController: NavController? = null) {
    val notifications = listOf(
        Notification(1, NotificationType.UPCOMING_DOSE, "Upcoming Dose", "lexapro in 5 hours 19 mins", "1 min ago", "lexapro"),
        Notification(2, NotificationType.REFILL_REMINDER, "Refill Reminder", "vitamin C is running low", "1 hour ago", "vitamin C"),
        Notification(3, NotificationType.STREAK_MILESTONE, "Streak Milestone", "7 days in a row!", "1 day ago"),
        Notification(4, NotificationType.MISSED_DOSE, "Missed Dose", "vitamin C at 8:00 AM", "2 week(s) ago", "vitamin C"),
    )

    // Outer black rectangle (first border) with white background
    Box(
        modifier = Modifier
            .padding(12.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(
                width = 1.5.dp,
                color = Color.Black,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 8.dp, horizontal = 6.dp)
    ) {
        // Inner black rectangle (second border) with white background
        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(
                    width = 1.5.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(vertical = 10.dp, horizontal = 12.dp)
        ) {
            // Notification cards content
            Column {
                notifications.forEachIndexed { index, notification ->
                    NotificationCard(notification = notification)
                    if (index < notifications.lastIndex) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewNotificationCardsWithBorders() {
    NotificationCardsWithBorders()
}

@Preview(showBackground = true)
@Composable
private fun PreviewSingleNotificationCard() {
    Column(modifier = Modifier.padding(16.dp)) {
        NotificationCard(
            notification = Notification(1, NotificationType.UPCOMING_DOSE, "Upcoming Dose", "lexapro in 5 hours 19 mins", "1 min ago", "lexapro")
        )
        Spacer(modifier = Modifier.height(16.dp))
        NotificationCard(
            notification = Notification(2, NotificationType.REFILL_REMINDER, "Refill Reminder", "vitamin C is running low", "1 hour ago", "vitamin C")
        )
        Spacer(modifier = Modifier.height(16.dp))
        NotificationCard(
            notification = Notification(3, NotificationType.STREAK_MILESTONE, "Streak Milestone", "7 days in a row!", "1 day ago")
        )
        Spacer(modifier = Modifier.height(16.dp))
        NotificationCard(
            notification = Notification(4, NotificationType.MISSED_DOSE, "Missed Dose", "vitamin C at 8:00 AM", "2 week(s) ago", "vitamin C")
        )
    }
}