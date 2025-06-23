package com.proiect.cargram.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.proiect.cargram.data.local.FavoriteDao
import com.proiect.cargram.data.local.PostDao
import com.proiect.cargram.data.local.UserDao
import com.proiect.cargram.data.repository.AuthRepository
import com.proiect.cargram.data.repository.AuthRepositoryImpl
import com.proiect.cargram.data.repository.PostRepository
import com.proiect.cargram.data.repository.PostRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth
    ): AuthRepository {
        return AuthRepositoryImpl(auth)
    }

    @Provides
    @Singleton
    fun providePostRepository(
        auth: FirebaseAuth,
        postDao: PostDao,
        userDao: UserDao,
        favoriteDao: FavoriteDao,
        @ApplicationContext context: android.content.Context
    ): PostRepository {
        return PostRepositoryImpl(auth, postDao, userDao, favoriteDao, context)
    }

    @Provides
    @Singleton
    fun provideVehicleRepository(
        vinDecoderApi: com.proiect.cargram.data.api.VinDecoderApi,
        vehicleDao: com.proiect.cargram.data.local.VehicleDao,
        authRepository: com.proiect.cargram.data.repository.AuthRepository,
        @com.proiect.cargram.di.VinDecoderApiKey apiKey: String,
        @com.proiect.cargram.di.VinDecoderSecretKey secretKey: String
    ): com.proiect.cargram.data.repository.VehicleRepository {
        return com.proiect.cargram.data.repository.VehicleRepositoryImpl(
            vinDecoderApi,
            vehicleDao,
            authRepository,
            apiKey,
            secretKey
        )
    }
} 