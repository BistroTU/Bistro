package edu.temple.bistro.data

import android.util.Log
import edu.temple.bistro.BuildConfig
import edu.temple.bistro.data.api.RestaurantDetailRequest
import edu.temple.bistro.data.api.RestaurantSearchBuilder
import edu.temple.bistro.data.api.YelpService
import edu.temple.bistro.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class YelpRepository(private val database: BistroDatabase) {

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
    private val appState by lazy {
        database.stateDao().getState()
    }
    private var refreshInProgress = false

    private var lastSearch: RestaurantSearchBuilder? = null

    val restaurants
        get() = database.restaurantDao().getRestaurants()

    val newRestaurants
        get() = database.restaurantDao().getNewRestaurants()

    val state
        get() = appState

    fun getNewRestaurants(limit: Int = 5): Flow<List<Restaurant>> {
        return database.restaurantDao().getNewRestaurants(limit)
    }

    suspend fun getRestaurant(id: String): Restaurant? {
        return withContext(Dispatchers.IO) {
            val dbRestaurant = database.restaurantDao().getRestaurant(id)
            if (dbRestaurant?.photos != null && (System.currentTimeMillis() - dbRestaurant.insertTime) < 1800000) return@withContext dbRestaurant
            RestaurantDetailRequest(id).callBlocking(yelpService)?.let { rest ->
                rest.insertTime = System.currentTimeMillis()
                if (dbRestaurant == null) database.restaurantDao().insertRestaurant(rest)
                else database.restaurantDao().updateRestaurant(rest.apply { userSeen = dbRestaurant.userSeen })
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
            if (database.restaurantDao().getUnseenRestaurantCount() <= 5 && !refreshInProgress) {
                if (lastSearch == null) {
                    appState.firstOrNull()?.searchParams?.let {
                        lastSearch = RestaurantSearchBuilder().apply { options = it }
                    }
                }
                lastSearch?.let { search ->
                    refreshInProgress = true
                    RestaurantSearchBuilder(search).apply {
                        setOffset(getOffset() + getLimit())
                        addSuccessCallback(this@YelpRepository::refreshSuccessCallback)
                        addFailureCallback(this@YelpRepository::refreshFailureCallback)
                        lastSearch = this
                        updateState(this.options)
                    }.call(yelpService)
                }
            }
        }
    }

    fun fetchRestaurants(builder: RestaurantSearchBuilder) {
        defaultScope.launch {
//            database.restaurantDao().getNewRestaurants().collectLatest {
//                database.restaurantDao().deleteRestaurant(*it.toTypedArray())
//            }
            builder
                .addSuccessCallback(this@YelpRepository::searchSuccessCallback)
                .addFailureCallback(this@YelpRepository::searchFailureCallback)
                .call(yelpService)
            lastSearch = builder
            updateState(builder.options)
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

    fun saveState(state: AppState) {
        defaultScope.launch {
            database.stateDao().updateState(state)
        }
    }

    private fun searchSuccessCallback(response: Response<RestaurantSearchResponse>) {
        if (response.isSuccessful) {
            Log.d(this::class.simpleName, response.body().toString())
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

    private fun refreshSuccessCallback(response: Response<RestaurantSearchResponse>) {
        refreshInProgress = false
        searchSuccessCallback(response)
    }

    private fun searchFailureCallback(err: Throwable) {
        Log.d(this::class.simpleName, "API Fail: ${err.message}")
    }

    private fun refreshFailureCallback(err: Throwable) {
        refreshInProgress = false
        searchFailureCallback(err)
    }

    private suspend fun updateState(params: String) {
        val state = appState.firstOrNull()
        if (state == null) {
            database.stateDao().insertState(AppState(params))
        }
        else {
            database.stateDao().updateState(state.apply { searchParams = params })
        }
    }
}