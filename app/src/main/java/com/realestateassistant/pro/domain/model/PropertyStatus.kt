package com.realestateassistant.pro.domain.model

/**
 * Перечисление для определения статусов объектов недвижимости.
 * Статусы могут изменяться на основе бронирований.
 */
enum class PropertyStatus {
    AVAILABLE,      // Доступен для бронирования
    RESERVED,       // Зарезервирован (есть активное бронирование)
    OCCUPIED,       // Занят (клиент въехал)
    UNAVAILABLE,    // Недоступен (например, на ремонте)
    PENDING_REVIEW, // Ожидает проверки администратором
    ARCHIVED;       // Архивирован (не отображается в активных списках)
    
    companion object {
        /**
         * Возвращает описание статуса для отображения
         */
        fun getDisplayName(status: PropertyStatus): String {
            return when (status) {
                AVAILABLE -> "Доступен"
                RESERVED -> "Зарезервирован"
                OCCUPIED -> "Занят"
                UNAVAILABLE -> "Недоступен"
                PENDING_REVIEW -> "На проверке"
                ARCHIVED -> "В архиве"
            }
        }
        
        /**
         * Возвращает цветовой код для отображения статуса
         */
        fun getColorCode(status: PropertyStatus): String {
            return when (status) {
                AVAILABLE -> "#4CAF50"     // Зеленый
                RESERVED -> "#FFC107"      // Желтый
                OCCUPIED -> "#F44336"      // Красный
                UNAVAILABLE -> "#9E9E9E"   // Серый
                PENDING_REVIEW -> "#2196F3" // Синий
                ARCHIVED -> "#607D8B"      // Темно-серый
            }
        }
    }
} 