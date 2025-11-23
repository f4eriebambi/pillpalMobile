package com.example.pillpalmobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pillpalmobile.*
import com.example.pillpalmobile.data.*
import com.example.pillpalmobile.model.*

@Composable
fun AppNavGraph(
    navController: NavHostController,
    medicationViewModel: MedicationViewModel
) {
    val context = LocalContext.current

    val api = ApiClient.instance.create(ApiService::class.java)
    val userRepo = UserRepository(api)

    AuthStore.loadUser(context)
    val startDestination = if (AuthStore.currentUser != null) "home" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ---------------------------------------------------------
        // LOGIN
        // ---------------------------------------------------------
        composable("login") {
            LoginScreen(
                onNavigateToHome = {
                    AuthStore.loadUser(context)
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate("register") },
                repo = userRepo
            )
        }

        // ---------------------------------------------------------
        // REGISTER
        // ---------------------------------------------------------
        composable("register") {
            CreateAccountScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onAccountCreated = {}
            )
        }

        // ---------------------------------------------------------
        // HOME
        // ---------------------------------------------------------
        composable("home") {

            AuthStore.loadUser(context)
            val user = AuthStore.currentUser

            if (user == null) {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            } else {

                val uiUser = UserUI(
                    id = user.user_id ?: -1,
                    name = user.full_name ?: (user.username ?: "Unknown"),
                    birthday = user.birth_date ?: "Unknown"
                )

                HomeScreen(
                    user = uiUser,
                    navController = navController,
                    viewModel = medicationViewModel
                )
            }
        }

        // ---------------------------------------------------------
        // CALENDAR
        // ---------------------------------------------------------
        composable("calendar") {

            AuthStore.loadUser(context)
            val user = AuthStore.currentUser

            if (user == null) {
                navController.navigate("login")
            } else {

                CalendarScreen(
                    userId = user.user_id ?: -1,
                    viewModel = medicationViewModel,
                    onAddMedication = {
                        navController.navigate("add_med")
                    }
                )
            }
        }

        // ---------------------------------------------------------
        // ADD MEDICATION
        // ---------------------------------------------------------
        composable("add_med") {

            AuthStore.loadUser(context)
            val user = AuthStore.currentUser

            if (user == null) {
                navController.navigate("login")
            } else {

                AddMedicationScreen(
                    userId = user.user_id ?: -1,
                    onCancel = {
                        navController.popBackStack()
                    },
                    onSave = { request: AddMedicationRequest ->

                        // 1️⃣ Crear en backend
                        medicationViewModel.addMedication(request)

                        // 2️⃣ Volver al calendario
                        navController.popBackStack()

                        // 3️⃣ Recargar medicinas desde backend
                        medicationViewModel.loadMedications(user.user_id ?: -1)
                    }
                )
            }
        }
    }
}
