package com.proiect.cargram.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.proiect.cargram.data.model.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
} 