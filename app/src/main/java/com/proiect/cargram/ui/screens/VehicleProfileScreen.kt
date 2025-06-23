package com.proiect.cargram.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.proiect.cargram.R
import com.proiect.cargram.ui.viewmodel.VehicleProfileViewModel
import com.proiect.cargram.data.model.Vehicle
import android.util.Log
import androidx.compose.ui.layout.ContentScale

@Composable
fun VehicleProfileScreen(
    darkMode: Boolean = false,
    viewModel: VehicleProfileViewModel = hiltViewModel(),
    onProfileComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var vin by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState) {
        Log.d("VehicleProfileScreen", "UI State updated: $uiState")
    }

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
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "CarGram Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 8.dp)
            )

            // Title
            Text(
                text = "Enter Vehicle Information",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // VIN Section
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Vehicle Identification Number (VIN)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = vin,
                        onValueChange = { if (it.length <= 17) vin = it.uppercase() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        placeholder = { Text("Enter 17-character VIN") },
                        singleLine = true
                    )

                    Button(
                        onClick = { 
                            Log.d("VehicleProfileScreen", "Decoding VIN: $vin")
                            viewModel.decodeVin(vin) 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        enabled = vin.length == 17 && !uiState.isLoading,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text("Decode VIN")
                        }
                    }
                }
            }

            // Divider with "or"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                )
                Text(
                    text = "or",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Divider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                )
            }

            // Manual Entry Section
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Manually enter vehicle's profile information",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    VehicleForm(
                        vehicle = uiState.vehicle ?: Vehicle(),
                        onValueChange = { field, value ->
                            Log.d("VehicleProfileScreen", "Updating field: $field with value: $value")
                            viewModel.updateVehicleField(field, value)
                        }
                    )
                }
            }

            // Error message
            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            // Continue button
            Button(
                onClick = {
                    viewModel.saveVehicle()
                    onProfileComplete()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = uiState.vehicle != null && !uiState.isLoading,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Continue")
            }

            // Add some padding at the bottom for better scrolling
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Loading indicator overlay
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun VehicleForm(
    vehicle: Vehicle,
    onValueChange: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Brand and Model Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = vehicle.brand,
                onValueChange = { onValueChange("brand", it) },
                label = { Text("Brand*") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            )
            
            OutlinedTextField(
                value = vehicle.model,
                onValueChange = { onValueChange("model", it) },
                label = { Text("Model*") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            )
        }

        // Body, CMC, and Fuel Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = vehicle.body,
                onValueChange = { onValueChange("body", it) },
                label = { Text("Body*") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            )
            
            OutlinedTextField(
                value = vehicle.cmc,
                onValueChange = { onValueChange("cmc", it) },
                label = { Text("CMC*") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            )
            
            OutlinedTextField(
                value = vehicle.fuel,
                onValueChange = { onValueChange("fuel", it) },
                label = { Text("Fuel*") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            )
        }

        // Year and HP Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = vehicle.year,
                onValueChange = { onValueChange("year", it) },
                label = { Text("Year*") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            )
            
            OutlinedTextField(
                value = vehicle.hp,
                onValueChange = { onValueChange("hp", it) },
                label = { Text("HP*") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            )
        }

        // Color and Country Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = vehicle.color,
                onValueChange = { onValueChange("color", it) },
                label = { Text("Color*") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            )
            
            OutlinedTextField(
                value = vehicle.country,
                onValueChange = { onValueChange("country", it) },
                label = { Text("Country") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
} 