package com.realestateassistant.pro.data.local.validation

/**
 * Класс для представления результата валидации
 */
sealed class ValidationResult {
    /**
     * Валидация прошла успешно
     */
    object Success : ValidationResult()

    /**
     * Валидация не прошла
     * @param message Сообщение об ошибке
     * @param field Поле, в котором произошла ошибка
     */
    data class Error(
        val message: String,
        val field: String? = null
    ) : ValidationResult()
}

/**
 * Функция для комбинирования нескольких результатов валидации
 * @return Первую ошибку, если она есть, иначе Success
 */
fun List<ValidationResult>.combine(): ValidationResult {
    return firstOrNull { it is ValidationResult.Error } ?: ValidationResult.Success
} 