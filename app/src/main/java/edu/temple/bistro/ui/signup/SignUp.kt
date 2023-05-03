package edu.temple.bistro.ui.signup

import android.widget.Toast
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import edu.temple.bistro.ui.BistroViewModel
import edu.temple.bistro.ui.navigation.NavigationItem

@Composable
fun SignUp(navController: NavHostController, bistroViewModel: BistroViewModel, signUpViewModel: SignUpViewModel = hiltViewModel()) {
    var email by rememberSaveable { mutableStateOf("")}
    var password by rememberSaveable { mutableStateOf("")}

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val state = signUpViewModel.signUpState.collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 30.dp, end = 30.dp),
    ) {
        TextField(modifier = Modifier.fillMaxWidth(), value = email, onValueChange = { email = it }, placeholder = { Text(text = "Email") })
        Spacer( modifier = Modifier.height(16.dp))
        TextField(modifier = Modifier.fillMaxWidth(), value = password, onValueChange = { password = it }, placeholder = { Text(text = "Password")})
        Button(
            onClick = {
                scope.launch {
                    signUpViewModel.signUpUser(email, password)
                }
            },
            modifier = Modifier.fillMaxWidth()
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