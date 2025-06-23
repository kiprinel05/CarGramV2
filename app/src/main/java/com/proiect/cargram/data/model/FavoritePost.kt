package com.proiect.cargram.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_posts")
data class FavoritePost(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val postId: String,
    val userId: String
) 