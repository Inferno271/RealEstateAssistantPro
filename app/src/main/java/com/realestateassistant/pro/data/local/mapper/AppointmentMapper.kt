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
            title = entity.title,
            description = entity.description,
            startTime = entity.startTime,
            endTime = entity.endTime,
            status = entity.status,
            type = entity.type,
            location = entity.location,
            notes = entity.notes,
            reminderTime = entity.reminderTime,
            isAllDay = entity.isAllDay,
            isRecurring = entity.isRecurring,
            recurrenceRule = entity.recurrenceRule,
            color = entity.color,
            attachments = entity.attachments,
            participants = entity.participants,
            clientName = entity.clientName,
            propertyAddress = entity.propertyAddress,
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
            title = domain.title,
            description = domain.description,
            startTime = domain.startTime,
            endTime = domain.endTime,
            status = domain.status,
            type = domain.type,
            location = domain.location,
            notes = domain.notes,
            reminderTime = domain.reminderTime,
            isAllDay = domain.isAllDay,
            isRecurring = domain.isRecurring,
            recurrenceRule = domain.recurrenceRule,
            color = domain.color,
            attachments = domain.attachments,
            participants = domain.participants,
            clientName = domain.clientName,
            propertyAddress = domain.propertyAddress,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            isSynced = false
        )
    }
} 