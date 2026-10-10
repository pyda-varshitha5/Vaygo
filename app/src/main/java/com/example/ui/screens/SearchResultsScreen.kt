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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.TransportOption
import com.example.ui.components.TransportCard
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
fun SearchResultsScreen(
    viewModel: TravelViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val categorizedPlans by viewModel.categorizedPlans.collectAsState()

    var selectedSort by remember { mutableStateOf("All") }

    val sortedOptions = remember(searchResults, selectedSort, searchQuery.totalBudget) {
        when (selectedSort) {
            "Smart Saver" -> searchResults.sortedBy { it.totalEstimatedCost }
            "Rapid Express" -> searchResults.sortedBy { it.durationHours }
            "Best Value" -> searchResults.sortedByDescending { it.rating * 100 - (it.totalEstimatedCost / 30) }
            "Highest Rated" -> searchResults.sortedByDescending { it.rating }
            else -> searchResults
        }
    }

    val affordableCount = searchResults.count { it.totalEstimatedCost <= searchQuery.totalBudget }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightNavyDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Route Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(VaygoScreen.HOME) },
                    modifier = Modifier.testTag("back_to_home_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${searchQuery.origin} → ${searchQuery.destination}",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "${searchQuery.date} • Budget: ₹${searchQuery.totalBudget} • ${searchQuery.travelers} Pax",
                        color = ElectricLavender,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Budget Intelligence Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MidnightNavyBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MidnightNavyCard)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "Budget Engine",
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Budget Intelligence",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x2210B981))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "$affordableCount of ${searchResults.size} options within budget",
                                color = EmeraldSuccess,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Vaygo adds mandatory taxes and estimated local drop-off transfers to ensure your ₹${searchQuery.totalBudget} budget is 100% protected.",
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Quick Filter / Sort Pills
        item {
            Text("SORT & PERSPECTIVES", color = TextMutedDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val sortOptions = listOf("All", "Smart Saver", "Rapid Express", "Best Value", "Highest Rated")
                items(sortOptions) { sort ->
                    val isSelected = selectedSort == sort
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) NeonCyan else MidnightNavyElevated)
                            .border(1.dp, if (isSelected) NeonCyan else MidnightNavyBorder, RoundedCornerShape(20.dp))
                            .clickable { selectedSort = sort }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("sort_filter_${sort.lowercase().replace(" ", "_")}")
                    ) {
                        Text(
                            text = sort,
                            color = if (isSelected) MidnightNavyDark else Color.White,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Loading State
        if (isSearching) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = NeonCyan)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Querying redBus, IRCTC & Authorized Carriers...",
                            color = TextSecondaryDark,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            // Categorized Plan Highlights (When "All" is selected)
            if (selectedSort == "All" && categorizedPlans.isNotEmpty()) {
                item {
                    Text("RECOMMENDED PLANS FOR YOU", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                items(categorizedPlans) { planCategory ->
                    TransportCard(
                        option = planCategory.option,
                        userBudget = searchQuery.totalBudget,
                        badgeTitle = planCategory.badge,
                        onSelectOption = {
                            viewModel.selectOptionForBooking(planCategory.option)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("ALL VERIFIED SCHEDULES", color = TextMutedDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Full List of Options
            items(sortedOptions) { option ->
                TransportCard(
                    option = option,
                    userBudget = searchQuery.totalBudget,
                    onSelectOption = {
                        viewModel.selectOptionForBooking(option)
                    }
                )
            }

            // Coverage explanation footer
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MidnightNavyElevated)
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Coverage",
                            tint = ElectricLavender,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Vaygo connects directly to authorized transport inventory feeds. Live seat counts and verified fares refreshed within last 2 minutes.",
                            color = TextSecondaryDark,
                            fontSize = 11.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
