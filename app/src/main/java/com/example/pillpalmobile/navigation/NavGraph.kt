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
                var medications by remember { mutableStateOf<List<MedicationResponse>>(emptyList()) }
                var isMedLoading by remember { mutableStateOf(true) }

                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                // ------------ LOAD USER PROFILE (UNCHANGED) ------------
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

                                    if (parsed != null) outputFormat.format(parsed) else data.birthday ?: "N/A"
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

                // ------------ LOAD MEDICATIONS (NEW PART) ------------
                LaunchedEffect(Unit) {
                    try {
                        val token = AuthStore.getToken(context)
                        if (token != null) {
                            medications = RetrofitClient.medicationService
                                .getMedications("Bearer $token")
                        }
                    } catch (e: Exception) {
                        println("Error fetching meds: $e")
                    }
                    isMedLoading = false
                }

                // ------------ UI ------------
                if (user != null) {
                    HomeScreen(
                        user = user!!,
                        medications = medications,
                        isMedicationLoading = isMedLoading,
                        navController = navController,
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

            composable("edit_med/{medId}") { backStackEntry ->
                val medId = backStackEntry.arguments?.getString("medId")?.toIntOrNull()
                val context = LocalContext.current
                var medication by remember { mutableStateOf<Medication?>(null) }
                var isLoading by remember { mutableStateOf(true) }

                // LOAD MEDICATION BY ID
                LaunchedEffect(medId) {
                    if (medId != null) {
                        try {
                            val token = AuthStore.getToken(context)
                            if (token != null) {
                                val response = RetrofitClient.medicationService.getMedicationById(
                                    "Bearer $token",
                                    medId
                                )

                                medication = Medication(
                                    id = response.med_id,
                                    name = response.name,
                                    reminderTimes = response.schedule?.times ?: emptyList(),
                                    medicationDate = response.active_start_date ?: "",
                                    repeatEnabled = response.schedule?.repeat_type != null,
                                    repeatFrequency = when (response.schedule?.repeat_type) {
                                        "daily" -> "Daily"
                                        "weekly" -> "Weekly"
                                        "custom" -> "Custom"
                                        else -> "Daily"
                                    },
                                    repeatDays = decodeDayMask(response.schedule?.day_mask),
                                    repeatStartDate = response.schedule?.custom_start.toEpochMillis(),
                                    repeatEndDate = response.schedule?.custom_end.toEpochMillis(),
                                    notes = response.notes ?: ""
                                )
                            }
                        } catch (e: Exception) {
                            println("Error loading med: $e")
                        }
                    }

                    isLoading = false
                }

                if (isLoading) {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    EditMedicationScreen(
                        medication = medication,
                        onNavigateBack = { navController.popBackStack() },
                        onDelete = { id ->
                            // TODO delete endpoint
                            navController.popBackStack()
                        },
                        onSave = { updatedMed ->
                            // TODO save API
                            navController.popBackStack()
                        }
                    )
                }
            }



        }
    }
}