package com.proiect.cargram.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.proiect.cargram.data.api.VinDecoderApi
import com.proiect.cargram.data.model.Vehicle
import com.proiect.cargram.di.VinDecoderApiKey
import com.proiect.cargram.di.VinDecoderSecretKey
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import javax.inject.Inject

class VehicleRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val vinDecoderApi: VinDecoderApi,
    @VinDecoderApiKey private val apiKey: String,
    @VinDecoderSecretKey private val secretKey: String
) {
    private fun calculateControlSum(vin: String): String {
        val input = "${vin.uppercase()}|decode|$apiKey|$secretKey"
        Log.d("VinDecoder", "Calculating control sum for input: $input")
        val md = MessageDigest.getInstance("SHA-1")
        val digest = md.digest(input.toByteArray())
        val controlSum = digest.joinToString("") { "%02x".format(it) }.substring(0, 10)
        Log.d("VinDecoder", "Generated control sum: $controlSum")
        return controlSum
    }

    suspend fun decodeVin(vin: String): Result<Vehicle> {
        return try {
            Log.d("VinDecoder", "Making API request for VIN: $vin")
            Log.d("VinDecoder", "Using API Key: $apiKey")
            
            val controlSum = calculateControlSum(vin)
            Log.d("VinDecoder", "Calculated control sum: $controlSum")
            
            val response = vinDecoderApi.decodeVin(apiKey, controlSum, vin.uppercase())
            Log.d("VinDecoder", "Response received: ${response.isSuccessful}")
            
            if (response.isSuccessful && response.body() != null) {
                val decoderResponse = response.body()!!
                Log.d("VinDecoder", "Raw response: ${decoderResponse.decode}")
                
                val decodedData = decoderResponse.decode.associate { it.label to it.getValueAsString() }
                Log.d("VinDecoder", "Decoded data map: $decodedData")
                
                val vehicle = Vehicle(
                    vin = vin,
                    brand = decodedData["Make"] ?: "",
                    model = decodedData["Model"] ?: "",
                    year = decodedData["Model Year"] ?: "",
                    body = decodedData["Body"] ?: "",
                    trim = decodedData["Trim"] ?: "",
                    series = decodedData["Series"] ?: "",
                    cmc = decodedData["Engine Displacement (ccm)"] ?: "",
                    hp = decodedData["Engine Power (HP)"] ?: "",
                    fuel = decodedData["Fuel Type - Primary"] ?: "",
                    transmission = decodedData["Transmission"] ?: "",
                    country = decodedData["Plant Country"] ?: "",
                    drive = decodedData["Drive"] ?: "",
                    engineCode = decodedData["Engine Code"] ?: "",
                    numberOfDoors = decodedData["Number of Doors"] ?: "",
                    numberOfSeats = decodedData["Number of Seats"] ?: ""
                )
                
                Log.d("VinDecoder", "Created vehicle object: $vehicle")
                Result.success(vehicle)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("VinDecoder", "API Error: $errorBody")
                Log.e("VinDecoder", "Response code: ${response.code()}")
                Result.failure(Exception("Failed to decode VIN: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("VinDecoder", "Exception while decoding VIN", e)
            Result.failure(e)
        }
    }

    suspend fun saveVehicle(vehicle: Vehicle, userId: String): Result<Unit> {
        return try {
            val vehicleWithUser = vehicle.copy(userId = userId)
            firestore.collection("vehicles")
                .add(vehicleWithUser)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVehiclesByUser(userId: String): Result<List<Vehicle>> {
        return try {
            val vehicles = firestore.collection("vehicles")
                .whereEqualTo("userId", userId)
                .get()
                .await()
                .toObjects(Vehicle::class.java)
            Result.success(vehicles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 