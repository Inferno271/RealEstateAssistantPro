package com.realestateassistant.pro.presentation.utils

/**
 * Форматирует строку времени для корректного отображения
 */
object TimeUtils {

    /**
     * Форматирует строку времени в формат ЧЧ:ММ
     * @param timeString Строка с временем, которая может быть в разных форматах
     * @return Отформатированное время в формате ЧЧ:ММ
     */
    fun formatTimeString(timeString: String?): String {
        if (timeString.isNullOrBlank()) {
            return ""
        }
        
        // Удаляем все нецифровые символы, кроме двоеточия
        val filtered = timeString.filter { it.isDigit() || it == ':' }
        
        // Если в строке уже есть двоеточие и формат правильный (Ч:ММ или ЧЧ:ММ)
        if (filtered.contains(':')) {
            val parts = filtered.split(':')
            if (parts.size == 2) {
                val hours = parts[0].toIntOrNull() ?: 0
                val minutes = parts[1].toIntOrNull() ?: 0
                
                // Проверяем валидность часов и минут
                if (hours in 0..23 && minutes in 0..59) {
                    return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
                }
            }
        }
        
        // Если в строке только цифры
        val digitsOnly = filtered.filter { it.isDigit() }
        if (digitsOnly.length >= 3) {
            val hours = digitsOnly.substring(0, minOf(2, digitsOnly.length)).toIntOrNull() ?: 0
            val validHours = hours.coerceIn(0, 23)
            
            val minutesStr = if (digitsOnly.length > 2) {
                digitsOnly.substring(2, minOf(4, digitsOnly.length))
            } else {
                "00"
            }
            
            val minutes = minutesStr.toIntOrNull() ?: 0
            val validMinutes = minutes.coerceIn(0, 59)
            
            return "${validHours.toString().padStart(2, '0')}:${validMinutes.toString().padStart(2, '0')}"
        }
        
        // Если недостаточно цифр, возвращаем исходную строку
        return timeString
    }
    
    /**
     * Проверяет, является ли строка с временем валидной
     * @param timeString Строка для проверки
     * @return true, если строка представляет корректное время
     */
    fun isValidTimeString(timeString: String?): Boolean {
        if (timeString.isNullOrBlank()) {
            return false
        }
        
        val formatted = formatTimeString(timeString)
        val parts = formatted.split(':')
        
        return parts.size == 2 && 
               parts[0].length == 2 && 
               parts[1].length == 2 &&
               parts[0].toIntOrNull() != null &&
               parts[1].toIntOrNull() != null
    }
} 