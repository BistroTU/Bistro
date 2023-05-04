package edu.temple.bistro.data.firebase

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class FirebaseUser (var first_name: String? = null,
                         var last_name: String? = null,
                         var username: String? = null,
                         var age_over_21: Boolean? = null,
                         var profile_picture: String? = null,
                         var filter_criteria: FirebaseFilterCriteria? = null,
                         var liked_places: MutableMap<String, FirebasePlace>? = null,
                         var disliked_places: MutableMap<String, FirebasePlace>? = null,
                         var liked_categories: MutableMap<String, FirebaseCategory>? = null,
                         var friends: MutableMap<String, FirebaseFriend>? = null,
                         var groups: MutableMap<String, Any?>? = null
                         ) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "first_name" to first_name,
            "last_name" to last_name,
            "username" to username,
            "age_over_21" to age_over_21,
            "profile_picture" to profile_picture,
            "filter_criteria" to filter_criteria,
            "liked_places" to liked_places?.keys?.associateWith { liked_places?.get(it)?.toMap() },
            "disliked_places" to disliked_places?.keys?.associateWith { disliked_places?.get(it)?.toMap() },
            "liked_categories" to liked_categories?.keys?.associateWith { liked_categories?.get(it)?.toMap() },
            "friends" to friends?.keys?.associateWith { friends?.get(it)?.toMap() },
            "groups" to groups
        )
    }
}