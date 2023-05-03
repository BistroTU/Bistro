package edu.temple.bistro.ui.restaurant

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import edu.temple.bistro.data.api.RestaurantSearchBuilder
import edu.temple.bistro.data.model.Category
import edu.temple.bistro.ui.BistroViewModel
import edu.temple.bistro.ui.theme.Inter
import kotlin.math.roundToInt

@Composable
fun FilterMenu(bistroViewModel: BistroViewModel) {
    val popularCategories = listOf(
        Category("sandwiches", "Sandwiches"),
        Category("pizza", "Pizza"),
        Category("chinese", "Chinese"),
        Category("breakfast_brunch", "Breakfast & Brunch"),
        Category("tradamerican", "American (Traditional)"),
        Category("delis", "Delis"),
        Category("burgers", "Burgers"),
        Category("mexican", "Mexican"),
        Category("newamerican", "American (New)"),
        Category("seafood", "Seafood"),
        Category("italian", "Italian"),
        Category("hotdogs", "Fast Food"),
        Category("chicken_wings", "Chicken Wings"),
        Category("cafes", "Cafes"),
        Category("salad", "Salad"),
        Category("japanese", "Japanese"),
        Category("cheesesteaks", "Cheesesteaks"),
        Category("sushi", "Sushi Bars"),
        Category("caribbean", "Caribbean"),
        Category("mediterranean", "Mediterranean"),
    )
    val selectedCategories = remember { mutableSetOf<Category>() }
    var searchRadius by remember { mutableStateOf(0.25f) }

    Column() {
        Title("Categories")
        FlowRow(
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 10.dp),
            mainAxisAlignment = FlowMainAxisAlignment.Start,
            mainAxisSize = SizeMode.Wrap,
            mainAxisSpacing = 5.dp,
            crossAxisSpacing = 6.dp,
        ) {
            popularCategories.forEach { category ->
                val selected = remember { mutableStateOf(false) }
                CategoryChip(category = category.title, selected = selected.value, onClick = {
                    selected.value = !selected.value
                    if (selected.value) {
                        selectedCategories.add(category)
                    } else {
                        selectedCategories.remove(category)
                    }
                })
            }
        }
        Title("Prices")
        FlowRow(
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 10.dp),
            mainAxisAlignment = FlowMainAxisAlignment.Start,
            mainAxisSize = SizeMode.Wrap,
            mainAxisSpacing = 5.dp,
            crossAxisSpacing = 6.dp,
        ) {
            CategoryChip(category = "$", selected = false) {

            }
            CategoryChip(category = "$$", selected = false) {

            }
            CategoryChip(category = "$$$", selected = false) {

            }
            CategoryChip(category = "$$$$", selected = false) {

            }
            CategoryChip(category = "$$$$$", selected = false) {

            }
        }
        Title("Maximum Distance")
        Slider(value = searchRadius, onValueChange = { searchRadius = it }, valueRange = 0.25f..20.0f, steps = 18)
        Text(text = "${searchRadius.toString()} miles")

        Button(onClick = {
            bistroViewModel.search.value = RestaurantSearchBuilder()
                .addCategories(selectedCategories)
                .setLatitude(bistroViewModel.location.value!!.latitude)
                .setLongitude(bistroViewModel.location.value!!.longitude)
                .setRadius((searchRadius*1609).toInt())
            bistroViewModel.executeSearch()
        }) {
            Text("Apply")
        }
    }
}

@Composable
fun Title(text: String) {
    Text(text =  text, fontSize = 30.sp, fontFamily = Inter, fontWeight = FontWeight.Bold)
}