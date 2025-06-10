package com.realestateassistant.pro.domain.model

import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID

/**
 * Модель встречи или показа объекта недвижимости.
 * 
 * @property id Уникальный идентификатор встречи/показа
 * @property propertyId Идентификатор объекта недвижимости
 * @property clientId Идентификатор клиента
 * @property title Заголовок встречи
 * @property description Описание встречи
 * @property startTime Время начала встречи в формате timestamp
 * @property endTime Время окончания встречи в формате timestamp
 * @property status Статус встречи
 * @property type Тип встречи
 * @property location Место встречи, если отличается от адреса объекта
 * @property notes Дополнительные примечания
 * @property reminderTime Время для напоминания в формате timestamp
 * @property isAllDay Флаг, указывающий, что встреча занимает весь день
 * @property isRecurring Флаг, указывающий на повторяющуюся встречу
 * @property recurrenceRule Правило повторения встречи
 * @property color Цвет для отображения в календаре
 * @property attachments Список прикрепленных файлов
 * @property participants Список участников встречи
 * @property createdAt Время создания записи
 * @property updatedAt Время обновления записи
 */
data class Appointment(
    val id: String = "",
    val title: String = "",
    val description: String? = null,
    val clientId: String = "",
    val clientName: String? = null,
    val propertyId: String = "",
    val propertyAddress: String? = null,
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val isAllDay: Boolean = false,
    val location: String? = null,
    val notes: String? = null,
    val type: AppointmentType = AppointmentType.CLIENT_MEETING,
    val status: AppointmentStatus = AppointmentStatus.SCHEDULED,
    val reminderTime: Long? = null,
    val isRecurring: Boolean = false,
    val recurrenceRule: RecurrenceRule? = null,
    val color: String = "#4285F4",
    val attachments: List<String> = emptyList(),
    val participants: List<Participant> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Возвращает дату и время начала как LocalDateTime
     */
    val startDateTime: LocalDateTime
        get() = Instant.ofEpochMilli(startTime)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    
    /**
     * Возвращает дату и время окончания как LocalDateTime
     */
    val endDateTime: LocalDateTime
        get() = Instant.ofEpochMilli(endTime)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    
    /**
     * Возвращает только дату начала
     */
    val startDate: LocalDate
        get() = startDateTime.toLocalDate()
    
    /**
     * Возвращает только дату окончания
     */
    val endDate: LocalDate
        get() = endDateTime.toLocalDate()
    
    /**
     * Возвращает только время начала
     */
    val startTimeOfDay: LocalTime
        get() = startDateTime.toLocalTime()
    
    /**
     * Возвращает только время окончания
     */
    val endTimeOfDay: LocalTime
        get() = endDateTime.toLocalTime()
    
    /**
     * Проверяет, находится ли встреча в указанном диапазоне дат
     */
    fun isInDateRange(rangeStart: Long, rangeEnd: Long): Boolean {
        return (startTime in rangeStart..rangeEnd) || 
               (endTime in rangeStart..rangeEnd) ||
               (startTime <= rangeStart && endTime >= rangeEnd)
    }
    
    companion object {
        /**
         * Генерирует уникальный идентификатор для встречи
         */
        fun generateId(): String = UUID.randomUUID().toString()
        
        /**
         * Создает встречу из даты, времени начала и времени окончания
         */
        fun fromDateTime(
            id: String = generateId(),
            date: LocalDate,
            startTime: LocalTime,
            endTime: LocalTime,
            isAllDay: Boolean = false,
            title: String = "",
            description: String? = null,
            clientId: String = "",
            clientName: String? = null,
            propertyId: String = "",
            propertyAddress: String? = null,
            location: String? = null,
            notes: String? = null,
            type: AppointmentType = AppointmentType.CLIENT_MEETING,
            status: AppointmentStatus = AppointmentStatus.SCHEDULED,
            reminderTime: Long? = null,
            isRecurring: Boolean = false,
            recurrenceRule: RecurrenceRule? = null,
            color: String = "#4285F4",
            attachments: List<String> = emptyList(),
            participants: List<Participant> = emptyList()
        ): Appointment {
            val startDateTime = if (isAllDay) {
                date.atStartOfDay()
            } else {
                date.atTime(startTime)
            }
            
            val endDateTime = if (isAllDay) {
                date.atTime(23, 59, 59)
            } else {
                date.atTime(endTime)
            }
            
            val startMillis = startDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
                
            val endMillis = endDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
                
            return Appointment(
                id = id,
                title = title,
                description = description,
                clientId = clientId,
                clientName = clientName,
                propertyId = propertyId,
                propertyAddress = propertyAddress,
                startTime = startMillis,
                endTime = endMillis,
                isAllDay = isAllDay,
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
        
        /**
         * Создает встречу из начальной и конечной дат и времени
         */
        fun fromDateTimeRange(
            id: String = generateId(),
            startDate: LocalDate,
            startTime: LocalTime,
            endDate: LocalDate,
            endTime: LocalTime,
            isAllDay: Boolean = false,
            title: String = "",
            description: String? = null,
            clientId: String = "",
            clientName: String? = null,
            propertyId: String = "",
            propertyAddress: String? = null,
            location: String? = null,
            notes: String? = null,
            type: AppointmentType = AppointmentType.CLIENT_MEETING,
            status: AppointmentStatus = AppointmentStatus.SCHEDULED,
            reminderTime: Long? = null,
            isRecurring: Boolean = false,
            recurrenceRule: RecurrenceRule? = null,
            color: String = "#4285F4",
            attachments: List<String> = emptyList(),
            participants: List<Participant> = emptyList()
        ): Appointment {
            val startDateTime = if (isAllDay) {
                startDate.atStartOfDay()
            } else {
                startDate.atTime(startTime)
            }
            
            val endDateTime = if (isAllDay) {
                endDate.atTime(23, 59, 59)
            } else {
                endDate.atTime(endTime)
            }
            
            val startMillis = startDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
                
            val endMillis = endDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
                
            return Appointment(
                id = id,
                title = title,
                description = description,
                clientId = clientId,
                clientName = clientName,
                propertyId = propertyId,
                propertyAddress = propertyAddress,
                startTime = startMillis,
                endTime = endMillis,
                isAllDay = isAllDay,
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
    }
}

/**
 * Модель участника встречи
 * 
 * @property id Идентификатор участника
 * @property name Имя участника
 * @property email Email участника
 * @property phone Телефон участника
 * @property role Роль участника
 * @property isConfirmed Флаг подтверждения участия
 */
data class Participant(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: ParticipantRole = ParticipantRole.OTHER,
    val isConfirmed: Boolean = false
)

/**
 * Роли участников встречи
 */
enum class ParticipantRole {
    CLIENT,     // Клиент
    OWNER,      // Владелец объекта
    INSPECTOR,  // Инспектор
    LAWYER,     // Юрист
    OTHER       // Другое
}

/**
 * Правило повторения встречи
 * 
 * @property frequency Частота повторения
 * @property interval Интервал повторения
 * @property count Количество повторений
 * @property until Дата окончания повторений
 * @property byDay Дни недели для повторения
 * @property byMonthDay Дни месяца для повторения
 * @property byMonth Месяцы для повторения
 * @property exceptions Исключения из правила повторения
 */
data class RecurrenceRule(
    val frequency: RecurrenceFrequency = RecurrenceFrequency.NONE,
    val interval: Int = 1,
    val count: Int? = null,
    val until: Long? = null,
    val byDay: List<DayOfWeek> = emptyList(),
    val byMonthDay: List<Int> = emptyList(),
    val byMonth: List<Int> = emptyList(),
    val exceptions: List<Long> = emptyList()
)

/**
 * Частота повторения
 */
enum class RecurrenceFrequency {
    NONE,     // Не повторяется
    DAILY,    // Ежедневно
    WEEKLY,   // Еженедельно
    MONTHLY,  // Ежемесячно
    YEARLY    // Ежегодно
}

/**
 * Дни недели
 */
enum class DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
} 