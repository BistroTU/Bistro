package edu.temple.bistro.ui

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.temple.bistro.data.BistroDatabase
import edu.temple.bistro.data.YelpRepository
import edu.temple.bistro.data.api.RestaurantSearchBuilder
import edu.temple.bistro.data.model.AppState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BistroViewModel(application: Application) : AndroidViewModel(application) {
    val database = BistroDatabase.getDatabase(getApplication())
    val yelpRepository = YelpRepository(database)

    val search = MutableStateFlow(RestaurantSearchBuilder())
    val location = MutableStateFlow<Location?>(null)

    lateinit var state: StateFlow<AppState?>
    init {
        viewModelScope.launch {
            state = yelpRepository.state.stateIn(viewModelScope)
            location.collect {
                if (yelpRepository.newRestaurants.first().isEmpty() && state.value?.searchParams?.isBlank() != false) {
                    if (it != null) {
                        search.value = RestaurantSearchBuilder()
                            .setLatitude(it.latitude)
                            .setLongitude(it.longitude)
                            .setRadius(8000)
                        executeSearch()
                    }
                }
            }
        }
    }

    fun executeSearch() {
        yelpRepository.fetchRestaurants(search.value)
    }
}