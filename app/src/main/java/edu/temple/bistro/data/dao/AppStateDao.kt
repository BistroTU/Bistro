package edu.temple.bistro.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import edu.temple.bistro.data.model.AppState
import kotlinx.coroutines.flow.Flow

@Dao
interface AppStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertState(state: AppState)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateState(state: AppState)

    @Query("SELECT * FROM AppState LIMIT 1")
    fun getState(): Flow<AppState>
}