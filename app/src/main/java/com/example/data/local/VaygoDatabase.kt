package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BookingEntity::class, SearchHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class VaygoDatabase : RoomDatabase() {
    abstract fun bookingDao(): BookingDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: VaygoDatabase? = null

        fun getInstance(context: Context): VaygoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VaygoDatabase::class.java,
                    "vaygo_travel.db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
