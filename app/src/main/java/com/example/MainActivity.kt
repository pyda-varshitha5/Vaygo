package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.components.VaygoBottomNav
import com.example.ui.components.VaygoTopAppBar
import com.example.ui.screens.BookingConfirmationScreen
import com.example.ui.screens.DisruptionRecoveryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JourneyTrackingScreen
import com.example.ui.screens.SavedTripsScreen
import com.example.ui.screens.SearchResultsScreen
import com.example.ui.screens.SeatSelectionScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MidnightNavyDark
import com.example.ui.theme.VaygoTheme
import com.example.ui.viewmodel.TravelViewModel
import com.example.ui.viewmodel.VaygoScreen

class MainActivity : ComponentActivity() {
    private val viewModel: TravelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VaygoTheme {
                VaygoApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun VaygoApp(viewModel: TravelViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isGuest by viewModel.isGuestMode.collectAsState()

    // Handle system back navigation gracefully
    BackHandler(enabled = currentScreen != VaygoScreen.HOME) {
        when (currentScreen) {
            VaygoScreen.SEAT_SELECTION -> viewModel.navigateTo(VaygoScreen.SEARCH_RESULTS)
            VaygoScreen.DISRUPTION_RECOVERY -> viewModel.navigateTo(VaygoScreen.JOURNEY_TRACKING)
            VaygoScreen.BOOKING_PASS -> viewModel.navigateTo(VaygoScreen.SAVED_TRIPS)
            else -> viewModel.navigateTo(VaygoScreen.HOME)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightNavyDark)
            .windowInsetsPadding(WindowInsets.statusBars),
        topBar = {
            VaygoTopAppBar(
                currentScreen = currentScreen,
                onLogoClick = { viewModel.navigateTo(VaygoScreen.HOME) },
                onProfileClick = { viewModel.navigateTo(VaygoScreen.SETTINGS) },
                onGuardianClick = { viewModel.navigateTo(VaygoScreen.JOURNEY_TRACKING) },
                isGuest = isGuest
            )
        },
        bottomBar = {
            VaygoBottomNav(
                currentScreen = currentScreen,
                onNavigate = { screen -> viewModel.navigateTo(screen) },
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            )
        },
        containerColor = MidnightNavyDark
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                VaygoScreen.HOME -> HomeScreen(viewModel = viewModel)
                VaygoScreen.SEARCH_RESULTS -> SearchResultsScreen(viewModel = viewModel)
                VaygoScreen.SEAT_SELECTION -> SeatSelectionScreen(viewModel = viewModel)
                VaygoScreen.BOOKING_PASS -> BookingConfirmationScreen(viewModel = viewModel)
                VaygoScreen.JOURNEY_TRACKING -> JourneyTrackingScreen(viewModel = viewModel)
                VaygoScreen.DISRUPTION_RECOVERY -> DisruptionRecoveryScreen(viewModel = viewModel)
                VaygoScreen.SAVED_TRIPS -> SavedTripsScreen(viewModel = viewModel)
                VaygoScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
