package com.example.pillpalmobile.navigation

import android.util.Log
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
import com.example.pillpalmobile.R
import com.example.pillpalmobile.data.AuthStore
import com.example.pillpalmobile.model.Medication
import com.example.pillpalmobile.model.User
import com.example.pillpalmobile.network.RetrofitClient
import com.example.pillpalmobile.screens.SettingsScreen
import com.example.pillpalmobile.HistoryScreen
import com.example.pillpalmobile.CalendarScreen
import kotlinx.coroutines.launch
import com.example.pillpalmobile.network.AlertListener

@Composable
fun AppNavGraph(navController: NavHostController) {

    var startDestination by remember { mutableStateOf<String?>(null) }

    // ONLY USE CACHED TOKEN (already loaded by MyApplication)
    LaunchedEffect(Unit) {
        val token = AuthStore.getCachedToken()
        startDestination = if (token.isNullOrEmpty()) "login" else "home"
    }

    if (startDestination == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = startDestination!!
    ) {

        // LOGIN
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

        // SIGNUP
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

        // HOME
        composable("home") {
            val ctx = LocalContext.current
            val scope = rememberCoroutineScope()

            var user by remember { mutableStateOf<User?>(null) }
            var medications by remember { mutableStateOf<List<Medication>>(emptyList()) }
            var isMedLoading by remember { mutableStateOf(true) }

            // LOAD PROFILE
            LaunchedEffect(Unit) {
                try {
                    val res = RetrofitClient.authService.getProfile()
                    if (res.isSuccessful) {
                        res.body()?.let { data ->
                            val firstName = data.full_name?.split(" ")?.firstOrNull() ?: "User"
                            user = User(
                                name = firstName,
                                birthday = data.birthday ?: "N/A",
                                avatarRes = R.drawable.pfp
                            )
                        }
                    } else {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("APP", "User load error: $e")
                }
            }

            // LOAD MEDICATIONS
            LaunchedEffect(Unit) {
                try {
                    val apiMeds = RetrofitClient.medicationService.getMedications()
                    medications = apiMeds.map { med ->
                        Medication(
                            id = med.med_id,
                            name = med.name,
                            reminderTimes = med.schedule?.times ?: emptyList(),
                            medicationDate = med.active_start_date ?: "",
                            repeatEnabled = med.schedule?.repeat_type != null,
                            repeatFrequency = when (med.schedule?.repeat_type) {
                                "daily" -> "Daily"
                                "weekly" -> "Weekly"
                                "custom" -> "Custom"
                                else -> "Daily"
                            },
                            repeatDays = decodeDayMask(med.schedule?.day_mask),
                            repeatStartDate = med.schedule?.custom_start.toEpochMillis(),
                            repeatEndDate = med.schedule?.custom_end.toEpochMillis(),
                            notes = med.notes ?: ""
                        )
                    }
                } catch (e: Exception) {
                    Log.e("APP", "Medication error: $e")
                }
                isMedLoading = false
            }

            if (user != null) {


                LaunchedEffect(Unit) {
                    AlertListener.start(context = ctx, deviceId = 1)
                }

                HomeScreen(
                    user = user!!,
                    medications = medications,
                    isMedicationLoading = isMedLoading,
                    navController = navController,
                    onLogout = {
                        scope.launch {
                            AuthStore.clearToken(ctx)
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


        // EDIT MEDICATION
        composable("edit_med/{medId}") { backStackEntry ->
            val medId = backStackEntry.arguments?.getString("medId")?.toIntOrNull()
            val ctx = LocalContext.current
            val scope = rememberCoroutineScope()

            var medication by remember { mutableStateOf<Medication?>(null) }
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(medId) {
                if (medId != null) {
                    try {
                        val res = RetrofitClient.medicationService.getMedicationById(medId)
                        medication = Medication(
                            id = res.med_id,
                            name = res.name,
                            reminderTimes = res.schedule?.times ?: emptyList(),
                            medicationDate = res.active_start_date ?: "",
                            repeatEnabled = res.schedule?.repeat_type != null,
                            repeatFrequency = when (res.schedule?.repeat_type) {
                                "daily" -> "Daily"
                                "weekly" -> "Weekly"
                                "custom" -> "Custom"
                                else -> "Daily"
                            },
                            repeatDays = decodeDayMask(res.schedule?.day_mask),
                            repeatStartDate = res.schedule?.custom_start.toEpochMillis(),
                            repeatEndDate = res.schedule?.custom_end.toEpochMillis(),
                            notes = res.notes ?: ""
                        )
                    } catch (_: Exception) { }
                }
                isLoading = false
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                EditMedicationScreen(
                    medication = medication,
                    onNavigateBack = { navController.popBackStack() },

                    onDelete = { medIdToDelete ->
                        scope.launch {
                            try {
                                val res =
                                    RetrofitClient.medicationService.deleteMedication(medIdToDelete)
                                if (res.isSuccessful) {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            } catch (_: Exception) { }
                        }
                    },

                    onSave = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // SETTINGS
        composable("settings") {
            val ctx = LocalContext.current
            val scope = rememberCoroutineScope()

            SettingsScreen(
                navController = navController,

                onLogoutConfirm = {
                    scope.launch {
                        AuthStore.clearToken(ctx)
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                },

                onDeleteAccountConfirm = {
                    scope.launch {
                        try {
                            RetrofitClient.authService.deleteAccount()
                        } catch (_: Exception) { }

                        AuthStore.clearToken(ctx)
                        navController.navigate("signup") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                }
            )
        }

        // HISTORY
        composable("history") {
            HistoryScreen(navController)
        }

        // CALENDAR
        composable("calendar") {
            CalendarScreen(
                navController = navController,
                onAddMedication = { navController.navigate("add_medication") }
            )
        }

        // ADD MEDICATION
        composable("add_medication") {
            AddMedicationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
