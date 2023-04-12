package edu.temple.bistro.ui.navigation

import android.widget.SearchView
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar() {
    Button(
        modifier = Modifier
            .height(43.dp)
            .width(223.dp)
        ,
        onClick = {},
        shape = RoundedCornerShape(61.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF292929), contentColor = Color.White),
    ) {
        Icon(
           Icons.Filled.Search,
           contentDescription = "search icon",
        )
//        Text(
//            modifier = Modifier.size(15.dp),
//            text = "Search")
    }
}