package edu.temple.bistro.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(val route: String, val icon: ImageVector) {
    object SignUpScreen: NavigationItem("sign_up_screen", Icons.Filled.AppRegistration)
    object HomeScreen: NavigationItem("home_screen", Icons.Filled.Home)
    object SettingsScreen: NavigationItem("settings_screen", Icons.Filled.Settings)
    object FriendsScreen: NavigationItem("friends_screen", Icons.Filled.People)

    object SignInScreen: NavigationItem("sign_in_screen", Icons.Filled.AppRegistration)

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}


