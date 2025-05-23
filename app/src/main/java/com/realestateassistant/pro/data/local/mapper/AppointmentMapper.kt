package com.realestateassistant.pro.data.local.mapper

import com.realestateassistant.pro.data.local.entity.AppointmentEntity
import com.realestateassistant.pro.domain.model.Appointment

/**
 * Маппер для преобразования Appointment между Entity и Domain моделями
 */
object AppointmentMapper {
    /**
     * Преобразует Entity модель в Domain модель
     */
    fun mapToDomain(entity: AppointmentEntity): Appointment {
        return Appointment(
            id = entity.id,
            propertyId = entity.propertyId,
            clientId = entity.clientId,
            agentId = entity.agentId,
            appointmentTime = entity.appointmentTime,
            duration = entity.duration,
            status = entity.status,
            type = entity.type,
            notes = entity.notes,
            reminderTime = entity.reminderTime,
            location = entity.location,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    /**
     * Преобразует Domain модель в Entity модель
     */
    fun mapToEntity(domain: Appointment): AppointmentEntity {
        return AppointmentEntity(
            id = domain.id,
            propertyId = domain.propertyId,
            clientId = domain.clientId,
            agentId = domain.agentId,
            appointmentTime = domain.appointmentTime,
            duration = domain.duration,
            status = domain.status,
            type = domain.type,
            notes = domain.notes,
            reminderTime = domain.reminderTime,
            location = domain.location,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            isSynced = false
        )
    }
} 