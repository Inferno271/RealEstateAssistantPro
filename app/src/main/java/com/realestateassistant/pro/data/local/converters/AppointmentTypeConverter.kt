package com.realestateassistant.pro.data.local.converters

import androidx.room.TypeConverter
import com.realestateassistant.pro.domain.model.AppointmentType

/**
 * Конвертер для преобразования типа встречи в строку и обратно
 */
class AppointmentTypeConverter {
    @TypeConverter
    fun fromAppointmentType(type: AppointmentType?): String {
        return type?.name ?: AppointmentType.SHOWING.name
    }
    
    @TypeConverter
    fun toAppointmentType(value: String?): AppointmentType {
        return try {
            if (value.isNullOrEmpty()) AppointmentType.SHOWING
            else AppointmentType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            AppointmentType.SHOWING
        }
    }
} 