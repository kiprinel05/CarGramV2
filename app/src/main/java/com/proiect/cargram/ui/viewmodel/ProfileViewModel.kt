package com.proiect.cargram.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proiect.cargram.data.model.Post
import com.proiect.cargram.data.model.User
import com.proiect.cargram.data.model.Vehicle
import com.proiect.cargram.data.repository.AuthRepository
import com.proiect.cargram.data.repository.PostRepository
import com.proiect.cargram.data.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.proiect.cargram.data.local.UserDao
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import androidx.lifecycle.SavedStateHandle

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isCurrentUser: Boolean = false,
    val user: User? = null,
    val vehicle: Vehicle? = null,
    val posts: List<Post> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val vehicleRepository: VehicleRepository,
    private val postRepository: PostRepository,
    private val userDao: UserDao,
    @ApplicationContext private val context: Context,
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val firebaseUserId = authRepository.getCurrentUser()?.uid
            if (firebaseUserId == null) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "User not found.")
                return@launch
            }
            val user = userDao.getUserById(firebaseUserId)
            val vehicle = vehicleRepository.getVehicleForUser(firebaseUserId)
            _uiState.value = _uiState.value.copy(user = user, vehicle = vehicle)
            postRepository.getPostsForUser(firebaseUserId).collect { posts ->
                _uiState.value = _uiState.value.copy(posts = posts, isLoading = false)
            }
        }
    }

    fun uploadProfilePicture(uri: Uri) {
        viewModelScope.launch {
            val firebaseUser = authRepository.getCurrentUser()
            if (firebaseUser == null) {
                _uiState.value = _uiState.value.copy(error = "User not authenticated")
                return@launch
            }
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.cacheDir, "profile_${firebaseUser.uid}.jpg")
                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                val localPath = file.absolutePath
                userDao.updateProfilePicturePath(firebaseUser.uid, localPath)
                loadProfile()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to save profile picture: ${e.message}", isLoading = false)
            }
        }
    }
} 