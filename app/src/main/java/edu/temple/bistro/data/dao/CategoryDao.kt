package edu.temple.bistro.data.dao

import androidx.room.*
import edu.temple.bistro.data.model.Category
import edu.temple.bistro.data.model.CategoryWithRestaurants
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(vararg category: Category)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateCategory(vararg category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM Category")
    fun getCategories(): Flow<List<Category>>

    @Query("SELECT * FROM Category")
    @Transaction
    fun getCategoriesWithRestaurants(): Flow<List<CategoryWithRestaurants>>

    @Query("SELECT * FROM Category WHERE alias == :alias")
    fun getCategory(alias: String): Flow<Category>

    @Query("SELECT * FROM Category WHERE alias == :alias")
    @Transaction
    fun getCategoryWithRestaurants(alias: String): Flow<CategoryWithRestaurants>
}