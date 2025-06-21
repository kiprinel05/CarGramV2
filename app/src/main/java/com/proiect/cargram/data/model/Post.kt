package com.proiect.cargram.data.model

import com.google.firebase.Timestamp

data class Post(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val userProfilePicture: String? = null,
    val imageUrl: String = "",
    val caption: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val likes: Int = 0,
    val comments: Int = 0,
    val shares: Int = 0,
    val likedBy: List<String> = emptyList(), // List of user IDs who liked the post
    val vehicleId: String? = null // Reference to the vehicle featured in the post
) 