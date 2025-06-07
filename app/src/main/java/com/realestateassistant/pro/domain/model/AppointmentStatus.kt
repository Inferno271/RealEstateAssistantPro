package com.realestateassistant.pro.domain.model

/**
 * Перечисление для определения статусов встреч
 */
enum class AppointmentStatus {
    SCHEDULED,      // Запланирована
    CONFIRMED,      // Подтверждена
    CANCELLED,      // Отменена
    COMPLETED,      // Завершена
    RESCHEDULED,    // Перенесена
    NO_SHOW,        // Не явились
    IN_PROGRESS     // В процессе
} 