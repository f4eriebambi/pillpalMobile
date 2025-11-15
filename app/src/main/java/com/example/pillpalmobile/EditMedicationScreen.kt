package com.example.pillpalmobile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pillpalmobile.model.Medication
import java.util.*

// https://developer.android.com/develop/ui/compose/components/time-pickers
// https://developer.android.com/develop/ui/compose/components/time-pickers-dialogs#:~:text=The%20AdvancedTimePickerExample%20composable%20creates%20a,between%20dial%20and%20input%20modes.
// https://www.youtube.com/watch?v=Ndp6RyDwPYs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMedicationScreen(
    medication: Medication? = null,
    onNavigateBack: () -> Unit = {},
    onDelete: (Int) -> Unit = {},
    onSave: (Medication) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf(medication?.name ?: "") }
    var medicationId by remember { mutableStateOf(medication?.id ?: 0) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var times by remember { mutableStateOf(mutableStateListOf("10:00", "15:00")) }
    var activeTimeIndex by remember { mutableStateOf(0) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDeleteMedicationDialog by remember { mutableStateOf(false) }
    var showRemoveTimeDialog by remember { mutableStateOf(-1) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
//            Spacer(modifier = Modifier.height(22.dp))

            // header section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 6.dp,
                        spotColor = Color.Black,
                        ambientColor = Color.Black,
//                        shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp),
                        clip = false
                    )
                    .background(
                        Color(0xFFFFFDF4)
                    )
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 60.dp)
            ) {
                Spacer(modifier = Modifier.height(22.dp))

                // cancel button
                Surface(
                    onClick = { showCancelDialog = true },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .align(Alignment.Start)
//                        .shadow(
//                            elevation = 4.dp,
//                            shape = RoundedCornerShape(20.dp)
//                        )
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 19.sp,
                        fontFamily = SFPro,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xff333333),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(38.dp))

                // med name with delete icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete medication",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { showDeleteMedicationDialog = true },
                        tint = Color(0xFF4A4A4A)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = name,
                            fontSize = 28.sp,
                            fontFamily = SFPro,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xff595880)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // border line
                        Box(
                            modifier = Modifier
                                .width(275.dp)
                                .height(2.dp)
                                .background(Color(0xFF595880))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // rest of content goes here
            Column(
//                modifier = Modifier.padding(horizontal = 20.dp)
                modifier = Modifier.fillMaxWidth()
            ) {
                // time picker section
                Text(
                    text = "Edit Reminder Time",
                    fontSize = 23.sp,
                    fontFamily = SFPro,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .padding(horizontal = 44.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 42.dp)
                        .border(1.dp, Color(0xffb7b7b7), RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        // big time display button
                        Button(
                            onClick = { showTimePicker = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFFFFF)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 2.dp
                            )
                        ) {
                            Text(
                                text = if (times.isNotEmpty() && activeTimeIndex < times.size)
                                    times[activeTimeIndex] else "Select Time",
                                fontSize = 32.sp,
                                fontFamily = Inter,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // lsit of times
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            times.forEachIndexed { index, time ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (index == activeTimeIndex) Color(0xFFACBD6F) else Color.White,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .border(
                                            2.dp,
                                            if (index == activeTimeIndex) Color.Black else Color(0xFFACBD6F),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { activeTimeIndex = index }
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    // time text
                                    Text(
                                        text = time,
                                        fontSize = 18.sp,
                                        fontFamily = Inter,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black,
                                        modifier = Modifier.weight(1f)
                                    )

                                    // show X button if more than 1 time
                                    if (times.size > 1) {
                                        IconButton(
                                            onClick = { showRemoveTimeDialog = index },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .align(Alignment.CenterVertically)
                                        ) {
                                            Text(
                                                text = "✕",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // add time button
                        Button(
                            onClick = {
                                if (times.size < 4) {
                                    times.add("10:00")
                                    activeTimeIndex = times.size - 1
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .border(2.dp, Color.Black, RoundedCornerShape(15.dp)),
                            enabled = times.size < 4,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 2.dp
                            ),
                            shape = RoundedCornerShape(10.dp)
                            ) {
                            Text(
                                text = "+ Add Time",
                                fontSize = 18.sp,
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // limit reminder
                        Text(
                            text = "You can add up to 4 reminder times.",
                            fontSize = 16.sp,
                            fontFamily = SFPro,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFA1A1A1),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // time picker modal
                if (showTimePicker) {

                    val timePickerState = rememberTimePickerState(
                        initialHour = 8,
                        initialMinute = 0,
                        is24Hour = true
                    )

                    AlertDialog(
                        onDismissRequest = { showTimePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val formattedTime = String.format(
                                        "%02d:%02d",
                                        timePickerState.hour,
                                        timePickerState.minute
                                    )

                                    if (activeTimeIndex < times.size) {
                                        times[activeTimeIndex] = formattedTime
                                    }

                                    showTimePicker = false
                                }
                            ) {
                                Text(
                                    text = "OK",
                                    fontFamily = SFPro,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFACBD6F),
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(horizontal = 42.dp)
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showTimePicker = false }
                            ) {
                                Text(
                                    text = "Cancel",
                                    fontFamily = SFPro,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF595880),
                                    fontSize = 20.sp,
//                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        },
                        text = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                Text(
                                    text = "Select Time",
                                    fontSize = 20.sp,
                                    fontFamily = SFPro,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF595880),
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                TimePicker(
                                    state = timePickerState,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        },
                        containerColor = Color(0xFFFFFDF4),
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // cancel button modal
        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                confirmButton = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(
                            onClick = {
                                showCancelDialog = false
                                // navigate back to home screen
                                // onNavigateBack()
                            },
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(15.dp)
                                )
                                .background(Color.White, RoundedCornerShape(15.dp))
                        ) {
                            Text(
                                text = "yes",
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp
                            )
                        }

                        TextButton(
                            onClick = { showCancelDialog = false },
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(15.dp)
                                )
                                .background(Color.White, RoundedCornerShape(15.dp))
                        ) {
                            Text(
                                text = "cancel",
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp
                            )
                        }
                    }
                },
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Discard changes?",
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            fontSize = 22.sp,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                },

                text = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Are you sure you want to leave without \nsaving?",
                            textAlign = TextAlign.Center,
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.Medium,
                            fontSize = 22.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            )
        }

        // delete med icon modal
        if (showDeleteMedicationDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteMedicationDialog = false },
                confirmButton = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(
                            onClick = {
                                showDeleteMedicationDialog = false
                                onDelete(medicationId)
                            },
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(15.dp)
                                )
                                .background(Color.White, RoundedCornerShape(15.dp))
                        ) {
                            Text(
                                text = "yes",
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp
                            )
                        }

                        TextButton(
                            onClick = { showDeleteMedicationDialog = false },
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(15.dp)
                                )
                                .background(Color.White, RoundedCornerShape(15.dp))
                        ) {
                            Text(
                                text = "cancel",
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp
                            )
                        }
                    }
                },
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Delete Medication?",
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            fontSize = 22.sp,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                },

                text = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Are you sure you want to delete this\nmedication?",
                            textAlign = TextAlign.Center,
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.Medium,
                            fontSize = 22.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            )
        }

        // remove time X button modal
        if (showRemoveTimeDialog >= 0) {
            AlertDialog(
                onDismissRequest = { showRemoveTimeDialog = -1 },
                confirmButton = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(
                            onClick = {
                                val indexToRemove = showRemoveTimeDialog
                                times.removeAt(indexToRemove)
                                if (activeTimeIndex >= times.size) {
                                    activeTimeIndex = times.size - 1
                                }
                                showRemoveTimeDialog = -1
                            },
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(15.dp)
                                )
                                .background(Color.White, RoundedCornerShape(15.dp))
                        ) {
                            Text(
                                text = "yes",
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp
                            )
                        }

                        TextButton(
                            onClick = { showRemoveTimeDialog = -1 },
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(15.dp)
                                )
                                .background(Color.White, RoundedCornerShape(15.dp))
                        ) {
                            Text(
                                text = "cancel",
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp
                            )
                        }
                    }
                },
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Remove Time?",
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            fontSize = 22.sp,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                },

                text = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Are you sure you want to remove this\nreminder time?",
                            textAlign = TextAlign.Center,
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.Medium,
                            fontSize = 22.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            )
        }
    }
}