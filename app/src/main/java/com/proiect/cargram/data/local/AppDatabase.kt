package com.proiect.cargram.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.proiect.cargram.data.model.Post
import com.proiect.cargram.data.model.User
import com.proiect.cargram.data.model.Vehicle

@Database(entities = [User::class, Post::class, Vehicle::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun vehicleDao(): VehicleDao
} 