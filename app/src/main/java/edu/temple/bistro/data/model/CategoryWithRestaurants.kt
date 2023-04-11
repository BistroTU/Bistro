package edu.temple.bistro.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class CategoryWithRestaurants (
    @Embedded val category: Category,
    @Relation(
        parentColumn = "alias",
        entityColumn = "id",
        associateBy = Junction(RestaurantCategoryReference::class)
    )
    val businesses: List<Restaurant>
)