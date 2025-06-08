package com.realestateassistant.pro.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.realestateassistant.pro.domain.model.AdditionalService
import com.realestateassistant.pro.domain.model.BookingStatus
import com.realestateassistant.pro.domain.model.PaymentStatus

/**
 * Конвертеры для типов данных, связанных с бронированием
 */
class BookingConverters {
    private val gson = Gson()
    
    /**
     * Конвертеры для списка дополнительных услуг
     */
    @TypeConverter
    fun fromAdditionalServicesList(value: List<AdditionalService>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toAdditionalServicesList(value: String): List<AdditionalService> {
        val listType = object : TypeToken<List<AdditionalService>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
    
    /**
     * Конвертеры для статуса бронирования
     */
    @TypeConverter
    fun fromBookingStatus(status: BookingStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toBookingStatus(value: String): BookingStatus {
        return try {
            BookingStatus.valueOf(value)
        } catch (e: Exception) {
            BookingStatus.PENDING
        }
    }
    
    /**
     * Конвертеры для статуса оплаты
     */
    @TypeConverter
    fun fromPaymentStatus(status: PaymentStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toPaymentStatus(value: String): PaymentStatus {
        return try {
            PaymentStatus.valueOf(value)
        } catch (e: Exception) {
            PaymentStatus.UNPAID
        }
    }
} 