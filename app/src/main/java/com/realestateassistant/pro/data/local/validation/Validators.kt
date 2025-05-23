package com.realestateassistant.pro.data.local.validation

import android.util.Patterns
import java.util.regex.Pattern

/**
 * Объект с функциями валидации для различных типов данных
 */
object Validators {
    private val PHONE_PATTERN = Pattern.compile("^\\+?[78][\\s\\-]?\\(?\\d{3}\\)?[\\s\\-]?\\d{3}[\\s\\-]?\\d{2}[\\s\\-]?\\d{2}\$")
    
    /**
     * Проверяет, что строка не пустая
     */
    fun validateRequired(value: String?, fieldName: String): ValidationResult {
        return if (value.isNullOrBlank()) {
            ValidationResult.Error("Поле '$fieldName' обязательно для заполнения", fieldName)
        } else {
            ValidationResult.Success
        }
    }

    /**
     * Проверяет, что число положительное
     */
    fun validatePositiveNumber(value: Double?, fieldName: String): ValidationResult {
        return when {
            value == null -> ValidationResult.Success
            value < 0 -> ValidationResult.Error("Значение поля '$fieldName' должно быть положительным", fieldName)
            else -> ValidationResult.Success
        }
    }

    /**
     * Проверяет, что число положительное
     */
    fun validatePositiveInt(value: Int?, fieldName: String): ValidationResult {
        return when {
            value == null -> ValidationResult.Success
            value < 0 -> ValidationResult.Error("Значение поля '$fieldName' должно быть положительным", fieldName)
            else -> ValidationResult.Success
        }
    }

    /**
     * Проверяет корректность email
     */
    fun validateEmail(email: String?, fieldName: String = "Email"): ValidationResult {
        return when {
            email.isNullOrBlank() -> ValidationResult.Success
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> 
                ValidationResult.Error("Некорректный формат email", fieldName)
            else -> ValidationResult.Success
        }
    }

    /**
     * Проверяет корректность номера телефона (российский формат)
     */
    fun validatePhone(phone: String?, fieldName: String = "Телефон"): ValidationResult {
        return when {
            phone.isNullOrBlank() -> ValidationResult.Success
            !PHONE_PATTERN.matcher(phone).matches() -> 
                ValidationResult.Error("Некорректный формат номера телефона", fieldName)
            else -> ValidationResult.Success
        }
    }

    /**
     * Проверяет, что значение находится в допустимом диапазоне
     */
    fun validateRange(
        value: Double?,
        min: Double,
        max: Double,
        fieldName: String
    ): ValidationResult {
        return when {
            value == null -> ValidationResult.Success
            value < min -> ValidationResult.Error(
                "Значение поля '$fieldName' должно быть не меньше $min",
                fieldName
            )
            value > max -> ValidationResult.Error(
                "Значение поля '$fieldName' должно быть не больше $max",
                fieldName
            )
            else -> ValidationResult.Success
        }
    }

    /**
     * Проверяет, что значение находится в допустимом диапазоне
     */
    fun validateRange(
        value: Int?,
        min: Int,
        max: Int,
        fieldName: String
    ): ValidationResult {
        return when {
            value == null -> ValidationResult.Success
            value < min -> ValidationResult.Error(
                "Значение поля '$fieldName' должно быть не меньше $min",
                fieldName
            )
            value > max -> ValidationResult.Error(
                "Значение поля '$fieldName' должно быть не больше $max",
                fieldName
            )
            else -> ValidationResult.Success
        }
    }

    /**
     * Проверяет максимальную длину строки
     */
    fun validateMaxLength(
        value: String?,
        maxLength: Int,
        fieldName: String
    ): ValidationResult {
        return when {
            value == null -> ValidationResult.Success
            value.length > maxLength -> ValidationResult.Error(
                "Длина поля '$fieldName' не должна превышать $maxLength символов",
                fieldName
            )
            else -> ValidationResult.Success
        }
    }

    /**
     * Проверяет, что список не пустой
     */
    fun validateNonEmptyList(
        list: List<*>?,
        fieldName: String
    ): ValidationResult {
        return when {
            list == null || list.isEmpty() -> ValidationResult.Error(
                "Список '$fieldName' не должен быть пустым",
                fieldName
            )
            else -> ValidationResult.Success
        }
    }
} 