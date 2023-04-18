package edu.temple.bistro.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.temple.bistro.data.BistroDatabase
import edu.temple.bistro.data.YelpRepository
import edu.temple.bistro.data.model.AppState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BistroViewModel(application: Application) : AndroidViewModel(application) {
    val yelpRepository = YelpRepository(BistroDatabase.getDatabase(getApplication()))

    lateinit var state: StateFlow<AppState>
    init {
        viewModelScope.launch {
            state = yelpRepository.state.stateIn(viewModelScope)
        }
    }
}