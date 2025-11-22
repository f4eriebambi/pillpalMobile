package com.example.pillpalmobile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LoginScreen(
    navController: NavController? = null,
    onNavigateToSignUp: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onForgotPassword: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(R.drawable.pillpal_team),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Welcome to",
                fontSize = 38.sp,
                fontFamily = Montserrat,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(1.dp))

            Text(
                text = "PillPal",
                fontSize = 38.sp,
                fontFamily = Montserrat,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(72.dp))

            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
            ) {
                // enter email
                Text(
                    text = "Email address",
                    fontSize = 20.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = {
                        Text(
                            text = "Email address",
                            color = Color(0xFF828282),
                            fontSize = 18.sp,
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.Normal
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 0.5.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(15.dp)
                        ),
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 18.sp,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Medium,
                        color = if (email.isNotEmpty()) Color.Black else Color(0xFF828282)
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFACBD6F),
                        unfocusedBorderColor = Color.Gray
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(36.dp))

                // password section
                Text(
                    text = "Password",
                    fontSize = 20.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text(
                            text = "Password",
                            color = Color(0xFF828282),
                            fontSize = 18.sp,
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.Normal
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 0.5.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(15.dp)
                        ),
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 18.sp,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Medium,
                        color = if (password.isNotEmpty()) Color.Black else Color(0xFF828282)
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(
                                    if (passwordVisible) R.drawable.password_show else R.drawable.password_hide
                                ),
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFACBD6F),
                        unfocusedBorderColor = Color.Gray
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                // forget password link (might remove)
                Text(
                    text = "Forgot Password?",
                    textAlign = TextAlign.Left,
                    fontSize = 14.sp,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clickable {
                            if (navController != null) {
                                // Navigate to forgot password screen if you have one
                                // navController.navigate("forgot-password")
                            } else {
                                onForgotPassword()
                            }
                        }
                )
            }

            Spacer(modifier = Modifier.height(76.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        // Use navController for navigation if available, otherwise fallback to callback
                        if (navController != null) {
                            navController.navigate("home") {
                                // Clear back stack so user can't go back to login
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            onNavigateToHome()
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
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(
                modifier = Modifier.padding(bottom = 16.dp).background(Color.White),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Don't have an account? ",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Sign up",
                    fontSize = 14.sp,
                    color = Color.Black,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        // Use navController for navigation if available, otherwise fallback to callback
                        if (navController != null) {
                            navController.navigate("create-account")
                        } else {
                            onNavigateToSignUp()
                        }
                    },
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}