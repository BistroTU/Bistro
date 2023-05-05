package edu.temple.bistro.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import edu.temple.bistro.data.model.Category
import edu.temple.bistro.data.model.Restaurant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.jvm.Throws

@RunWith(AndroidJUnit4::class)
class BistroDatabaseTest {
    private lateinit var db: BistroDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, BistroDatabase::class.java).build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun restaurantTest() = runBlocking {
        val original = Restaurant(
            "testid",
            "testalias",
            "Test",
            "http://testurl.com/image.png",
            false,
            "http://testurl.com",
            10,
            listOf(Category("testcatalias", "testcattitle")),
            5f,
            null,
            listOf("Takeout"),
            "$$$$",
            null,
            "(215) 867-5309",
            "(215) 867-5309",
            1000f,
            null,
            null
        )
        db.restaurantDao().insertRestaurant(original)
        var retrieved = db.restaurantDao().getRestaurant(original.id)
        assertEquals(original, retrieved)
        assertEquals(1, db.restaurantDao().getUnseenRestaurantCount())
        assert(db.restaurantDao().getRestaurants().first().isNotEmpty())
        assert(db.restaurantDao().getNewRestaurants().first().isNotEmpty())
        retrieved!!.userSeen = true
        db.restaurantDao().updateRestaurant(retrieved)
        assert(db.restaurantDao().getRestaurants().first().isNotEmpty())
        assert(db.restaurantDao().getNewRestaurants().first().isEmpty())
        retrieved = db.restaurantDao().getRestaurant(original.id)
        assertNotEquals(original, retrieved)
        assertTrue(retrieved!!.userSeen)
    }

    @Test
    @Throws(Exception::class)
    fun categoryTest() = runBlocking {
        val original = Category("TestAlias", "TestTitle")
        db.categoryDao().insertCategory(original)
        var retrieved = db.categoryDao().getCategory(original.alias).first()
        assertEquals(original, retrieved)
        assertTrue(db.categoryDao().getCategories().first().contains(original))
        val new = Category("TestAlias", "NewTitle")
        db.categoryDao().updateCategory(new)
        retrieved = db.categoryDao().getCategory(original.alias).first()
        assertNotEquals(original, retrieved)
        assertEquals(new, retrieved)
    }
}