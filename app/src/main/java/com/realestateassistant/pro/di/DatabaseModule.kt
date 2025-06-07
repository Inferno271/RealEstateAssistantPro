package com.realestateassistant.pro.di

import android.content.Context
import android.util.Log
import com.realestateassistant.pro.data.local.AppDatabase
import com.realestateassistant.pro.data.local.dao.AppointmentDao
import com.realestateassistant.pro.data.local.dao.ClientDao
import com.realestateassistant.pro.data.local.dao.PropertyDao
import com.realestateassistant.pro.data.local.dao.UserDao
import com.realestateassistant.pro.data.local.security.DatabaseEncryption
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import java.io.File
import javax.inject.Singleton

/**
 * Модуль Dagger Hilt для предоставления зависимостей базы данных Room.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val TAG = "DatabaseModule"
    private const val DATABASE_NAME = "realestate_database"
    
    // CoroutineScope для асинхронных операций с базой данных
    private val databaseScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    /**
     * Предоставляет экземпляр класса DatabaseEncryption
     */
    @Provides
    @Singleton
    fun provideDatabaseEncryption(
        @ApplicationContext context: Context
    ): DatabaseEncryption {
        try {
            val encryption = DatabaseEncryption(context)
            // Для проверки получаем пароль - это вызовет исключение если что-то не так
            encryption.getDatabasePassword()
            return encryption
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при создании DatabaseEncryption", e)
            
            // Если при инициализации произошла ошибка, 
            // пробуем сбросить шифрование и очистить данные базы
            resetAllStorage(context)
            
            // Возвращаем новый экземпляр
            return DatabaseEncryption(context)
        }
    }
    
    /**
     * Сбрасывает все файлы хранилища при критических ошибках
     */
    private fun resetAllStorage(context: Context) {
        try {
            Log.w(TAG, "Полный сброс хранилища из-за критической ошибки")
            
            // Сбрасываем базу данных
            resetDatabaseFiles(context)
            
            // Сбрасываем защищенные настройки
            val sharedPrefsDir = File(context.applicationInfo.dataDir, "shared_prefs")
            arrayOf("secure_db.xml", "fallback_secure_db.xml").forEach { fileName ->
                val file = File(sharedPrefsDir, fileName)
                if (file.exists()) {
                    val deleted = file.delete()
                    Log.d(TAG, "Файл $fileName ${if (deleted) "удален" else "не удален"}")
                }
            }
            
            Log.d(TAG, "Полный сброс хранилища выполнен")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при сбросе хранилища", e)
        }
    }
    
    /**
     * Сбрасывает файлы базы данных при критических ошибках
     */
    private fun resetDatabaseFiles(context: Context) {
        try {
            Log.w(TAG, "Сброс файлов базы данных из-за критической ошибки")
            
            // Получаем директорию с файлами базы данных
            val databaseFile = context.getDatabasePath(DATABASE_NAME)
            val databaseDir = databaseFile.parentFile
            
            if (databaseDir != null && databaseDir.exists()) {
                // Удаляем все файлы, связанные с базой данных
                databaseDir.listFiles()?.forEach { file ->
                    if (file.name.startsWith(DATABASE_NAME)) {
                        val success = file.delete()
                        Log.d(TAG, "Файл базы данных ${file.name} ${if (success) "удален" else "не удален"}")
                    }
                }
                
                Log.d(TAG, "Файлы базы данных сброшены")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при сбросе файлов базы данных", e)
        }
    }
    
    /**
     * Предоставляет экземпляр базы данных Room
     * 
     * Метод будет использовать синхронную инициализацию, но для большинства операций
     * рекомендуется использовать асинхронный запуск через ApplicationInitializer
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        databaseEncryption: DatabaseEncryption
    ): AppDatabase {
        Log.d(TAG, "Запрошен экземпляр базы данных через провайдер")
        
        try {
            // Проверяем, был ли уже инициализирован экземпляр базы данных асинхронно
            val existingInstance = runBlocking {
                try {
                    // Пытаемся получить асинхронно инициализированный экземпляр с минимальным ожиданием
                    val deferred = AppDatabase.getInstanceAsync(context, databaseEncryption)
                    if (deferred.isCompleted) {
                        Log.d(TAG, "Найден уже инициализированный экземпляр базы данных")
                        deferred.await()
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка при попытке получить асинхронный экземпляр", e)
                    null
                }
            }
            
            if (existingInstance != null) {
                return existingInstance
            }
            
            Log.d(TAG, "Инициализация базы данных синхронным методом")
            return AppDatabase.getInstance(context, databaseEncryption)
        } catch (e: Exception) {
            Log.e(TAG, "Критическая ошибка при инициализации базы данных", e)
            
            // Полный сброс хранилища в случае критической ошибки
            resetAllStorage(context)
            
            try {
                // Повторная попытка с новым экземпляром DatabaseEncryption
                val newEncryption = DatabaseEncryption(context)
                return AppDatabase.getInstance(context, newEncryption)
            } catch (e2: Exception) {
                Log.e(TAG, "Повторная ошибка при инициализации базы данных", e2)
                
                // Если и после полного сброса база не инициализируется,
                // выбрасываем исключение, чтобы сигнализировать о критической ошибке
                throw RuntimeException("Невозможно инициализировать базу данных даже после сброса", e2)
            }
        }
    }
    
    /**
     * Предоставляет DAO для работы с объектами недвижимости
     */
    @Provides
    @Singleton
    fun providePropertyDao(database: AppDatabase): PropertyDao {
        return database.propertyDao()
    }
    
    /**
     * Предоставляет DAO для работы с клиентами
     */
    @Provides
    @Singleton
    fun provideClientDao(database: AppDatabase): ClientDao {
        return database.clientDao()
    }
    
    /**
     * Предоставляет DAO для работы с встречами
     */
    @Provides
    @Singleton
    fun provideAppointmentDao(database: AppDatabase): AppointmentDao {
        return database.appointmentDao()
    }
    
    /**
     * Предоставляет DAO для работы с пользователями
     */
    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
} 