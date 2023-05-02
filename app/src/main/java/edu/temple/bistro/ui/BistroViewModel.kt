package edu.temple.bistro.ui

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import edu.temple.bistro.FirebaseHelper
import edu.temple.bistro.Friend
import edu.temple.bistro.data.BistroDatabase
import edu.temple.bistro.data.YelpRepository
import edu.temple.bistro.data.api.RestaurantSearchBuilder
import edu.temple.bistro.data.model.AppState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BistroViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseDb = Firebase.database.apply {
        setPersistenceEnabled(true)
    }
    val firebase = FirebaseHelper(firebaseDb)
    val roomDb = BistroDatabase.getDatabase(getApplication())
    val yelpRepository = YelpRepository(roomDb)

    val search = MutableStateFlow(RestaurantSearchBuilder())
    val location = MutableStateFlow<Location?>(null)
    val currentUser = MutableStateFlow<FirebaseUser?>(null)

    private val _friends = MutableStateFlow<List<Friend>?>(null)

    val friends
        get() = _friends.asStateFlow()

    lateinit var state: StateFlow<AppState?>
    init {
        viewModelScope.launch {
            state = yelpRepository.state.stateIn(viewModelScope)
            location.collect {
                if (it == null) return@collect
                if (yelpRepository.newRestaurants.first().isEmpty() && state.value?.searchParams?.isBlank() != false) {
                    search.value = RestaurantSearchBuilder()
                        .setLatitude(it.latitude)
                        .setLongitude(it.longitude)
                        .setRadius(8000)
                    executeSearch()
                }
            }
        }
        viewModelScope.launch {
            currentUser.collect {
                if (it == null) return@collect
                firebase.getFriends(it.email!!, this@BistroViewModel::friendsListCallback)
            }
        }
    }

    fun executeSearch() {
        yelpRepository.fetchRestaurants(search.value)
    }

    private fun friendsListCallback(friends: List<Friend>) {
        _friends.value = friends
    }
}