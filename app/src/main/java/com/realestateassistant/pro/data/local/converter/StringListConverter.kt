package com.realestateassistant.pro.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

/**
 * Конвертер для списков строк и целых чисел для Room
 */
class StringListConverter {
    private val gson = Gson()
    
    /**
     * Конвертирует JSON-строку в список строк
     */
    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        if (value == null || value.isEmpty() || value == "null") {
            return emptyList()
        }
        
        return try {
            // Проверяем, начинается ли строка с [
            if (!value.trim().startsWith("[")) {
                // Если это не массив, возвращаем список с одним элементом
                listOf(value)
            } else {
                val listType = object : TypeToken<List<String>>() {}.type
                gson.fromJson(value, listType)
            }
        } catch (e: JsonSyntaxException) {
            println("DEBUG: StringListConverter.fromStringList - ошибка парсинга: $value")
            e.printStackTrace()
            emptyList()
        }
    }
    
    /**
     * Конвертирует список строк в JSON-строку
     */
    @TypeConverter
    fun toStringList(list: List<String>?): String {
        if (list == null || list.isEmpty()) {
            return "[]"
        }
        
        return gson.toJson(list)
    }
    
    /**
     * Конвертирует JSON-строку в список целых чисел
     */
    @TypeConverter
    fun fromIntList(value: String?): List<Int> {
        if (value == null || value.isEmpty() || value == "null") {
            return emptyList()
        }
        
        return try {
            // Проверяем, начинается ли строка с [
            if (!value.trim().startsWith("[")) {
                // Если это не массив, пробуем преобразовать в одно число
                val singleValue = value.toIntOrNull()
                if (singleValue != null) {
                    listOf(singleValue)
                } else {
                    emptyList()
                }
            } else {
                val listType = object : TypeToken<List<Int>>() {}.type
                gson.fromJson(value, listType)
            }
        } catch (e: JsonSyntaxException) {
            println("DEBUG: StringListConverter.fromIntList - ошибка парсинга: $value")
            e.printStackTrace()
            emptyList()
        }
    }
    
    /**
     * Конвертирует список целых чисел в JSON-строку
     */
    @TypeConverter
    fun toIntList(list: List<Int>?): String {
        if (list == null || list.isEmpty()) {
            return "[]"
        }
        
        return gson.toJson(list)
    }
} 