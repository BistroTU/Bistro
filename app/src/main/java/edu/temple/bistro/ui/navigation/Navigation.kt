package edu.temple.bistro.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
//import edu.temple.Screen
import edu.temple.bistro.ui.BistroViewModel
import edu.temple.bistro.ui.navigation.screens.*
import edu.temple.bistro.ui.theme.Inter

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun Navigation(navController: NavHostController, startScreen: String, viewModel: BistroViewModel, innerPadding: PaddingValues) {
    NavHost(navController = navController, startDestination = startScreen) {
        composable(route = NavigationItem.HomeScreen.route) {
            HomeScreen(navController, viewModel, innerPadding)
        }
        composable(
            route = NavigationItem.SettingsScreen.route,
        ) {
            SettingsScreen(navController)
        }
        composable(
            route = NavigationItem.SignUpScreen.route,
        ) {
            SignUpScreen(navController, viewModel)
        }
        composable(
            route = NavigationItem.SignInScreen.route,
        ) {
            SignInScreen(navController, viewModel)
        }
        composable(
            route = NavigationItem.FriendsScreen.route
        ) {
            FriendsScreen(navController, viewModel)
        }
        composable(route = NavigationItem.LikedPlacesScreen.route) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Liked Places",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    fontFamily = Inter,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                viewModel.fireRepo.getUserFlow(viewModel.firebaseUser.value!!.username!!).value?.liked_places?.values?.let { it1 ->
                    PlacesScreen(
                        title = "Liked Places",
                        places = it1.toList()
                    )
                }
            }
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
