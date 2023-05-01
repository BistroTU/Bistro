package edu.temple.bistro.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppState (
    @ColumnInfo(name = "search_params")
    var searchParams: String = "",
    @PrimaryKey
    val id: Int = 0
)