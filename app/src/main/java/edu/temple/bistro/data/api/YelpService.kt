package edu.temple.bistro.data.api

import edu.temple.bistro.data.model.Restaurant
import edu.temple.bistro.data.model.RestaurantSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface YelpService {
    @GET("businesses/search")
    fun searchRestaurants(@QueryMap options: Map<String, String>): Call<RestaurantSearchResponse>

    @GET("businesses/{id}")
    fun getRestaurantDetail(
        @Path("id") id: String,
        @QueryMap options: Map<String, String>?
    ): Call<Restaurant>
}