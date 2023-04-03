package edu.temple.bistro.data

import android.util.Log
import edu.temple.bistro.data.api.YelpService
import edu.temple.bistro.data.model.Restaurant
import edu.temple.bistro.data.model.RestaurantCategoryReference
import edu.temple.bistro.data.model.RestaurantSearchResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class YelpRepository(private val database: YelpDatabase) {

    companion object {
        val API_TOKEN = "uXCBcUZJU7th_HYlcApVyqe8IFe1DegdYhrVXg_5vINo2_hne8G3s3xoPBUCmhIn0BawkhC9kraEemOZCLFK3pS2gN7Kx0CQK4CmUuI6d9J2O3WTMyluhA06GwMeZHYx"
    }

    private lateinit var httpClient: OkHttpClient
    private lateinit var retrofit: Retrofit
    private lateinit var yelpService: YelpService

    private val defaultScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }


    suspend fun init() {
        withContext(Dispatchers.IO) {
            if (!this@YelpRepository::httpClient.isInitialized) {
                httpClient = OkHttpClient.Builder().addInterceptor {
                    val req = it.request().newBuilder()
                        .addHeader("Authorization", "Bearer $API_TOKEN")
                        .build()
                    it.proceed(req)
                }.build()
            }
            if (!this@YelpRepository::retrofit.isInitialized) {
                retrofit = Retrofit.Builder()
                    .client(httpClient)
                    .baseUrl("https://api.yelp.com/v3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            if (!this@YelpRepository::yelpService.isInitialized) {
                yelpService = retrofit.create(YelpService::class.java)
            }
        }
    }
    suspend fun fetchRestaurants() {
        withContext(Dispatchers.IO) {
            val optionsMap = mutableMapOf(
                Pair("latitude", "39.977730"),
                Pair("longitude", "-75.156400"),
                Pair("categories", "bars"),
                Pair("device_platform", "android"),
                Pair("limit", "20")
            )
            val call = yelpService.searchRestaurants(optionsMap)
            call.enqueue(object : Callback<RestaurantSearchResponse> {
                override fun onResponse(
                    call: Call<RestaurantSearchResponse>,
                    response: Response<RestaurantSearchResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d(this@YelpRepository::class.simpleName, response.body().toString())
                        defaultScope.launch {
                            response.body()!!.restaurants.forEach { rest ->
                                database.restaurantDao().insertRestaurant(rest)
                                rest.categories.forEach { cat ->
                                    database.categoryDao().insertCategory(cat)
                                    database.restaurantDao().insertRestaurantCategories(
                                        RestaurantCategoryReference(rest.id, cat.alias)
                                    )
                                }
                            }
                        }
                    }
                    else {
                        Log.d(this@YelpRepository::class.simpleName, "API Unsuccessful")
                    }
                }

                override fun onFailure(call: Call<RestaurantSearchResponse>, t: Throwable) {
                    Log.d(this@YelpRepository::class.simpleName, "API Fail: ${t.message}")
                }

            })
        }
    }

    fun getRestaurants() = database.restaurantDao().getRestaurants()

    suspend fun insertRestaurants(vararg restaurant: Restaurant) = database.restaurantDao().insertRestaurant(
        *restaurant)

}