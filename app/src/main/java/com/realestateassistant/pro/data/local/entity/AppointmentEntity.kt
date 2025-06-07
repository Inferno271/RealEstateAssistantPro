package com.realestateassistant.pro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.realestateassistant.pro.domain.model.AppointmentStatus
import com.realestateassistant.pro.domain.model.AppointmentType
import com.realestateassistant.pro.domain.model.Participant
import com.realestateassistant.pro.domain.model.RecurrenceRule

/**
 * Конвертеры типов для Room
 */
class AppointmentConverters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromRecurrenceRule(recurrenceRule: RecurrenceRule?): String? {
        return if (recurrenceRule == null) null else gson.toJson(recurrenceRule)
    }
    
    @TypeConverter
    fun toRecurrenceRule(json: String?): RecurrenceRule? {
        return if (json == null) null else gson.fromJson(json, RecurrenceRule::class.java)
    }
    
    @TypeConverter
    fun fromParticipantList(participants: List<Participant>?): String? {
        return if (participants == null) null else gson.toJson(participants)
    }
    
    @TypeConverter
    fun toParticipantList(json: String?): List<Participant>? {
        if (json == null) return null
        val type = object : TypeToken<List<Participant>>() {}.type
        return gson.fromJson(json, type)
    }
}

/**
 * Entity класс для хранения встреч в базе данных
 */
@Entity(tableName = "appointments")
@TypeConverters(AppointmentConverters::class)
data class AppointmentEntity(
    @PrimaryKey
    val id: String,
    val propertyId: String,
    val clientId: String,
    val agentId: String,
    val title: String,
    val description: String?,
    val startTime: Long,
    val endTime: Long,
    val status: AppointmentStatus,
    val type: AppointmentType,
    val location: String?,
    val notes: String?,
    val reminderTime: Long?,
    val isAllDay: Boolean,
    val isRecurring: Boolean,
    val recurrenceRule: RecurrenceRule?,
    val color: String,
    val attachments: List<String>,
    val participants: List<Participant>,
    val clientName: String?,
    val propertyAddress: String?,
    
    // Локальные системные поля
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
) 