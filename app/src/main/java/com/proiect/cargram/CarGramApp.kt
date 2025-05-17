package com.proiect.cargram

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.initialize
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CarGramApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
    }
} 