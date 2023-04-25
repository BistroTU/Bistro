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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.temple.bistro.Place

@Composable
fun PlacesScreen(likedPlaces: List<Place>, seenPlaces: List<Place>) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            backgroundColor = MaterialTheme.colors.primary
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 }
            ) {
                Text(
                    text = "Liked",
                    color = if (selectedTabIndex == 0) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onPrimary.copy(0.6f)
                )
            }
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 }
            ) {
                Text(
                    text = "Recently Viewed",
                    color = if (selectedTabIndex == 1) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onPrimary.copy(0.6f)
                )
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            if (selectedTabIndex == 0) {
                LikedPlaceList(likedPlaces)
            } else {
                SeenPlaceList(seenPlaces)
            }
        }
    }
}

@Composable
fun LikedPlaceList(places: List<Place>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Liked Places",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.h5
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(places) { place ->
                PlaceListItem(place)
            }
        }
    }
}

@Composable
fun SeenPlaceList(places: List<Place>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Recently Viewed Places",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.h5
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(places) { place ->
                PlaceListItem(place)
            }
        }
    }
}

@Composable
fun PlaceListItem(place: Place) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Add the UI elements for the place item here
        Text(text = place.name)
        // ...
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PlacesScreen(listOf(Place("Mission Taqueria",1),Place("Morimoto",2)), listOf(Place("Charlie was a sinner.",1),Place("Sampan",2)))
}