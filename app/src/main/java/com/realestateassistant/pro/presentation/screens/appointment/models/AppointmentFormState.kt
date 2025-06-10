package com.realestateassistant.pro.presentation.screens.appointment.models

import com.realestateassistant.pro.domain.model.Appointment
import com.realestateassistant.pro.domain.model.AppointmentStatus
import com.realestateassistant.pro.domain.model.AppointmentType
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.model.Participant
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.model.RecurrenceRule
import java.util.UUID
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

/**
 * Состояние формы для создания/редактирования встречи
 */
data class AppointmentFormState(
    val id: String = "",
    val propertyId: String = "",
    val clientId: String = "",
    val title: String = "",
    val description: String = "",
    
    // Новые поля для даты и времени с использованием LocalDate и LocalTime
    val startDate: LocalDate = LocalDate.now(),
    val startTime: LocalTime = LocalTime.now().withSecond(0).withNano(0),
    val endDate: LocalDate = LocalDate.now(),
    val endTime: LocalTime = LocalTime.now().plusHours(1).withSecond(0).withNano(0),
    
    val status: AppointmentStatus = AppointmentStatus.SCHEDULED,
    val type: AppointmentType = AppointmentType.SHOWING,
    val location: String = "",
    val notes: String = "",
    val reminderTime: Long? = null,
    val isAllDay: Boolean = false,
    val isRecurring: Boolean = false,
    val recurrenceRule: RecurrenceRule? = null,
    val color: String = "#4285F4",
    val attachments: List<String> = emptyList(),
    val participants: List<Participant> = emptyList(),
    
    // Данные для отображения
    val clientName: String = "",
    val propertyAddress: String = "",
    val availableClients: List<Client> = emptyList(),
    val availableProperties: List<Property> = emptyList(),
    
    // Состояние валидации
    val titleError: String? = null,
    val propertyError: String? = null,
    val clientError: String? = null,
    val timeError: String? = null,
    val startDateError: String? = null,
    val endDateError: String? = null,
    val locationError: String? = null,
    
    // Состояние загрузки
    val isLoading: Boolean = false,
    val isPropertyLoading: Boolean = false,
    val isClientLoading: Boolean = false,
    
    // Состояние выбора
    val showPropertySelector: Boolean = false,
    val showClientSelector: Boolean = false,
    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false,
    val showColorPicker: Boolean = false,
    val showRecurrenceDialog: Boolean = false,
    val showReminderDialog: Boolean = false,
    val showAttachmentPicker: Boolean = false,
    val showParticipantDialog: Boolean = false,
    
    // Флаги для управления диалогами
    val isStartDateSelection: Boolean = true,
    val isStartTimeSelection: Boolean = true
) {
    /**
     * Валидна ли форма
     */
    val isValid: Boolean
        get() {
            val isEndTimeValid = if (!isAllDay && startDate.isEqual(endDate)) {
                endTime.isAfter(startTime)
            } else {
                true
            }
            
            val isEndDateValid = endDate.isAfter(startDate) || endDate.isEqual(startDate)
            
            return title.isNotBlank() && 
                propertyId.isNotBlank() && 
                clientId.isNotBlank() && 
                   isEndDateValid &&
                   isEndTimeValid &&
                titleError == null && 
                clientError == null && 
                propertyError == null && 
                startDateError == null && 
                endDateError == null && 
                timeError == null
        }
    
    /**
     * Возвращает список ошибок валидации
     */
    fun getValidationErrors(): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        
        if (title.isBlank()) {
            errors["title"] = "Заголовок не может быть пустым"
        }
        
        if (propertyId.isBlank()) {
            errors["property"] = "Необходимо выбрать объект недвижимости"
        }
        
        if (clientId.isBlank()) {
            errors["client"] = "Необходимо выбрать клиента"
        }
        
        if (endDate.isBefore(startDate)) {
            errors["endDate"] = "Дата окончания не может быть раньше даты начала"
        }
        
        if (!isAllDay && endDate.isEqual(startDate) && (endTime.isBefore(startTime) || endTime.equals(startTime))) {
            errors["time"] = "Время окончания должно быть позже времени начала"
        }
        
        return errors
    }
    
    /**
     * Преобразует состояние формы в модель встречи
     */
    fun toAppointment(): Appointment {
        return Appointment.fromDateTimeRange(
            id = if (id.isBlank()) UUID.randomUUID().toString() else id,
            startDate = startDate,
            startTime = startTime,
            endDate = endDate,
            endTime = endTime,
            isAllDay = isAllDay,
            title = title,
            description = description,
            clientId = clientId,
            clientName = clientName,
            propertyId = propertyId,
            propertyAddress = propertyAddress,
            location = location,
            notes = notes,
            type = type,
            status = status,
            reminderTime = reminderTime,
            isRecurring = isRecurring,
            recurrenceRule = recurrenceRule,
            color = color,
            attachments = attachments,
            participants = participants
        )
    }
    
    companion object {
        /**
         * Создает состояние формы из модели встречи
         */
        fun fromAppointment(appointment: Appointment): AppointmentFormState {
            return AppointmentFormState(
                id = appointment.id,
                propertyId = appointment.propertyId,
                clientId = appointment.clientId,
                title = appointment.title,
                description = appointment.description ?: "",
                startDate = appointment.startDate,
                startTime = appointment.startTimeOfDay,
                endDate = appointment.endDate,
                endTime = appointment.endTimeOfDay,
                status = appointment.status,
                type = appointment.type,
                location = appointment.location ?: "",
                notes = appointment.notes ?: "",
                reminderTime = appointment.reminderTime,
                isAllDay = appointment.isAllDay,
                isRecurring = appointment.isRecurring,
                recurrenceRule = appointment.recurrenceRule,
                color = appointment.color,
                attachments = appointment.attachments,
                participants = appointment.participants,
                clientName = appointment.clientName ?: "",
                propertyAddress = appointment.propertyAddress ?: ""
            )
        }
    }
}

/**
 * Минимальная информация о клиенте для списков выбора
 */
data class ClientMinimal(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String
)

/**
 * Минимальная информация об объекте недвижимости для списков выбора
 */
data class PropertyMinimal(
    val id: String,
    val address: String,
    val type: String
) 