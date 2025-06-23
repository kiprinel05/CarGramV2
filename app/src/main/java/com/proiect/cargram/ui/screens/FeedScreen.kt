package com.proiect.cargram.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.proiect.cargram.R
import com.proiect.cargram.data.model.Post
import com.proiect.cargram.ui.viewmodel.FeedViewModel
import com.proiect.cargram.ui.viewmodel.SortType
import com.proiect.cargram.ui.viewmodel.FeedTab
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel

sealed class BottomNavItem(val route: String, val icon: Int) {
    object Home : BottomNavItem("home", R.drawable.home)
    object CreatePost : BottomNavItem("create_post", R.drawable.share)
    object Profile : BottomNavItem("profile", R.drawable.profile)
    object Settings : BottomNavItem("settings", R.drawable.settings)
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (BottomNavItem) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        tonalElevation = 0.dp
    ) {
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.CreatePost,
            BottomNavItem.Profile,
            BottomNavItem.Settings
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    icon = {
                        Image(
                            painter = painterResource(id = item.icon),
                            contentDescription = item.route,
                            modifier = Modifier.size(28.dp),
                            colorFilter = ColorFilter.tint(
                                if (currentRoute == item.route) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                }
                            )
                        )
                    },
                    selected = currentRoute == item.route,
                    onClick = { onNavigate(item) },
                    modifier = Modifier.weight(1f),
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    darkMode: Boolean = false,
    onNavigateToCreatePost: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToMessages: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUserId = viewModel.getCurrentUserId()

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

        // Main Content
        Scaffold(
            topBar = {
                FeedTopBar(
                    onNotificationsClick = onNavigateToNotifications,
                    onMessagesClick = onNavigateToMessages,
                    onSortClick = { viewModel.setSortType(it) },
                    currentSortType = uiState.sortType
                )
            },
            bottomBar = {
                BottomNavBar(
                    currentRoute = BottomNavItem.Home.route,
                    onNavigate = { item ->
                        when (item) {
                            BottomNavItem.Home -> {} // Already on home
                            BottomNavItem.CreatePost -> onNavigateToCreatePost()
                            BottomNavItem.Profile -> currentUserId?.let { onNavigateToProfile(it) }
                            BottomNavItem.Settings -> onNavigateToSettings()
                        }
                    }
                )
            },
            containerColor = Color.Transparent // Make scaffold background transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Tabs For you / Favorites
                FeedTabs(
                    selectedTab = uiState.selectedTab,
                    onTabSelected = { viewModel.setTab(it) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            items(uiState.posts) { post ->
                                PostCard(
                                    post = post,
                                    currentUserId = currentUserId,
                                    isFavorite = viewModel.isFavorite(post.id),
                                    onLikeClick = { viewModel.likePost(post.id) },
                                    onUnlikeClick = { viewModel.unlikePost(post.id) },
                                    onFavoriteClick = {
                                        if (viewModel.isFavorite(post.id)) {
                                            viewModel.removeFavorite(post.id)
                                        } else {
                                            viewModel.addFavorite(post.id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                    uiState.error?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedTopBar(
    onNotificationsClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onSortClick: (SortType) -> Unit,
    currentSortType: SortType
) {
    var showSortMenu by remember { mutableStateOf(false) }
    
    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .height(32.dp)
                    .padding(start = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "CarGram Logo",
                    modifier = Modifier.fillMaxHeight(),
                    contentScale = ContentScale.Fit
                )
            }
        },
        actions = {
            // Sort button
            Box {
                IconButton(
                    onClick = { showSortMenu = true }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = "Sort posts",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { 
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Timeline")
                                if (currentSortType == SortType.TIMELINE) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        onClick = {
                            onSortClick(SortType.TIMELINE)
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { 
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Most Liked")
                                if (currentSortType == SortType.LIKES) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        onClick = {
                            onSortClick(SortType.LIKES)
                            showSortMenu = false
                        }
                    )
                }
            }
            
            IconButton(onClick = onNotificationsClick) {
                Image(
                    painter = painterResource(id = R.drawable.like),
                    contentDescription = "Notifications",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
            }
            IconButton(onClick = onMessagesClick) {
                Image(
                    painter = painterResource(id = R.drawable.message),
                    contentDescription = "Messages",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            titleContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun FeedTabs(selectedTab: FeedTab, onTabSelected: (FeedTab) -> Unit) {
    val tabs = listOf(FeedTab.FOR_YOU to "For you", FeedTab.FAVORITES to "Favorites")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        tabs.forEach { (tab, label) ->
            val selected = tab == selectedTab
            val underlineWidth by animateDpAsState(
                targetValue = if (selected) 36.dp else 0.dp,
                animationSpec = tween(durationMillis = 250), label = "underline"
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
                    .clickable(
                        onClick = { onTabSelected(tab) },
                        indication = rememberRipple(bounded = false, color = MaterialTheme.colorScheme.primary),
                        interactionSource = remember { MutableInteractionSource() }
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { contentDescription = label }
                )
                AnimatedVisibility(visible = selected) {
                    Box(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .height(4.dp)
                            .width(underlineWidth)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    currentUserId: String?,
    isFavorite: Boolean,
    onLikeClick: () -> Unit,
    onUnlikeClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column {
            // Post header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User profile picture
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.userProfilePicture)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = post.username,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                )
                IconButton(
                    onClick = { /* Show more options */ },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            // Post image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f/3f)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.imagePath)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Post image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like button and count
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (currentUserId != null) {
                                if (post.likedBy.contains(currentUserId)) {
                                    onUnlikeClick()
                                } else {
                                    onLikeClick()
                                }
                            }
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Image(
                            painter = painterResource(
                                id = if (currentUserId != null && post.likedBy.contains(currentUserId)) {
                                    R.drawable.like
                                } else {
                                    R.drawable.like
                                }
                            ),
                            contentDescription = "Like",
                            modifier = Modifier.size(24.dp),
                            colorFilter = if (currentUserId != null && post.likedBy.contains(currentUserId)) {
                                ColorFilter.tint(Color(0xFFE91E63)) // Pink color for liked
                            } else {
                                ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                            }
                        )
                    }
                    Text(
                        text = "${post.likes} likes",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
                // Favorite button
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.favorites),
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            // Caption
            if (!post.caption.isNullOrBlank()) {
                Text(
                    text = post.caption,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
} 