package edu.temple.bistro.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import edu.temple.bistro.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val firebaseAuth: FirebaseAuth
    fun loginUser(email: String, password: String): Flow<Resource<AuthResult>>
    fun registerUser(email: String, password: String): Flow<Resource<AuthResult>>
}