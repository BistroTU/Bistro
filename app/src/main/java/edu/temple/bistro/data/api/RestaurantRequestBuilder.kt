package edu.temple.bistro.data.api

import edu.temple.bistro.data.model.Category
import edu.temple.bistro.data.model.RestaurantSearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantRequestBuilder {

    private val optionsMap = mutableMapOf(
        Pair("device_platform", "android"),
        Pair("limit", "20")
    )
    private val categoryList = mutableListOf<Category>()
    private val priceSet = mutableSetOf<Int>()
    private val successCallbacks = mutableListOf<(Response<RestaurantSearchResponse>) -> Unit>()
    private val failureCallbacks = mutableListOf<(Throwable) -> Unit>()

    fun setLatitude(value: Double) = apply {
        optionsMap["latitude"] = value.toString()
    }

    fun setLongitude(value: Double) = apply {
        optionsMap["longitude"] = value.toString()
    }

    fun setRadius(value: Int) = apply {
        optionsMap["radius"] = value.toString()
    }

    fun addCategory(value: Category) = apply {
        categoryList.add(value)
    }

    fun addCategories(value: Collection<Category>) = apply {
        categoryList.addAll(value)
    }

    fun addPrice(value: Int) = apply {
        if ((value >= 1) && (value <= 4)) {
            priceSet.add(value)
        }
    }

    fun addPrices(value: Collection<Int>) = apply {
        priceSet.addAll(value.filter { (it >= 1) && (it <= 4) })
    }

    fun setOpenNow(value: Boolean) = apply {
        optionsMap["open_now"] = value.toString()
    }

    fun setLimit(value: Int) = apply {
        optionsMap["limit"] = value.toString()
    }

    fun addSuccessCallback(callback: (Response<RestaurantSearchResponse>) -> Unit) = apply {
        successCallbacks.add(callback)
    }

    fun addFailureCallback(callback: (Throwable) -> Unit) = apply {
        failureCallbacks.add(callback)
    }

    fun call(yelpService: YelpService) = apply {
        if (categoryList.size > 0) optionsMap["categories"] = categoryList.joinToString { it.alias }
        if (priceSet.size > 0) optionsMap["price"] = priceSet.joinToString()
        yelpService.searchRestaurants(optionsMap)
            .enqueue(object : Callback<RestaurantSearchResponse> {
                override fun onResponse(
                    call: Call<RestaurantSearchResponse>,
                    response: Response<RestaurantSearchResponse>
                ) {
                    successCallbacks.forEach { it(response) }
                }

                override fun onFailure(call: Call<RestaurantSearchResponse>, t: Throwable) {
                    failureCallbacks.forEach { it(t) }
                }

            })
    }
}