package com.example.pillpalmobile.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pillpalmobile.CreateAccountScreen
import com.example.pillpalmobile.HomeScreen
import com.example.pillpalmobile.LoginScreen
import com.example.pillpalmobile.data.ApiClient
import com.example.pillpalmobile.data.ApiService
import com.example.pillpalmobile.data.AuthStore
import com.example.pillpalmobile.model.MedicationResponse
import com.example.pillpalmobile.model.UserUI

@Composable
fun AppNavGraph(navController: NavHostController) {
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        startDestination = if (AuthStore.currentUser != null) "home" else "login"
    }

    val api = remember { ApiClient.instance.create(ApiService::class.java) }

    if (startDestination != null) {
        NavHost(
            navController = navController,
            startDestination = startDestination!!
        ) {
            composable("login") {
                LoginScreen(
                    onNavigateToSignUp = { navController.navigate("signup") },
                    onNavigateToHome = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onForgotPassword = { /* TODO */ }
                )
            }

            composable("signup") {
                CreateAccountScreen(
                    onAccountCreated = {
                        navController.navigate("home") {
                            popUpTo("signup") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate("login") {
                            popUpTo("signup") { inclusive = true }
                        }
                    }
                )
            }

            composable("home") {
                val backendUser = AuthStore.currentUser

                var uiUser by remember { mutableStateOf<UserUI?>(null) }
                var meds by remember { mutableStateOf<List<MedicationResponse>>(emptyList()) }
                var isMedsLoading by remember { mutableStateOf(true) }

                LaunchedEffect(backendUser?.user_id) {
                    val u = backendUser ?: run {
                        isMedsLoading = false
                        return@LaunchedEffect
                    }

                    // nombre + cumpleaños ficticio (no viene en el LoginResponse)
                    val firstName = u.full_name
                        .split(" ")
                        .firstOrNull()
                        ?.ifBlank { "User" }
                        ?: "User"

                    uiUser = UserUI(
                        name = firstName,
                        birthday = "05/11/1970" // placeholder hasta que tengas birth_date real
                    )

                    isMedsLoading = true

                    val response = try {
                        api.getMedications(u.user_id)
                    } catch (e: Exception) {
                        isMedsLoading = false
                        meds = emptyList()
                        return@LaunchedEffect
                    }

                    if (response.isSuccessful) {
                        meds = response.body() ?: emptyList()
                    } else {
                        meds = emptyList()
                    }

                    isMedsLoading = false
                }

                val userUiLocal = uiUser
                if (userUiLocal != null) {
                    HomeScreen(
                        user = userUiLocal,
                        medications = meds,
                        isMedicationLoading = isMedsLoading,
                        navController = navController,
                        onLogout = {
                            // si quieres implementar logout más tarde
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
