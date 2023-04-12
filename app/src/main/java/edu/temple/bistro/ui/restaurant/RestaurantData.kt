package edu.temple.bistro.ui.restaurant

data class RestaurantData(
    val name: String,
    val location: String,
    val categories: Array<String>,
    val distanceMiles: Float,
    val stars: Int
)
