package com.realestateassistant.pro

import android.app.Application
import android.os.StrictMode
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.realestateassistant.pro.data.worker.WorkManagerHelper
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Основной класс приложения.
 * Инициализирует Dagger Hilt для внедрения зависимостей.
 * Базы данных инициализируются асинхронно через Jetpack App Startup.
 */
@HiltAndroidApp
class RealEstateApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var workManagerHelper: WorkManagerHelper
    
    companion object {
        private const val TAG = "RealEstateApplication"
        private const val DEBUG = true // Константа для режима отладки
    }
    
    override fun onCreate() {
        // Опционально: включаем StrictMode для отладки потоков
        if (DEBUG) {
            enableStrictModeForDebug()
        }
        
        super.onCreate()
        
        Log.d(TAG, "Запуск приложения")
        
        // Инициализация Yandex MapKit
        MapKitFactory.setApiKey("80b06c04-6156-4dfd-a086-2cee0b05fdb8")
        
        // База данных инициализируется асинхронно через ApplicationInitializer
        // с помощью Jetpack App Startup (см. AndroidManifest.xml)
        Log.d(TAG, "База данных будет инициализирована асинхронно через App Startup")
        
        // Инициализация логирования с Timber
        if (DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        // Планируем периодическое обновление статусов объектов недвижимости
        workManagerHelper.schedulePropertyStatusUpdates(this)
    }
    
    /**
     * Включает строгий режим для обнаружения блокировок основного потока и других проблем
     * Используется только в режиме отладки
     */
    private fun enableStrictModeForDebug() {
        Log.d(TAG, "Включение StrictMode для отладки потоков")
        
        // Настройка политики для потоков - обнаруживает блокирующие операции в основном потоке
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .detectCustomSlowCalls() // Обнаруживает пользовательские медленные вызовы
                .permitDiskReads() // Разрешаем чтение с диска, чтобы избежать блокировки UI
                .penaltyLog() // Записываем нарушения в logcat без блокировки UI
                .build()
        )
        
        // Настройка политики для виртуальной машины - обнаруживает утечки ресурсов
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .detectLeakedRegistrationObjects() // Обнаруживает утечки регистраций
                .detectActivityLeaks() // Обнаруживает утечки Activity
                .detectCleartextNetwork() // Обнаруживает незашифрованный сетевой трафик
                .detectContentUriWithoutPermission() // Обнаруживает доступ к URI без разрешений
                .detectUnsafeIntentLaunch() // Обнаруживает небезопасные запуски Intent
                .penaltyLog() // Записываем нарушения в logcat
                .build()
        )
        
        Log.d(TAG, "StrictMode настроен для обнаружения проблем производительности")
    }
    
    /**
     * Конфигурация для WorkManager с поддержкой Hilt
     */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
} 