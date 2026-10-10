package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DisruptionSeverity
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CoralDisruption
import com.example.ui.theme.ElectricLavender
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MidnightNavyBorder
import com.example.ui.theme.MidnightNavyCard
import com.example.ui.theme.MidnightNavyDark
import com.example.ui.theme.MidnightNavyElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanGlow
import com.example.ui.theme.RichViolet
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.TravelViewModel
import com.example.ui.viewmodel.VaygoScreen

@Composable
fun JourneyTrackingScreen(
    viewModel: TravelViewModel,
    modifier: Modifier = Modifier
) {
    val telemetry by viewModel.journeyTelemetry.collectAsState()
    val booking = viewModel.activeBooking.collectAsState().value
    val searchQuery by viewModel.searchQuery.collectAsState()

    val origin = booking?.origin ?: searchQuery.origin
    val destination = booking?.destination ?: searchQuery.destination
    val operator = booking?.operatorName ?: "Orange Travels Premium Sleeper"

    // Infinite pulsing animation for current vehicle dot
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightNavyDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(VaygoScreen.HOME) },
                    modifier = Modifier.testTag("back_to_home_from_track")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Live Journey Tracking",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$origin → $destination ($operator)",
                        color = NeonCyan,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Live Route Map Canvas Representation
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .testTag("live_map_canvas_card"),
                colors = CardDefaults.cardColors(containerColor = MidnightNavyCard)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        val startX = 60f
                        val startY = canvasHeight * 0.75f
                        val endX = canvasWidth - 60f
                        val endY = canvasHeight * 0.25f

                        // Highway path line
                        drawLine(
                            color = Color(0xFF282856),
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = 14f
                        )

                        // Progress line
                        val currentProgress = telemetry.routeProgressPercent
                        val currentX = startX + (endX - startX) * currentProgress
                        val currentY = startY + (endY - startY) * currentProgress

                        drawLine(
                            color = NeonCyan,
                            start = Offset(startX, startY),
                            end = Offset(currentX, currentY),
                            strokeWidth = 8f
                        )

                        // Start Pin
                        drawCircle(color = ElectricLavender, radius = 10f, center = Offset(startX, startY))

                        // Destination Pin
                        drawCircle(color = RichViolet, radius = 10f, center = Offset(endX, endY))

                        // Current Vehicle Pulsing Glow & Dot
                        drawCircle(
                            color = NeonCyan.copy(alpha = 0.35f),
                            radius = pulseRadius,
                            center = Offset(currentX, currentY)
                        )
                        drawCircle(
                            color = NeonCyan,
                            radius = 9f,
                            center = Offset(currentX, currentY)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 4f,
                            center = Offset(currentX, currentY)
                        )
                    }

                    // Map overlay labels
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MidnightNavyElevated.copy(alpha = 0.9f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldSuccess)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Vehicle GPS Live", color = EmeraldSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Speed & Telemetry Badge
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MidnightNavyElevated.copy(alpha = 0.9f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${telemetry.currentSpeedKmH.toInt()} km/h",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        // Current Location & Progress Details
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
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Current Telemetry Location",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = telemetry.lastKnownLocation,
                        color = ElectricLavender,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Last GPS ping: ${telemetry.lastGpsUpdateTimestamp} • Speed: ${telemetry.currentSpeedKmH} km/h",
                        color = TextMutedDark,
                        fontSize = 10.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${telemetry.distanceCoveredKm} km completed",
                            color = TextSecondaryDark,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${telemetry.totalDistanceKm - telemetry.distanceCoveredKm} km remaining",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { telemetry.routeProgressPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = NeonCyan,
                        trackColor = MidnightNavyElevated,
                    )
                }
            }
        }

        // Journey Guardian 40-Minute Stop Monitor
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        1.dp,
                        if (telemetry.stationaryMinutes >= 40) CoralDisruption else EmeraldSuccess.copy(alpha = 0.5f),
                        RoundedCornerShape(16.dp)
                    ),
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
                                imageVector = Icons.Default.Security,
                                contentDescription = "Guardian",
                                tint = if (telemetry.stationaryMinutes >= 40) CoralDisruption else EmeraldSuccess,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Stationary Stop Detection",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (telemetry.stationaryMinutes >= 40) Color(0x33EF4444) else Color(0x2210B981)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${telemetry.stationaryMinutes} / 40 min Threshold",
                                color = if (telemetry.stationaryMinutes >= 40) CoralDisruption else EmeraldSuccess,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Vaygo continuously monitors verified GPS pings. If continuous stationary time exceeds 40 minutes, Guardian auto-alerts you and provisions verified alternative buses, trains, and cabs within your remaining budget.",
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Instant Disruption Simulation CTA
                    Button(
                        onClick = { viewModel.triggerDisruptionSimulation(simulate40MinStop = true) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CoralDisruption,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("trigger_40min_stop_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alert",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Simulate 40-Min Breakdown Event",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
