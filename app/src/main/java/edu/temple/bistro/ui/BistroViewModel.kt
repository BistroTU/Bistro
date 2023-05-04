package edu.temple.bistro.ui

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import edu.temple.bistro.FirebaseHelper
import edu.temple.bistro.Friend
import edu.temple.bistro.data.BistroDatabase
import edu.temple.bistro.data.YelpRepository
import edu.temple.bistro.data.api.RestaurantSearchBuilder
import edu.temple.bistro.data.firebase.FirebaseUser
import edu.temple.bistro.data.model.AppState
import edu.temple.bistro.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BistroViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseDb = Firebase.database.apply {
        setPersistenceEnabled(true)
    }
    val fireRepo = FirebaseRepository(firebaseDb, getApplication())
    val roomDb = BistroDatabase.getDatabase(getApplication())
    val yelpRepository = YelpRepository(roomDb)

    val search = MutableStateFlow(RestaurantSearchBuilder())
    val location = MutableStateFlow<Location?>(null)
    val authUser = MutableStateFlow(FirebaseAuth.getInstance().currentUser)
    val firebaseUser = MutableStateFlow<FirebaseUser?>(null)

    var state: StateFlow<AppState?>? = null
    init {
        viewModelScope.launch {
            state = yelpRepository.state.stateIn(viewModelScope)
            location.collect {
                if (it == null) return@collect
                if (yelpRepository.newRestaurants.first().isEmpty()) {
                    search.value = RestaurantSearchBuilder()
                        .setLatitude(it.latitude)
                        .setLongitude(it.longitude)
                        .setRadius(8000)
                    executeSearch()
                }
            }
        }
        viewModelScope.launch {
            authUser.collect {
                if (it == null) return@collect
                Log.d("BistroVM", it.toString())
                fireRepo.registerUser(it.email!!)
                viewModelScope.launch {
                    fireRepo.getUserFlow(it.email!!).collect { fu ->
                        firebaseUser.value = fu
                    }
                }
            }
        }

    }

    fun executeSearch() {
        yelpRepository.fetchRestaurants(search.value)
    }
}