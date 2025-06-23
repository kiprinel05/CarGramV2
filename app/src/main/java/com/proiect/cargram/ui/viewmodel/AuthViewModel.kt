package com.proiect.cargram.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proiect.cargram.data.repository.AuthRepository
import com.proiect.cargram.data.repository.VehicleRepository
import com.proiect.cargram.data.local.UserDao
import com.proiect.cargram.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val hasVehicleProfile: Boolean = false,
    val registrationComplete: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val vehicleRepository: VehicleRepository,
    private val userDao: UserDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepository.signIn(email, password)
                .onSuccess {
                    val firebaseUser = authRepository.getCurrentUser()
                    if (firebaseUser != null) {
                        withContext(Dispatchers.IO) {
                            var existingUser = userDao.getUserById(firebaseUser.uid)
                            var username = existingUser?.username
                            if (username.isNullOrBlank()) {
                                // Dacă userul nu există în Room, îl creăm cu datele de bază
                                username = firebaseUser.displayName ?: "user"
                            }
                            val user = User(
                                id = firebaseUser.uid,
                                username = username ?: "user",
                                email = firebaseUser.email ?: "",
                                profilePicturePath = existingUser?.profilePicturePath ?: ""
                            )
                            userDao.insertUser(user)
                        }
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Authentication failed"
                    )
                }
        }
    }

    fun signUp(email: String, password: String, username: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepository.signUp(email, password, username)
                .onSuccess {
                    val firebaseUser = authRepository.getCurrentUser()
                    if (firebaseUser != null) {
                        withContext(Dispatchers.IO) {
                            val existingUser = userDao.getUserById(firebaseUser.uid)
                            val user = User(
                                id = firebaseUser.uid,
                                username = username,
                                email = firebaseUser.email ?: "",
                                profilePicturePath = existingUser?.profilePicturePath ?: ""
                            )
                            userDao.insertUser(user)
                            userDao.getUserById(firebaseUser.uid)
                        }
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        hasVehicleProfile = false,
                        registrationComplete = true,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
        }
    }

    fun completeVehicleProfile() {
        _uiState.value = _uiState.value.copy(
            hasVehicleProfile = true
        )
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
} 