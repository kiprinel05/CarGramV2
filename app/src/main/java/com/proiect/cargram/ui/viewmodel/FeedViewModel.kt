package com.proiect.cargram.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proiect.cargram.data.model.Post
import com.proiect.cargram.data.repository.AuthRepository
import com.proiect.cargram.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortType {
    TIMELINE,
    LIKES
}

enum class FeedTab { FOR_YOU, FAVORITES }

data class FeedUiState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String? = null,
    val sortType: SortType = SortType.TIMELINE,
    val selectedTab: FeedTab = FeedTab.FOR_YOU,
    val favoritePostIds: Set<String> = emptySet()
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val authRepository: AuthRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TAB_KEY = "feed_tab"
    }

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        val initialTab = savedStateHandle.get<FeedTab>(TAB_KEY) ?: FeedTab.FOR_YOU
        _uiState.value = _uiState.value.copy(selectedTab = initialTab)
        observeFavorites()
        loadPosts()
    }

    private fun observeFavorites() {
        val userId = getCurrentUserId() ?: return
        viewModelScope.launch {
            postRepository.getFavoritesForUser(userId).collectLatest { favorites ->
                val favIds = favorites.map { it.postId }.toSet()
                _uiState.value = _uiState.value.copy(favoritePostIds = favIds)

                if (_uiState.value.selectedTab == FeedTab.FAVORITES) {
                    filterPostsByTab(_uiState.value.posts, favIds)
                }
            }
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                postRepository.getPostsFlow().collect { posts ->
                    val currentUserId = getCurrentUserId()
                    val updatedPosts = posts.map { post ->
                        if (currentUserId != null && post.likedBy.contains(currentUserId)) {
                            post.copy(likedBy = post.likedBy + currentUserId)
                        } else {
                            post
                        }
                    }
                    val sortedPosts = sortPosts(updatedPosts, _uiState.value.sortType)
                    filterPostsByTab(sortedPosts, _uiState.value.favoritePostIds)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private fun filterPostsByTab(posts: List<Post>, favoriteIds: Set<String>) {
        val filtered = when (_uiState.value.selectedTab) {
            FeedTab.FOR_YOU -> posts
            FeedTab.FAVORITES -> posts.filter { favoriteIds.contains(it.id) }
        }
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            posts = filtered
        )
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
        filterPostsByTab(sortedPosts, _uiState.value.favoritePostIds)
        _uiState.value = _uiState.value.copy(sortType = sortType)
    }

    fun setTab(tab: FeedTab) {
        savedStateHandle[TAB_KEY] = tab
        _uiState.value = _uiState.value.copy(selectedTab = tab, isLoading = true)
        loadPosts()
    }

    fun getCurrentUserId(): String? {
        return authRepository.getCurrentUser()?.uid
    }

    fun likePost(postId: String) {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            currentUser?.let { user ->
                postRepository.likePost(postId, user.uid)
                updatePostLikeStatus(postId, user.uid, true)
            }
        }
    }

    fun unlikePost(postId: String) {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            currentUser?.let { user ->
                postRepository.unlikePost(postId, user.uid)
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
        val sortedPosts = sortPosts(updatedPosts, _uiState.value.sortType)
        filterPostsByTab(sortedPosts, _uiState.value.favoritePostIds)
    }

    fun addFavorite(postId: String) {
        val userId = getCurrentUserId() ?: return
        viewModelScope.launch {
            postRepository.addFavorite(postId, userId)
        }
    }

    fun removeFavorite(postId: String) {
        val userId = getCurrentUserId() ?: return
        viewModelScope.launch {
            postRepository.removeFavorite(postId, userId)
        }
    }

    fun isFavorite(postId: String): Boolean {
        return _uiState.value.favoritePostIds.contains(postId)
    }

    fun sharePost(postId: String) {
        viewModelScope.launch {
            postRepository.sharePost(postId)
            val currentPosts = _uiState.value.posts
            val updatedPosts = currentPosts.map { post ->
                if (post.id == postId) {
                    post.copy(shares = post.shares + 1)
                } else {
                    post
                }
            }
            val sortedPosts = sortPosts(updatedPosts, _uiState.value.sortType)
            filterPostsByTab(sortedPosts, _uiState.value.favoritePostIds)
        }
    }
} 