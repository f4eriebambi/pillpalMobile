package com.example.pillpalmobile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pillpalmobile.model.Medication

@Composable
fun EditMedicationScreen(
    medication: Medication?,
    onNavigateBack: () -> Unit,
    onDelete: (Int) -> Unit,
    onSave: (Medication) -> Unit
) {
    var name by remember { mutableStateOf(medication?.name ?: "") }
    var notes by remember { mutableStateOf(medication?.notes ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = "Edit Medication")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onNavigateBack) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    val base = medication ?: Medication(
                        id = -1,
                        name = name,
                        notes = notes
                    )

                    onSave(
                        base.copy(
                            name = name,
                            notes = notes
                        )
                    )
                }
            ) {
                Text("Save")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (medication != null && medication.id != -1) {
            Button(onClick = { onDelete(medication.id) }) {
                Text("Delete")
            }
        }
    }
}
