package com.proiect.cargram.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proiect.cargram.data.model.Post
import com.proiect.cargram.data.repository.AuthRepository
import com.proiect.cargram.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortType {
    TIMELINE,
    LIKES
}

data class FeedUiState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String? = null,
    val sortType: SortType = SortType.TIMELINE
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        loadPosts()
    }

    private fun loadPosts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                postRepository.getPostsFlow().collect { posts ->
                    // Update posts with current user's like status
                    val currentUserId = getCurrentUserId()
                    val updatedPosts = posts.map { post ->
                        if (currentUserId != null && post.likedBy.contains(currentUserId)) {
                            post.copy(likedBy = post.likedBy + currentUserId)
                        } else {
                            post
                        }
                    }
                    
                    // Sort posts based on current sort type
                    val sortedPosts = sortPosts(updatedPosts, _uiState.value.sortType)
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        posts = sortedPosts,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private fun sortPosts(posts: List<Post>, sortType: SortType): List<Post> {
        return when (sortType) {
            SortType.TIMELINE -> posts.sortedByDescending { it.timestamp }
            SortType.LIKES -> posts.sortedByDescending { it.likes }
        }
    }

    fun setSortType(sortType: SortType) {
        val currentPosts = _uiState.value.posts
        val sortedPosts = sortPosts(currentPosts, sortType)
        _uiState.value = _uiState.value.copy(
            sortType = sortType,
            posts = sortedPosts
        )
    }

    fun getCurrentUserId(): String? {
        return authRepository.getCurrentUser()?.uid
    }

    fun likePost(postId: String) {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            currentUser?.let { user ->
                postRepository.likePost(postId, user.uid)
                // Update local state immediately for better UX
                updatePostLikeStatus(postId, user.uid, true)
            }
        }
    }

    fun unlikePost(postId: String) {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            currentUser?.let { user ->
                postRepository.unlikePost(postId, user.uid)
                // Update local state immediately for better UX
                updatePostLikeStatus(postId, user.uid, false)
            }
        }
    }

    private fun updatePostLikeStatus(postId: String, userId: String, isLiked: Boolean) {
        val currentPosts = _uiState.value.posts
        val updatedPosts = currentPosts.map { post ->
            if (post.id == postId) {
                val currentLikes = post.likes
                val currentLikedBy = post.likedBy
                if (isLiked) {
                    post.copy(
                        likes = currentLikes + 1,
                        likedBy = currentLikedBy + userId
                    )
                } else {
                    post.copy(
                        likes = (currentLikes - 1).coerceAtLeast(0),
                        likedBy = currentLikedBy - userId
                    )
                }
            } else {
                post
            }
        }
        
        // Re-sort posts after updating like status
        val sortedPosts = sortPosts(updatedPosts, _uiState.value.sortType)
        _uiState.value = _uiState.value.copy(posts = sortedPosts)
    }

    fun sharePost(postId: String) {
        viewModelScope.launch {
            postRepository.sharePost(postId)
            // Update local state immediately for better UX
            val currentPosts = _uiState.value.posts
            val updatedPosts = currentPosts.map { post ->
                if (post.id == postId) {
                    post.copy(shares = post.shares + 1)
                } else {
                    post
                }
            }
            _uiState.value = _uiState.value.copy(posts = updatedPosts)
        }
    }
} 