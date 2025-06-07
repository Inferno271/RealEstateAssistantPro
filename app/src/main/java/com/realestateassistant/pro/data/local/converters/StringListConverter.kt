package com.realestateassistant.pro.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Конвертер для преобразования списка строк в JSON строку и обратно
 */
class StringListConverter {
    private val gson = Gson()
    
    /**
     * Преобразует список строк в JSON строку
     */
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return if (value == null || value.isEmpty()) {
            ""
        } else {
            gson.toJson(value)
        }
    }
    
    /**
     * Преобразует JSON строку в список строк
     */
    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) {
            return emptyList()
        }
        
        val listType = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson(value, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }
} 