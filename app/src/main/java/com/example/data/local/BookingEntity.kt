package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey
    val pnr: String,
    val itineraryId: String,
    val provider: String,
    val operatorName: String,
    val transportType: String,
    val origin: String,
    val destination: String,
    val travelDate: String,
    val departureTime: String,
    val arrivalTime: String,
    val selectedSeats: String, // Comma separated e.g. "L12, L13"
    val passengerName: String,
    val passengerPhone: String,
    val totalPaid: Int,
    val boardingPoint: String,
    val dropPoint: String,
    val bookingStatus: String = "CONFIRMED", // "CONFIRMED", "DISRUPTED", "RECOVERED"
    val timestamp: Long = System.currentTimeMillis()
)
