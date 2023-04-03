package edu.temple.bistro.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import edu.temple.bistro.data.model.Restaurant
import edu.temple.bistro.data.model.RestaurantCategoryReference
import edu.temple.bistro.data.model.RestaurantWithCategories
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurant(vararg restaurant: Restaurant)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRestaurant(vararg restaurant: Restaurant)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRestaurantCategories(vararg ref: RestaurantCategoryReference)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateRestaurantCategories(vararg ref: RestaurantCategoryReference)

    @Delete
    suspend fun deleteRestaurant(business: Restaurant)

    @Query("SELECT * FROM Restaurant")
    @Transaction
    fun getRestaurants(): Flow<List<RestaurantWithCategories>>

    @Query("SELECT * FROM Restaurant WHERE id == :id")
    @Transaction
    fun getRestaurant(id: String): Flow<RestaurantWithCategories>
}