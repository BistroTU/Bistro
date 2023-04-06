package edu.temple.bistro.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Restaurant (
    @PrimaryKey
    val id: String,
    val alias: String,
    val name: String,
    @ColumnInfo(name = "image_url")
    @SerializedName("image_url")
    val imageUrl: String,
    @ColumnInfo(name = "is_closed")
    @SerializedName("is_closed")
    val isClosed: Boolean,
    val url: String,
    @ColumnInfo(name = "review_count")
    @SerializedName("review_count")
    val reviewCount: Int,
    val categories: List<Category>,
    val rating: Float,
    @Embedded
    val coordinates: Coordinates?,
    val transactions: List<String>,
    val price: String,
    @Embedded
    val location: Location?,
    val phone: String,
    @ColumnInfo(name = "display_phone")
    @SerializedName("display_phone")
    val displayPhone: String,
    var distance: Float,
    val photos: List<String>?,
    val hours: List<Hours>?,
    // This isn't part of the Yelp API
    var userSeen: Boolean = false,
    var insertTime: Long = System.currentTimeMillis()
)

data class Coordinates (
    val latitude: Float,
    val longitude: Float
)

data class Location (
    val address1: String,
    @ColumnInfo(defaultValue = "")
    val address2: String?,
    @ColumnInfo(defaultValue = "")
    val address3: String?,
    val city: String,
    @ColumnInfo(name = "zip_code")
    @SerializedName("zip_code")
    val zipCode: String,
    val country: String,
    val state: String,
    @ColumnInfo(name = "display_address")
    @SerializedName("display_address")
    val displayAddress: List<String>
)

@Entity
data class Category (
    @PrimaryKey
    val alias: String,
    val title: String
)

data class Hours (
    @SerializedName("hour_type")
    val hourType: String,
    val open: List<OpenHours>,
    @SerializedName("is_open_now")
    val isOpenNow: Boolean,
)

data class OpenHours (
    val day: Int,
    val start: String,
    val end: String,
    @SerializedName("is_overnight")
    val isOvernight: Boolean
)