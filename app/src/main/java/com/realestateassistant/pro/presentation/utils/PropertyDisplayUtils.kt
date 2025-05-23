package com.realestateassistant.pro.presentation.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * Утилиты для форматирования отображения данных недвижимости
 */
object PropertyDisplayUtils {
    
    /**
     * Форматирует цену для отображения с разделителями тысяч
     */
    fun formatPrice(price: Double): String {
        val format = NumberFormat.getNumberInstance(Locale("ru"))
        return format.format(price)
    }
    
    /**
     * Возвращает правильное склонение слова "комната" в зависимости от числа
     */
    fun getRoomsText(roomsCount: Int): String {
        return when {
            roomsCount % 10 == 1 && roomsCount % 100 != 11 -> "комната"
            roomsCount % 10 in 2..4 && roomsCount % 100 !in 12..14 -> "комнаты"
            else -> "комнат"
        }
    }
    
    /**
     * Форматирует номер телефона для отображения
     */
    fun formatPhoneForDisplay(phone: String): String {
        val digits = phone.filter { it.isDigit() }
        if (digits.length != 11) return phone
        
        return buildString {
            append("+")
            append(digits[0])
            append(" (")
            append(digits.substring(1, 4))
            append(") ")
            append(digits.substring(4, 7))
            append("-")
            append(digits.substring(7, 9))
            append("-")
            append(digits.substring(9, 11))
        }
    }
    
    /**
     * Форматирует дату обновления недвижимости для удобного отображения
     */
    fun formatUpdatedDate(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        // Меньше суток
        return when {
            diff < 60 * 60 * 1000 -> {
                val minutes = (diff / (60 * 1000)).toInt()
                "$minutes ${getMinutesText(minutes)} назад"
            }
            diff < 24 * 60 * 60 * 1000 -> {
                val hours = (diff / (60 * 60 * 1000)).toInt()
                "$hours ${getHoursText(hours)} назад"
            }
            diff < 48 * 60 * 60 * 1000 -> "вчера"
            diff < 7 * 24 * 60 * 60 * 1000 -> {
                val days = (diff / (24 * 60 * 60 * 1000)).toInt()
                "$days ${getDaysText(days)} назад"
            }
            else -> {
                val sdf = SimpleDateFormat("d MMMM", Locale("ru"))
                sdf.format(Date(timestamp))
            }
        }
    }
    
    /**
     * Возвращает правильное склонение слова "минута" в зависимости от числа
     */
    private fun getMinutesText(minutes: Int): String {
        return when {
            minutes % 10 == 1 && minutes % 100 != 11 -> "минуту"
            minutes % 10 in 2..4 && minutes % 100 !in 12..14 -> "минуты"
            else -> "минут"
        }
    }
    
    /**
     * Возвращает правильное склонение слова "час" в зависимости от числа
     */
    private fun getHoursText(hours: Int): String {
        return when {
            hours % 10 == 1 && hours % 100 != 11 -> "час"
            hours % 10 in 2..4 && hours % 100 !in 12..14 -> "часа"
            else -> "часов"
        }
    }
    
    /**
     * Возвращает правильное склонение слова "день" в зависимости от числа
     */
    private fun getDaysText(days: Int): String {
        return when {
            days % 10 == 1 && days % 100 != 11 -> "день"
            days % 10 in 2..4 && days % 100 !in 12..14 -> "дня"
            else -> "дней"
        }
    }
    
    /**
     * Определяет статус недвижимости для отображения
     */
    fun getPropertyStatusText(isActive: Boolean, isBooked: Boolean): String {
        return when {
            isBooked -> "Забронировано"
            isActive -> "Активно"
            else -> "Архив"
        }
    }
    
    /**
     * Возвращает текст для отображения уровней здания
     */
    fun getLevelsText(levelsCount: Int): String {
        return when {
            levelsCount % 10 == 1 && levelsCount % 100 != 11 -> "уровень"
            levelsCount % 10 in 2..4 && levelsCount % 100 !in 12..14 -> "уровня"
            else -> "уровней"
        }
    }
} 