package edu.temple.bistro.data.model

import com.google.gson.annotations.SerializedName

data class RestaurantSearchResponse (
    @SerializedName("businesses")
    val restaurants: List<Restaurant>,
    val total: Int,
    val region: Region
)

data class Region (
    val center: Coordinates
)