package edu.temple.bistro.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class RestaurantWithCategories (
    @Embedded val business: Restaurant,
    @Relation(
        parentColumn = "id",
        entityColumn = "alias",
        associateBy = Junction(RestaurantCategoryReference::class)
    )
    val categories: List<Category>
)