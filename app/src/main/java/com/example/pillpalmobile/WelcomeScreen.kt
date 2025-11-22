package com.example.pillpalmobile.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pillpalmobile.MontserratFont
import com.example.pillpalmobile.R

@Composable
fun WelcomeScreen(
    navController: NavController? = null,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToSignUp: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(1.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(150.dp))

        Image(
            painter = painterResource(R.drawable.pillpal_welcome),
            contentDescription = "Welcome to PillPal",
            modifier = Modifier
                .size(300.dp)
                .padding(bottom = 1.dp)
        )

        Spacer(modifier = Modifier.height(64.dp))

        // Welcome text
        Text(
            text = "Welcome to PillPal",
            fontSize = 32.sp,
            fontFamily = MontserratFont,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF595880),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Your personal medication manager",
            fontSize = 16.sp,
            fontFamily = MontserratFont,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 72.dp)
        )

        // sign in button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
        ) {
            Button(
                onClick = {
                    // Use navController if available, otherwise fallback to callback
                    if (navController != null) {
                        navController.navigate("login")
                    } else {
                        onNavigateToLogin()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(
                        width = 2.dp,
                        color = Color(0xFF595880),
                        shape = RoundedCornerShape(15.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFCBCBE7)
                ),
                shape = RoundedCornerShape(15.dp)
            ) {
                Text(
                    text = "Sign in",
                    fontSize = 24.sp,
                    color = Color(0xFF595880),
                    fontFamily = MontserratFont,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // create account button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
        ) {
            Button(
                onClick = {
                    // Use navController if available, otherwise fallback to callback
                    if (navController != null) {
                        navController.navigate("create-account")
                    } else {
                        onNavigateToSignUp()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(
                        width = 2.dp,
                        color = Color(0xFFCBCBE7),
                        shape = RoundedCornerShape(15.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF595880)
                ),
                shape = RoundedCornerShape(15.dp)
            ) {
                Text(
                    text = "Create Account",
                    fontSize = 24.sp,
                    color = Color.White,
                    fontFamily = MontserratFont,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Skip login for development (optional)
        Spacer(modifier = Modifier.height(32.dp))
        TextButton(
            onClick = {
                // Navigate directly to home screen (for testing)
                navController?.navigate("home") {
                    popUpTo(0) { inclusive = true }
                }
            }
        ) {
            Text(
                text = "Skip and go to app",
                fontSize = 14.sp,
                color = Color.Gray,
                fontFamily = MontserratFont,
                fontWeight = FontWeight.Medium
            )
        }
    }
}