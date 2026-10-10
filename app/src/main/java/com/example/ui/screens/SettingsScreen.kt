package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricLavender
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MidnightNavyBorder
import com.example.ui.theme.MidnightNavyCard
import com.example.ui.theme.MidnightNavyDark
import com.example.ui.theme.MidnightNavyElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.RichViolet
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.TravelViewModel
import com.example.ui.viewmodel.VaygoScreen

@Composable
fun SettingsScreen(
    viewModel: TravelViewModel,
    modifier: Modifier = Modifier
) {
    val isGuestMode by viewModel.isGuestMode.collectAsState()
    val userProfileName by viewModel.userProfileName.collectAsState()

    var pushAlertsEnabled by remember { mutableStateOf(true) }
    var smsAlertsEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightNavyDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(VaygoScreen.HOME) },
                    modifier = Modifier.testTag("back_from_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Profile & Guardian Settings",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Profile Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MidnightNavyBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MidnightNavyCard)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(MidnightNavyElevated)
                            .border(1.dp, ElectricLavender, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = ElectricLavender,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userProfileName,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isGuestMode) "Guest Mode (Local Search & Offline Passes)" else "Verified Account (Full Cloud Sync)",
                            color = if (isGuestMode) NeonCyan else EmeraldSuccess,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = { viewModel.toggleAuthMode() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RichViolet,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("toggle_guest_mode_button")
                    ) {
                        Text(text = if (isGuestMode) "Sign In" else "Sign Out", fontSize = 11.sp)
                    }
                }
            }
        }

        // Journey Guardian Parameters
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MidnightNavyBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MidnightNavyCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Guardian Config",
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Journey Guardian Rule Configuration",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Stationary Stop Trigger", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Continuous stop required to trigger alert", color = TextSecondaryDark, fontSize = 10.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MidnightNavyElevated)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("40 Minutes", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("High-Priority Push Alerts", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Alert upon vehicle stop >40m or cancellation", color = TextSecondaryDark, fontSize = 10.sp)
                        }
                        Switch(
                            checked = pushAlertsEnabled,
                            onCheckedChange = { pushAlertsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NeonCyan
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("SMS Contingency Alerts", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Sends offline SMS if data connectivity drops", color = TextSecondaryDark, fontSize = 10.sp)
                        }
                        Switch(
                            checked = smsAlertsEnabled,
                            onCheckedChange = { smsAlertsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NeonCyan
                            )
                        )
                    }
                }
            }
        }

        // About Vaygo Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MidnightNavyBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MidnightNavyCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("About Vaygo", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Vaygo is an AI-powered smart travel planner, booking assistant & journey recovery platform. Designed to ensure travelers in India and beyond never get stranded.",
                        color = TextSecondaryDark,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Version 1.0.0 • Production Release",
                        color = TextMutedDark,
                        fontSize = 10.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
