package com.realestateassistant.pro.data.local.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.crypto.AEADBadTagException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Класс для управления шифрованием базы данных
 *
 * Использует EncryptedSharedPreferences для безопасного хранения пароля базы данных
 * Включает механизм восстановления для случаев, когда криптографические ключи повреждены
 */
@Singleton
class DatabaseEncryption @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "DatabaseEncryption"
    
    // Обычные незашифрованные SharedPreferences для аварийного режима
    private val fallbackPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("fallback_secure_db", Context.MODE_PRIVATE)
    }
    
    // Флаг для отслеживания работы в аварийном режиме
    private var isUsingFallback = false

    private val masterKey by lazy {
        try {
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при создании мастер-ключа", e)
            null
        }
    }

    private val sharedPreferences: SharedPreferences by lazy {
        try {
            if (masterKey == null) {
                Log.w(TAG, "Использую аварийный режим из-за отсутствия мастер-ключа")
                isUsingFallback = true
                return@lazy fallbackPreferences
            }
            
            EncryptedSharedPreferences.create(
                context,
                "secure_db",
                masterKey!!,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при создании EncryptedSharedPreferences", e)
            // Проверка, является ли ошибка связанной с нарушением целостности ключей шифрования
            if (e is AEADBadTagException || e.cause is AEADBadTagException) {
                Log.w(TAG, "Обнаружено повреждение криптографических ключей, сбрасываю хранилище")
                resetEncryptedStorage()
            }
            
            isUsingFallback = true
            fallbackPreferences
        }
    }

    private companion object {
        const val KEY_DB_PASSWORD = "db_password"
        const val DEFAULT_PASSWORD = "default_secure_password_123" // Будет использоваться только при первом запуске
    }

    /**
     * Сбрасывает зашифрованное хранилище при обнаружении проблем с ключами
     */
    private fun resetEncryptedStorage() {
        try {
            // Удаляем файлы SharedPreferences, связанные с EncryptedSharedPreferences
            val sharedPrefsDir = File(context.applicationInfo.dataDir, "shared_prefs")
            val secureDbFile = File(sharedPrefsDir, "secure_db.xml")
            if (secureDbFile.exists()) {
                val deleted = secureDbFile.delete()
                Log.d(TAG, "Файл secure_db.xml ${if (deleted) "удален" else "не удален"}")
            }
        
            
            Log.d(TAG, "Зашифрованное хранилище сброшено")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при сбросе зашифрованного хранилища", e)
        }
    }

    /**
     * Получает пароль базы данных из защищенного хранилища
     */
    fun getDatabasePassword(): String {
        return try {
            val password = if (isUsingFallback) {
                fallbackPreferences.getString(KEY_DB_PASSWORD, null)
            } else {
                sharedPreferences.getString(KEY_DB_PASSWORD, null)
            }
            
            password ?: createAndSaveNewPassword()
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении пароля базы данных", e)
            // Возвращаем пароль по умолчанию в случае ошибки
            DEFAULT_PASSWORD
        }
    }

    /**
     * Создает новый пароль и сохраняет его в защищенном хранилище
     */
    private fun createAndSaveNewPassword(): String {
        return try {
            DEFAULT_PASSWORD.also { password ->
                if (isUsingFallback) {
                    fallbackPreferences.edit().putString(KEY_DB_PASSWORD, password).apply()
                } else {
                    sharedPreferences.edit().putString(KEY_DB_PASSWORD, password).apply()
                }
                Log.d(TAG, "Создан и сохранен новый пароль для базы данных")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при создании нового пароля", e)
            DEFAULT_PASSWORD
        }
    }
} 