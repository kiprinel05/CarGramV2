package com.proiect.cargram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.proiect.cargram.data.repository.AuthRepository
import com.proiect.cargram.data.repository.AuthRepositoryImpl
import com.proiect.cargram.ui.navigation.AuthNavGraph
import com.proiect.cargram.ui.theme.CarGramTheme
import com.proiect.cargram.ui.viewmodel.AuthViewModel
import com.proiect.cargram.ui.viewmodel.SettingsViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var authRepository: AuthRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
            CarGramTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AuthNavGraph(
                        navController = navController,
                        authViewModel = hiltViewModel(),
                        darkMode = isDarkMode
                    )
                }
            }
        }
    }
}