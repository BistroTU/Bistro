package edu.temple.bistro.data.model

import androidx.room.Entity
import androidx.room.Index

@Entity(
    primaryKeys = ["id", "alias"],
    indices = [Index(value = ["alias"])]
)
data class RestaurantCategoryReference (
    val id: String, // Restaurant ID
    val alias: String // Category alias
)