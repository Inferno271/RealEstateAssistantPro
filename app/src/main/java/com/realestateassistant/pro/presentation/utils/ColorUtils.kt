package com.realestateassistant.pro.presentation.utils

import androidx.compose.ui.graphics.Color

/**
 * Преобразует строку с цветом в объект Color
 */
fun parseColor(colorString: String): Color {
    return try {
        // Если строка начинается с # - это HEX цвет
        if (colorString.startsWith("#")) {
            Color(android.graphics.Color.parseColor(colorString))
        } else {
            // Если строка не начинается с #, пробуем добавить # и распарсить
            Color(android.graphics.Color.parseColor("#$colorString"))
        }
    } catch (e: Exception) {
        // Если не удалось распарсить, возвращаем серый цвет по умолчанию
        Color.Gray
    }
} 