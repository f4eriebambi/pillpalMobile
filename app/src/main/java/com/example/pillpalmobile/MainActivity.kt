package com.example.pillpalmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.pillpalmobile.model.MedicationViewModel
import com.example.pillpalmobile.navigation.AppNavGraph
import com.example.pillpalmobile.ui.theme.PillPalMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PillPalMobileTheme {

                val navController = rememberNavController()
                val medicationViewModel: MedicationViewModel = viewModel()

                AppNavGraph(
                    navController = navController,
                    medicationViewModel = medicationViewModel
                )
            }
        }
    }
}
