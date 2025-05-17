package com.proiect.cargram.data.model

data class Vehicle(
    val id: String = "",
    val vin: String = "",
    val brand: String = "",
    val model: String = "",
    val year: String = "",
    val body: String = "",
    val trim: String = "",
    val series: String = "",
    val cmc: String = "",  // Engine Displacement
    val hp: String = "",   // Engine Power (HP)
    val fuel: String = "", // Fuel Type - Primary
    val transmission: String = "",
    val color: String = "",
    val country: String = "", // Plant Country
    val userId: String = "",
    // Additional fields from API that might be useful
    val drive: String = "",
    val engineCode: String = "",
    val numberOfDoors: String = "",
    val numberOfSeats: String = ""
) 