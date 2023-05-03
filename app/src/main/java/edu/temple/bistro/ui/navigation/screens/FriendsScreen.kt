package edu.temple.bistro.ui.navigation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import coil.compose.AsyncImage
import edu.temple.bistro.R
import edu.temple.bistro.ui.BistroViewModel
import edu.temple.bistro.ui.friends.ProfilePicture

@Composable
fun FriendsScreen(navController: NavController?, viewModel: BistroViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyColumn {
            items(items=listOf(""), itemContent = {
                Row {
                    ProfilePicture(imageId = R.drawable.dominos)
                }
            })
        }
    }
}