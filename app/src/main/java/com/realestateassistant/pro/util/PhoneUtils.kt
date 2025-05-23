package com.realestateassistant.pro.util

import com.realestateassistant.pro.presentation.components.Country
import com.realestateassistant.pro.presentation.components.popularCountries
import com.realestateassistant.pro.presentation.components.otherCountries

/**
 * Утилиты для работы с телефонными номерами
 */
object PhoneUtils {

    /**
     * Форматирует номер телефона для отображения в зависимости от кода страны
     */
    fun formatInternationalPhoneNumber(fullNumber: String): String {
        if (fullNumber.isBlank()) return ""
        
        // Предварительная очистка номера от лишних символов
        val cleanNumber = fullNumber.replace(Regex("[^\\d+]"), "")
        
        // Находим код страны
        val matchedCountry = (popularCountries + otherCountries).find { country ->
            cleanNumber.startsWith(country.dialCode)
        }
        
        if (matchedCountry != null) {
            // Получаем номер без кода страны
            val dialCode = matchedCountry.dialCode
            val nationalNumber = cleanNumber.substring(dialCode.length)
            
            // Разные форматы в зависимости от страны
            return when (matchedCountry.code) {
                "RU", "KZ" -> formatRussianPhoneNumber(dialCode, nationalNumber)
                "US" -> formatUsPhoneNumber(dialCode, nationalNumber)
                "GB" -> formatUKPhoneNumber(dialCode, nationalNumber)
                "DE" -> formatGermanPhoneNumber(dialCode, nationalNumber)
                else -> formatDefaultPhoneNumber(dialCode, nationalNumber)
            }
        }
        
        // Если код страны не определен, но начинается с +
        if (cleanNumber.startsWith("+")) {
            val plusIndex = cleanNumber.indexOf('+')
            var digitIndex = plusIndex + 1
            
            // Находим конец кода страны (обычно 1-3 цифры после +)
            while (digitIndex < cleanNumber.length && 
                  cleanNumber[digitIndex].isDigit() &&
                  digitIndex - plusIndex <= 4) {
                digitIndex++
            }
            
            val countryCode = cleanNumber.substring(plusIndex, digitIndex)
            val nationalNumber = cleanNumber.substring(digitIndex)
            
            return formatDefaultPhoneNumber(countryCode, nationalNumber)
        }
        
        // Если номер без кода, форматируем как российский
        return formatRussianPhoneNumber("+7", cleanNumber)
    }
    
    /**
     * Нормализует номер телефона в международный формат E.164
     */
    fun normalizeInternationalPhoneNumber(phoneNumber: String): String {
        // Убираем все нецифровые символы, кроме "+"
        val cleanNumber = phoneNumber.replace(Regex("[^\\d+]"), "")
        
        // Если номер начинается с "+" - проверяем форматирование
        if (cleanNumber.startsWith("+")) {
            // Проверяем наличие кода страны
            val matchedCountry = (popularCountries + otherCountries).find { country ->
                cleanNumber.startsWith(country.dialCode)
            }
            
            // Если код страны распознан, считаем номер валидным
            if (matchedCountry != null) {
                return cleanNumber
            }
            
            // Если код страны не распознан, но есть +, пробуем извлечь первые 1-3 цифры как код
            val plusIndex = cleanNumber.indexOf('+')
            var digitIndex = plusIndex + 1
            
            // Находим конец кода страны (обычно 1-3 цифры после +)
            while (digitIndex < cleanNumber.length && 
                  cleanNumber[digitIndex].isDigit() &&
                  digitIndex - plusIndex <= 4) {
                digitIndex++
            }
            
            if (digitIndex > plusIndex + 1) {
                // Есть какой-то код страны
                return cleanNumber
            }
        }
        
        // Российские номера с 8
        if (cleanNumber.startsWith("8") && cleanNumber.length == 11) {
            // Заменяем 8 на +7 для российских номеров
            return "+7${cleanNumber.substring(1)}"
        }
        
        // Если номер длиной 10 цифр - считаем его российским
        if (cleanNumber.length == 10 && cleanNumber.all { it.isDigit() }) {
            return "+7$cleanNumber"
        }
        
        // Для номеров с кодом страны без +
        if (cleanNumber.length > 10) {
            // Проверяем известные коды стран
            for (country in popularCountries + otherCountries) {
                val codeDigits = country.dialCode.replace("+", "")
                if (cleanNumber.startsWith(codeDigits)) {
                    return "+$cleanNumber"
                }
            }
        }
        
        // Если не удалось определить формат, добавляем +7 по умолчанию
        return "+7$cleanNumber"
    }
    
    /**
     * Проверяет, является ли номер телефона валидным
     */
    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        if (phoneNumber.isBlank() || !phoneNumber.startsWith("+")) {
            return false
        }
        
        val cleanNumber = phoneNumber.replace(Regex("[^\\d+]"), "")
        val digitsCount = cleanNumber.count { it.isDigit() }
        
        // Находим код страны
        val matchedCountry = (popularCountries + otherCountries).find { country ->
            cleanNumber.startsWith(country.dialCode)
        }
        
        if (matchedCountry != null) {
            // Проверяем длину национального номера в зависимости от страны
            val codeDigitsCount = matchedCountry.dialCode.count { it.isDigit() }
            val nationalDigitsCount = digitsCount - codeDigitsCount
            
            return when (matchedCountry.code) {
                "RU", "KZ" -> nationalDigitsCount == 10 
                "US", "CA" -> nationalDigitsCount == 10
                "GB" -> nationalDigitsCount >= 9 && nationalDigitsCount <= 10
                "DE" -> nationalDigitsCount >= 10 && nationalDigitsCount <= 11
                "FR" -> nationalDigitsCount == 9
                else -> nationalDigitsCount >= 7 && nationalDigitsCount <= 15
            }
        }
        
        // Если код страны не определен
        return digitsCount >= 10 && digitsCount <= 15
    }
    
    /**
     * Устаревший метод для обратной совместимости - возвращает номер без кода страны
     */
    fun normalizePhoneNumber(phoneNumber: String): String {
        val internationalNumber = normalizeInternationalPhoneNumber(phoneNumber)
        // Если номер начинается с кода России, возвращаем только 10 цифр
        return if (internationalNumber.startsWith("+7")) {
            internationalNumber.replace("+7", "")
        } else {
            // Пытаемся получить национальную часть номера
            val digitsOnly = internationalNumber.filter { it.isDigit() }
            if (digitsOnly.length > 10) {
                digitsOnly.takeLast(10)
            } else {
                digitsOnly
            }
        }
    }
    
    /**
     * Форматирование для российских номеров: +7 (XXX) XXX-XX-XX
     */
    private fun formatRussianPhoneNumber(dialCode: String, number: String): String {
        val digits = number.filter { it.isDigit() }
        
        return when {
            digits.isEmpty() -> dialCode
            digits.length <= 3 -> "$dialCode (${digits.take(3)})"
            digits.length <= 6 -> "$dialCode (${digits.take(3)}) ${digits.substring(3)}"
            digits.length <= 8 -> "$dialCode (${digits.take(3)}) ${digits.substring(3, 6)}-${digits.substring(6)}"
            else -> "$dialCode (${digits.take(3)}) ${digits.substring(3, 6)}-${digits.substring(6, 8)}-${digits.substring(8)}"
        }
    }
    
    /**
     * Форматирование для американских номеров: +1 (XXX) XXX-XXXX
     */
    private fun formatUsPhoneNumber(dialCode: String, number: String): String {
        val digits = number.filter { it.isDigit() }
        
        return when {
            digits.isEmpty() -> dialCode
            digits.length <= 3 -> "$dialCode (${digits.take(3)})"
            digits.length <= 6 -> "$dialCode (${digits.take(3)}) ${digits.substring(3)}"
            else -> "$dialCode (${digits.take(3)}) ${digits.substring(3, 6)}-${digits.substring(6)}"
        }
    }
    
    /**
     * Форматирование для британских номеров: +44 XXXX XXXXXX
     */
    private fun formatUKPhoneNumber(dialCode: String, number: String): String {
        val digits = number.filter { it.isDigit() }
        
        return when {
            digits.isEmpty() -> dialCode
            digits.length <= 4 -> "$dialCode ${digits.take(4)}"
            else -> "$dialCode ${digits.take(4)} ${digits.substring(4)}"
        }
    }
    
    /**
     * Форматирование для немецких номеров: +49 XXX XXXXXXX
     */
    private fun formatGermanPhoneNumber(dialCode: String, number: String): String {
        val digits = number.filter { it.isDigit() }
        
        return when {
            digits.isEmpty() -> dialCode
            digits.length <= 3 -> "$dialCode ${digits.take(3)}"
            else -> "$dialCode ${digits.take(3)} ${digits.substring(3)}"
        }
    }
    
    /**
     * Форматирование для номеров других стран: +XX XXXXXXXXX
     */
    private fun formatDefaultPhoneNumber(dialCode: String, number: String): String {
        val digits = number.filter { it.isDigit() }
        
        return if (digits.isEmpty()) {
            dialCode
        } else {
            // Группировка цифр по 3 для удобства чтения
            val formatted = StringBuilder()
            digits.forEachIndexed { index, digit ->
                formatted.append(digit)
                if ((index + 1) % 3 == 0 && index < digits.length - 1) {
                    formatted.append(" ")
                }
            }
            
            "$dialCode ${formatted}"
        }
    }
} 