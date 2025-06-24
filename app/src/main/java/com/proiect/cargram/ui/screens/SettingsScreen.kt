package com.proiect.cargram.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.proiect.cargram.R
import com.proiect.cargram.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    darkMode: Boolean = false,
    onLogout: () -> Unit,
    onNavigateHome: (() -> Unit)? = null,
    onNavigateCreatePost: (() -> Unit)? = null,
    onNavigateProfile: (() -> Unit)? = null,
    onNavigateSettings: (() -> Unit)? = null
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(
                id = if (darkMode) R.drawable.background_darkmode else R.drawable.background
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        
        Scaffold(
            bottomBar = {
                BottomNavBar(
                    currentRoute = "settings",
                    onNavigate = { item ->
                        when (item) {
                            BottomNavItem.Home -> onNavigateHome?.invoke()
                            BottomNavItem.CreatePost -> onNavigateCreatePost?.invoke()
                            BottomNavItem.Profile -> onNavigateProfile?.invoke()
                            BottomNavItem.Settings -> onNavigateSettings?.invoke()
                        }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.settings),
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Settings", style = MaterialTheme.typography.headlineMedium)
                }
                Spacer(modifier = Modifier.height(40.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dark Mode", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(16.dp))
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { viewModel.setDarkMode(it) }
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logout),
                        contentDescription = "Logout",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout")
                }
            }
        }
    }
} 