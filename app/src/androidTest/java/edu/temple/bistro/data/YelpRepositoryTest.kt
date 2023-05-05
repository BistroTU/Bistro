package edu.temple.bistro.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import edu.temple.bistro.data.api.RestaurantSearchBuilder
import edu.temple.bistro.data.model.Category
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*

import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.jvm.Throws

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class YelpRepositoryTest {
    private lateinit var repository: YelpRepository
    private lateinit var db: BistroDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, BistroDatabase::class.java).build()
        repository = YelpRepository(db)
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun fetchRestaurants() {
        repository.fetchRestaurants(
            RestaurantSearchBuilder()
            .setLatitude(39.977730)
            .setLongitude(-75.156400)
            .setLimit(20)
        )
        Thread.sleep(3000)
        runTest {
            val restaurants = repository.restaurants.first()
            assertEquals(20, restaurants.size)
        }
    }

    @Test
    @Throws(Exception::class)
    fun markRestaurantSeen() = runBlocking {
        repository.fetchRestaurants(
            RestaurantSearchBuilder()
                .setLatitude(39.977730)
                .setLongitude(-75.156400)
                .setLimit(20)
        )
        delay(3000)
        var restaurant = repository.restaurants.first().first()
        repository.markRestaurantSeen(restaurant)
        delay(1000)
        restaurant = db.restaurantDao().getRestaurant(restaurant.id)!!
        assertEquals(true, restaurant.userSeen)
    }

    @Test
    @Throws(Exception::class)
    fun getRestaurant() = runBlocking {
        val restaurant = repository.getRestaurant("hdiuRS9sVZSMReZm4oV5SA")
        assertNotNull(restaurant)
        assertNotNull(restaurant!!.photos) // If photos array exists, then we got detailed data from API
    }

    @Test
    @Throws(Exception::class)
    fun listRefresh() = runBlocking {
        repository.fetchRestaurants(
            RestaurantSearchBuilder()
                .setLatitude(39.977730)
                .setLongitude(-75.156400)
                .setLimit(20)
        )
        delay(3000)
        val restaurants = repository.newRestaurants.first().subList(0, 16)
        repository.markRestaurantSeen(*restaurants.toTypedArray()) // Simulate swiping on 16 restaurants to trigger refresh
        delay(3000)
        assert(repository.newRestaurants.first().size > 20)
    }
}