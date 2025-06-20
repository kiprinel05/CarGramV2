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

sealed class Screen(val route: String) {
    object Login : Screen("login?email={email}") {
        fun createRoute(email: String = "") = "login?email=$email"
    }
    object Register : Screen("register")
    object VehicleProfile : Screen("vehicle_profile")
    object MainFeed : Screen("main_feed")
    object Search : Screen("search")
    object Profile : Screen("profile")
}

@Composable
fun AuthNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val uiState by authViewModel.uiState.collectAsState()

    // Handle navigation based on authentication state
    LaunchedEffect(uiState.isAuthenticated, uiState.hasVehicleProfile, uiState.registrationComplete) {
        when {
            // After successful registration and vehicle profile setup
            uiState.isAuthenticated && uiState.hasVehicleProfile && uiState.registrationComplete -> {
                navController.navigate(Screen.MainFeed.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
            // After successful login
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
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegistrationSuccess = { email ->
                    navController.navigate(Screen.VehicleProfile.route)
                }
            )
        }

        composable(Screen.VehicleProfile.route) {
            VehicleProfileScreen(
                onProfileComplete = {
                    authViewModel.completeVehicleProfile()
                }
            )
        }

        composable(Screen.MainFeed.route) {
            val feedViewModel = hiltViewModel<FeedViewModel>()
            FeedScreen(
                viewModel = feedViewModel,
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(Screen.Search.route) {
            // TODO: Implement search screen
        }

        composable(Screen.Profile.route) {
            val profileViewModel = hiltViewModel<ProfileViewModel>()
            val uiState by profileViewModel.uiState.collectAsState()
            ProfileScreen(
                uiState = uiState,
                onReload = { profileViewModel.loadProfile() },
                onProfileImageSelected = { uri -> profileViewModel.uploadProfilePicture(uri) }
            )
        }
    }
} 