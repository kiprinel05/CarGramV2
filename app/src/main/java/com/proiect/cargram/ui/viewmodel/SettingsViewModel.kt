package com.proiect.cargram.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.proiect.cargram.data.api.FuelPricesApi
import com.proiect.cargram.data.api.FuelPrices
import com.proiect.cargram.data.local.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

data class FuelPricesUiState(
    val isLoading: Boolean = false,
    val fuelPrices: FuelPrices? = null,
    val error: String? = null,
    val isMockData: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    private val fuelPricesApi: FuelPricesApi
) : AndroidViewModel(application) {
    
    val isDarkMode: StateFlow<Boolean> = ThemePreferences.isDarkMode(application)
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _fuelPricesState = MutableStateFlow(FuelPricesUiState())
    val fuelPricesState: StateFlow<FuelPricesUiState> = _fuelPricesState.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            ThemePreferences.setDarkMode(getApplication(), enabled)
        }
    }

    fun loadFuelPrices() {
        viewModelScope.launch {
            _fuelPricesState.value = _fuelPricesState.value.copy(isLoading = true, error = null, isMockData = false)
            try {
                val response = fuelPricesApi.getFuelPrices()
                
                if (response.fuelPrices.regular > 0) {
                    _fuelPricesState.value = _fuelPricesState.value.copy(
                        isLoading = false,
                        fuelPrices = response.fuelPrices,
                        isMockData = false
                    )
                } else {
                    throw Exception("Invalid API response")
                }
            } catch (e: Exception) {
                Log.e("FuelPrices", "API error: ${e.message}", e)
                val mockFuelPrices = FuelPrices(
                    regular = 3.11,
                    midgrade = 3.69,
                    premium = 4.04,
                    diesel = 3.47,
                    e85 = 2.57,
                    electric = 0.15,
                    cng = 2.99,
                    lpg = 3.33
                )
                
                _fuelPricesState.value = _fuelPricesState.value.copy(
                    isLoading = false,
                    fuelPrices = mockFuelPrices,
                    isMockData = true
                )
            }
        }
    }
} 