package com.example.pillpalmobile.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pillpalmobile.*
import com.example.pillpalmobile.data.AuthStore
import com.example.pillpalmobile.model.User
import com.example.pillpalmobile.network.RetrofitClient
import kotlinx.coroutines.launch
import com.example.pillpalmobile.R


@Composable
fun AppNavGraph(navController: NavHostController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var startDestination by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val token = AuthStore.getToken(context)
        startDestination = if (token != null) "home" else "login"
    }

    if (startDestination != null) {
        NavHost(navController = navController, startDestination = startDestination!!) {

            composable("login") {
                LoginScreen(
                    onNavigateToSignUp = { navController.navigate("signup") },
                    onNavigateToHome = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onForgotPassword = {}
                )
            }

            composable("signup") {
                CreateAccountScreen(
                    onAccountCreated = {
                        navController.navigate("home") {
                            popUpTo("signup") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.navigate("login") }
                )
            }
            composable("home") {
                var user by remember { mutableStateOf<User?>(null) }
                val context = LocalContext.current

                LaunchedEffect(Unit) {
                    try {
                        val token = AuthStore.getToken(context)
                        if (token == null) return@LaunchedEffect

                        val response = RetrofitClient.authService.getProfile("Bearer $token")

                        if (response.isSuccessful) {
                            val data = response.body()

                            if (data != null) {

                                val firstName = data.full_name
                                    ?.split(" ")
                                    ?.firstOrNull()
                                    ?.ifBlank { "User" }
                                    ?: "User"

                                // FORMAT BIRTHDAY → dd/MM/yy
                                val formattedBirthday = try {
                                    val inputFormats = listOf(
                                        "yyyy-MM-dd",
                                        "yyyy-MM-dd HH:mm:ss",
                                        "EEE MMM dd HH:mm:ss zzz yyyy",
                                        "yyyy-MM-dd'T'HH:mm:ss'Z'"
                                    )

                                    val outputFormat = java.text.SimpleDateFormat(
                                        "dd/MM/yy",
                                        java.util.Locale.getDefault()
                                    )

                                    var parsed: java.util.Date? = null

                                    for (pattern in inputFormats) {
                                        try {
                                            val sdf = java.text.SimpleDateFormat(
                                                pattern,
                                                java.util.Locale.getDefault()
                                            )
                                            parsed = sdf.parse(data.birthday)
                                            if (parsed != null) break
                                        } catch (_: Exception) {
                                        }
                                    }

                                    if (parsed != null) outputFormat.format(parsed) else data.birthday
                                        ?: "N/A"
                                } catch (_: Exception) {
                                    data.birthday ?: "N/A"
                                }

                                user = User(
                                    name = firstName,
                                    birthday = formattedBirthday,
                                    avatarRes = R.drawable.pfp
                                )
                            }

                        }

                    } catch (_: Exception) {
                    }
                }

                val scope = rememberCoroutineScope()

                if (user != null) {
                    HomeScreen(
                        user = user!!,
                        onLogout = {
                            scope.launch {
                                AuthStore.clearToken(context)
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        }
                    )
                } else {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

        }
    }
}