package com.realestateassistant.pro.data.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Хелпер для работы с WorkManager
 */
@Singleton
class WorkManagerHelper @Inject constructor() {

    /**
     * Планирует периодическое обновление статусов объектов недвижимости
     * на основе бронирований
     */
    fun schedulePropertyStatusUpdates(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .build()
            
        val workRequest = PeriodicWorkRequestBuilder<PropertyStatusUpdateWorker>(
            // Запускаем каждые 4 часа
            4, TimeUnit.HOURS,
            // С гибким интервалом в 30 минут
            30, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()
            
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PropertyStatusUpdateWorker.WORK_NAME,
            // Заменяем существующую работу, если она уже запланирована
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    /**
     * Отменяет периодическое обновление статусов объектов недвижимости
     */
    fun cancelPropertyStatusUpdates(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(PropertyStatusUpdateWorker.WORK_NAME)
    }
} 