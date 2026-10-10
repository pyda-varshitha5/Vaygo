package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.TransitEnterexit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.TransportOption
import com.example.data.model.TransportType
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.ElectricLavender
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MidnightNavyBorder
import com.example.ui.theme.MidnightNavyCard
import com.example.ui.theme.MidnightNavyElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.RichViolet
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun TransportCard(
    option: TransportOption,
    userBudget: Int,
    badgeTitle: String? = null,
    onSelectOption: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showBreakdown by remember { mutableStateOf(false) }

    val totalCost = option.totalEstimatedCost
    val isWithinBudget = totalCost <= userBudget
    val budgetDifference = userBudget - totalCost

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.dp,
                if (badgeTitle != null) NeonCyan.copy(alpha = 0.5f) else MidnightNavyBorder,
                RoundedCornerShape(16.dp)
            )
            .testTag("transport_card_${option.id}"),
        colors = CardDefaults.cardColors(containerColor = MidnightNavyCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header Row: Badge Title & Provider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (badgeTitle != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x3306B6D4))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = badgeTitle,
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val icon = when (option.transportType) {
                            TransportType.BUS -> Icons.Default.DirectionsBus
                            TransportType.TRAIN -> Icons.Default.Train
                            TransportType.CAB -> Icons.Default.DirectionsCar
                            TransportType.MULTIMODAL -> Icons.Default.TransitEnterexit
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = option.transportType.name,
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = option.provider,
                            color = TextSecondaryDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Confidence badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = option.dataConfidence,
                        color = EmeraldSuccess,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Operator Name & Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = option.operatorName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                // Rating Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MidnightNavyElevated)
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = AmberWarning,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${option.rating}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " (${option.reviewCount})",
                        color = TextMutedDark,
                        fontSize = 10.sp
                    )
                }
            }

            // Seat Type label
            Text(
                text = option.seatType,
                color = ElectricLavender,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Timings & Duration Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = option.departureTime,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = option.boardingPoint.take(24) + if (option.boardingPoint.length > 24) "..." else "",
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )
                }

                // Journey Line with duration
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = option.durationHours,
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(NeonCyan)
                        )
                        Box(
                            modifier = Modifier
                                .width(56.dp)
                                .height(2.dp)
                                .background(MidnightNavyBorder)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "to",
                            tint = NeonCyan,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    if (!option.isDirect) {
                        Text(
                            text = "1 Transfer",
                            color = AmberWarning,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = option.arrivalTime,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = option.dropPoint.take(24) + if (option.dropPoint.length > 24) "..." else "",
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Price & Budget Comparison Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MidnightNavyElevated)
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "₹$totalCost",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "All-inclusive",
                                color = TextMutedDark,
                                fontSize = 10.sp
                            )
                        }

                        // Remaining budget or extra required
                        if (isWithinBudget) {
                            Text(
                                text = "₹$budgetDifference within your ₹$userBudget budget",
                                color = EmeraldSuccess,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Text(
                                text = "₹${-budgetDifference} over budget",
                                color = AmberWarning,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Price breakdown toggle
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { showBreakdown = !showBreakdown }
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Cost breakdown",
                            tint = ElectricLavender,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showBreakdown) "Hide split" else "Cost split",
                            color = ElectricLavender,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Expandable Transparent Cost Breakdown
            if (showBreakdown) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x1A000000))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Base Ticket Fare", color = TextSecondaryDark, fontSize = 11.sp)
                        Text("₹${option.baseFare}", color = Color.White, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Taxes & Mandatory Fees", color = TextSecondaryDark, fontSize = 11.sp)
                        Text("₹${option.mandatoryTaxes}", color = Color.White, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Est. Local Transfer (Auto/Cab to Center)", color = TextSecondaryDark, fontSize = 11.sp)
                        Text("₹${option.estimatedTransferCost}", color = Color.White, fontSize = 11.sp)
                    }
                    if (option.transferNotes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Note: ${option.transferNotes}",
                            color = ElectricLavender,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer: Seats left & Book CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (option.liveSeatAvailability < 5) AmberWarning else EmeraldSuccess
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${option.liveSeatAvailability} seats left",
                        color = if (option.liveSeatAvailability < 5) AmberWarning else TextSecondaryDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = onSelectOption,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RichViolet,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("book_option_${option.id}")
                ) {
                    Text(
                        text = if (option.transportType == TransportType.CAB) "Book Cab" else "Select Seats",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
