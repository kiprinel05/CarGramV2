package com.proiect.cargram.data.api

import retrofit2.http.GET

data class FuelPriceResponse(
    val fuelPrices: FuelPrices
)

data class FuelPrices(
    val regular: Double,
    val midgrade: Double,
    val premium: Double,
    val diesel: Double,
    val e85: Double,
    val electric: Double,
    val cng: Double? = null,
    val lpg: Double? = null
)

interface FuelPricesApi {
    @GET("ws/rest/fuelprices")
    suspend fun getFuelPrices(): FuelPriceResponse
} 