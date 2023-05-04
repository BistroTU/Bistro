package edu.temple.bistro.data.firebase

data class FirebaseFilterCriteria (
    var price_level: Int? = null,
    var max_distance: Int? = null,
    var categories: Map<String, Boolean>? = null
        )