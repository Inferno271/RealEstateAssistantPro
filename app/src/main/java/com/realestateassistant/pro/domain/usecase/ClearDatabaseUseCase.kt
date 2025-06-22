package com.realestateassistant.pro.domain.usecase

import android.util.Log
import com.realestateassistant.pro.data.local.dao.PropertyDao
import com.realestateassistant.pro.data.local.dao.ClientDao
import com.realestateassistant.pro.data.local.dao.AppointmentDao
import com.realestateassistant.pro.data.local.dao.BookingDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * UseCase для очистки базы данных приложения
 */
class ClearDatabaseUseCase @Inject constructor(
    private val propertyDao: PropertyDao,
    private val clientDao: ClientDao,
    private val appointmentDao: AppointmentDao,
    private val bookingDao: BookingDao
) {
    /**
     * Очищает все таблицы базы данных
     * @return результат операции с информацией о количестве удаленных записей
     */
    suspend operator fun invoke(): Result<ClearDatabaseResult> = withContext(Dispatchers.IO) {
        try {
            Log.d("ClearDatabaseUseCase", "Начинаем очистку базы данных")
            
            // Получаем количество записей перед очисткой для статистики
            val propertiesCount = propertyDao.getPropertyCount()
            val clientsCount = clientDao.getClientCount()
            
            // Выполняем SQL-запросы для очистки таблиц
            // Порядок важен из-за внешних ключей
            bookingDao.deleteAllBookings()
            appointmentDao.deleteAllAppointments()
            propertyDao.deleteAllProperties()
            clientDao.deleteAllClients()
            
            Log.d("ClearDatabaseUseCase", "База данных успешно очищена")
            
            // Возвращаем результат с информацией о количестве удаленных записей
            Result.success(
                ClearDatabaseResult(
                    propertiesDeleted = propertiesCount,
                    clientsDeleted = clientsCount,
                    totalItemsDeleted = propertiesCount + clientsCount
                )
            )
        } catch (e: Exception) {
            Log.e("ClearDatabaseUseCase", "Ошибка при очистке базы данных", e)
            Result.failure(e)
        }
    }
    
    /**
     * Класс для представления результата очистки базы данных
     */
    data class ClearDatabaseResult(
        val propertiesDeleted: Int,
        val clientsDeleted: Int,
        val totalItemsDeleted: Int
    )
} 