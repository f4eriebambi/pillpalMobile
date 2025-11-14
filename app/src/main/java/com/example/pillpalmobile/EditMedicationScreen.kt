package com.example.pillpalmobile

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pillpalmobile.model.Medication
import java.util.*


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
                        elevation = 8.dp,
                        spotColor = Color.Black,
                        ambientColor = Color.Black,
                        shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp),
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
                    onClick = onNavigateBack,
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF0F0F0),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 17.sp,
                        fontFamily = SFPro,
                        fontWeight = FontWeight.Medium,
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
                            .clickable { onDelete(medicationId) },
                        tint = Color(0xFF4A4A4A)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = name,
                            fontSize = 24.sp,
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

            Spacer(modifier = Modifier.height(24.dp))

        }
    }
}