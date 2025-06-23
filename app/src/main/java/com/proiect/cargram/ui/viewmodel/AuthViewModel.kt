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
            Log.d("AuthDebug", "Starting signUp for email: $email, username: $username")
            
            authRepository.signUp(email, password, username)
                .onSuccess {
                    val firebaseUser = authRepository.getCurrentUser()
                    Log.d("AuthDebug", "SignUp successful, firebaseUser: ${firebaseUser?.uid}")
                    Log.d("AuthDebug", "Firebase user email: ${firebaseUser?.email}")
                    Log.d("AuthDebug", "Firebase user displayName: ${firebaseUser?.displayName}")
                    
                    if (firebaseUser != null) {
                        withContext(Dispatchers.IO) {
                            val existingUser = userDao.getUserById(firebaseUser.uid)
                            Log.d("AuthDebug", "Existing user in Room: $existingUser")
                            
                            val user = User(
                                id = firebaseUser.uid,
                                username = username,
                                email = firebaseUser.email ?: "",
                                profilePicturePath = existingUser?.profilePicturePath ?: ""
                            )
                            Log.d("AuthDebug", "Creating user for Room: $user")
                            userDao.insertUser(user)
                            
                            // Verify the user was saved
                            val savedUser = userDao.getUserById(firebaseUser.uid)
                            Log.d("AuthDebug", "User saved to Room: $savedUser")
                            
                            // Test AuthRepository again after saving
                            val testCurrentUser = authRepository.getCurrentUser()
                            Log.d("AuthDebug", "AuthRepository.getCurrentUser() after save: ${testCurrentUser?.uid}")
                        }
                    } else {
                        Log.e("AuthDebug", "Firebase user is null after signUp")
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
                    Log.e("AuthDebug", "SignUp failed", exception)
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