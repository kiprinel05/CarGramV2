package com.proiect.cargram.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.proiect.cargram.ui.screens.LoginScreen
import com.proiect.cargram.ui.screens.RegisterScreen
import com.proiect.cargram.ui.screens.VehicleProfileScreen
import com.proiect.cargram.ui.viewmodel.AuthViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login?email={email}") {
        fun createRoute(email: String = "") = "login?email=$email"
    }
    object Register : Screen("register")
    object VehicleProfile : Screen("vehicle_profile")
    object MainFeed : Screen("main_feed")
}

@Composable
fun AuthNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
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
                },
                onLoginSuccess = {
                    navController.navigate(Screen.MainFeed.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
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
                    navController.navigate(Screen.Login.createRoute()) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.MainFeed.route) {
        }
    }
} 