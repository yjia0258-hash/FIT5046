package com.example.dailywell.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dailywell.ui.screens.ChartScreen
import com.example.dailywell.ui.screens.CheckInScreen
import com.example.dailywell.ui.screens.HistoryScreen
import com.example.dailywell.ui.screens.HomeScreen
import com.example.dailywell.ui.screens.LoginScreen
import com.example.dailywell.ui.screens.ProfileScreen
import com.example.dailywell.ui.screens.SearchScreen
import com.example.dailywell.ui.screens.SignUpScreen
import com.example.dailywell.ui.screens.TipsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Save userId after successful login
    var loggedInUserId by remember { mutableIntStateOf(0) }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Login
        composable("login") {
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate("signup")
                },
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Sign Up
        composable("signup") {
            SignUpScreen(
                onNavigateToLogin = {
                    navController.navigate("login")
                },
                onSignUpSuccess = {
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        // Home
        composable("home") {
            HomeScreen(
                userId = loggedInUserId,
                onNavigateToCheckIn = {
                    navController.navigate("checkin")
                },
                onNavigateToTips = {
                    navController.navigate("tips")
                }
            )
        }

        // Daily Check-in (new entry)
        composable("checkin") {
            CheckInScreen(
                onSaveSuccess = {
                    navController.navigate("history") {
                        popUpTo("checkin") { inclusive = true }
                    }
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        // Daily Check-in (edit existing entry)
        composable("checkin/{entryId}") { backStackEntry ->
            val entryId = backStackEntry.arguments?.getString("entryId")?.toIntOrNull()
            CheckInScreen(
                entryId = entryId,
                onSaveSuccess = {
                    navController.navigate("history") {
                        popUpTo("checkin/{entryId}") { inclusive = true }
                    }
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        // History
        composable("history") {
            HistoryScreen(
                onNavigateToEdit = { entryId ->
                    navController.navigate("checkin/$entryId")
                }
            )
        }

        // Search
        composable("search") {
            SearchScreen()
        }

        // Weekly Report / Chart
        composable("chart") {
            ChartScreen()
        }

        // Profile
        composable("profile") {
            ProfileScreen(
                userId = loggedInUserId,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Tips / Support
        composable("tips") {
            TipsScreen(
                userId = loggedInUserId,
                onNavigateToHome = {
                    navController.navigate("home")
                }
            )
        }
    }
}