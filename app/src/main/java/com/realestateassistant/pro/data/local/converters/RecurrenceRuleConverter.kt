package com.realestateassistant.pro.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.realestateassistant.pro.domain.model.RecurrenceRule

/**
 * Конвертер для преобразования правила повторения встречи в JSON строку и обратно
 */
class RecurrenceRuleConverter {
    private val gson = Gson()
    
    @TypeConverter
    fun fromRecurrenceRule(rule: RecurrenceRule?): String {
        return if (rule == null) {
            ""
        } else {
            gson.toJson(rule)
        }
    }
    
    @TypeConverter
    fun toRecurrenceRule(value: String?): RecurrenceRule? {
        if (value.isNullOrEmpty()) {
            return null
        }
        
        return try {
            gson.fromJson(value, RecurrenceRule::class.java)
        } catch (e: Exception) {
            null
        }
    }
} 