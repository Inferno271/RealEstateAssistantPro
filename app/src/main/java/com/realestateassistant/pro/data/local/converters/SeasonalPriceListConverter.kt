package com.realestateassistant.pro.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.realestateassistant.pro.data.local.entity.SeasonalPriceEntity

/**
 * Конвертер для преобразования списка сезонных цен в JSON строку и обратно
 */
class SeasonalPriceListConverter {
    private val gson = Gson()
    
    @TypeConverter
    fun fromSeasonalPriceList(value: List<SeasonalPriceEntity>?): String {
        return if (value == null) {
            ""
        } else {
            gson.toJson(value)
        }
    }
    
    @TypeConverter
    fun toSeasonalPriceList(value: String?): List<SeasonalPriceEntity> {
        if (value.isNullOrEmpty()) {
            return emptyList()
        }
        
        val listType = object : TypeToken<List<SeasonalPriceEntity>>() {}.type
        return gson.fromJson(value, listType)
    }
} 