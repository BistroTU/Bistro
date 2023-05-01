package edu.temple.bistro

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
//import edu.temple.Screen
import edu.temple.bistro.ui.BistroViewModel
import edu.temple.bistro.ui.navigation.NavigationItem
import edu.temple.bistro.ui.navigation.screens.HomeScreen
import edu.temple.bistro.ui.navigation.screens.SignUpScreen

@Composable
fun Navigation(navController: NavHostController, viewModel: BistroViewModel, innerPadding: PaddingValues) {
    NavHost(navController = navController, startDestination = NavigationItem.HomeScreen.route) {
        composable(route = NavigationItem.HomeScreen.route) {
            HomeScreen(navController, viewModel, innerPadding)
        }
        composable(
            route = NavigationItem.SettingsScreen.route,
        ) {
            SettingsScreen()
        }
        composable(
            route = NavigationItem.SignUpScreen.route,
        ) {
            SignUpScreen()
        }
//        composable(
//            route = NavigationItem.SettingsScreen.route + "/{name}",
//            arguments = listOf(
//                navArgument("name") {
//                    type = NavType.StringType
//                    defaultValue = "None"
//                }
//            )
//        ) {
//            SettingsScreen(name = it.arguments?.getString("name"))
//        }
    }
}

@Composable
fun SettingsScreen() {
    Box(
        modifier = Modifier.fillMaxSize()) {
        Text(text = "Hello")
    }
}