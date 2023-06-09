package edu.temple.bistro.ui.navigation.screens

import android.widget.Toast
import androidx.compose.animation.expandHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import edu.temple.bistro.ui.BistroViewModel
import edu.temple.bistro.ui.navigation.NavigationItem
import edu.temple.bistro.ui.signup.SignUpViewModel
import edu.temple.bistro.ui.theme.Inter

@Composable
fun SignUpScreen(navController: NavHostController, bistroViewModel: BistroViewModel, signUpViewModel: SignUpViewModel = hiltViewModel()) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("")}
    var password by rememberSaveable { mutableStateOf("")}

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val state = signUpViewModel.signUpState.collectAsState(initial = null)
    val user = bistroViewModel.authUser.collectAsState()

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 30.dp, end = 30.dp),
    ) {
        Text(text =  "Bistro", fontSize = 30.sp, fontFamily = Inter, fontWeight = FontWeight.Bold, modifier = Modifier.padding(0.dp,0.dp,0.dp,50.dp).fillMaxWidth(), textAlign = TextAlign.Center)
        TextField(modifier = Modifier.fillMaxWidth(), value = firstName, onValueChange = { firstName = it }, placeholder = { Text(text = "First Name") })
        TextField(modifier = Modifier.fillMaxWidth(), value = lastName, onValueChange = { lastName = it }, placeholder = { Text(text = "Last Name") })
        TextField(modifier = Modifier.fillMaxWidth(), value = email, onValueChange = { email = it }, placeholder = { Text(text = "Email") })
        Spacer( modifier = Modifier.height(16.dp))
        TextField(modifier = Modifier.fillMaxWidth(), value = password, onValueChange = { password = it }, placeholder = { Text(text = "Password")})
        Button(
            onClick = {
                scope.launch {
                    signUpViewModel.signUpUser(firstName, lastName, email, password, bistroViewModel)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(0.dp,25.dp,0.dp,0.dp)
        ) {
            Text(
                modifier = Modifier.padding(7.dp),
                text = "Sign Up"
            )
        }
        Row(horizontalArrangement = Arrangement.Center) {
            Text("Already have an account? ")
            Text("Click here to sign in", color = Color.Blue, modifier = Modifier.clickable { navController.navigate(NavigationItem.SignInScreen.route) })
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
    LaunchedEffect(key1 = user.value) {
        if (user.value != null) {
            navController.navigate(NavigationItem.HomeScreen.route)
        }
    }
}