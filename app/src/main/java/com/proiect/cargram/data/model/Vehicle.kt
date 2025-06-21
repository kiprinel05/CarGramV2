package com.proiect.cargram.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey
    val vin: String = "",
    val userId: String = "",
    val make: String = "",
    val model: String = "",
    val year: String = "",
    val engine: String = "",
    val fuelType: String = "",
    val brand: String = "",
    val body: String = "",
    val trim: String = "",
    val series: String = "",
    val cmc: String = "",
    val hp: String = "",
    val fuel: String = "",
    val transmission: String = "",
    val country: String = "",
    val drive: String = "",
    val engineCode: String = "",
    val numberOfDoors: String = "",
    val numberOfSeats: String = "",
    val color: String = ""
) 