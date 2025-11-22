package com.example.pillpalmobile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pillpalmobile.ui.theme.Inter
import com.example.pillpalmobile.ui.theme.Montserrat
import com.example.pillpalmobile.ui.theme.AppNavigationBar   // ⭐ IMPORT CORRECTO

@Composable
fun SettingsScreen(
    onNavigateBackHome: () -> Unit = {},
    onEditName: () -> Unit = {},
    onOpenAbout: () -> Unit = {},
    onOpenHowToUse: () -> Unit = {},
    onOpenPrivacyPolicy: () -> Unit = {},
    onOpenTerms: () -> Unit = {},
    onLogoutConfirm: () -> Unit = {},
    onDeleteAccountConfirm: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 36.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Settings",
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Normal,
                    fontSize = 32.sp,
                    color = Color.Black
                )

                Image(
                    painter = painterResource(id = R.drawable.pixel_health),
                    contentDescription = "Pixel Health Logo",
                    modifier = Modifier
                        .size(width = 120.dp, height = 64.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(28.dp)
                    )
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFFF5F5F5), Color.White)
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {

                Column {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEDEDED), RoundedCornerShape(30.dp))
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TabButton(
                            label = "General",
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 }
                        )
                        TabButton(
                            label = "Help & Support",
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (selectedTab == 0) {
                        GeneralSettingsSection(onEditName, onOpenAbout)
                    } else {
                        HelpSupportSection(onOpenHowToUse, onOpenPrivacyPolicy, onOpenTerms)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
            NotificationSection()
            Spacer(modifier = Modifier.height(24.dp))

            LogoutButtons(
                onShowLogout = { showLogoutDialog = true },
                onShowDelete = { showDeleteDialog = true }
            )

            Spacer(modifier = Modifier.height(80.dp))
        }


        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            AppNavigationBar()
        }

        if (showLogoutDialog) {
            LogoutConfirmDialog(
                title = "Log Out",
                message = "Do you want to log out?",
                onConfirm = {
                    showLogoutDialog = false
                    onLogoutConfirm()
                },
                onDismiss = { showLogoutDialog = false }
            )
        }

        if (showDeleteDialog) {
            LogoutConfirmDialog(
                title = "Delete Account",
                message = "This action cannot be undone.",
                onConfirm = {
                    showDeleteDialog = false
                    onDeleteAccountConfirm()
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
    }
}

// ----------------------- (EL RESTO DE TUS FUNCIONES IGUAL) -----------------------

@Composable
fun RowScope.TabButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = if (selected) Color.White else Color.Transparent
    val textColor = if (selected) Color.Black else Color(0xFF9A9A9A)
    val fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal

    Box(
        modifier = Modifier
            .weight(1f)
            .height(38.dp)
            .background(background, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = Montserrat,
            fontSize = 16.sp,
            fontWeight = fontWeight,
            color = textColor
        )
    }
}

@Composable
fun GeneralSettingsSection(
    onEditName: () -> Unit,
    onOpenAbout: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SettingRow("Name", "Edit", true, onEditName)
        SettingRow("Date of birth", "Not Editable", false)
        SettingRow("About PillPal", "Click Here", true, onOpenAbout)
        SettingRow("Time Zone", "System Default", false)
    }
}

@Composable
fun HelpSupportSection(
    onOpenHowToUse: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTerms: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SettingRow("How to use PillPal", "Click Here", true, onOpenHowToUse)
        ToggleSettingRow(label = "Dyslexia-Friendly Font")
        SettingRow("Privacy Policy", "Click Here", true, onOpenPrivacyPolicy)
        SettingRow("Terms of Service", "Click Here", true, onOpenTerms)
    }
}

@Composable
fun NotificationSection() {
    var sound by remember { mutableStateOf(true) }
    var vibration by remember { mutableStateOf(true) }
    var deviceNotif by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFF918C84), RoundedCornerShape(14.dp))
            .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 14.dp)
    ) {
        Text(
            text = "Notifications & Alerts",
            fontFamily = Montserrat,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 14.dp, vertical = 4.dp)
        ) {
            NotificationToggle("Sound", sound) { sound = it }
            NotificationToggle("Vibration", vibration) { vibration = it }
            NotificationToggle("Device Notifications", deviceNotif) { deviceNotif = it }
        }
    }
}

@Composable
fun LogoutButtons(
    onShowLogout: () -> Unit,
    onShowDelete: () -> Unit
) {
    Column {

        Button(
            onClick = onShowLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(15.dp)
                ),
            colors = ButtonDefaults.buttonColors(Color(0xFFCC0000)),
            shape = RoundedCornerShape(15.dp)
        ) {
            Text(
                "Log Out",
                fontFamily = Montserrat,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = onShowDelete,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(15.dp)
                )
                .background(Color.White, RoundedCornerShape(15.dp)),
            shape = RoundedCornerShape(15.dp),
            border = BorderStroke(2.dp, Color.Red)
        ) {
            Text(
                "Delete Account",
                fontFamily = Montserrat,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = Color.Red
            )
        }
    }
}

@Composable
fun SettingRow(label: String, value: String, clickable: Boolean, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable(enabled = clickable) { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontFamily = Inter, fontSize = 16.sp)

        Column(horizontalAlignment = Alignment.End) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    value,
                    fontFamily = Inter,
                    fontSize = 16.sp,
                    color = if (clickable) Color.Black else Color.Gray
                )

                if (clickable) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Image(
                        painter = painterResource(id = R.drawable.up_right_arrow),
                        contentDescription = null,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            HorizontalDivider(
                color = Color(0xFFE5E5E5),
                thickness = 1.dp,
                modifier = Modifier.width(130.dp)
            )
        }
    }
}

@Composable
fun ToggleSettingRow(label: String) {
    var enabled by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontFamily = Inter, fontSize = 16.sp)

        Switch(
            checked = enabled,
            onCheckedChange = { enabled = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF4CAF50),
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.Gray
            ),
            modifier = Modifier.padding(end = 2.dp)
        )
    }
}

@Composable
fun NotificationToggle(label: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = Inter,
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.padding(start = 2.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Switch(
            checked = value,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF4CAF50),
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.Gray
            ),
            modifier = Modifier.padding(end = 2.dp)
        )
    }
}

@Composable
fun LogoutConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.30f))
                .clickable { onDismiss() }
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White.copy(alpha = 0.85f))
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(horizontal = 28.dp, vertical = 26.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = message,
                    fontFamily = Inter,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Button(
                        onClick = {
                            onConfirm()
                        },
                        colors = ButtonDefaults.buttonColors(Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.width(110.dp)
                    ) {
                        Text("OK", color = Color.Black, fontFamily = Montserrat)
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.width(110.dp)
                    ) {
                        Text("Cancel", color = Color.Black, fontFamily = Montserrat)
                    }
                }
            }
        }
    }
}
