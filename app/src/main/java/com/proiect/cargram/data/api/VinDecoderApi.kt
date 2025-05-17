package com.proiect.cargram.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface VinDecoderApi {
    @GET("{apiKey}/{controlSum}/decode/{vin}.json")
    suspend fun decodeVin(
        @Path("apiKey") apiKey: String,
        @Path("controlSum") controlSum: String,
        @Path("vin") vin: String
    ): Response<VinDecoderResponse>
} 