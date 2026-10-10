package com.example.data.repository

import com.example.data.model.PlanCategory
import com.example.data.model.SeatBerthType
import com.example.data.model.SeatDeck
import com.example.data.model.SeatItem
import com.example.data.model.TransportOption
import com.example.data.model.TransportType
import com.example.data.model.TravelSearchQuery
import kotlinx.coroutines.delay
import java.util.UUID

class TravelInventoryRepository {

    // Simulates parallel provider search (Buses, Trains, Cabs, Multimodal)
    suspend fun searchOptions(query: TravelSearchQuery): List<TransportOption> {
        delay(400) // Brief realistic network delay
        val allOptions = generateOptionsForRoute(query.origin, query.destination, query.date)

        // Filter by selected transport modes
        val modeFiltered = allOptions.filter { query.transportModes.contains(it.transportType) }

        return modeFiltered
    }

    fun categorizePlans(options: List<TransportOption>, budget: Int): List<PlanCategory> {
        if (options.isEmpty()) return emptyList()

        val affordable = options.filter { it.totalEstimatedCost <= budget }
        val pool = if (affordable.isNotEmpty()) affordable else options

        val categories = mutableListOf<PlanCategory>()

        // 1. Cheapest Feasible
        pool.minByOrNull { it.totalEstimatedCost }?.let { cheapest ->
            categories.add(
                PlanCategory(
                    title = "Smart Saver",
                    subtitle = "Lowest total cost within budget",
                    badge = "Cheapest Feasible",
                    option = cheapest
                )
            )
        }

        // 2. Fastest Feasible
        val fastest = pool.filter { it.id != categories.firstOrNull()?.option?.id }
            .minByOrNull { parseDurationMinutes(it.durationHours) }
            ?: pool.minByOrNull { parseDurationMinutes(it.durationHours) }

        if (fastest != null && categories.none { it.option.id == fastest.id }) {
            categories.add(
                PlanCategory(
                    title = "Rapid Express",
                    subtitle = "Minimal travel duration",
                    badge = "Fastest Feasible",
                    option = fastest
                )
            )
        }

        // 3. Best Value (Balanced Score: cost, rating, duration)
        val bestValue = pool.filter { opt -> categories.none { it.option.id == opt.id } }
            .maxByOrNull { it.rating * 100 - (it.totalEstimatedCost / 30) }
            ?: pool.firstOrNull()

        if (bestValue != null && categories.none { it.option.id == bestValue.id }) {
            categories.add(
                PlanCategory(
                    title = "Optimal Balance",
                    subtitle = "Best blend of comfort, speed & budget",
                    badge = "Best Overall Value",
                    option = bestValue
                )
            )
        }

        // 4. Highest Rated Feasible
        val topRated = pool.filter { opt -> categories.none { it.option.id == opt.id } }
            .maxByOrNull { it.rating }

        if (topRated != null && categories.none { it.option.id == topRated.id }) {
            categories.add(
                PlanCategory(
                    title = "Premium Comfort",
                    subtitle = "Top passenger reviews & hospitality",
                    badge = "Highest Rated",
                    option = topRated
                )
            )
        }

        // 5. Multimodal Option
        val multimodal = options.firstOrNull { it.transportType == TransportType.MULTIMODAL && categories.none { c -> c.option.id == it.id } }
        if (multimodal != null) {
            categories.add(
                PlanCategory(
                    title = "Smart Multimodal",
                    subtitle = "Combined Train + Intercity Transfer",
                    badge = "Multimodal Combo",
                    option = multimodal
                )
            )
        }

        return categories
    }

    fun getDisruptionAlternatives(
        currentLocation: String,
        destination: String,
        remainingBudget: Int
    ): List<TransportOption> {
        val recoveryOptions = listOf(
            TransportOption(
                id = "recovery_bus_1",
                provider = "IntrCity SmartBus",
                operatorName = "IntrCity SmartBus (Rescue Route)",
                transportType = TransportType.BUS,
                departureTime = "In 25 mins (Immediate Boarding)",
                arrivalTime = "06:30 AM Tomorrow",
                durationHours = "6h 30m",
                boardingPoint = "Suryapet Toll Plaza Bypass (500m from breakdown spot)",
                dropPoint = "$destination Central Bus Station",
                baseFare = 780,
                mandatoryTaxes = 60,
                estimatedTransferCost = 100,
                liveSeatAvailability = 7,
                seatType = "AC Sleeper (Lower Berth)",
                rating = 4.8f,
                reviewCount = 3890,
                amenities = listOf("Immediate Pickup", "Live SOS Tracking", "Air Conditioned", "Water Bottle"),
                cancellationPolicy = "Instant Emergency Seat Lock",
                dataConfidence = "Verified Nearby Inventory",
                trackingSupported = true
            ),
            TransportOption(
                id = "recovery_train_2",
                provider = "Indian Railways (Tatkal/Current)",
                operatorName = "Godavari Superfast Express",
                transportType = TransportType.TRAIN,
                departureTime = "In 45 mins",
                arrivalTime = "05:45 AM Tomorrow",
                durationHours = "5h 15m",
                boardingPoint = "Miryalaguda Junction (12 km via Auto)",
                dropPoint = "$destination Railway Station",
                baseFare = 450,
                mandatoryTaxes = 40,
                estimatedTransferCost = 180,
                liveSeatAvailability = 14,
                seatType = "3A AC Tier (Current Avail)",
                rating = 4.6f,
                reviewCount = 8900,
                amenities = listOf("Bedroll Provided", "Pantry Meals", "Dedicated Berth", "Charging Socket"),
                cancellationPolicy = "Standard IRCTC Rules",
                dataConfidence = "IRCTC Current Availability Confirmed",
                trackingSupported = true
            ),
            TransportOption(
                id = "recovery_cab_3",
                provider = "Uber Intercity Emergency Pool",
                operatorName = "Verified Sedan Express (Shared 2 Pax)",
                transportType = TransportType.CAB,
                departureTime = "In 10 mins",
                arrivalTime = "04:30 AM Tomorrow",
                durationHours = "4h 00m",
                boardingPoint = "Direct Pickup at Current GPS Spot",
                dropPoint = "$destination Doorstep Drop",
                baseFare = 1150,
                mandatoryTaxes = 95,
                estimatedTransferCost = 0,
                liveSeatAvailability = 2,
                seatType = "Prime Sedan AC (Window)",
                rating = 4.9f,
                reviewCount = 920,
                amenities = listOf("Immediate Doorstep", "Fast Highway Tolls", "AC Sedan", "Luggage Boot"),
                cancellationPolicy = "Free cancellation until arrival",
                dataConfidence = "Live Driver 4 mins away",
                trackingSupported = true
            )
        )

        return recoveryOptions.sortedBy { it.totalEstimatedCost }
    }

    fun generateSeatMapForOption(option: TransportOption): List<SeatItem> {
        val seats = mutableListOf<SeatItem>()
        val isSleeper = option.seatType.contains("Sleeper", ignoreCase = true)

        if (isSleeper) {
            // Lower Deck: 15 sleeper berths (Rows 1 to 5, Columns 1 (single) & 2,3 (double))
            for (row in 1..5) {
                // Column 1 (Left single berth)
                seats.add(
                    SeatItem(
                        seatNumber = "L${row * 3 - 2}",
                        deck = SeatDeck.LOWER,
                        berthType = SeatBerthType.SLEEPER,
                        price = option.baseFare,
                        isAvailable = row != 2 && row != 4,
                        isLadiesOnly = row == 1,
                        row = row,
                        col = 1
                    )
                )
                // Column 2 (Right aisle berth)
                seats.add(
                    SeatItem(
                        seatNumber = "L${row * 3 - 1}",
                        deck = SeatDeck.LOWER,
                        berthType = SeatBerthType.SLEEPER,
                        price = option.baseFare,
                        isAvailable = row != 1,
                        isLadiesOnly = false,
                        row = row,
                        col = 2
                    )
                )
                // Column 3 (Right window berth)
                seats.add(
                    SeatItem(
                        seatNumber = "L${row * 3}",
                        deck = SeatDeck.LOWER,
                        berthType = SeatBerthType.SLEEPER,
                        price = option.baseFare + 50,
                        isAvailable = row != 3,
                        isLadiesOnly = false,
                        row = row,
                        col = 3
                    )
                )
            }

            // Upper Deck: 15 sleeper berths
            for (row in 1..5) {
                seats.add(
                    SeatItem(
                        seatNumber = "U${row * 3 - 2}",
                        deck = SeatDeck.UPPER,
                        berthType = SeatBerthType.SLEEPER,
                        price = option.baseFare - 40,
                        isAvailable = row != 1 && row != 5,
                        isLadiesOnly = false,
                        row = row,
                        col = 1
                    )
                )
                seats.add(
                    SeatItem(
                        seatNumber = "U${row * 3 - 1}",
                        deck = SeatDeck.UPPER,
                        berthType = SeatBerthType.SLEEPER,
                        price = option.baseFare - 40,
                        isAvailable = row != 3,
                        isLadiesOnly = false,
                        row = row,
                        col = 2
                    )
                )
                seats.add(
                    SeatItem(
                        seatNumber = "U${row * 3}",
                        deck = SeatDeck.UPPER,
                        berthType = SeatBerthType.SLEEPER,
                        price = option.baseFare + 20,
                        isAvailable = true,
                        isLadiesOnly = false,
                        row = row,
                        col = 3
                    )
                )
            }
        } else {
            // Seater Coach (e.g. 2+2 layout)
            for (row in 1..6) {
                for (col in 1..4) {
                    val seatNum = "S${(row - 1) * 4 + col}"
                    seats.add(
                        SeatItem(
                            seatNumber = seatNum,
                            deck = SeatDeck.LOWER,
                            berthType = SeatBerthType.SEATER,
                            price = option.baseFare,
                            isAvailable = (row + col) % 3 != 0,
                            isLadiesOnly = row == 1 && (col == 1 || col == 2),
                            row = row,
                            col = col
                        )
                    )
                }
            }
        }

        return seats
    }

    private fun parseDurationMinutes(duration: String): Int {
        val hours = Regex("(\\d+)h").find(duration)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 8
        val minutes = Regex("(\\d+)m").find(duration)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0
        return hours * 60 + minutes
    }

    private fun generateOptionsForRoute(origin: String, destination: String, date: String): List<TransportOption> {
        val routeHash = (origin.hashCode() + destination.hashCode()).let { if (it < 0) -it else it }
        val basePriceBus = 850 + (routeHash % 300)
        val basePriceTrain = 520 + (routeHash % 250)

        return listOf(
            TransportOption(
                id = "bus_orange_1",
                provider = "redBus / SeatSeller",
                operatorName = "Orange Travels Premium Sleeper",
                transportType = TransportType.BUS,
                departureTime = "07:30 PM",
                arrivalTime = "06:15 AM",
                durationHours = "10h 45m",
                boardingPoint = "$origin Gurudwara Bus Stand / RTC Complex",
                dropPoint = "$destination Ameerpet / Miyapur Cross",
                baseFare = basePriceBus,
                mandatoryTaxes = 65,
                estimatedTransferCost = 120,
                liveSeatAvailability = 9,
                seatType = "2+1 AC Sleeper (Capella)",
                rating = 4.7f,
                reviewCount = 2140,
                amenities = listOf("WiFi", "Live GPS", "USB Port", "Personal Screen", "Blanket"),
                cancellationPolicy = "Full refund if cancelled 6 hours before departure",
                dataConfidence = "Verified Live Inventory",
                trackingSupported = true
            ),
            TransportOption(
                id = "train_vande_2",
                provider = "IRCTC / Indian Railways",
                operatorName = "Vande Bharat Express (20833)",
                transportType = TransportType.TRAIN,
                departureTime = "05:45 AM",
                arrivalTime = "02:15 PM",
                durationHours = "8h 30m",
                boardingPoint = "$origin Railway Station (Platform 1)",
                dropPoint = "$destination Secunderabad Junction",
                baseFare = basePriceTrain + 420,
                mandatoryTaxes = 50,
                estimatedTransferCost = 80,
                liveSeatAvailability = 18,
                seatType = "AC Chair Car (CC)",
                rating = 4.9f,
                reviewCount = 5820,
                amenities = listOf("Catering Included", "High Speed", "Panoramic Windows", "CCTV", "Bio-Vacuum Toilet"),
                cancellationPolicy = "IRCTC Standard Tatkal / General Refund Policy",
                dataConfidence = "IRCTC Real-time Quota Confirmed",
                trackingSupported = true
            ),
            TransportOption(
                id = "bus_intrcity_3",
                provider = "AbhiBus Partner",
                operatorName = "IntrCity SmartBus Multi-Axle",
                transportType = TransportType.BUS,
                departureTime = "09:00 PM",
                arrivalTime = "07:30 AM",
                durationHours = "10h 30m",
                boardingPoint = "$origin Rama Talkies SmartBus Lounge",
                dropPoint = "$destination Gachibowli ORR Junction",
                baseFare = basePriceBus - 110,
                mandatoryTaxes = 55,
                estimatedTransferCost = 90,
                liveSeatAvailability = 14,
                seatType = "Volvo AC Semi-Sleeper",
                rating = 4.6f,
                reviewCount = 1670,
                amenities = listOf("Captain on Board", "Private AC Lounge", "Live Vehicle Tracking", "Reading Light"),
                cancellationPolicy = "Free reschedule up to 2 hours before trip",
                dataConfidence = "Verified Live Inventory",
                trackingSupported = true
            ),
            TransportOption(
                id = "train_garibrath_4",
                provider = "IRCTC / Indian Railways",
                operatorName = "Garib Rath Express (12739)",
                transportType = TransportType.TRAIN,
                departureTime = "08:40 PM",
                arrivalTime = "08:10 AM",
                durationHours = "11h 30m",
                boardingPoint = "$origin Central Station",
                dropPoint = "$destination Nampally / Secunderabad",
                baseFare = basePriceTrain - 60,
                mandatoryTaxes = 35,
                estimatedTransferCost = 75,
                liveSeatAvailability = 32,
                seatType = "3A AC Sleeper Economy",
                rating = 4.4f,
                reviewCount = 9420,
                amenities = listOf("Economical AC", "Bedroll on Request", "Pantry Food", "Charging Ports"),
                cancellationPolicy = "IRCTC Standard Refund Policy",
                dataConfidence = "IRCTC Live Timetable & Availability",
                trackingSupported = true
            ),
            TransportOption(
                id = "cab_intercity_5",
                provider = "Uber Intercity / MakeMyTrip",
                operatorName = "Verified Intercity Sedan (AC)",
                transportType = TransportType.CAB,
                departureTime = "Flexible (On Demand)",
                arrivalTime = "+8h 00m from start",
                durationHours = "8h 00m",
                boardingPoint = "$origin Doorstep Pickup",
                dropPoint = "$destination Exact Destination Drop",
                baseFare = 2800,
                mandatoryTaxes = 180,
                estimatedTransferCost = 0,
                liveSeatAvailability = 3,
                seatType = "Private Sedan 4-Seater",
                rating = 4.8f,
                reviewCount = 640,
                amenities = listOf("Zero Walking", "Private Cabin", "Stop Whenever Needed", "Highway Fastag"),
                cancellationPolicy = "Free cancellation until 1 hr before pickup",
                dataConfidence = "Partner Guaranteed Availability",
                trackingSupported = true
            ),
            TransportOption(
                id = "multimodal_smart_6",
                provider = "Vaygo MultiModal Router",
                operatorName = "Express Intercity (Train + Connecting Cab)",
                transportType = TransportType.MULTIMODAL,
                departureTime = "06:30 PM",
                arrivalTime = "05:00 AM",
                durationHours = "10h 30m",
                boardingPoint = "$origin Station",
                dropPoint = "$destination Hitec City",
                baseFare = basePriceTrain + 150,
                mandatoryTaxes = 45,
                estimatedTransferCost = 140,
                liveSeatAvailability = 12,
                seatType = "3A Train + Pre-booked EV Cab",
                rating = 4.7f,
                reviewCount = 820,
                amenities = listOf("Guaranteed Layover Connection", "Luggage Assistance", "Combined Ticket"),
                cancellationPolicy = "Single-ticket joint cancellation guarantee",
                isDirect = false,
                transferNotes = "45m seamless connection at Vijayawada Junction with EV cab standby",
                dataConfidence = "Multi-provider Synchronized Sync",
                trackingSupported = true
            )
        )
    }
}
