package com.realestateassistant.pro.domain.model

/**
 * Перечисление для определения типов встреч
 */
enum class AppointmentType {
    SHOWING,            // Показ объекта
    CLIENT_MEETING,     // Встреча с клиентом
    PROPERTY_INSPECTION,// Осмотр объекта
    CONTRACT_SIGNING,   // Подписание договора
    KEY_HANDOVER,       // Передача ключей
    OWNER_MEETING,      // Встреча с собственником
    SIGNING,            // Подписание документов
    INSPECTION,         // Инспекция объекта
    PHOTO_SESSION,      // Фотосессия объекта
    MAINTENANCE,        // Техническое обслуживание
    OTHER               // Другое
} 