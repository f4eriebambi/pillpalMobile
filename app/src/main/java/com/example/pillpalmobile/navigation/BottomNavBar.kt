package com.example.pillpalmobile.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.pillpalmobile.R
import com.example.pillpalmobile.Montserrat

@Composable
fun BottomNavBar(navController: NavHostController, current: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        NavItem(
            iconRes = R.drawable.home,
            label = "home",
            selected = current == "home",
            onClick = { navController.navigate("home") }
        )

        NavItem(
            iconRes = R.drawable.history,
            label = "history",
            selected = current == "history",
            onClick = { navController.navigate("history") }
        )

        NavItem(
            iconRes = R.drawable.add_calendar,
            label = "calendar",
            selected = current == "calendar",
            onClick = { navController.navigate("calendar") }
        )

        NavItem(
            iconRes = R.drawable.user_settings,
            label = "settings",
            selected = current == "settings",
            onClick = { navController.navigate("settings") }
        )
    }
}

@Composable
fun NavItem(
    iconRes: Int,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Box(
            modifier = Modifier
                .size(70.dp)
                .background(
                    color = if (selected) Color(0xFFCBCBE7) else Color.White,
                    shape = CircleShape
                )
                .border(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) Color(0xFF595880) else Color(0xFF7C8081),
                    shape = CircleShape
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = Color.Unspecified
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = Montserrat,
            color = Color.Black
        )
    }
}
