package com.proiect.cargram.di

import android.content.Context
import androidx.room.Room
import com.proiect.cargram.data.local.AppDatabase
import com.proiect.cargram.data.local.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "cargram_db"
        ).build()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
} 