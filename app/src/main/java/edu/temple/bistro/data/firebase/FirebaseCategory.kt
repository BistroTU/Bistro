package edu.temple.bistro.data.firebase

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class FirebaseCategory(var alias: String? = "",
                            var title: String? = "")
{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "alias" to alias,
            "title" to title
        )
    }
}