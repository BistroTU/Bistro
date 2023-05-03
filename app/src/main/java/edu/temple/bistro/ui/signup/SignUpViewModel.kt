package edu.temple.bistro.ui.signup

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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val repository: AuthRepository): ViewModel() {
    val _signUpState = Channel<SignInState>()
    val signUpState = _signUpState.receiveAsFlow()

    fun signUpUser(firstName: String, lastName: String, email: String, password: String, bistroViewModel: BistroViewModel) = viewModelScope.launch {
        repository.registerUser(email, password).collect {result ->
            when(result) {
                is Resource.Success -> {
                    _signUpState.send(SignInState(isSuccess = "Sign Up Success"))
                    val user = repository.firebaseAuth.currentUser
                    bistroViewModel.firebase.addUser(user!!.uid, user.email!!, firstName, lastName)
                    bistroViewModel.currentUser.value = user
                }
                is Resource.Loading ->{
                    _signUpState.send(SignInState(isLoading = true))
                }
                is Resource.Error ->{

                    _signUpState.send(SignInState(isError = result.message))
                }
            }
        }
    }


}
