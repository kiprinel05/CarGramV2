package com.proiect.cargram.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proiect.cargram.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreatePostUiState(
    val selectedImageUri: Uri? = null,
    val caption: String = "",
    val vehicleInfo: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()

    fun setSelectedImage(uri: Uri) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    fun setCaption(caption: String) {
        _uiState.update { it.copy(caption = caption) }
    }

    fun setVehicleInfo(vehicleInfo: String) {
        _uiState.update { it.copy(vehicleInfo = vehicleInfo) }
    }

    fun createPost(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val currentState = _uiState.value
        
        if (currentState.selectedImageUri == null) {
            onError("Te rog selectează o imagine")
            return
        }
        
        if (currentState.caption.isEmpty()) {
            onError("Te rog adaugă o descriere")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val result = postRepository.createPost(
                    imageUri = currentState.selectedImageUri!!,
                    caption = currentState.caption,
                    vehicleInfo = currentState.vehicleInfo
                )
                
                result.fold(
                    onSuccess = {
                        _uiState.update { it.copy(isLoading = false) }
                        onSuccess()
                    },
                    onFailure = { exception ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                error = exception.message ?: "A apărut o eroare la crearea postării"
                            ) 
                        }
                        onError(exception.message ?: "A apărut o eroare la crearea postării")
                    }
                )
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = e.message ?: "A apărut o eroare la crearea postării"
                    ) 
                }
                onError(e.message ?: "A apărut o eroare la crearea postării")
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
} 