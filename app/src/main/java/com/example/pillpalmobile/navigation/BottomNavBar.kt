package com.example.pillpalmobile.navigation

import androidx.compose.foundation.Image
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
import coil.compose.rememberAsyncImagePainter
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
            iconUrl = "https://cdn-icons-png.flaticon.com/128/8370/8370951.png",
            label = "home",
            selected = current == "home",
            onClick = { navController.navigate("home") }
        )

        NavItem(
            iconUrl = "https://cdn-icons-png.flaticon.com/128/8690/8690853.png",
            label = "history",
            selected = current == "history",
            onClick = { navController.navigate("history") }
        )

        NavItem(
            iconUrl = "https://cdn-icons-png.flaticon.com/128/2886/2886665.png",
            label = "calendar",
            selected = current == "calendar",
            onClick = { navController.navigate("calendar") }
        )

        NavItem(
            iconUrl = "https://cdn-icons-png.flaticon.com/512/9623/9623115.png",
            label = "settings",
            selected = current == "settings",
            onClick = { navController.navigate("settings") }
        )
    }
}

@Composable
fun NavItem(
    iconUrl: String,
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
            Image(
                painter = rememberAsyncImagePainter(iconUrl),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = Montserrat,
            color = Color.Black
        )
    }
}
