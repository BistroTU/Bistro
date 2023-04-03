package edu.temple.bistro.data

import android.util.Log
import edu.temple.bistro.BuildConfig
import edu.temple.bistro.data.api.RestaurantSearchBuilder
import edu.temple.bistro.data.api.YelpService
import edu.temple.bistro.data.model.Category
import edu.temple.bistro.data.model.Restaurant
import edu.temple.bistro.data.model.RestaurantCategoryReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class YelpRepository(private val database: YelpDatabase) {

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
                        .addHeader("Authorization", "Bearer ${BuildConfig.YELP_TOKEN}")
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
            RestaurantSearchBuilder()
                .setLatitude(39.977730)
                .setLongitude(-75.156400)
                .addCategory(Category("bars", "Bars"))
                .setLimit(20)
                .addSuccessCallback { response ->
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
                        Log.d(this@YelpRepository::class.simpleName, "API Unsuccessful ${response.errorBody()?.string()}")
                    }
                }
                .addFailureCallback {
                    Log.d(this@YelpRepository::class.simpleName, "API Fail: ${it.message}")
                }
                .call(yelpService)
        }
    }

    fun getRestaurants() = database.restaurantDao().getRestaurants()

    suspend fun insertRestaurants(vararg restaurant: Restaurant) = database.restaurantDao().insertRestaurant(
        *restaurant)

}