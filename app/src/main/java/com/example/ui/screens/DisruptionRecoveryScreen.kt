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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TransportCard
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CoralDisruption
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
fun DisruptionRecoveryScreen(
    viewModel: TravelViewModel,
    modifier: Modifier = Modifier
) {
    val telemetry by viewModel.journeyTelemetry.collectAsState()
    val recoveryAlternatives by viewModel.recoveryAlternatives.collectAsState()
    val aiAdvisory by viewModel.aiAdvisory.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightNavyDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(VaygoScreen.JOURNEY_TRACKING) },
                    modifier = Modifier.testTag("back_from_recovery_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Vaygo Journey Recovery",
                        color = CoralDisruption,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Autonomous Contingency Routing Engine",
                        color = ElectricLavender,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Critical Disruption Alert Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, CoralDisruption, RoundedCornerShape(16.dp))
                    .testTag("critical_disruption_alert_banner"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E0A16))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0x33EF4444)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Critical Disruption",
                                tint = CoralDisruption,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "Stationary Stop Alert (>40 mins)",
                                color = CoralDisruption,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Continuous stop detected: 42 minutes",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = telemetry.disruptionAlertMessage
                            ?: "Vaygo Journey Guardian: Your tracked vehicle has remained stationary for over 40 minutes at NH-65 Suryapet Bypass. We're actively querying affordable alternatives.",
                        color = Color.White,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                // Dismiss false alarm / resume normal
                                viewModel.triggerDisruptionSimulation(simulate40MinStop = false)
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("dismiss_false_alarm_button")
                        ) {
                            Text("Vehicle Resumed (Dismiss)", color = ElectricLavender, fontSize = 11.sp)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MidnightNavyElevated)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "GPS verified",
                                color = EmeraldSuccess,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Gemini AI Advisory Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MidnightNavyCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI",
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Vaygo Guardian AI Assistance",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (isAiLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = NeonCyan,
                                strokeWidth = 2.dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = aiAdvisory
                            ?: "Vaygo Guardian verified that your vehicle has stopped at NH-65 Suryapet Bypass for 42 minutes. We found 3 immediate alternative options within your remaining budget so you reach your destination without getting stranded.",
                        color = ElectricLavender,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Recovery Alternatives Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RESCUE & ALTERNATIVE ROUTES",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${recoveryAlternatives.size} verified options",
                    color = TextMutedDark,
                    fontSize = 11.sp
                )
            }
        }

        items(recoveryAlternatives) { alternative ->
            TransportCard(
                option = alternative,
                userBudget = searchQuery.totalBudget,
                badgeTitle = "Emergency Replacement",
                onSelectOption = {
                    viewModel.rebookDisruptedJourney(alternative)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
