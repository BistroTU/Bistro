package edu.temple.bistro.data

import androidx.room.TypeConverter
import edu.temple.bistro.data.model.Category
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun stringListToJSON(list: List<String>?): String? {
        return list?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun stringListFromJSON(value: String?): List<String>? {
        return value?.let {
            Gson().fromJson(it, Array<String>::class.java).toList()
//            val json = JSONArray(it)
//            val list = mutableListOf<String>()
//            for (i in 0 until json.length()) {
//                list.add(json.getString(i))
//            }
//            list
        }
    }

    @TypeConverter
    fun categoryListToJSON(list: List<Category>?): String? {
        return list?.let {
            Gson().toJson(it)
//            val jsonArray = JSONArray()
//            list.forEach {
//                jsonArray.put(Gson().toJson(it))
//            }
//            jsonArray.toString()
        }
    }

    @TypeConverter
    fun categoryListFromJSON(value: String?): List<Category>? {
        return value?.let {
            Gson().fromJson(it, Array<Category>::class.java).toList()
        }
    }
}