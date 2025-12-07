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
import com.example.pillpalmobile.model.MedicationResponse
import com.example.pillpalmobile.model.Medication
import com.example.pillpalmobile.screens.SettingsScreen
import com.example.pillpalmobile.HistoryScreen
import com.example.pillpalmobile.CalendarScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    var startDestination by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val token = AuthStore.getToken(context)
        startDestination = if (token != null) "home" else "login"
    }

    if (startDestination != null) {
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
                var user by remember { mutableStateOf<User?>(null) }
                var medications by remember { mutableStateOf<List<MedicationResponse>>(emptyList()) }
                var isMedLoading by remember { mutableStateOf(true) }

                val ctx = LocalContext.current
                val coroutine = rememberCoroutineScope()

                // LOAD USER
                LaunchedEffect(Unit) {
                    try {
                        val token = AuthStore.getToken(ctx)
                        if (token != null) {

                            val response = RetrofitClient.authService.getProfile("Bearer $token")

                            if (response.isSuccessful) {
                                val data = response.body()
                                if (data != null) {

                                    val firstName = data.full_name
                                        ?.split(" ")
                                        ?.firstOrNull()
                                        ?.ifBlank { "User" }
                                        ?: "User"

                                    val formattedBirthday = try {
                                        val formats = listOf(
                                            "yyyy-MM-dd",
                                            "yyyy-MM-dd HH:mm:ss",
                                            "EEE MMM dd HH:mm:ss zzz yyyy",
                                            "yyyy-MM-dd'T'HH:mm:ss'Z'"
                                        )
                                        val outFormat = java.text.SimpleDateFormat("dd/MM/yy")

                                        var parsed: java.util.Date? = null

                                        for (f in formats) {
                                            try {
                                                parsed = java.text.SimpleDateFormat(f)
                                                    .parse(data.birthday)
                                                if (parsed != null) break
                                            } catch (_: Exception) {}
                                        }

                                        parsed?.let { outFormat.format(it) }
                                            ?: data.birthday ?: "N/A"

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

                        }
                    } catch (_: Exception) {}
                }

                // LOAD MEDICATIONS
                LaunchedEffect(Unit) {
                    try {
                        val token = AuthStore.getToken(ctx)
                        if (token != null) {
                            medications = RetrofitClient.medicationService
                                .getMedications("Bearer $token")
                        }
                    } catch (e: Exception) {
                        println("Med error: $e")
                    }
                    isMedLoading = false
                }

                if (user != null) {
                    HomeScreen(
                        user = user!!,
                        medications = medications,
                        isMedicationLoading = isMedLoading,
                        navController = navController,
                        onLogout = {
                            coroutine.launch {
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
                var medication by remember { mutableStateOf<Medication?>(null) }
                var isLoading by remember { mutableStateOf(true) }

                LaunchedEffect(medId) {
                    if (medId != null) {
                        try {
                            val token = AuthStore.getToken(ctx)
                            if (token != null) {
                                val res = RetrofitClient.medicationService.getMedicationById(
                                    "Bearer $token",
                                    medId
                                )

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
                            }
                        } catch (_: Exception) {}
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
                        onDelete = { navController.popBackStack() },
                        onSave = { navController.popBackStack() }
                    )
                }
            }


            composable("settings") {
                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                SettingsScreen(
                    navController = navController,

                    onLogoutConfirm = {
                        scope.launch {
                            AuthStore.clearToken(context)
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    },

                    onDeleteAccountConfirm = {
                        scope.launch {
                            val token = AuthStore.getToken(context)

                            if (token != null) {
                                try {
                                    RetrofitClient.authService.deleteAccount("Bearer $token")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            AuthStore.clearToken(context)

                            navController.navigate("signup") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    }
                )
            }


            composable("history") {
                HistoryScreen(navController)
            }

            composable("calendar") {

                CalendarScreen(
                    navController = navController,
                    onAddMedication = {
                        navController.navigate("add_medication")
                    }
                )
            }
            // ADD MEDICATION SCREEN
            composable("add_medication") {
                AddMedicationScreen(navController)
            }


        }
    }
}
