package edu.temple.bistro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.temple.bistro.data.dao.AppStateDao
import edu.temple.bistro.data.dao.RestaurantDao
import edu.temple.bistro.data.dao.CategoryDao
import edu.temple.bistro.data.model.AppState
import edu.temple.bistro.data.model.Category
import edu.temple.bistro.data.model.Restaurant
import edu.temple.bistro.data.model.RestaurantCategoryReference

@Database(
    entities = [Restaurant::class, Category::class, RestaurantCategoryReference::class, AppState::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BistroDatabase : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao
    abstract fun categoryDao(): CategoryDao

    abstract fun stateDao(): AppStateDao

    companion object {
        @Volatile
        private var INSTANCE: BistroDatabase? = null

        fun getDatabase(context: Context): BistroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BistroDatabase::class.java,
                    "bistro"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}