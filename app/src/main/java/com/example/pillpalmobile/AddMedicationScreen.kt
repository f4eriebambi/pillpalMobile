package com.example.pillpalmobile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pillpalmobile.model.AddMedicationRequest
import androidx.compose.runtime.mutableStateMapOf

@Composable
fun AddMedicationScreen(
    userId: Int,
    onSave: (AddMedicationRequest) -> Unit,
    onCancel: () -> Unit
)
 {
    var name by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("08:00") }

    var repeatType by remember { mutableStateOf("daily") }

    // Usamos un mapa reactivo para que Compose se entere de los cambios
    val days = remember {
        mutableStateMapOf(
            "Mon" to false,
            "Tue" to false,
            "Wed" to false,
            "Thu" to false,
            "Fri" to false,
            "Sat" to false,
            "Sun" to false
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("Add Medication", fontSize = 26.sp)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Medication name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Text("Time (HH:MM)")
        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Text("Repeat Type")
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = repeatType == "daily",
                onClick = { repeatType = "daily" }
            )
            Text("Daily")

            Spacer(Modifier.width(20.dp))

            RadioButton(
                selected = repeatType == "weekly",
                onClick = { repeatType = "weekly" }
            )
            Text("Weekly")
        }

        if (repeatType == "weekly") {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Checkbox(
                            checked = days[day] == true,
                            onCheckedChange = { checked -> days[day] = checked }
                        )
                        Text(day)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onCancel) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    val mask = if (repeatType == "daily") {
                        // todos los días
                        "1111111"
                    } else {
                        listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
                            .map { if (days[it] == true) "1" else "0" }
                            .joinToString("")
                    }

                    onSave(
                        AddMedicationRequest(
                            user_id = userId,
                            name = name,
                            notes = notes.ifBlank { null },
                            times = listOf(time),
                            repeat_type = repeatType,
                            day_mask = mask
                        )
                    )
                },
                enabled = name.isNotBlank() && time.isNotBlank()
            ) {
                Text("Save")
            }
        }
    }
}
