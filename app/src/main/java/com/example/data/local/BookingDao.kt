package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY timestamp DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE pnr = :pnr LIMIT 1")
    suspend fun getBookingByPnr(pnr: String): BookingEntity?

    @Query("SELECT * FROM bookings WHERE bookingStatus = 'CONFIRMED' OR bookingStatus = 'DISRUPTED' ORDER BY timestamp DESC LIMIT 1")
    fun getActiveJourney(): Flow<BookingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    @Query("DELETE FROM bookings WHERE pnr = :pnr")
    suspend fun deleteBooking(pnr: String)
}
