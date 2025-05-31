package com.realestateassistant.pro.presentation.model

/**
 * Перечисление для типов аренды.
 * Используется для типобезопасного выбора типа аренды в компонентах.
 */
enum class RentalType {
    LONG_TERM,    // Долгосрочная аренда
    SHORT_TERM;   // Краткосрочная (посуточная) аренда
    
    /**
     * Возвращает человекочитаемое название типа аренды.
     * 
     * @return Локализованное название типа аренды для отображения в UI
     */
    fun getDisplayName(): String {
        return when (this) {
            LONG_TERM -> "Длительная"
            SHORT_TERM -> "Посуточная"
        }
    }
    
    /**
     * Преобразует enum в строковое представление для хранения в базе данных.
     * 
     * @return Строковое представление типа аренды для хранения
     */
    fun toStringValue(): String {
        return when (this) {
            LONG_TERM -> "длительная"
            SHORT_TERM -> "посуточная"
        }
    }
    
    companion object {
        /**
         * Преобразует строковое представление типа аренды в соответствующее значение перечисления.
         * 
         * @param value Строковое представление типа аренды
         * @return Значение перечисления или LONG_TERM по умолчанию
         */
        fun fromString(value: String): RentalType {
            return when (value.lowercase()) {
                "длительная" -> LONG_TERM
                "посуточная" -> SHORT_TERM
                "оба_варианта" -> LONG_TERM // Для обратной совместимости со старыми данными
                else -> LONG_TERM
            }
        }
    }
} 