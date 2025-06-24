package com.proiect.cargram.di

import com.proiect.cargram.data.api.VinDecoderApi
import com.proiect.cargram.data.api.FuelPricesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import javax.inject.Qualifier
import okhttp3.Headers
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class VinDecoderRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FuelPricesRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    @VinDecoderRetrofit
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.vindecoder.eu/3.2/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @FuelPricesRetrofit
    fun provideFuelPricesRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.fueleconomy.gov/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideVinDecoderApi(@VinDecoderRetrofit retrofit: Retrofit): VinDecoderApi {
        return retrofit.create(VinDecoderApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFuelPricesApi(@FuelPricesRetrofit fuelPricesRetrofit: Retrofit): FuelPricesApi {
        return fuelPricesRetrofit.create(FuelPricesApi::class.java)
    }

    @Provides
    @Singleton
    @VinDecoderApiKey
    fun provideApiKey(): String {
        return "e8affb3c50fa"
    }

    @Provides
    @Singleton
    @VinDecoderSecretKey
    fun provideSecretKey(): String {
        return "3b09b541cb"
    }
} 