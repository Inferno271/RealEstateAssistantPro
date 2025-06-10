package com.realestateassistant.pro.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.realestateassistant.pro.domain.usecase.property.UpdatePropertyStatusesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Worker для автоматического обновления статусов объектов недвижимости
 * на основе бронирований
 */
@HiltWorker
class PropertyStatusUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val updatePropertyStatusesUseCase: UpdatePropertyStatusesUseCase
) : CoroutineWorker(context, workerParams) {
    
    private val TAG = "PropertyStatusWorker"
    
    override suspend fun doWork(): Result {
        Log.d(TAG, "Запуск работы по обновлению статусов недвижимости")
        
        return try {
            // Обновляем статусы всех объектов на основе бронирований
            val result = updatePropertyStatusesUseCase()
            
            if (result.isSuccess) {
                Log.d(TAG, "Статусы объектов недвижимости успешно обновлены")
                Result.success()
            } else {
                Log.e(TAG, "Ошибка при обновлении статусов: ${result.exceptionOrNull()?.message}")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Исключение при обновлении статусов объектов недвижимости", e)
            Result.retry()
        }
    }
    
    companion object {
        const val WORK_NAME = "property_status_update_worker"
    }
} 