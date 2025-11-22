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
import com.example.pillpalmobile.data.UserRepository
import com.example.pillpalmobile.model.MedicationResponse
import com.example.pillpalmobile.model.UserUI

@Composable
fun AppNavGraph(navController: NavHostController) {

    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        startDestination = if (AuthStore.currentUser != null) "home" else "login"
    }

    if (startDestination != null) {
        NavHost(
            navController = navController,
            startDestination = startDestination!!
        ) {

            // ---------------------------------------------------------
            // LOGIN SCREEN
            // ---------------------------------------------------------
            composable("login") {

                // Create API + Repository for LoginScreen
                val api = ApiClient.instance.create(ApiService::class.java)
                val repo = UserRepository(api)

                LoginScreen(
                    onNavigateToSignUp = { navController.navigate("signup") },
                    onNavigateToHome = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    repo = repo
                )
            }

            // ---------------------------------------------------------
            // SIGNUP SCREEN
            // ---------------------------------------------------------
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

            // ---------------------------------------------------------
            // HOME SCREEN
            // ---------------------------------------------------------
            composable("home") {

                val api = ApiClient.instance.create(ApiService::class.java)
                val backendUser = AuthStore.currentUser

                var uiUser by remember { mutableStateOf<UserUI?>(null) }
                var meds by remember { mutableStateOf<List<MedicationResponse>>(emptyList()) }
                var isMedsLoading by remember { mutableStateOf(true) }

                LaunchedEffect(backendUser?.user_id) {

                    val u = backendUser ?: run {
                        isMedsLoading = false
                        return@LaunchedEffect
                    }

                    // FIX: full_name safe call
                    val firstName = u.full_name
                        ?.split(" ")
                        ?.firstOrNull()
                        ?.ifBlank { "User" }
                        ?: "User"

                    uiUser = UserUI(
                        name = firstName,
                        birthday = "05/11/1970" // placeholder until backend birth_date exists
                    )

                    isMedsLoading = true

                    // FIX: user_id is Int? → must convert to Int
                    val response = try {
                        api.getMedications(u.user_id ?: 0)
                    } catch (e: Exception) {
                        isMedsLoading = false
                        meds = emptyList()
                        return@LaunchedEffect
                    }

                    meds = if (response.isSuccessful) {
                        response.body() ?: emptyList()
                    } else emptyList()

                    isMedsLoading = false
                }

                val userUiLocal = uiUser

                if (userUiLocal != null) {
                    HomeScreen(
                        user = userUiLocal,
                        medications = meds,
                        isMedicationLoading = isMedsLoading,
                        navController = navController,
                        onLogout = {}
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
