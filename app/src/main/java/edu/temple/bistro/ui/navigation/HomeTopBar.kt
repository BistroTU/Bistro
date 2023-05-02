package edu.temple.bistro.ui.navigation

import android.location.Geocoder
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import edu.temple.bistro.ui.BistroViewModel

@Composable
fun HomeTopBar(viewModel: BistroViewModel) {
    val address = Geocoder(LocalContext.current)
}