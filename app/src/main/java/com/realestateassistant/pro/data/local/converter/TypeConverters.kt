package com.realestateassistant.pro.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.realestateassistant.pro.data.local.entity.SeasonalPriceEntity
import com.realestateassistant.pro.domain.model.AppointmentStatus
import com.realestateassistant.pro.domain.model.AppointmentType

/**
 * Конвертер для списков строк
 */
class ListStringConverter {
    private val gson = Gson()
    
    @TypeConverter
    fun fromList(value: List<String>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
}

/**
 * Конвертер для списка сезонных цен
 */
class SeasonalPriceListConverter {
    private val gson = Gson()
    
    @TypeConverter
    fun fromList(value: List<SeasonalPriceEntity>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toList(value: String): List<SeasonalPriceEntity> {
        val listType = object : TypeToken<List<SeasonalPriceEntity>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
}

/**
 * Конвертер для статуса встречи (AppointmentStatus)
 */
class AppointmentStatusConverter {
    @TypeConverter
    fun fromStatus(status: AppointmentStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toStatus(value: String): AppointmentStatus {
        return try {
            AppointmentStatus.valueOf(value)
        } catch (e: Exception) {
            AppointmentStatus.SCHEDULED
        }
    }
}

/**
 * Конвертер для типа встречи (AppointmentType)
 */
class AppointmentTypeConverter {
    @TypeConverter
    fun fromType(type: AppointmentType): String {
        return type.name
    }
    
    @TypeConverter
    fun toType(value: String): AppointmentType {
        return try {
            AppointmentType.valueOf(value)
        } catch (e: Exception) {
            AppointmentType.OTHER
        }
    }
} 