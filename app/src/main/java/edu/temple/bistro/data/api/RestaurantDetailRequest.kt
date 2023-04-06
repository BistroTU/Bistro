package edu.temple.bistro.data.api

import edu.temple.bistro.data.model.Restaurant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantDetailRequest(private var id: String = "") {

    private val optionsMap = mapOf(
        Pair("device_platform", "android"),
    )
    private val successCallbacks = mutableListOf<(Response<Restaurant>) -> Unit>()
    private val failureCallbacks = mutableListOf<(Throwable) -> Unit>()

    fun setID(value: String) = apply {
        id = value
    }

    fun addSuccessCallback(callback: (Response<Restaurant>) -> Unit) = apply {
        successCallbacks.add(callback)
    }

    fun addFailureCallback(callback: (Throwable) -> Unit) = apply {
        failureCallbacks.add(callback)
    }

    suspend fun call(yelpService: YelpService) {
        withContext(Dispatchers.IO) {
            yelpService.getRestaurantDetail(id, optionsMap)
                .enqueue(object : Callback<Restaurant> {
                    override fun onResponse(
                        call: Call<Restaurant>,
                        response: Response<Restaurant>
                    ) {
                        successCallbacks.forEach { it(response) }
                    }

                    override fun onFailure(call: Call<Restaurant>, t: Throwable) {
                        failureCallbacks.forEach { it(t) }
                    }

                })
        }
    }

    fun callBlocking(yelpService: YelpService, processCallbacks: Boolean = false): Restaurant? {
        return try {
            val response = yelpService.getRestaurantDetail(id, optionsMap).execute()
            if (processCallbacks) { successCallbacks.forEach { it(response) } }
            response.body()
        } catch (e: Exception) {
            if (processCallbacks) { failureCallbacks.forEach { it(e) } }
            null
        }

    }
}