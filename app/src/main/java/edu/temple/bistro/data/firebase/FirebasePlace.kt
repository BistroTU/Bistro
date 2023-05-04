package edu.temple.bistro.data.firebase

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class FirebasePlace (
    var id: String? = null,
    var name: String? = null,
    var timestamp: Long? = null
        )
{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "timestamp" to timestamp
        )
    }
}