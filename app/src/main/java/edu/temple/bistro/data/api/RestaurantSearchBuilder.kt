package edu.temple.bistro.data.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.temple.bistro.data.model.Category
import edu.temple.bistro.data.model.RestaurantSearchResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantSearchBuilder(builder: RestaurantSearchBuilder? = null) {

    private val optionsMapType = object : TypeToken<MutableMap<String, String>>() {}.type
    private val optionsMap = mutableMapOf(
        Pair("device_platform", "android"),
        Pair("limit", "20")
    )

    init {
        builder?.let { optionsMap.putAll(it.optionsMap) }
    }

    private val categorySet = mutableSetOf<Category>()
    private val priceSet = mutableSetOf<Int>()
    private val successCallbacks = mutableListOf<(Response<RestaurantSearchResponse>) -> Unit>()
    private val failureCallbacks = mutableListOf<(Throwable) -> Unit>()

    var options
        get() = Gson().toJson(optionsMap)
        set(value) = optionsMap.putAll(Gson().fromJson(value, optionsMapType))

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
        categorySet.add(value)
    }

    fun addCategories(value: Collection<Category>) = apply {
        categorySet.addAll(value)
    }

    fun addPrice(value: Int) = apply {
        if ((value >= 1) && (value <= 4)) {
            priceSet.add(value)
        }
    }

    fun addPriceStr(value: String) = apply {
        addPrice(value.length)
    }

    fun addPrices(value: Collection<Int>) = apply {
        priceSet.addAll(value.filter { (it >= 1) && (it <= 4) })
    }

    fun addPricesStr(value: Collection<String>) = apply {
        addPrices(value.map { it.length })
    }

    fun setOpenNow(value: Boolean) = apply {
        optionsMap["open_now"] = value.toString()
    }

    fun setLimit(value: Int) = apply {
        optionsMap["limit"] = value.toString()
    }

    fun getLimit() = optionsMap["limit"]?.toIntOrNull() ?: 0

    fun setOffset(value: Int) = apply {
        optionsMap["offset"] = value.toString()
    }

    fun getOffset() = optionsMap["offset"]?.toIntOrNull() ?: 0

    fun addSuccessCallback(callback: (Response<RestaurantSearchResponse>) -> Unit) = apply {
        successCallbacks.add(callback)
    }

    fun addFailureCallback(callback: (Throwable) -> Unit) = apply {
        failureCallbacks.add(callback)
    }

    suspend fun call(yelpService: YelpService) {
        if (categorySet.size > 0) optionsMap["categories"] = categorySet.joinToString { it.alias }
        if (priceSet.size > 0) optionsMap["price"] = priceSet.joinToString()
        withContext(Dispatchers.IO) {
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
}