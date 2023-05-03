package edu.temple.bistro.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.temple.bistro.data.repository.AuthRepository
import edu.temple.bistro.ui.BistroViewModel
import edu.temple.bistro.ui.signin.SignInState
import edu.temple.bistro.util.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val repository: AuthRepository): ViewModel() {
    val _signInState = Channel<SignInState>()
    val signInState = _signInState.receiveAsFlow()

    fun signInUser(email: String, password: String, bistroViewModel: BistroViewModel) = viewModelScope.launch {
        repository.loginUser(email, password).collect {result ->
            when(result) {
                is Resource.Success -> {
                    _signInState.send(SignInState(isSuccess = "Sign In Success"))
                    bistroViewModel.currentUser.value = repository.firebaseAuth.currentUser
                }
                is Resource.Loading ->{
                    _signInState.send(SignInState(isLoading = true))
                }
                is Resource.Error ->{

                    _signInState.send(SignInState(isError = result.message))
                }
            }
        }
    }


}
