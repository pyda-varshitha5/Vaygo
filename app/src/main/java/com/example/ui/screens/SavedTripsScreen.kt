package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.data.local.BookingEntity
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
fun SavedTripsScreen(
    viewModel: TravelViewModel,
    modifier: Modifier = Modifier
) {
    val savedBookings by viewModel.allSavedBookings.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightNavyDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Saved Trips & Tickets",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Persisted offline on your device via Room DB",
                        color = ElectricLavender,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x2210B981))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${savedBookings.size} Bookings",
                        color = EmeraldSuccess,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (savedBookings.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, MidnightNavyBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = MidnightNavyCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MidnightNavyElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = "Empty",
                                tint = ElectricLavender,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "No Saved Trips Yet",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Search for a route and confirm your seats. Your tickets and live telemetry will appear here.",
                            color = TextSecondaryDark,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.navigateTo(VaygoScreen.HOME) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RichViolet,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Plan", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Plan a Journey")
                        }
                    }
                }
            }
        } else {
            items(savedBookings) { booking ->
                SavedBookingCard(
                    booking = booking,
                    onViewPass = {
                        viewModel.viewBookingDetails(booking)
                    },
                    onTrack = {
                        viewModel.viewBookingDetails(booking)
                        viewModel.navigateTo(VaygoScreen.JOURNEY_TRACKING)
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SavedBookingCard(
    booking: BookingEntity,
    onViewPass: () -> Unit,
    onTrack: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MidnightNavyBorder, RoundedCornerShape(16.dp))
            .testTag("saved_booking_${booking.pnr}"),
        colors = CardDefaults.cardColors(containerColor = MidnightNavyCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.pnr,
                    color = NeonCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x2210B981))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = booking.bookingStatus,
                        color = EmeraldSuccess,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = booking.operatorName,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${booking.origin} → ${booking.destination} • ${booking.travelDate}",
                color = ElectricLavender,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Seats: ${booking.selectedSeats}", color = TextSecondaryDark, fontSize = 11.sp)
                Text("Paid: ₹${booking.totalPaid}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onViewPass,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MidnightNavyElevated,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Digital Pass", fontSize = 12.sp)
                }

                Button(
                    onClick = onTrack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RichViolet,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = "Track", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Track Live", fontSize = 12.sp)
                }
            }
        }
    }
}
