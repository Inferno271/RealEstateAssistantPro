package com.realestateassistant.pro.di

import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import com.realestateassistant.pro.data.local.AppDatabase
import com.realestateassistant.pro.data.local.security.DatabaseEncryption
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Инициализатор приложения для Jetpack App Startup.
 * Выполняет инициализацию базы данных и других критических компонентов в фоновом потоке при запуске приложения через Jetpack App Startup.
 */
class ApplicationInitializer : Initializer<Unit> {

    private val TAG = "ApplicationInitializer"
    private val initializerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Точка входа для доступа к зависимостям, необходимым для инициализации
     */
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface InitializerEntryPoint {
        fun databaseEncryption(): DatabaseEncryption
    }

    /**
     * Выполняет инициализацию приложения в фоновом потоке
     */
    override fun create(context: Context) {
        Log.d(TAG, "Запуск инициализации приложения")
        
        // Получаем доступ к зависимостям через EntryPoint
        initializerScope.launch {
            try {
                val entryPoint = EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    InitializerEntryPoint::class.java
                )
                
                // Получаем зависимости
                val databaseEncryption = entryPoint.databaseEncryption()
                
                // Инициализируем базу данных в фоновом потоке
                val dbDeferred = AppDatabase.getInstanceAsync(context, databaseEncryption)
                
                // Дожидаемся инициализации базы данных
                val db = dbDeferred.await()
                Log.d(TAG, "База данных инициализирована успешно")
                
                // Проверяем соединение с базой данных
                try {
                    // Простой запрос для проверки соединения
                    val propertyCount = db.propertyDao().getPropertyCount()
                    Log.d(TAG, "Проверка соединения с базой данных: объектов в базе: $propertyCount")
                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка при проверке соединения с базой данных", e)
                }
                
                // Здесь можно добавить инициализацию других критичных компонентов
                
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при инициализации приложения", e)
            }
        }
        
        Log.d(TAG, "Инициализация приложения запущена в фоновом потоке")
    }

    /**
     * Зависимости для инициализатора
     */
    override fun dependencies(): List<Class<out Initializer<*>>> {
        // У этого инициализатора нет зависимостей
        return emptyList()
    }
} 