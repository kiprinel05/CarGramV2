package com.proiect.cargram.data.local

import androidx.room.*
import com.proiect.cargram.data.model.FavoritePost
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoritePost)

    @Delete
    suspend fun removeFavorite(favorite: FavoritePost)

    @Query("SELECT * FROM favorite_posts WHERE userId = :userId")
    fun getFavoritesForUser(userId: String): Flow<List<FavoritePost>>

    @Query("SELECT * FROM favorite_posts WHERE userId = :userId AND postId = :postId LIMIT 1")
    suspend fun getFavoriteByUserAndPost(userId: String, postId: String): FavoritePost?
} 