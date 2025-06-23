package com.proiect.cargram.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.proiect.cargram.data.local.PostDao
import com.proiect.cargram.data.local.UserDao
import com.proiect.cargram.data.local.FavoriteDao
import com.proiect.cargram.data.model.Post
import com.proiect.cargram.data.model.FavoritePost
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject

interface PostRepository {
    fun getPostsFlow(): Flow<List<Post>>
    fun getPostsForUser(userId: String): Flow<List<Post>>
    suspend fun createPost(imageUri: Uri, caption: String, vehicleInfo: String): Result<Unit>
    suspend fun likePost(postId: String, userId: String): Result<Unit>
    suspend fun unlikePost(postId: String, userId: String): Result<Unit>
    suspend fun sharePost(postId: String): Result<Unit>
    suspend fun getPostById(postId: String): Result<Post>
    suspend fun createPostFromCloud(post: Post)

    // Favorite methods
    suspend fun addFavorite(postId: String, userId: String)
    suspend fun removeFavorite(postId: String, userId: String)
    fun getFavoritesForUser(userId: String): Flow<List<FavoritePost>>
    suspend fun isFavorite(postId: String, userId: String): Boolean
}

class PostRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val postDao: PostDao,
    private val userDao: UserDao,
    private val favoriteDao: FavoriteDao,
    @ApplicationContext private val context: Context
) : PostRepository {

    override fun getPostsFlow(): Flow<List<Post>> {
        return postDao.getAllPosts()
    }

    override fun getPostsForUser(userId: String): Flow<List<Post>> {
        return postDao.getPostsForUser(userId)
    }

    override suspend fun createPost(imageUri: Uri, caption: String, vehicleInfo: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("User not authenticated")
            var localUser = userDao.getUserById(user.uid)
            var username = localUser?.username
            if (username.isNullOrBlank()) {
                username = user.displayName ?: "User"
                // Update local Room user cu username-ul corect
                val updatedUser = localUser?.copy(username = username) ?: com.proiect.cargram.data.model.User(
                    id = user.uid,
                    username = username,
                    email = user.email ?: "",
                    profilePicturePath = localUser?.profilePicturePath ?: ""
                )
                userDao.insertUser(updatedUser)
                localUser = updatedUser
            }
            // 1. Copy image to internal storage and get local path
            val localImagePath = saveImageToInternalStorage(imageUri)
            // 2. Create Post object for local database
            val postId = UUID.randomUUID().toString()
            val localPost = Post(
                id = postId,
                userId = user.uid,
                username = username ?: user.displayName ?: "User",
                userProfilePicture = localUser?.profilePicturePath,
                imagePath = localImagePath,
                caption = caption,
                timestamp = Timestamp.now(),
                vehicleId = if (vehicleInfo.isNotEmpty()) vehicleInfo else null
            )
            // 3. Save full post to local database
            postDao.insertPost(localPost)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "${UUID.randomUUID()}.jpg"
        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file.absolutePath
    }
    
    override suspend fun likePost(postId: String, userId: String): Result<Unit> {
        return try {
            // Actualizare în Room
            val localPost = postDao.getPostById(postId)
            if (localPost != null) {
                val updatedPost = localPost.copy(
                    likes = localPost.likes + 1,
                    likedBy = localPost.likedBy + userId
                )
                postDao.insertPost(updatedPost)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun unlikePost(postId: String, userId: String): Result<Unit> {
        return try {
            // Actualizare în Room
            val localPost = postDao.getPostById(postId)
            if (localPost != null) {
                val updatedPost = localPost.copy(
                    likes = (localPost.likes - 1).coerceAtLeast(0),
                    likedBy = localPost.likedBy - userId
                )
                postDao.insertPost(updatedPost)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun sharePost(postId: String): Result<Unit> {
        return try {
            val localPost = postDao.getPostById(postId)
            if (localPost != null) {
                val updatedPost = localPost.copy(shares = localPost.shares + 1)
                postDao.insertPost(updatedPost)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPostById(postId: String): Result<Post> {
        return try {
            val post = postDao.getPostById(postId)
            if (post != null) {
                Result.success(post)
            } else {
                Result.failure(NoSuchElementException("Post not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createPostFromCloud(post: Post) {
        postDao.insertPost(post)
    }

    // Favorite methods
    override suspend fun addFavorite(postId: String, userId: String) {
        favoriteDao.addFavorite(FavoritePost(postId = postId, userId = userId))
    }

    override suspend fun removeFavorite(postId: String, userId: String) {
        val fav = favoriteDao.getFavoriteByUserAndPost(userId, postId)
        if (fav != null) {
            favoriteDao.removeFavorite(fav)
        }
    }

    override fun getFavoritesForUser(userId: String): Flow<List<FavoritePost>> {
        return favoriteDao.getFavoritesForUser(userId)
    }

    override suspend fun isFavorite(postId: String, userId: String): Boolean {
        return favoriteDao.getFavoriteByUserAndPost(userId, postId) != null
    }
} 