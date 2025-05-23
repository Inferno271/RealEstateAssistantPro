package com.realestateassistant.pro.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.realestateassistant.pro.domain.model.AppointmentStatus
import com.realestateassistant.pro.domain.model.AppointmentType

/**
 * Entity класс для хранения встреч/показов в локальной базе данных
 */
@Entity(
    tableName = "appointments",
    foreignKeys = [
        ForeignKey(
            entity = PropertyEntity::class,
            parentColumns = ["id"],
            childColumns = ["propertyId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("propertyId"),
        Index("clientId")
    ]
)
data class AppointmentEntity(
    @PrimaryKey
    val id: String,
    val propertyId: String,
    val clientId: String,
    val agentId: String,
    val appointmentTime: Long,
    val duration: Int, // продолжительность в минутах
    val status: AppointmentStatus,
    val type: AppointmentType,
    val notes: String,
    val reminderTime: Long?,
    val location: String,
    
    // Локальные системные поля
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
) 