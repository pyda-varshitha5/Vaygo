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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricLavender
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MidnightNavyCard
import com.example.ui.theme.MidnightNavyDark
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanGlow
import com.example.ui.theme.RichViolet
import com.example.ui.viewmodel.VaygoScreen

@Composable
fun VaygoTopAppBar(
    currentScreen: VaygoScreen,
    onLogoClick: () -> Unit,
    onProfileClick: () -> Unit,
    onGuardianClick: () -> Unit,
    isGuest: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MidnightNavyDark)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Motif
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onLogoClick() }
                    .testTag("brand_logo_button")
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(RichViolet, NeonCyan)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "V",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Vaygo",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0x3306B6D4))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "AI",
                                color = NeonCyanGlow,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "Smart Journey & Recovery",
                        color = ElectricLavender.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Top Status & Actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Guardian Shield Quick Status
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x2210B981))
                        .border(1.dp, EmeraldSuccess.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .clickable { onGuardianClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("guardian_status_pill"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(EmeraldSuccess)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Guardian Active",
                            color = EmeraldSuccess,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Profile / Guest Pill
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MidnightNavyCard)
                        .border(1.dp, ElectricLavender.copy(alpha = 0.3f), CircleShape)
                        .clickable { onProfileClick() }
                        .testTag("profile_avatar_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User Profile",
                        tint = ElectricLavender,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun VaygoBottomNav(
    currentScreen: VaygoScreen,
    onNavigate: (VaygoScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("bottom_navigation_bar"),
        containerColor = MidnightNavyDark,
        tonalElevation = 8.dp
    ) {
        val navItems = listOf(
            Triple(VaygoScreen.HOME, Icons.Default.Explore, "Plan"),
            Triple(VaygoScreen.SEARCH_RESULTS, Icons.Default.Navigation, "Routes"),
            Triple(VaygoScreen.JOURNEY_TRACKING, Icons.Default.Security, "Guardian"),
            Triple(VaygoScreen.SAVED_TRIPS, Icons.Default.BookmarkBorder, "Trips")
        )

        navItems.forEach { (screen, icon, label) ->
            val selected = currentScreen == screen ||
                    (screen == VaygoScreen.SEARCH_RESULTS && currentScreen == VaygoScreen.SEAT_SELECTION) ||
                    (screen == VaygoScreen.SAVED_TRIPS && currentScreen == VaygoScreen.BOOKING_PASS) ||
                    (screen == VaygoScreen.JOURNEY_TRACKING && currentScreen == VaygoScreen.DISRUPTION_RECOVERY)

            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MidnightNavyDark,
                    selectedTextColor = NeonCyan,
                    indicatorColor = NeonCyan,
                    unselectedIconColor = ElectricLavender.copy(alpha = 0.6f),
                    unselectedTextColor = ElectricLavender.copy(alpha = 0.6f)
                ),
                modifier = Modifier.testTag("nav_item_${label.lowercase()}")
            )
        }
    }
}
