package edu.temple.bistro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.temple.bistro.data.dao.RestaurantDao
import edu.temple.bistro.data.dao.CategoryDao
import edu.temple.bistro.data.model.Category
import edu.temple.bistro.data.model.Restaurant
import edu.temple.bistro.data.model.RestaurantCategoryReference

@Database(
    entities = [Restaurant::class, Category::class, RestaurantCategoryReference::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class YelpDatabase : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: YelpDatabase? = null

        fun getDatabase(context: Context): YelpDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    YelpDatabase::class.java,
                    "yelp"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}