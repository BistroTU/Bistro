package edu.temple.bistro.ui.navigation.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.temple.bistro.data.firebase.FirebasePlace

@Composable
fun PlacesScreen(title: String, places: List<FirebasePlace>) {
    val context = LocalContext.current

    LazyColumn(modifier = Modifier.fillMaxWidth()
        .padding(horizontal = 16.dp)) {
        items(places) {
            Text(
                text = it.name!!,
                fontSize = 24.sp,
                modifier = Modifier.padding(vertical = 4.dp)
                    .clickable {
                        it.url?.let { url -> context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PlacesScreen("Liked Places",listOf(FirebasePlace("1", "Mission Taqueria"),FirebasePlace("2", "Morimoto"), FirebasePlace("3", "Charlie was a sinner."), FirebasePlace("2","Sampan")))
}