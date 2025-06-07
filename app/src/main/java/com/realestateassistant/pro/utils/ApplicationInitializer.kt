package com.realestateassistant.pro.utils

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
import coil.Coil
import coil.ImageLoader

/**
 * Инициализатор приложения, который выполняет тяжелые операции инициализации в фоновом потоке.
 * Использует Jetpack App Startup для автоматического запуска при старте приложения.
 */
class ApplicationInitializer : Initializer<Unit> {
    private val initializerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val TAG = "ApplicationInitializer"

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ApplicationInitializerEntryPoint {
        fun databaseEncryption(): DatabaseEncryption
    }

    override fun create(context: Context) {
        Log.d(TAG, "Начало инициализации приложения")
        
        // Инициализация Coil
        initializeCoil(context)
        
        initializerScope.launch {
            try {
                Log.d(TAG, "Запуск инициализации базы данных в фоновом потоке")
                // Получаем экземпляр DatabaseEncryption через Hilt EntryPoint
                val entryPoint = EntryPointAccessors.fromApplication(
                    context, ApplicationInitializerEntryPoint::class.java
                )
                val databaseEncryption = entryPoint.databaseEncryption()
                
                // Выполняем предварительную инициализацию базы данных
                val db = AppDatabase.getInstanceAsync(context, databaseEncryption)
                db.await()
                
                Log.d(TAG, "Инициализация базы данных успешно завершена")
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при инициализации базы данных", e)
            }
        }
        
        Log.d(TAG, "Инициализация приложения запущена")
    }

    /**
     * Инициализирует Coil для асинхронной загрузки изображений
     * @param context Контекст приложения
     */
    private fun initializeCoil(context: Context) {
        try {
            Log.d(TAG, "Инициализация Coil ImageLoader")
            
            // Создаем оптимизированный ImageLoader
            val imageLoader = CoilUtils.createImageLoader(context)
            
            // Устанавливаем его как глобальный экземпляр
            Coil.setImageLoader(imageLoader)
            
            Log.d(TAG, "Coil ImageLoader успешно инициализирован")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при инициализации Coil", e)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        // Этот инициализатор не имеет зависимостей
        return emptyList()
    }
} 