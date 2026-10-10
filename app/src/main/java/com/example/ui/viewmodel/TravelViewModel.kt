package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BookingEntity
import com.example.data.local.SearchHistoryEntity
import com.example.data.local.VaygoDatabase
import com.example.data.model.DisruptionSeverity
import com.example.data.model.JourneyTelemetry
import com.example.data.model.PassengerInfo
import com.example.data.model.PlanCategory
import com.example.data.model.SeatDeck
import com.example.data.model.SeatItem
import com.example.data.model.TransportOption
import com.example.data.model.TransportType
import com.example.data.model.TravelSearchQuery
import com.example.data.repository.GeminiTravelService
import com.example.data.repository.TravelInventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class VaygoScreen {
    HOME,
    SEARCH_RESULTS,
    SEAT_SELECTION,
    BOOKING_PASS,
    JOURNEY_TRACKING,
    DISRUPTION_RECOVERY,
    SAVED_TRIPS,
    SETTINGS
}

class TravelViewModel(application: Application) : AndroidViewModel(application) {

    private val db = VaygoDatabase.getInstance(application)
    private val repository = TravelInventoryRepository()
    private val geminiService = GeminiTravelService()

    // Screen navigation state
    private val _currentScreen = MutableStateFlow(VaygoScreen.HOME)
    val currentScreen: StateFlow<VaygoScreen> = _currentScreen.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow(TravelSearchQuery())
    val searchQuery: StateFlow<TravelSearchQuery> = _searchQuery.asStateFlow()

    // Results
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _searchResults = MutableStateFlow<List<TransportOption>>(emptyList())
    val searchResults: StateFlow<List<TransportOption>> = _searchResults.asStateFlow()

    private val _categorizedPlans = MutableStateFlow<List<PlanCategory>>(emptyList())
    val categorizedPlans: StateFlow<List<PlanCategory>> = _categorizedPlans.asStateFlow()

    // Selected Option for Booking
    private val _selectedOption = MutableStateFlow<TransportOption?>(null)
    val selectedOption: StateFlow<TransportOption?> = _selectedOption.asStateFlow()

    // Seat map & selections
    private val _seatList = MutableStateFlow<List<SeatItem>>(emptyList())
    val seatList: StateFlow<List<SeatItem>> = _seatList.asStateFlow()

    private val _selectedDeck = MutableStateFlow(SeatDeck.LOWER)
    val selectedDeck: StateFlow<SeatDeck> = _selectedDeck.asStateFlow()

    private val _selectedSeatNumbers = MutableStateFlow<Set<String>>(emptySet())
    val selectedSeatNumbers: StateFlow<Set<String>> = _selectedSeatNumbers.asStateFlow()

    // Passenger info
    private val _passengerInfo = MutableStateFlow(PassengerInfo(name = "Karthik Verma", age = "28", gender = "Male", phone = "+91 98765 43210"))
    val passengerInfo: StateFlow<PassengerInfo> = _passengerInfo.asStateFlow()

    // Active booking for pass
    private val _activeBooking = MutableStateFlow<BookingEntity?>(null)
    val activeBooking: StateFlow<BookingEntity?> = _activeBooking.asStateFlow()

    // Saved bookings from Room
    val allSavedBookings: StateFlow<List<BookingEntity>> = db.bookingDao().getAllBookings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Recent searches from Room
    val recentSearches: StateFlow<List<SearchHistoryEntity>> = db.searchHistoryDao().getRecentSearches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Journey Guardian Telemetry & Disruption Detection
    private val _journeyTelemetry = MutableStateFlow(JourneyTelemetry())
    val journeyTelemetry: StateFlow<JourneyTelemetry> = _journeyTelemetry.asStateFlow()

    private val _recoveryAlternatives = MutableStateFlow<List<TransportOption>>(emptyList())
    val recoveryAlternatives: StateFlow<List<TransportOption>> = _recoveryAlternatives.asStateFlow()

    private val _aiAdvisory = MutableStateFlow<String?>(null)
    val aiAdvisory: StateFlow<String?> = _aiAdvisory.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // Guest vs authenticated mode
    private val _isGuestMode = MutableStateFlow(true)
    val isGuestMode: StateFlow<Boolean> = _isGuestMode.asStateFlow()

    private val _userProfileName = MutableStateFlow("Guest Traveler")
    val userProfileName: StateFlow<String> = _userProfileName.asStateFlow()

    init {
        // Preload default route search
        performSearch(_searchQuery.value)
    }

    fun navigateTo(screen: VaygoScreen) {
        _currentScreen.value = screen
    }

    fun updateSearchQuery(query: TravelSearchQuery) {
        _searchQuery.value = query
    }

    fun swapOriginDestination() {
        val current = _searchQuery.value
        _searchQuery.value = current.copy(
            origin = current.destination,
            destination = current.origin
        )
    }

    fun setQuickCity(origin: String, destination: String) {
        _searchQuery.value = _searchQuery.value.copy(origin = origin, destination = destination)
        performSearch(_searchQuery.value)
    }

    fun processNaturalLanguageQuery(naturalText: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val parsedQuery = geminiService.parseNaturalLanguageQuery(naturalText)
            _searchQuery.value = parsedQuery
            _isAiLoading.value = false
            performSearch(parsedQuery)
        }
    }

    fun performSearch(query: TravelSearchQuery) {
        viewModelScope.launch {
            _isSearching.value = true
            _searchQuery.value = query

            // Record to Room search history
            db.searchHistoryDao().insertSearch(
                SearchHistoryEntity(
                    origin = query.origin,
                    destination = query.destination,
                    date = query.date,
                    budget = query.totalBudget
                )
            )

            val results = repository.searchOptions(query)
            _searchResults.value = results
            _categorizedPlans.value = repository.categorizePlans(results, query.totalBudget)
            _isSearching.value = false
        }
    }

    fun selectOptionForBooking(option: TransportOption) {
        _selectedOption.value = option
        _seatList.value = repository.generateSeatMapForOption(option)
        // Default select first available seat
        val firstAvail = _seatList.value.firstOrNull { it.isAvailable }
        _selectedSeatNumbers.value = if (firstAvail != null) setOf(firstAvail.seatNumber) else emptySet()
        _currentScreen.value = VaygoScreen.SEAT_SELECTION
    }

    fun toggleSeatSelection(seatNumber: String) {
        val current = _selectedSeatNumbers.value.toMutableSet()
        if (current.contains(seatNumber)) {
            if (current.size > 1) { // keep at least 1 seat selected
                current.remove(seatNumber)
            }
        } else {
            current.add(seatNumber)
        }
        _selectedSeatNumbers.value = current
    }

    fun selectDeck(deck: SeatDeck) {
        _selectedDeck.value = deck
    }

    fun updatePassengerInfo(info: PassengerInfo) {
        _passengerInfo.value = info
    }

    fun confirmBooking() {
        val option = _selectedOption.value ?: return
        val seats = _selectedSeatNumbers.value.joinToString(", ")
        val seatCount = _selectedSeatNumbers.value.size
        val totalCost = option.totalEstimatedCost * seatCount
        val pnr = "VYG-" + (100000..999999).random()

        val booking = BookingEntity(
            pnr = pnr,
            itineraryId = option.id,
            provider = option.provider,
            operatorName = option.operatorName,
            transportType = option.transportType.name,
            origin = _searchQuery.value.origin,
            destination = _searchQuery.value.destination,
            travelDate = _searchQuery.value.date,
            departureTime = option.departureTime,
            arrivalTime = option.arrivalTime,
            selectedSeats = seats,
            passengerName = _passengerInfo.value.name.ifBlank { "Karthik Verma" },
            passengerPhone = _passengerInfo.value.phone.ifBlank { "+91 98765 43210" },
            totalPaid = totalCost,
            boardingPoint = option.boardingPoint,
            dropPoint = option.dropPoint,
            bookingStatus = "CONFIRMED"
        )

        viewModelScope.launch {
            db.bookingDao().insertBooking(booking)
            _activeBooking.value = booking
            // Reset telemetry to normal active trip
            _journeyTelemetry.value = JourneyTelemetry(
                currentSpeedKmH = 68.0f,
                routeProgressPercent = 0.35f,
                distanceCoveredKm = 195,
                totalDistanceKm = 620,
                lastKnownLocation = "NH-65 Highway near Suryapet Toll",
                lastGpsUpdateTimestamp = "Just now",
                stationaryMinutes = 0,
                severity = DisruptionSeverity.NORMAL,
                disruptionAlertMessage = null
            )
            _currentScreen.value = VaygoScreen.BOOKING_PASS
        }
    }

    fun viewBookingDetails(booking: BookingEntity) {
        _activeBooking.value = booking
        _currentScreen.value = VaygoScreen.BOOKING_PASS
    }

    // JOURNEY GUARDIAN: 40-Minute Vehicle Stop Disruption Simulation & Real Detection Logic
    fun triggerDisruptionSimulation(simulate40MinStop: Boolean) {
        viewModelScope.launch {
            val booking = _activeBooking.value
            val dest = booking?.destination ?: _searchQuery.value.destination
            val loc = if (simulate40MinStop) "NH-65 Highway near Suryapet Toll (Stationary 42 mins)" else "Service Disrupted by Operator"

            if (simulate40MinStop) {
                val alertMsg = "Vaygo Journey Guardian: Tracked vehicle has remained stationary for 42 minutes (threshold >40m). GPS verified at Suryapet Bypass. Checking immediate affordable alternatives..."
                _journeyTelemetry.value = JourneyTelemetry(
                    currentSpeedKmH = 0.0f,
                    routeProgressPercent = 0.48f,
                    distanceCoveredKm = 245,
                    totalDistanceKm = 620,
                    lastKnownLocation = "NH-65 Suryapet Bypass (Vehicle Stationary)",
                    lastGpsUpdateTimestamp = "1 min ago",
                    stationaryMinutes = 42,
                    severity = DisruptionSeverity.VEHICLE_STOP_CRITICAL,
                    disruptionAlertMessage = alertMsg
                )

                _isAiLoading.value = true
                val recoveryPlans = repository.getDisruptionAlternatives(loc, dest, _searchQuery.value.totalBudget)
                _recoveryAlternatives.value = recoveryPlans

                val advice = geminiService.getDisruptionRecoveryAdvice(
                    incident = "Vehicle stationary over 40 minutes",
                    location = loc,
                    destination = dest,
                    remainingBudget = 750
                )
                _aiAdvisory.value = advice
                _isAiLoading.value = false
                _currentScreen.value = VaygoScreen.DISRUPTION_RECOVERY
            } else {
                // Reset to smooth normal journey
                _journeyTelemetry.value = JourneyTelemetry(
                    currentSpeedKmH = 72.0f,
                    routeProgressPercent = 0.65f,
                    distanceCoveredKm = 405,
                    totalDistanceKm = 620,
                    lastKnownLocation = "NH-65 Highway passing Nalgonda",
                    lastGpsUpdateTimestamp = "Just now",
                    stationaryMinutes = 0,
                    severity = DisruptionSeverity.NORMAL,
                    disruptionAlertMessage = null
                )
                _currentScreen.value = VaygoScreen.JOURNEY_TRACKING
            }
        }
    }

    fun rebookDisruptedJourney(alternative: TransportOption) {
        selectOptionForBooking(alternative)
    }

    fun toggleAuthMode() {
        val currentGuest = _isGuestMode.value
        _isGuestMode.value = !currentGuest
        _userProfileName.value = if (!currentGuest) "Guest Traveler" else "Karthik Verma (Verified)"
    }
}
