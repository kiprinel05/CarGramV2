package com.proiect.cargram.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.proiect.cargram.data.local.PostDao
import com.proiect.cargram.data.model.Post
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
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
}

class PostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val postDao: PostDao,
    @ApplicationContext private val context: Context
) : PostRepository {

    override fun getPostsFlow(): Flow<List<Post>> {
        // Now primarily fetches from local DB for the feed
        return postDao.getAllPosts()
    }

    override fun getPostsForUser(userId: String): Flow<List<Post>> {
        // Fetches from local DB for a specific user profile
        return postDao.getPostsForUser(userId)
    }

    override suspend fun createPost(imageUri: Uri, caption: String, vehicleInfo: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("User not authenticated")

            // 1. Copy image to internal storage and get local path
            val localImagePath = saveImageToInternalStorage(imageUri)

            // 2. Create Post object for local database
            val postId = UUID.randomUUID().toString()
            val localPost = Post(
                id = postId,
                userId = user.uid,
                username = user.displayName ?: "User",
                userProfilePicture = user.photoUrl?.toString(),
                imagePath = localImagePath,
                caption = caption,
                timestamp = Timestamp.now(),
                vehicleId = if (vehicleInfo.isNotEmpty()) vehicleInfo else null
            )

            // 3. Save full post to local database
            postDao.insertPost(localPost)

            // 4. Create data map for Firestore (without image path)
            val firestorePostData = mapOf(
                "id" to postId,
                "userId" to localPost.userId,
                "username" to localPost.username,
                "userProfilePicture" to localPost.userProfilePicture,
                "caption" to localPost.caption,
                "timestamp" to localPost.timestamp,
                "likes" to localPost.likes,
                "comments" to localPost.comments,
                "shares" to localPost.shares,
                "likedBy" to localPost.likedBy,
                "vehicleId" to localPost.vehicleId
            )
            
            // 5. Save metadata-only to Firestore
            firestore.collection("posts").document(postId).set(firestorePostData).await()
            
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
            // Actualizare Firestore (cum e deja)
            val postRef = firestore.collection("posts").document(postId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)
                val post = snapshot.toObject(Post::class.java)
                val currentLikes = post?.likes ?: 0
                val currentLikedBy = post?.likedBy ?: listOf<String>()
                if (userId !in currentLikedBy) {
                    transaction.update(postRef, mapOf(
                        "likes" to currentLikes + 1,
                        "likedBy" to currentLikedBy + userId
                    ))
                }
            }.await()
            // Actualizare și în Room
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
            // Actualizare Firestore (cum e deja)
            val postRef = firestore.collection("posts").document(postId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)
                val post = snapshot.toObject(Post::class.java)
                val currentLikes = post?.likes ?: 0
                val currentLikedBy = post?.likedBy ?: listOf<String>()
                if (userId in currentLikedBy) {
                    transaction.update(postRef, mapOf(
                        "likes" to (currentLikes - 1).coerceAtLeast(0),
                        "likedBy" to currentLikedBy - userId
                    ))
                }
            }.await()
            // Actualizare și în Room
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
            val postRef = firestore.collection("posts").document(postId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)
                val currentShares = snapshot.getLong("shares") ?: 0
                transaction.update(postRef, "shares", currentShares + 1)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPostById(postId: String): Result<Post> {
        return try {
            val snapshot = firestore.collection("posts").document(postId).get().await()
            val post = snapshot.toObject(Post::class.java)?.copy(id = snapshot.id)
            if (post != null) {
                Result.success(post)
            } else {
                Result.failure(NoSuchElementException("Post not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 