package edu.temple.bistro.ui.navigation.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import edu.temple.bistro.ui.BistroViewModel
import edu.temple.bistro.ui.navigation.NavigationItem
import edu.temple.bistro.ui.signin.SignInViewModel
import edu.temple.bistro.ui.theme.Inter
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(navController: NavHostController, bistroViewModel: BistroViewModel, signInViewModel: SignInViewModel = hiltViewModel()) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val state = signInViewModel.signInState.collectAsState(initial = null)

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 30.dp, end = 30.dp),
    ) {
        Text(text =  "Bistro", fontSize = 30.sp, fontFamily = Inter, fontWeight = FontWeight.Bold, modifier = Modifier.padding(0.dp,0.dp,0.dp,50.dp).fillMaxWidth(), textAlign = TextAlign.Center)
        TextField(modifier = Modifier.fillMaxWidth(), value = email, onValueChange = { email = it }, placeholder = { Text(text = "Email") })
        Spacer( modifier = Modifier.height(16.dp))
        TextField(modifier = Modifier.fillMaxWidth(), value = password, onValueChange = { password = it }, placeholder = { Text(text = "Password") })
        Button(
            onClick = {
                scope.launch {
                    signInViewModel.signInUser(email, password, bistroViewModel)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(0.dp,25.dp,0.dp,0.dp)
        ) {
            Text(
                modifier = Modifier.padding(7.dp),
                text = "Sign In"
            )
        }
        Row(horizontalArrangement = Arrangement.Center) {
            Text("   Need an account? ")
            Text("Click here to create one", color = Color.Blue, modifier = Modifier.clickable { navController.navigate(NavigationItem.SignUpScreen.route) })
        }
    }

    LaunchedEffect(key1 = state.value?.isSuccess) {
        scope.launch {
            if (state.value?.isSuccess?.isNotEmpty() == true) {
                val success = state.value?.isSuccess
                Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                navController.navigate(NavigationItem.HomeScreen.route)
            }
        }
    }
    LaunchedEffect(key1 = state.value?.isError) {
        scope.launch {
            if (state.value?.isError?.isNotBlank() == true) {
                val error = state.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }
}