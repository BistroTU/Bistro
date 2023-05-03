package edu.temple.bistro.ui.navigation

import android.location.Geocoder
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Filter1
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.temple.bistro.ui.BistroViewModel
import edu.temple.bistro.ui.theme.BistroRed
import edu.temple.bistro.ui.theme.Inter

@Composable
fun HomeTopBar(viewModel: BistroViewModel, isFilterMenuOpen: MutableState<Boolean>) {
    val location = viewModel.location.collectAsState()
    val iconTint = if(isFilterMenuOpen.value) BistroRed else Color.Gray
    if (location.value != null) {
        val address = Geocoder(LocalContext.current).getFromLocation(
            location.value!!.latitude,
            location.value!!.longitude,
            1
        );
        Row(modifier = Modifier.padding(15.dp, 0.dp)) {
            Text(
                address!!.get(0).locality,
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Icon(Icons.Filled.FilterAlt, "filter", tint = iconTint, modifier = Modifier.clickable { isFilterMenuOpen.value = !isFilterMenuOpen.value })
        }
    }
}