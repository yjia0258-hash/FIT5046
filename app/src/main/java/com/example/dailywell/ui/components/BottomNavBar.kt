package com.example.dailywell.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

// Navy color for selected tab
private val NavySelected = Color(0xFF3D5A80)
private val NavUnselected = Color(0xFF9E9E9E)

// Data class representing each bottom nav item
data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

// All 5 bottom navigation items matching the UI design
val bottomNavItems = listOf(
    BottomNavItem(
        label = "Home",
        route = "home",
        icon = Icons.Default.Home
    ),
    BottomNavItem(
        label = "History",
        route = "history",
        icon = Icons.Default.History
    ),
    BottomNavItem(
        label = "Search",
        route = "search",
        icon = Icons.Default.Search
    ),
    BottomNavItem(
        label = "Report",
        route = "chart",
        icon = Icons.Default.BarChart
    ),
    BottomNavItem(
        label = "Profile",
        route = "profile",
        icon = Icons.Default.Person
    )
)

@Composable
fun BottomNavBar(navController: NavController) {
    // Get current route to highlight the active tab
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    // Avoid re-navigating to the same screen
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Pop up to home to avoid building up a large back stack
                            popUpTo("home") {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NavySelected,
                    selectedTextColor = NavySelected,
                    unselectedIconColor = NavUnselected,
                    unselectedTextColor = NavUnselected,
                    indicatorColor = Color(0xFFEEEEF6)
                )
            )
        }
    }
}