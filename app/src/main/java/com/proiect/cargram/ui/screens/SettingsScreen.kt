package com.proiect.cargram.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val fuelPricesState by viewModel.fuelPricesState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadFuelPrices()
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
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
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                // Settings Header
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
                
                // Dark Mode Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "App Settings",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Dark Mode", style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.weight(1f))
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { viewModel.setDarkMode(it) }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Fuel Prices Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "Fuel Prices (US)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                if (fuelPricesState.isMockData) {
                                    Text(
                                        text = "Estimated prices (API unavailable)",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = { viewModel.loadFuelPrices() },
                                enabled = !fuelPricesState.isLoading
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh fuel prices"
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        when {
                            fuelPricesState.isLoading -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Loading fuel prices...")
                                }
                            }
                            fuelPricesState.error != null -> {
                                Text(
                                    text = "Error: ${fuelPricesState.error}",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            fuelPricesState.fuelPrices != null -> {
                                val prices = fuelPricesState.fuelPrices!!
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FuelPriceRow("Regular", prices.regular)
                                    FuelPriceRow("Midgrade", prices.midgrade)
                                    FuelPriceRow("Premium", prices.premium)
                                    FuelPriceRow("Diesel", prices.diesel)
                                    FuelPriceRow("E85", prices.e85)
                                    FuelPriceRow("Electric", prices.electric)
                                    prices.cng?.let { FuelPriceRow("CNG", it) }
                                    prices.lpg?.let { FuelPriceRow("LPG", it) }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Logout Button
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logout),
                        contentDescription = "Logout",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout")
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun FuelPriceRow(
    fuelType: String,
    price: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = fuelType,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = if (fuelType == "Electric") {
                "$${String.format("%.2f", price)}/kWh"
            } else {
                "$${String.format("%.2f", price)}/gal"
            },
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.primary
        )
    }
} 