package edu.temple.bistro

data class FilterCriteria(
    val price_level: Int,
    val max_distance: Int,
    val categories: Map<String, Boolean>
)
