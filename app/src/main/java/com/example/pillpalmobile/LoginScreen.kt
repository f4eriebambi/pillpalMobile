package com.example.pillpalmobile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.pillpalmobile.data.AuthStore
import com.example.pillpalmobile.network.RetrofitClient
import com.example.pillpalmobile.model.LoginRequest

@Composable
fun LoginScreen(
    onNavigateToSignUp: () -> Unit,
    onNavigateToHome: () -> Unit,
    onForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current


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
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {

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
                        Text("Email address", color = Color(0xFF828282), fontSize = 18.sp)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, Color.Gray, RoundedCornerShape(15.dp)),
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(36.dp))

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
                        Text("Password", color = Color(0xFF828282), fontSize = 18.sp)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, Color.Gray, RoundedCornerShape(15.dp)),
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(
                                    if (passwordVisible) R.drawable.password_show else R.drawable.password_hide
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Text(
                    text = "Forgot Password?",
                    fontSize = 14.sp,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable { onForgotPassword() }
                )
            }

            if (errorMessage != null) {
                Text(errorMessage!!, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(60.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()

                    println("Login button clicked: $email / $password")

                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Please enter email & password"
                        return@Button
                    }

                    scope.launch {
                        try {
                            val res = RetrofitClient.authService.login(
                                LoginRequest(email.trim(), password.trim())
                            )
                            println("Login Response Code: ${res.code()}")

                            if (res.isSuccessful) {
                                val body = res.body()
                                if (body != null) {
                                    AuthStore.saveToken(context, body.token)
                                    AuthStore.loadToken(context)
                                    onNavigateToHome()

                                } else {
                                    errorMessage = "Empty response"
                                }
                            } else {
                                errorMessage = "Invalid credentials (${res.code()})"
                            }
                        } catch (e: Exception) {
                            println("Login Error: ${e.localizedMessage}")
                            errorMessage = "Network error"
                        }
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(2.dp, Color(0xFF595880), RoundedCornerShape(15.dp)),
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

            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Don't have an account? ", fontFamily = Inter)
                Text(
                    text = "Sign up",
                    fontFamily = Inter,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onNavigateToSignUp() }
                )
            }
        }
    }
}
