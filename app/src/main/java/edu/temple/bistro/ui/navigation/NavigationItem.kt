package edu.temple.bistro.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(val route: String, val icon: ImageVector, val contentDescription: String?) {
    object SignUpScreen: NavigationItem("sign_up_screen", Icons.Filled.AppRegistration, null)
    object HomeScreen: NavigationItem("home_screen", Icons.Filled.Home, "Home Screen")
    object SettingsScreen: NavigationItem("settings_screen", Icons.Filled.Settings, "Settings Screen")
    object FriendsScreen: NavigationItem("friends_screen", Icons.Filled.People, "Friends Screen")
    object SignInScreen: NavigationItem("sign_in_screen", Icons.Filled.AppRegistration, null)
    object LikedPlacesScreen: NavigationItem("liked_places_screen", Icons.Filled.Favorite, "Liked Places Screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}


