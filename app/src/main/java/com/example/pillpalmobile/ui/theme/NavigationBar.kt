package com.example.pillpalmobile.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pillpalmobile.R

@Composable
fun AppNavigationBar(   // ⭐ RENOMBRADO AQUÍ
    onHome: () -> Unit = {},
    onHistory: () -> Unit = {},
    onAdd: () -> Unit = {},
    onAlerts: () -> Unit = {},
    onSettings: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavButton(R.drawable.home, "home") { onHome() }
        NavButton(R.drawable.history, "history") { onHistory() }
        NavButton(R.drawable.add_calendar, "add") { onAdd() }
        NavButton(R.drawable.bell, "alerts") { onAlerts() }
        NavButton(R.drawable.user_settings, "settings") { onSettings() }
    }
}

@Composable
fun NavButton(iconRes: Int, label: String, onClick: () -> Unit) {
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
                modifier = Modifier.size(34.dp),
                tint = Color.Unspecified
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}
