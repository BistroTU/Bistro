package edu.temple.bistro

data class User(
    val first_name: String,
    val last_name: String,
    val username: String,
    val age_over_21: Boolean,
    val profile_picture: String,
    val filter_criteria: FilterCriteria,
    val liked_places: Map<String, Place>,
    val disliked_places: Map<String, Place>,
    val friends: Map<String, Friend>
)
