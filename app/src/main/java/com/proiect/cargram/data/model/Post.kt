package com.proiect.cargram.data.model

import com.google.firebase.Timestamp

data class Post(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val userProfilePicture: String = "",
    val imageUrl: String = "",
    val caption: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val likes: Int = 0,
    val comments: Int = 0,
    val shares: Int = 0,
    val likedBy: List<String> = listOf(), // List of user IDs who liked the post
    val vehicleId: String = "" // Reference to the vehicle featured in the post
) 