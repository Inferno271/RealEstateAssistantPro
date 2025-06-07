package com.realestateassistant.pro.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.realestateassistant.pro.domain.model.Participant

/**
 * Конвертер для преобразования списка участников встречи в JSON строку и обратно
 */
class ParticipantListConverter {
    private val gson = Gson()
    
    @TypeConverter
    fun fromParticipantList(value: List<Participant>?): String {
        return if (value == null) {
            ""
        } else {
            gson.toJson(value)
        }
    }
    
    @TypeConverter
    fun toParticipantList(value: String?): List<Participant> {
        if (value.isNullOrEmpty()) {
            return emptyList()
        }
        
        val listType = object : TypeToken<List<Participant>>() {}.type
        return gson.fromJson(value, listType)
    }
} 