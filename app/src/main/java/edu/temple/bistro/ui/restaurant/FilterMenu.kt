package edu.temple.bistro.ui.restaurant

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import java.text.DecimalFormat

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
    val prices = listOf(
        "$",
        "$$",
        "$$$",
        "$$$$"
    )

    val selectedCategories = rememberSaveable { bistroViewModel.search.value.getCategories().toMutableSet() }
    val selectedPriceRanges = rememberSaveable { bistroViewModel.search.value.getPrices().toMutableSet() }
    var searchRadius by rememberSaveable { mutableStateOf(bistroViewModel.search.value.getRadius()*0.000621371f) }

    Column(modifier = Modifier.padding(20.dp)) {
        Title("Categories")
        FlowRow(
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 10.dp),
            mainAxisAlignment = FlowMainAxisAlignment.Start,
            mainAxisSize = SizeMode.Wrap,
            mainAxisSpacing = 5.dp,
            crossAxisSpacing = 6.dp,
        ) {
            popularCategories.forEach { category ->
                val selected = remember { mutableStateOf(selectedCategories.contains(category)) }
                val chipContentAction = if (selected.value) "Remove ${category} from filters" else "Add ${category} to filters"
                InfoChip(category = category.title, selected = selected.value, chipContentDescription = chipContentAction, onClick = {
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
            prices.forEach { price ->
                val selected = remember { mutableStateOf(selectedPriceRanges.contains(price)) }
                val chipContentAction = if (selected.value) "Remove ${price} from filters" else "Add ${price} to filters"

                InfoChip(category = price, selected = selected.value, chipContentDescription = chipContentAction, onClick = {
                    selected.value = !selected.value
                    if (selected.value) {
                        selectedPriceRanges.add(price)
                    } else {
                        selectedPriceRanges.remove(price)
                    }
                },)
            }
        }
        Title("Maximum Distance")
        Slider(value = searchRadius, onValueChange = { searchRadius = it }, valueRange = 0.5f..20.0f, steps = 38)
        Text(text = "${DecimalFormat("#.##").format(searchRadius)} miles")

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                bistroViewModel.search.value = RestaurantSearchBuilder()
                    .addCategories(selectedCategories)
                    .addPricesStr(selectedPriceRanges)
                    .setLatitude(bistroViewModel.location.value!!.latitude)
                    .setLongitude(bistroViewModel.location.value!!.longitude)
                    .setRadius((searchRadius * 1609).toInt())
                bistroViewModel.executeSearch()
            }) {
                Text("Apply")
            }
        }
    }
}

@Composable
fun Title(text: String) {
    Text(text =  text, fontSize = 30.sp, fontFamily = Inter, fontWeight = FontWeight.Bold, modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 10.dp))
}