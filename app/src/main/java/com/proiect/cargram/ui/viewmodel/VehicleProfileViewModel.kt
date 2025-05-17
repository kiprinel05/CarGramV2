package com.proiect.cargram.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proiect.cargram.data.model.Vehicle
import com.proiect.cargram.data.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VehicleProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val vehicle: Vehicle? = null,
    val isVinDecoded: Boolean = false
)

@HiltViewModel
class VehicleProfileViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VehicleProfileUiState())
    val uiState: StateFlow<VehicleProfileUiState> = _uiState.asStateFlow()

    init {
        Log.d("VehicleProfileVM", "ViewModel initialized")
    }

    fun decodeVin(vin: String) {
        viewModelScope.launch {
            Log.d("VehicleProfileVM", "Starting VIN decode for: $vin")
            
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            vehicleRepository.decodeVin(vin)
                .onSuccess { vehicle ->
                    Log.d("VehicleProfileVM", "VIN decode successful: $vehicle")
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            vehicle = vehicle,
                            isVinDecoded = true,
                            error = null
                        )
                    }
                    Log.d("VehicleProfileVM", "Updated UI state: ${_uiState.value}")
                }
                .onFailure { exception ->
                    Log.e("VehicleProfileVM", "VIN decode failed", exception)
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = exception.message
                        )
                    }
                }
        }
    }

    fun updateVehicleField(field: String, value: String) {
        Log.d("VehicleProfileVM", "Updating field: $field with value: $value")
        
        val currentVehicle = _uiState.value.vehicle ?: Vehicle()
        val updatedVehicle = when (field) {
            "brand" -> currentVehicle.copy(brand = value)
            "model" -> currentVehicle.copy(model = value)
            "body" -> currentVehicle.copy(body = value)
            "cmc" -> currentVehicle.copy(cmc = value)
            "fuel" -> currentVehicle.copy(fuel = value)
            "year" -> currentVehicle.copy(year = value)
            "hp" -> currentVehicle.copy(hp = value)
            "color" -> currentVehicle.copy(color = value)
            "country" -> currentVehicle.copy(country = value)
            "trim" -> currentVehicle.copy(trim = value)
            "series" -> currentVehicle.copy(series = value)
            "transmission" -> currentVehicle.copy(transmission = value)
            "drive" -> currentVehicle.copy(drive = value)
            "engineCode" -> currentVehicle.copy(engineCode = value)
            "numberOfDoors" -> currentVehicle.copy(numberOfDoors = value)
            "numberOfSeats" -> currentVehicle.copy(numberOfSeats = value)
            else -> currentVehicle
        }
        
        _uiState.update { it.copy(vehicle = updatedVehicle) }
        Log.d("VehicleProfileVM", "Vehicle updated: ${_uiState.value.vehicle}")
    }

    fun saveVehicle(userId: String) {
        viewModelScope.launch {
            Log.d("VehicleProfileVM", "Saving vehicle for user: $userId")
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            _uiState.value.vehicle?.let { vehicle ->
                vehicleRepository.saveVehicle(vehicle, userId)
                    .onSuccess {
                        Log.d("VehicleProfileVM", "Vehicle saved successfully")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    .onFailure { exception ->
                        Log.e("VehicleProfileVM", "Failed to save vehicle", exception)
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = exception.message
                            )
                        }
                    }
            }
        }
    }
} 