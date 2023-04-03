package edu.temple.bistro.data

import androidx.room.TypeConverter
import edu.temple.bistro.data.model.Category
import com.google.gson.Gson
import edu.temple.bistro.data.model.Hours

class Converters {
    @TypeConverter
    fun stringListToJSON(list: List<String>?): String? {
        return list?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun stringListFromJSON(value: String?): List<String>? {
        return value?.let {
            Gson().fromJson(it, Array<String>::class.java).toList()
        }
    }

    @TypeConverter
    fun categoryListToJSON(list: List<Category>?): String? {
        return list?.let {
            Gson().toJson(it)
        }
    }

    @TypeConverter
    fun categoryListFromJSON(value: String?): List<Category>? {
        return value?.let {
            Gson().fromJson(it, Array<Category>::class.java).toList()
        }
    }

    @TypeConverter
    fun hoursListToJSON(list: List<Hours>?): String? {
        return list?.let {
            Gson().toJson(it)
        }
    }

    @TypeConverter
    fun hoursListFromJSON(value: String?): List<Hours>? {
        return value?.let {
            Gson().fromJson(it, Array<Hours>::class.java).toList()
        }
    }
}