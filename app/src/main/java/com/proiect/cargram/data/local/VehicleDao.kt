package com.proiect.cargram.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.proiect.cargram.data.model.Vehicle

@Dao
interface VehicleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: Vehicle)

    @Query("SELECT * FROM vehicles WHERE userId = :userId LIMIT 1")
    suspend fun getVehicleForUser(userId: String): Vehicle?
} 