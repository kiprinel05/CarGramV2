package com.proiect.cargram.data.repository

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.proiect.cargram.data.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface PostRepository {
    fun getPostsFlow(): Flow<List<Post>>
    suspend fun createPost(post: Post): Result<Post>
    suspend fun createPost(imageUri: Uri, caption: String, vehicleInfo: String): Result<Post>
    suspend fun likePost(postId: String, userId: String): Result<Unit>
    suspend fun unlikePost(postId: String, userId: String): Result<Unit>
    suspend fun sharePost(postId: String): Result<Unit>
    suspend fun getPostById(postId: String): Result<Post>
}

class PostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PostRepository {
    
    private val testPosts = listOf(
        Post(
            id = "1",
            userId = "test_user1",
            username = "BMW Enthusiast",
            userProfilePicture = "android.resource://com.proiect.cargram/drawable/app_logo",
            imageUrl = "android.resource://com.proiect.cargram/drawable/app_logo",
            caption = "Check out this amazing BMW M4 Competition in Isle of Man Green! 🚗💨 #BMW #M4Competition",
            timestamp = Timestamp.now(),
            likes = 21,
            comments = 4,
            shares = 4,
            likedBy = listOf(),
            vehicleId = "bmw_m4"
        ),
        Post(
            id = "2",
            userId = "test_user2",
            username = "CarSpotter",
            userProfilePicture = "android.resource://com.proiect.cargram/drawable/background",
            imageUrl = "android.resource://com.proiect.cargram/drawable/background",
            caption = "Beautiful sunset with my new ride! 🌅 #CarLife #Automotive",
            timestamp = Timestamp.now(),
            likes = 15,
            comments = 2,
            shares = 1,
            likedBy = listOf(),
            vehicleId = "test_car"
        ),
        Post(
            id = "3",
            userId = "test_user3",
            username = "AutoPassion",
            userProfilePicture = "android.resource://com.proiect.cargram/drawable/app_logo",
            imageUrl = "android.resource://com.proiect.cargram/drawable/app_logo",
            caption = "Perfect day for a mountain drive! 🏔️ #DrivingPleasure #CarCommunity",
            timestamp = Timestamp.now(),
            likes = 32,
            comments = 7,
            shares = 3,
            likedBy = listOf(),
            vehicleId = "test_car2"
        )
    )
    
    override fun getPostsFlow(): Flow<List<Post>> = flow {
        try {
            // For testing, emit the test posts instead of fetching from Firestore
            emit(testPosts)
            
            /* Commented out Firestore implementation for now
            val snapshot = firestore.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val posts = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Post::class.java)?.copy(id = doc.id)
            }
            emit(posts)
            */
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    override suspend fun createPost(post: Post): Result<Post> {
        return try {
            val docRef = firestore.collection("posts").document()
            val postWithId = post.copy(id = docRef.id)
            docRef.set(postWithId).await()
            Result.success(postWithId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createPost(imageUri: Uri, caption: String, vehicleInfo: String): Result<Post> {
        return try {
            // For now, create a simple post with the provided data
            // In a real implementation, you would upload the image to Firebase Storage first
            val post = Post(
                id = "", // Will be set by Firestore
                userId = "current_user", // Should get from Auth
                username = "Current User", // Should get from Auth
                userProfilePicture = "android.resource://com.proiect.cargram/drawable/app_logo",
                imageUrl = imageUri.toString(),
                caption = caption,
                timestamp = Timestamp.now(),
                likes = 0,
                comments = 0,
                shares = 0,
                likedBy = listOf(),
                vehicleId = if (vehicleInfo.isNotEmpty()) "custom_vehicle" else null
            )
            
            createPost(post)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun likePost(postId: String, userId: String): Result<Unit> {
        return try {
            val postRef = firestore.collection("posts").document(postId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)
                val post = snapshot.toObject(Post::class.java)
                val currentLikes = post?.likes ?: 0
                val currentLikedBy = post?.likedBy ?: listOf()
                
                if (userId !in currentLikedBy) {
                    transaction.update(postRef, mapOf(
                        "likes" to currentLikes + 1,
                        "likedBy" to currentLikedBy + userId
                    ))
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun unlikePost(postId: String, userId: String): Result<Unit> {
        return try {
            val postRef = firestore.collection("posts").document(postId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)
                val post = snapshot.toObject(Post::class.java)
                val currentLikes = post?.likes ?: 0
                val currentLikedBy = post?.likedBy ?: listOf()
                
                if (userId in currentLikedBy) {
                    transaction.update(postRef, mapOf(
                        "likes" to (currentLikes - 1).coerceAtLeast(0),
                        "likedBy" to currentLikedBy - userId
                    ))
                }
            }.await()
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