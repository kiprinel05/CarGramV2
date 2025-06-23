package com.proiect.cargram.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.proiect.cargram.R
import com.proiect.cargram.ui.viewmodel.ProfileUiState
import androidx.compose.ui.zIndex
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    darkMode: Boolean = false,
    onReload: (() -> Unit)? = null,
    onProfileImageSelected: ((Uri) -> Unit)? = null,
    onNavigateHome: (() -> Unit)? = null,
    onNavigateCreatePost: (() -> Unit)? = null,
    onNavigateProfile: (() -> Unit)? = null,
    onNavigateSettings: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { onProfileImageSelected?.invoke(it) }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(
                id = if (darkMode) R.drawable.background_darkmode else R.drawable.background
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Scaffold(
            bottomBar = {
                BottomNavBar(
                    currentRoute = "profile",
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.error != null) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = uiState.error, color = MaterialTheme.colorScheme.error)
                        if (onReload != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = onReload) { Text("Retry") }
                        }
                    }
                } else {
                    val user = uiState.user
                    val vehicle = uiState.vehicle
                    val posts = uiState.posts
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.85f))
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(12.dp, RoundedCornerShape(24.dp)),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 24.dp, horizontal = 12.dp)
                            ) {
                                // Avatar + camera
                                Box(contentAlignment = Alignment.BottomEnd) {
                                    val profilePicPath = user?.profilePicturePath
                                    val painter = if (!profilePicPath.isNullOrBlank()) {
                                        rememberAsyncImagePainter(model = "file://$profilePicPath")
                                    } else {
                                        painterResource(id = R.drawable.app_logo)
                                    }
                                    Image(
                                        painter = painter,
                                        contentDescription = "Profile picture",
                                        modifier = Modifier
                                            .size(90.dp)
                                            .clip(CircleShape)
                                            .shadow(8.dp, CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { imagePickerLauncher.launch("image/*") },
                                        modifier = Modifier
                                            .size(24.dp)
                                            .zIndex(1f)
                                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                                            .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = "Change profile picture",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = user?.username ?: "-",
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    ProfileStat(number = posts.size.toString(), label = "posts")
                                    ProfileStat(number = posts.sumOf { it.likes }.toString(), label = "likes")
                                    ProfileStat(number = posts.sumOf { it.shares }.toString(), label = "favourites")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { /* TODO - edit profile */ },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Edit profile", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { /* TODO - share profile */ },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Text("Share profile", fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(8.dp, RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.97f)),
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = vehicle?.let { "${it.brand} ${it.model} ${it.trim} (${it.year})" } ?: "No vehicle linked",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    textAlign = TextAlign.Center
                                )
                                if (vehicle != null) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = listOfNotNull(
                                            vehicle.cmc.takeIf { it.isNotBlank() },
                                            vehicle.hp.takeIf { it.isNotBlank() }?.let { it + " HP" },
                                            vehicle.transmission.takeIf { it.isNotBlank() },
                                            vehicle.fuel.takeIf { it.isNotBlank() }
                                        ).joinToString(", "),
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "VIN: ${vehicle.vin}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Posts",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(start = 4.dp, bottom = 8.dp)
                        )
                        ProfilePostsGrid(posts)
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileStat(number: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = number, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(text = label, fontSize = 15.sp, color = Color.Gray)
    }
}

@Composable
fun ProfilePostsGrid(posts: List<com.proiect.cargram.data.model.Post>) {
    val rows = (posts.size + 2) / 3
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (col in 0 until 3) {
                    val index = row * 3 + col
                    if (index < posts.size) {
                        Card(
                            modifier = Modifier
                                .size(100.dp)
                                .shadow(4.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            val imagePath = posts[index].imagePath
                            if (!imagePath.isNullOrBlank()) {
                                Image(
                                    painter = rememberAsyncImagePainter(model = "file://$imagePath"),
                                    contentDescription = "Post image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.app_logo),
                                    contentDescription = "Post image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.size(100.dp))
                    }
                }
            }
        }
    }
}