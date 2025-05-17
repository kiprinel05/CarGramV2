package com.proiect.cargram.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class VinDecoderApiKey

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class VinDecoderSecretKey 