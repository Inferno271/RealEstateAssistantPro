package com.realestateassistant.pro.core.result

/**
 * Класс для представления результата операции, который может быть успешным или содержать ошибку
 */
sealed class Result<out T> {
    /**
     * Успешный результат операции с данными
     */
    data class Success<T>(val data: T) : Result<T>()
    
    /**
     * Ошибка операции с сообщением
     */
    data class Error(val message: String) : Result<Nothing>()
    
    /**
     * Получить данные из успешного результата или null, если результат - ошибка
     */
    fun getOrNull(): T? {
        return when (this) {
            is Success -> data
            is Error -> null
        }
    }
    
    /**
     * Проверить, является ли результат успешным
     */
    val isSuccess: Boolean
        get() = this is Success
    
    /**
     * Проверить, является ли результат ошибкой
     */
    val isError: Boolean
        get() = this is Error
    
    companion object {
        /**
         * Создать успешный результат
         */
        fun <T> success(data: T): Result<T> = Success(data)
        
        /**
         * Создать результат с ошибкой
         */
        fun error(message: String): Result<Nothing> = Error(message)
    }
} 