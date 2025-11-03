package com.example.pillpalmobile.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pillpalmobile.R

@Composable
fun LoginScreen(
    onNavigateToSignUp: () -> Unit,
    onNavigateToHome: () -> Unit,
    onForgotPassword: () -> Unit
) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
//            .background(Color.White)
                .padding(24.dp)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.pillpal_icon),
                contentDescription = "PillPal Icon",
                modifier = Modifier
                    .size(90.dp)
                    .padding(top = 8.dp)
                    .align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Welcome Back!",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "HI! Good to see you again :)",
                fontSize = 18.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Text(
                text = ".☆ ˖ִ ࣪⚝₊ ⊹˚",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}