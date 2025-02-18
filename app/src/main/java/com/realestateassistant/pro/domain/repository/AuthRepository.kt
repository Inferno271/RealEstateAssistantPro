package com.realestateassistant.pro.domain.repository

import com.realestateassistant.pro.domain.model.User

/**
 * Интерфейс репозитория аутентификации.
 * Определяет основные методы для работы с пользовательской аутентификацией.
 */
interface AuthRepository {
    /**
     * Выполняет вход пользователя по email и паролю
     * @param email Email пользователя
     * @param password Пароль пользователя
     * @return Result с доменной моделью User при успехе
     */
    suspend fun signInWithEmail(email: String, password: String): Result<User>

    /**
     * Регистрирует нового пользователя с помощью email и пароля
     * @param email Email пользователя
     * @param password Пароль пользователя
     * @return Result с доменной моделью User при успехе
     */
    suspend fun signUpWithEmail(email: String, password: String): Result<User>

    /**
     * Выполняет вход через Google аккаунт
     * @param idToken Токен Google
     * @return Result с доменной моделью User при успехе
     */
    suspend fun signInWithGoogle(idToken: String): Result<User>

    /**
     * Выполняет выход из системы
     */
    fun signOut()

    /**
     * Возвращает текущего аутентифицированного пользователя
     * @return User или null, если пользователь не аутентифицирован
     */
    fun getCurrentUser(): User?
} 