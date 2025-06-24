package com.proiect.cargram.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.proiect.cargram.ui.screens.*
import com.proiect.cargram.ui.viewmodel.AuthViewModel
import com.proiect.cargram.ui.viewmodel.FeedViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.proiect.cargram.ui.viewmodel.ProfileViewModel
import com.proiect.cargram.ui.screens.SettingsScreen

sealed class Screen(val route: String) {
    object Login : Screen("login?email={email}") {
        fun createRoute(email: String = "") = "login?email=$email"
    }
    object Register : Screen("register")
    object VehicleProfile : Screen("vehicle_profile")
    object MainFeed : Screen("main_feed")
    object Search : Screen("search")
    object Profile : Screen("profile?userId={userId}") {
        fun createRoute(userId: String? = null): String {
            return if (userId != null) "profile?userId=$userId" else "profile?userId="
        }
    }
    object CreatePost : Screen("create_post")
}

@Composable
fun AuthNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    darkMode: Boolean = false
) {
    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isAuthenticated, uiState.hasVehicleProfile, uiState.registrationComplete) {
        when {
            !uiState.isAuthenticated -> {
                navController.navigate(Screen.Login.createRoute()) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
            uiState.isAuthenticated && uiState.hasVehicleProfile && uiState.registrationComplete -> {
                navController.navigate(Screen.MainFeed.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
            uiState.isAuthenticated && !uiState.registrationComplete -> {
                navController.navigate(Screen.MainFeed.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.createRoute()
    ) {
        composable(
            route = Screen.Login.route,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email")
            LoginScreen(
                viewModel = authViewModel,
                initialEmail = email ?: "",
                darkMode = darkMode,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                darkMode = darkMode,
                onRegistrationSuccess = { email ->
                    navController.navigate(Screen.VehicleProfile.route)
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.VehicleProfile.route) {
            VehicleProfileScreen(
                darkMode = darkMode,
                onProfileComplete = {
                    authViewModel.completeVehicleProfile()
                }
            )
        }

        composable(Screen.MainFeed.route) {
            if (!uiState.isAuthenticated) {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Login.createRoute()) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            } else {
                val feedViewModel = hiltViewModel<FeedViewModel>()
                FeedScreen(
                    viewModel = feedViewModel,
                    darkMode = darkMode,
                    onNavigateToCreatePost = {
                        navController.navigate(Screen.CreatePost.route)
                    },
                    onNavigateToProfile = { currentUserId ->
                        navController.navigate(Screen.Profile.createRoute(currentUserId))
                    },
                    onNavigateToSettings = { navController.navigate("settings") },
                    onNavigateToNotifications = {
                        // TODO - implement notifications navigation
                    },
                    onNavigateToMessages = {
                        // TODO - implement messages navigation
                    }
                )
            }
        }

        composable(Screen.Search.route) {
            // TODO - implement search screen
        }

        composable(
            route = Screen.Profile.route,
            arguments = listOf(navArgument("userId") {
                type = NavType.StringType
                nullable = true
            })
        ) {
            if (!uiState.isAuthenticated) {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Login.createRoute()) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            } else {
                val profileViewModel = hiltViewModel<ProfileViewModel>()
                val uiState by profileViewModel.uiState.collectAsState()
                ProfileScreen(
                    uiState = uiState,
                    darkMode = darkMode,
                    onReload = { profileViewModel.loadProfile() },
                    onProfileImageSelected = { uri -> profileViewModel.uploadProfilePicture(uri) },
                    onNavigateHome = { navController.navigate(Screen.MainFeed.route) },
                    onNavigateCreatePost = { navController.navigate(Screen.CreatePost.route) },
                    onNavigateProfile = {
                        val userId = profileViewModel.uiState.value.user?.id
                        navController.navigate(Screen.Profile.createRoute(userId))
                    },
                    onNavigateSettings = { navController.navigate("settings") }
                )
            }
        }

        composable(Screen.CreatePost.route) {
            if (!uiState.isAuthenticated) {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Login.createRoute()) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            } else {
                CreatePostScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    darkMode = darkMode
                )
            }
        }

        composable("settings") {
            if (!uiState.isAuthenticated) {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Login.createRoute()) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            } else {
                SettingsScreen(
                    darkMode = darkMode,
                    onLogout = {
                        authViewModel.signOut()
                        navController.navigate(Screen.Login.createRoute()) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onNavigateHome = { navController.navigate(Screen.MainFeed.route) },
                    onNavigateCreatePost = { navController.navigate(Screen.CreatePost.route) },
                    onNavigateProfile = { 
                        navController.navigate(Screen.Profile.createRoute(null))
                    },
                    onNavigateSettings = { navController.navigate("settings") }
                )
            }
        }
    }
} 