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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.SeatBerthType
import com.example.data.model.SeatDeck
import com.example.data.model.SeatItem
import com.example.ui.theme.AmberWarning
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
fun SeatSelectionScreen(
    viewModel: TravelViewModel,
    modifier: Modifier = Modifier
) {
    val option = viewModel.selectedOption.collectAsState().value ?: return
    val seatList by viewModel.seatList.collectAsState()
    val selectedDeck by viewModel.selectedDeck.collectAsState()
    val selectedSeatNumbers by viewModel.selectedSeatNumbers.collectAsState()
    val passengerInfo by viewModel.passengerInfo.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val currentDeckSeats = seatList.filter { it.deck == selectedDeck }
    val isSleeper = option.seatType.contains("Sleeper", ignoreCase = true)

    val seatCount = selectedSeatNumbers.size
    val totalFare = option.totalEstimatedCost * seatCount

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightNavyDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Navigation & Bus Title
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(VaygoScreen.SEARCH_RESULTS) },
                    modifier = Modifier.testTag("back_to_results_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = option.operatorName,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${option.seatType} • Departs ${option.departureTime}",
                        color = NeonCyan,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Deck Toggle Tabs (if Sleeper)
        if (isSleeper) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MidnightNavyCard)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedDeck == SeatDeck.LOWER) RichViolet else Color.Transparent)
                            .clickable { viewModel.selectDeck(SeatDeck.LOWER) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Lower Deck",
                            color = if (selectedDeck == SeatDeck.LOWER) Color.White else TextSecondaryDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedDeck == SeatDeck.UPPER) RichViolet else Color.Transparent)
                            .clickable { viewModel.selectDeck(SeatDeck.UPPER) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Upper Deck",
                            color = if (selectedDeck == SeatDeck.UPPER) Color.White else TextSecondaryDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Seat Legend
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LegendItem(color = MidnightNavyElevated, border = NeonCyan, label = "Available")
                LegendItem(color = NeonCyan, border = NeonCyan, label = "Selected")
                LegendItem(color = Color(0xFF282846), border = Color(0xFF38385C), label = "Occupied")
                LegendItem(color = Color(0xFF4A154B), border = Color(0xFFFF69B4), label = "Ladies")
            }
        }

        // Visual Coach Layout
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, MidnightNavyBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MidnightNavyCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Driver cabin representation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MidnightNavyElevated)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = "Driver Cabin ✇", color = TextMutedDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Render rows
                    val rows = currentDeckSeats.groupBy { it.row }
                    rows.toSortedMap().forEach { (rowNumber, seatsInRow) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isSleeper) {
                                // Single left berth
                                val leftSeat = seatsInRow.firstOrNull { it.col == 1 }
                                if (leftSeat != null) {
                                    SeatBox(
                                        seat = leftSeat,
                                        isSelected = selectedSeatNumbers.contains(leftSeat.seatNumber),
                                        onToggle = { viewModel.toggleSeatSelection(leftSeat.seatNumber) },
                                        width = 68.dp,
                                        height = 42.dp
                                    )
                                } else {
                                    Spacer(modifier = Modifier.width(68.dp))
                                }

                                // Aisle
                                Spacer(modifier = Modifier.width(32.dp))

                                // Right double berths
                                val midSeat = seatsInRow.firstOrNull { it.col == 2 }
                                val rightSeat = seatsInRow.firstOrNull { it.col == 3 }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (midSeat != null) {
                                        SeatBox(
                                            seat = midSeat,
                                            isSelected = selectedSeatNumbers.contains(midSeat.seatNumber),
                                            onToggle = { viewModel.toggleSeatSelection(midSeat.seatNumber) },
                                            width = 68.dp,
                                            height = 42.dp
                                        )
                                    }
                                    if (rightSeat != null) {
                                        SeatBox(
                                            seat = rightSeat,
                                            isSelected = selectedSeatNumbers.contains(rightSeat.seatNumber),
                                            onToggle = { viewModel.toggleSeatSelection(rightSeat.seatNumber) },
                                            width = 68.dp,
                                            height = 42.dp
                                        )
                                    }
                                }
                            } else {
                                // 2+2 layout
                                seatsInRow.forEach { seat ->
                                    SeatBox(
                                        seat = seat,
                                        isSelected = selectedSeatNumbers.contains(seat.seatNumber),
                                        onToggle = { viewModel.toggleSeatSelection(seat.seatNumber) },
                                        width = 48.dp,
                                        height = 44.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Passenger Details Form
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MidnightNavyBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MidnightNavyCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Passenger Contact Details",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = passengerInfo.name,
                        onValueChange = { viewModel.updatePassengerInfo(passengerInfo.copy(name = it)) },
                        label = { Text("Lead Traveler Name") },
                        modifier = Modifier.fillMaxWidth().testTag("passenger_name_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MidnightNavyElevated,
                            unfocusedContainerColor = MidnightNavyElevated,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = MidnightNavyBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = passengerInfo.age,
                            onValueChange = { viewModel.updatePassengerInfo(passengerInfo.copy(age = it)) },
                            label = { Text("Age") },
                            modifier = Modifier.weight(1f).testTag("passenger_age_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MidnightNavyElevated,
                                unfocusedContainerColor = MidnightNavyElevated,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = MidnightNavyBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = passengerInfo.phone,
                            onValueChange = { viewModel.updatePassengerInfo(passengerInfo.copy(phone = it)) },
                            label = { Text("Mobile Phone") },
                            modifier = Modifier.weight(2f).testTag("passenger_phone_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MidnightNavyElevated,
                                unfocusedContainerColor = MidnightNavyElevated,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = MidnightNavyBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Fare Breakdown & Confirm Booking Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MidnightNavyElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Selected: ${selectedSeatNumbers.joinToString(", ")} ($seatCount Seat${if (seatCount > 1) "s" else ""})",
                                color = ElectricLavender,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "₹$totalFare",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(Taxes & transfers included)",
                                    color = TextMutedDark,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.confirmBooking() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RichViolet,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("confirm_booking_button")
                        ) {
                            Text(
                                text = "Confirm & Book",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SeatBox(
    seat: SeatItem,
    isSelected: Boolean,
    onToggle: () -> Unit,
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp
) {
    val bgColor = when {
        isSelected -> NeonCyan
        !seat.isAvailable -> Color(0xFF282846)
        seat.isLadiesOnly -> Color(0xFF4A154B)
        else -> MidnightNavyElevated
    }

    val borderColor = when {
        isSelected -> NeonCyan
        !seat.isAvailable -> Color(0xFF38385C)
        seat.isLadiesOnly -> Color(0xFFFF69B4)
        else -> NeonCyan.copy(alpha = 0.3f)
    }

    val textColor = when {
        isSelected -> MidnightNavyDark
        !seat.isAvailable -> TextMutedDark
        else -> Color.White
    }

    Box(
        modifier = Modifier
            .size(width, height)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(enabled = seat.isAvailable) { onToggle() }
            .testTag("seat_item_${seat.seatNumber}"),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = seat.seatNumber,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            if (seat.isAvailable && !isSelected) {
                Text(
                    text = "₹${seat.price}",
                    color = ElectricLavender,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, border: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
                .border(1.dp, border, RoundedCornerShape(3.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, color = TextSecondaryDark, fontSize = 10.sp)
    }
}
