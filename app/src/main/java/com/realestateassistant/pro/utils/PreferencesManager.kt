package com.realestateassistant.pro.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Класс для управления SharedPreferences
 * Предоставляет методы для сохранения и получения различных типов данных
 */
class PreferencesManager(context: Context) {
    
    private val preferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME, Context.MODE_PRIVATE
    )
    private val gson = Gson()
    
    /**
     * Сохраняет список строк в SharedPreferences
     * 
     * @param key Ключ для сохранения
     * @param list Список строк для сохранения
     */
    fun saveStringList(key: String, list: List<String>) {
        val json = gson.toJson(list)
        preferences.edit().putString(key, json).apply()
    }
    
    /**
     * Получает список строк из SharedPreferences
     * 
     * @param key Ключ для получения данных
     * @param defaultValue Значение по умолчанию, если данные не найдены
     * @return Список строк или значение по умолчанию
     */
    fun getStringList(key: String, defaultValue: List<String> = emptyList()): List<String> {
        val json = preferences.getString(key, null)
        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            try {
                gson.fromJson(json, type)
            } catch (e: Exception) {
                defaultValue
            }
        } else {
            defaultValue
        }
    }
    
    /**
     * Сохраняет строковое значение в SharedPreferences
     * 
     * @param key Ключ для сохранения
     * @param value Строковое значение
     */
    fun saveString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }
    
    /**
     * Получает строковое значение из SharedPreferences
     * 
     * @param key Ключ для получения данных
     * @param defaultValue Значение по умолчанию, если данные не найдены
     * @return Строковое значение или значение по умолчанию
     */
    fun getString(key: String, defaultValue: String = ""): String {
        return preferences.getString(key, defaultValue) ?: defaultValue
    }
    
    /**
     * Сохраняет целочисленное значение в SharedPreferences
     * 
     * @param key Ключ для сохранения
     * @param value Целочисленное значение
     */
    fun saveInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }
    
    /**
     * Получает целочисленное значение из SharedPreferences
     * 
     * @param key Ключ для получения данных
     * @param defaultValue Значение по умолчанию, если данные не найдены
     * @return Целочисленное значение или значение по умолчанию
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return preferences.getInt(key, defaultValue)
    }
    
    /**
     * Сохраняет булево значение в SharedPreferences
     * 
     * @param key Ключ для сохранения
     * @param value Булево значение
     */
    fun saveBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }
    
    /**
     * Получает булево значение из SharedPreferences
     * 
     * @param key Ключ для получения данных
     * @param defaultValue Значение по умолчанию, если данные не найдены
     * @return Булево значение или значение по умолчанию
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }
    
    /**
     * Удаляет данные по ключу из SharedPreferences
     * 
     * @param key Ключ для удаления данных
     */
    fun remove(key: String) {
        preferences.edit().remove(key).apply()
    }
    
    /**
     * Очищает все данные в SharedPreferences
     */
    fun clear() {
        preferences.edit().clear().apply()
    }
    
    /**
     * Проверяет наличие ключа в SharedPreferences
     * 
     * @param key Ключ для проверки
     * @return true, если ключ существует, иначе false
     */
    fun contains(key: String): Boolean {
        return preferences.contains(key)
    }
    
    companion object {
        private const val PREFERENCES_NAME = "real_estate_assistant_preferences"
    }
} 