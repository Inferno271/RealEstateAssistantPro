package com.realestateassistant.pro.presentation.utils

/**
 * Склонение слова "комната" в зависимости от числа
 */
fun getRoomsText(roomsCount: Int): String {
    return when {
        roomsCount % 10 == 1 && roomsCount % 100 != 11 -> "комната"
        roomsCount % 10 in 2..4 && (roomsCount % 100 < 10 || roomsCount % 100 >= 20) -> "комнаты"
        else -> "комнат"
    }
}

/**
 * Склонение слова "день" в зависимости от числа
 */
fun getDaysText(days: Int): String {
    return when {
        days % 10 == 1 && days % 100 != 11 -> "день"
        days % 10 in 2..4 && (days % 100 < 10 || days % 100 >= 20) -> "дня"
        else -> "дней"
    }
}

/**
 * Форматирование цены с разделением тысяч
 */
fun formatPrice(price: Double): String {
    return String.format("%,d", price.toInt()).replace(",", " ")
}

/**
 * Форматирование телефонного номера для отображения
 */
fun formatPhoneForDisplay(phone: String): String {
    if (phone.length != 10) return phone
    
    return "+7 (${phone.substring(0, 3)}) ${phone.substring(3, 6)}-${phone.substring(6, 8)}-${phone.substring(8, 10)}"
} 