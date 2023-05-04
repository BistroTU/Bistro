package edu.temple.bistro.ui.navigation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import edu.temple.bistro.ui.BistroViewModel
import edu.temple.bistro.ui.navigation.NavigationItem

@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings") }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { navController.navigate(NavigationItem.SignInScreen.route) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Log Out", fontSize = 24.sp)
                }
            }
        }
    )
}