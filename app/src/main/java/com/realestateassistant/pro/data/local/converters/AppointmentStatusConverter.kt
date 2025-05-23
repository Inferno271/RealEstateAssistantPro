package com.realestateassistant.pro.data.local.converters

import androidx.room.TypeConverter
import com.realestateassistant.pro.domain.model.AppointmentStatus

/**
 * Конвертер для преобразования статуса встречи в строку и обратно
 */
class AppointmentStatusConverter {
    @TypeConverter
    fun fromAppointmentStatus(status: AppointmentStatus?): String {
        return status?.name ?: AppointmentStatus.SCHEDULED.name
    }
    
    @TypeConverter
    fun toAppointmentStatus(value: String?): AppointmentStatus {
        return try {
            if (value.isNullOrEmpty()) AppointmentStatus.SCHEDULED
            else AppointmentStatus.valueOf(value)
        } catch (e: IllegalArgumentException) {
            AppointmentStatus.SCHEDULED
        }
    }
} 