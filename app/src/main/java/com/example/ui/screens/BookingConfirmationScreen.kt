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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
fun BookingConfirmationScreen(
    viewModel: TravelViewModel,
    modifier: Modifier = Modifier
) {
    val booking = viewModel.activeBooking.collectAsState().value ?: return

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightNavyDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Success Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0x2210B981))
                    .border(2.dp, EmeraldSuccess, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = EmeraldSuccess,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Booking Confirmed & Saved",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Protected by Vaygo Journey Guardian™",
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Digital Boarding Pass
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .testTag("digital_boarding_pass_card"),
                colors = CardDefaults.cardColors(containerColor = MidnightNavyCard)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // PNR & Operator header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("PNR NUMBER", color = TextMutedDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = booking.pnr,
                                color = NeonCyan,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x2210B981))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "VERIFIED PASS",
                                color = EmeraldSuccess,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = booking.operatorName,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${booking.provider} • ${booking.transportType}",
                        color = ElectricLavender,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Route Points
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(booking.origin, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                            Text("Board: ${booking.departureTime}", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(booking.boardingPoint.take(22) + "...", color = TextSecondaryDark, fontSize = 10.sp)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(booking.destination, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                            Text("Arrive: ${booking.arrivalTime}", color = ElectricLavender, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(booking.dropPoint.take(22) + "...", color = TextSecondaryDark, fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Passenger & Seat Breakdown
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MidnightNavyElevated)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("PASSENGER", color = TextMutedDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(booking.passengerName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("SEATS", color = TextMutedDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(booking.selectedSeats, color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("TOTAL PAID", color = TextMutedDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("₹${booking.totalPaid}", color = EmeraldSuccess, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stylized QR code representation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "QR Ticket",
                            tint = Color.White,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Scan at boarding point for instant contactless verification.\nStored offline in your device.",
                            color = TextSecondaryDark,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Action Buttons
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Track Live on Map CTA
                Button(
                    onClick = { viewModel.navigateTo(VaygoScreen.JOURNEY_TRACKING) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RichViolet,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("track_journey_cta")
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "Track",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Launch Journey Guardian & Live Map", fontWeight = FontWeight.Bold)
                }

                // Simulate Disruption to test Vaygo's key feature!
                OutlinedButton(
                    onClick = {
                        viewModel.triggerDisruptionSimulation(simulate40MinStop = true)
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("simulate_disruption_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Simulate",
                        tint = AmberWarning,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Simulate Vehicle Stop >40min (Test Recovery)",
                        color = AmberWarning,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.navigateTo(VaygoScreen.SAVED_TRIPS) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("View In Saved Trips", color = ElectricLavender, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
