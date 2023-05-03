package edu.temple.bistro.ui.navigation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.temple.bistro.Place

@Composable
fun PlacesScreen(places: List<Place>) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        places.forEach { place ->
            Text(
                text = place.name,
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PlacesScreen(listOf(Place("Mission Taqueria",1),Place("Morimoto",2), Place("Charlie was a sinner.",1), Place("Sampan",2)))
}