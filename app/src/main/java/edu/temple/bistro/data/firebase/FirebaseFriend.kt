package edu.temple.bistro.data.firebase

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class FirebaseFriend (
    var username: String? = null,
    var friend_status: String? = null
)
{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "username" to username,
            "friend_status" to friend_status
        )
    }
}