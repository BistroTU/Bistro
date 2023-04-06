package edu.temple.bistro.data

import android.util.Log
import edu.temple.bistro.BuildConfig
import edu.temple.bistro.data.api.RestaurantDetailRequest
import edu.temple.bistro.data.api.RestaurantSearchBuilder
import edu.temple.bistro.data.api.YelpService
import edu.temple.bistro.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class YelpRepository(private val database: YelpDatabase) {

    private val httpClient by lazy {
        OkHttpClient.Builder().addInterceptor {
            val req = it.request().newBuilder()
                .addHeader("Authorization", "Bearer ${BuildConfig.YELP_TOKEN}")
                .build()
            it.proceed(req)
        }.build()
    }
    private val retrofit by lazy {
        Retrofit.Builder()
            .client(httpClient)
            .baseUrl("https://api.yelp.com/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private val yelpService by lazy {
        retrofit.create(YelpService::class.java)
    }
    private val defaultScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }

    private var lastSearch: RestaurantSearchBuilder? = null

    val restaurants
        get() = database.restaurantDao().getRestaurants()

    val newRestaurants
        get() = database.restaurantDao().getNewRestaurants()

    suspend fun getRestaurant(id: String): Restaurant? {
        return withContext(Dispatchers.IO) {
            RestaurantDetailRequest(id).callBlocking(yelpService)?.let { rest ->
                rest.insertTime = System.currentTimeMillis()
                database.restaurantDao().insertRestaurant(rest)
                rest.categories.forEach { cat ->
                    database.categoryDao().insertCategory(cat)
                    database.restaurantDao().insertRestaurantCategories(
                        RestaurantCategoryReference(rest.id, cat.alias)
                    )
                }
                return@withContext rest
            }
            return@withContext null
        }
    }

    fun markRestaurantSeen(vararg restaurant: Restaurant) {
        defaultScope.launch {
            restaurant.forEach { it.userSeen = true }
            database.restaurantDao().updateRestaurant(*restaurant)
            if (database.restaurantDao().getUnseenRestaurantCount() <= 5) {
                lastSearch?.let { search ->
                    RestaurantSearchBuilder(search).apply {
                        setOffset(getOffset() + getLimit())
                        addSuccessCallback(this@YelpRepository::searchSuccessCallback)
                        addFailureCallback(this@YelpRepository::searchFailureCallback)
                    }.call(yelpService)
                }
            }
        }
    }

    fun fetchRestaurants(builder: RestaurantSearchBuilder) {
        defaultScope.launch {
            builder
                .addSuccessCallback(this@YelpRepository::searchSuccessCallback)
                .addFailureCallback(this@YelpRepository::searchFailureCallback)
                .call(yelpService)
        }
    }
    fun fetchRestaurants() {
        // Example request from around campus
        fetchRestaurants(RestaurantSearchBuilder()
            .setLatitude(39.977730)
            .setLongitude(-75.156400)
            .addCategory(Category("bars", "Bars"))
            .setLimit(20))
    }

    private fun searchSuccessCallback(response: Response<RestaurantSearchResponse>) {
        if (response.isSuccessful) {
            Log.d(this::class.simpleName, response.body().toString())
            Log.d(this::class.simpleName, response.body()!!.total.toString())
            defaultScope.launch {
                response.body()!!.restaurants.forEach { rest ->
                    rest.insertTime = System.currentTimeMillis()
                    database.restaurantDao().insertRestaurant(rest)
                    rest.categories.forEach { cat ->
                        database.categoryDao().insertCategory(cat)
                        database.restaurantDao().insertRestaurantCategories(
                            RestaurantCategoryReference(rest.id, cat.alias)
                        )
                    }
                }
            }
        } else {
            Log.d(this::class.simpleName, "API Unsuccessful ${response.errorBody()?.string()}")
        }
    }

    private fun searchFailureCallback(err: Throwable) {
        Log.d(this::class.simpleName, "API Fail: ${err.message}")
    }
}