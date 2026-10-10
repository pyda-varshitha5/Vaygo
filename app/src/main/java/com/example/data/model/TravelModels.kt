package com.example.data.model

import com.squareup.moshi.JsonClass

enum class TransportType {
    BUS, TRAIN, CAB, MULTIMODAL
}

@JsonClass(generateAdapter = true)
data class TravelSearchQuery(
    val origin: String = "Visakhapatnam",
    val destination: String = "Hyderabad",
    val date: String = "Tomorrow",
    val timePreference: String = "Evening (6 PM - 11 PM)",
    val travelers: Int = 1,
    val totalBudget: Int = 1500,
    val transportModes: Set<TransportType> = setOf(TransportType.BUS, TransportType.TRAIN, TransportType.CAB, TransportType.MULTIMODAL),
    val seatPreference: String = "Sleeper / Comfortable",
    val naturalQuery: String = ""
)

@JsonClass(generateAdapter = true)
data class TransportOption(
    val id: String,
    val provider: String, // e.g. "redBus", "IRCTC / Indian Railways", "AbhiBus", "Zingbus", "Uber Intercity"
    val operatorName: String, // e.g. "Orange Travels", "Vande Bharat Express", "Garib Rath Express"
    val transportType: TransportType,
    val departureTime: String,
    val arrivalTime: String,
    val durationHours: String,
    val boardingPoint: String,
    val dropPoint: String,
    val baseFare: Int,
    val mandatoryTaxes: Int,
    val estimatedTransferCost: Int, // e.g. local auto/cab from drop point to final city center
    val liveSeatAvailability: Int,
    val seatType: String, // "AC Sleeper 2+1", "AC Chair Car", "Executive Chair Car", "3A AC Tier"
    val rating: Float, // e.g. 4.7f
    val reviewCount: Int, // e.g. 1420
    val amenities: List<String>, // "WiFi", "Charging Point", "Live GPS", "Emergency SOS", "Blanket"
    val cancellationPolicy: String, // "Full refund up to 4 hrs before departure"
    val isDirect: Boolean = true,
    val transferNotes: String = "",
    val dataConfidence: String = "Verified Live Inventory", // "Verified Live", "Provider Confirmed"
    val trackingSupported: Boolean = true
) {
    val totalEstimatedCost: Int
        get() = baseFare + mandatoryTaxes + estimatedTransferCost
}

data class PlanCategory(
    val title: String,
    val subtitle: String,
    val badge: String,
    val option: TransportOption
)

enum class SeatDeck {
    LOWER, UPPER
}

enum class SeatBerthType {
    SLEEPER, SEATER
}

data class SeatItem(
    val seatNumber: String,
    val deck: SeatDeck,
    val berthType: SeatBerthType,
    val price: Int,
    val isAvailable: Boolean,
    val isLadiesOnly: Boolean = false,
    val row: Int,
    val col: Int
)

data class PassengerInfo(
    val name: String = "",
    val age: String = "",
    val gender: String = "Male",
    val phone: String = ""
)

enum class DisruptionSeverity {
    NORMAL, SUSPECTED_DELAY, VEHICLE_STOP_CRITICAL, SERVICE_CANCELLED
}

data class JourneyTelemetry(
    val currentSpeedKmH: Float = 64.5f,
    val routeProgressPercent: Float = 0.42f,
    val distanceCoveredKm: Int = 230,
    val totalDistanceKm: Int = 620,
    val lastKnownLocation: String = "NH-65 Highway near Suryapet, Telangana",
    val lastGpsUpdateTimestamp: String = "Just now",
    val stationaryMinutes: Int = 0,
    val severity: DisruptionSeverity = DisruptionSeverity.NORMAL,
    val disruptionAlertMessage: String? = null
)
