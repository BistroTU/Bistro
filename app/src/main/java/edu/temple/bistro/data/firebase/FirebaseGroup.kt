package edu.temple.bistro.data.firebase

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class FirebaseGroup(
    var id: String? = null,
    var members: MutableList<String>? = null
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "members" to members
        )
    }
}