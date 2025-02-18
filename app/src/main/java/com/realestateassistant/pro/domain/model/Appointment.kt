package com.realestateassistant.pro.domain.model

/**
 * Модель встречи или показа объекта недвижимости.
 * 
 * @property id Уникальный идентификатор встречи/показа
 * @property propertyId Идентификатор объекта недвижимости
 * @property clientId Идентификатор клиента
 * @property agentId Идентификатор агента
 * @property appointmentTime Время встречи/показа в формате timestamp
 * @property duration Продолжительность в минутах
 * @property status Статус встречи (например, "запланировано", "завершено", "отменено")
 * @property type Тип встречи (например, "показ объекта", "встреча для обсуждения", "подписание документов", "другое")
 * @property notes Дополнительные примечания
 * @property reminderTime Время для напоминания
 * @property location Место встречи, если отличается от адреса объекта
 * @property createdAt Время создания записи
 * @property updatedAt Время обновления записи
 */
data class Appointment(
    val id: String = "",
    val propertyId: String = "",
    val clientId: String = "",
    val agentId: String = "",
    val appointmentTime: Long = 0L,
    val duration: Int = 60, // продолжительность в минутах
    val status: AppointmentStatus = AppointmentStatus.SCHEDULED,
    val type: AppointmentType = AppointmentType.SHOWING,
    val notes: String = "",
    val reminderTime: Long? = null, // время для напоминания
    val location: String = "", // место встречи, если отличается от адреса объекта
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class AppointmentStatus {
    SCHEDULED, // запланирована
    CONFIRMED, // подтверждена
    COMPLETED, // завершена
    CANCELLED, // отменена
    RESCHEDULED // перенесена
}

enum class AppointmentType {
    SHOWING, // показ объекта
    MEETING, // встреча для обсуждения
    SIGNING, // подписание документов
    OTHER // другое
} 