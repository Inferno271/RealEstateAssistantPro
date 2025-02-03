package com.realestateassistant.pro.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Менеджер аутентификации Firebase.
 * Отвечает за все операции, связанные с аутентификацией пользователей:
 * - Вход по email/password
 * - Регистрация новых пользователей
 * - Аутентификация через Google
 * - Выход из системы
 */
@Singleton
class FirebaseAuthManager @Inject constructor() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Выполняет вход пользователя с помощью email и пароля
     * @param email Email пользователя
     * @param password Пароль пользователя
     * @return Result с объектом FirebaseUser в случае успеха или исключением в случае ошибки
     */
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Authentication failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Регистрирует нового пользователя с помощью email и пароля
     * @param email Email пользователя
     * @param password Пароль пользователя
     * @return Result с объектом FirebaseUser в случае успеха или исключением в случае ошибки
     */
    suspend fun signUpWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Registration failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Выполняет вход через Google аккаунт
     * @param idToken Токен, полученный от Google Sign-In
     * @return Result с объектом FirebaseUser в случае успеха или исключением в случае ошибки
     */
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Google authentication failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Выполняет выход текущего пользователя из системы
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Возвращает текущего аутентифицированного пользователя или null
     * @return FirebaseUser? Текущий пользователь или null, если пользователь не аутентифицирован
     */
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
} 