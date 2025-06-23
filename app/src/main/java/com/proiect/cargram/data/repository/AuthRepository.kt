package com.proiect.cargram.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import android.util.Log

interface AuthRepository {
    suspend fun signUp(email: String, password: String, username: String): Result<FirebaseUser>
    suspend fun signIn(email: String, password: String): Result<FirebaseUser>
    fun signOut()
    fun getCurrentUser(): FirebaseUser?
}

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override suspend fun signUp(email: String, password: String, username: String): Result<FirebaseUser> {
        return try {
            Log.d("AuthRepoDebug", "Creating Firebase user for email: $email")
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Log.d("AuthRepoDebug", "Firebase user created: ${result.user?.uid}")
            
            // Update display name for the user
            result.user?.let { firebaseUser ->
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()
                Log.d("AuthRepoDebug", "User profile updated with display name: $username")
            }
            
            Result.success(result.user!!)
        } catch (e: Exception) {
            Log.e("AuthRepoDebug", "SignUp failed", e)
            Result.failure(e)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUser(): FirebaseUser? {
        val currentUser = auth.currentUser
        Log.d("AuthRepoDebug", "getCurrentUser called, result: ${currentUser?.uid}")
        return currentUser
    }
} 