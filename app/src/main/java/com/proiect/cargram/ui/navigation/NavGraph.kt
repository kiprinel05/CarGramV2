package com.proiect.cargram.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.proiect.cargram.ui.screens.LoginScreen
import com.proiect.cargram.ui.screens.RegisterScreen
import com.proiect.cargram.ui.viewmodel.AuthViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login?email={email}") {
        fun createRoute(email: String = "") = "login?email=$email"
    }
    object Register : Screen("register")
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
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegistrationSuccess = { email ->
                    navController.navigate(Screen.Login.createRoute(email)) {
                        popUpTo(Screen.Login.createRoute()) { inclusive = true }
                    }
                }
            )
        }
    }
} 