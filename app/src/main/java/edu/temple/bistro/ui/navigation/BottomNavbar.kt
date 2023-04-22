package edu.temple.bistro.ui.navigation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController

@Composable
fun BottomNavbar(navController: NavHostController, items: List<NavigationItem>) {
    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Gray,
    ) {
        val currentRoute = currentRoute(navController)
        items.forEach { screen ->
            BottomNavigationItem(
                selected = currentRoute == currentRoute(navController),
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route)
                    }
                },
                icon = {
                    Icon(
                        screen.icon,
                        "icon",
                    )
               },
            )
        }
    }
}

@Composable
private fun currentRoute(navController: NavHostController): String? {
    return navController.currentBackStackEntry?.destination?.route
}