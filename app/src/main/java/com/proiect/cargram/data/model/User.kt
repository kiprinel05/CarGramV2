package com.proiect.cargram.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String = "",
    val username: String = "",
    val email: String = "",
    val profilePicturePath: String = ""
)