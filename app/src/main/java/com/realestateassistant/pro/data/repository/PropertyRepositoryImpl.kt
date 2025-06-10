package com.realestateassistant.pro.data.repository

import com.realestateassistant.pro.data.local.dao.BookingDao
import com.realestateassistant.pro.data.local.dao.PropertyDao
import com.realestateassistant.pro.data.mapper.PropertyMapper
import com.realestateassistant.pro.domain.model.BookingStatus
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.model.PropertyStatus
import com.realestateassistant.pro.domain.repository.PropertyRepository
import com.realestateassistant.pro.network.YandexGeocoderService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import android.util.Log
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull

/**
 * Реализация репозитория для работы с объектами недвижимости
 */
class PropertyRepositoryImpl @Inject constructor(
    private val propertyDao: PropertyDao,
    private val propertyMapper: PropertyMapper,
    private val bookingDao: BookingDao
) : PropertyRepository {

    private val TAG = "PropertyRepositoryImpl"
    
    /**
     * Добавляет новый объект недвижимости в базу данных
     * Все операции выполняются в фоновом потоке
     */
    override suspend fun addProperty(property: Property): Result<Property> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Добавление нового объекта недвижимости")
            
            // Дополнительная проверка адреса и геокодирование, если координаты отсутствуют
            val propertyWithCoordinates = if (property.latitude == null || property.longitude == null) {
                if (property.address.isNotEmpty()) {
                    try {
                        val coordinates = YandexGeocoderService.getCoordinates(property.address)
                        if (coordinates != null) {
                            property.copy(
                                latitude = coordinates.first,
                                longitude = coordinates.second
                            )
                        } else {
                            property
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Ошибка при геокодировании адреса: ${property.address}", e)
                        property
                    }
                } else {
                    property
                }
            } else {
                property
            }
            
            // Генерируем новый ID, если он пустой
            val propertyWithId = if (propertyWithCoordinates.id.isEmpty()) {
                propertyWithCoordinates.copy(id = UUID.randomUUID().toString())
            } else {
                propertyWithCoordinates
            }
            
            val entity = propertyMapper.toEntity(propertyWithId)
            propertyDao.insertProperty(entity)
            
            Log.d(TAG, "Объект недвижимости успешно добавлен с ID: ${propertyWithId.id}")
            
            // Возвращаем полный объект с присвоенным идентификатором
            Result.success(propertyWithId)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при добавлении объекта недвижимости", e)
            Result.failure(e)
        }
    }
    
    /**
     * Обновляет существующий объект недвижимости в базе данных
     * Все операции выполняются в фоновом потоке
     */
    override suspend fun updateProperty(property: Property): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (property.id.isEmpty()) {
                Log.e(TAG, "Невозможно обновить объект недвижимости - пустой ID")
                return@withContext Result.failure(Exception("Property id пустой."))
            }
            
            Log.d(TAG, "Обновление объекта недвижимости с ID: ${property.id}")
            
            // Проверяем и обновляем координаты, если они отсутствуют
            val propertyWithCoordinates = if (property.latitude == null || property.longitude == null) {
                if (property.address.isNotEmpty()) {
                    try {
                        val coordinates = YandexGeocoderService.getCoordinates(property.address)
                        if (coordinates != null) {
                            property.copy(
                                latitude = coordinates.first,
                                longitude = coordinates.second
                            )
                        } else {
                            property
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Ошибка при геокодировании адреса: ${property.address}", e)
                        property
                    }
                } else {
                    property
                }
            } else {
                property
            }
            
            val entity = propertyMapper.toEntity(propertyWithCoordinates)
            propertyDao.updateProperty(entity)
            
            Log.d(TAG, "Объект недвижимости успешно обновлен")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при обновлении объекта недвижимости", e)
            Result.failure(e)
        }
    }

    /**
     * Удаляет объект недвижимости из базы данных
     * Все операции выполняются в фоновом потоке
     */
    override suspend fun deleteProperty(propertyId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Удаление объекта недвижимости с ID: $propertyId")
            
            // Удаляем из локальной базы данных
            propertyDao.deleteProperty(propertyId)
            
            Log.d(TAG, "Объект недвижимости успешно удален")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при удалении объекта недвижимости", e)
            Result.failure(e)
        }
    }

    /**
     * Получает объект недвижимости по его ID
     * Все операции выполняются в фоновом потоке
     */
    override suspend fun getProperty(propertyId: String): Result<Property> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Получение объекта недвижимости с ID: $propertyId")
            
            // Получаем из локальной базы данных
            val localEntity = propertyDao.getProperty(propertyId)
            
            if (localEntity != null) {
                // Если нашли в локальной базе, возвращаем
                val property = propertyMapper.mapToDomain(localEntity)
                Log.d(TAG, "Объект недвижимости найден")
                Result.success(property)
            } else {
                Log.e(TAG, "Объект недвижимости не найден")
                Result.failure(Exception("Объект недвижимости не найден."))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении объекта недвижимости", e)
            Result.failure(e)
        }
    }

    /**
     * Получает список всех объектов недвижимости
     * Все операции выполняются в фоновом потоке
     */
    override suspend fun getAllProperties(): Result<List<Property>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Получение списка всех объектов недвижимости")
            
            // Получаем все объекты из локальной базы данных
            val localEntities = propertyDao.getAllProperties().first()
            
            // Преобразуем в список моделей
            val properties = localEntities.map { propertyMapper.mapToDomain(it) }
            
            Log.d(TAG, "Получено ${properties.size} объектов недвижимости")
            Result.success(properties)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении списка объектов недвижимости", e)
            Result.failure(e)
        }
    }
    
    /**
     * Наблюдает за всеми объектами недвижимости через Flow
     * Автоматически выполняется в фоновом потоке и обрабатывает ошибки
     */
    override fun observeAllProperties(): Flow<List<Property>> {
        return propertyDao.getAllProperties()
            .map { entities -> entities.map { propertyMapper.mapToDomain(it) } }
            .catch { e -> 
                Log.e(TAG, "Ошибка при наблюдении за объектами недвижимости", e)
                throw e
            }
            .flowOn(Dispatchers.IO)
    }
    
    /**
     * Наблюдает за объектами недвижимости определенного типа через Flow
     * Автоматически выполняется в фоновом потоке и обрабатывает ошибки
     */
    override fun observePropertiesByType(type: String): Flow<List<Property>> {
        return propertyDao.getPropertiesByType(type)
            .map { entities -> entities.map { propertyMapper.mapToDomain(it) } }
            .catch { e -> 
                Log.e(TAG, "Ошибка при наблюдении за объектами недвижимости типа $type", e)
                throw e
            }
            .flowOn(Dispatchers.IO)
    }
    
    /**
     * Синхронизирует объекты недвижимости с удаленным источником
     * Метод оставлен для совместимости с интерфейсом
     */
    override suspend fun syncProperties() {
        Log.d(TAG, "Запуск синхронизации объектов недвижимости")
        // Метод оставлен для совместимости с интерфейсом, но логика с Firebase удалена
        // При необходимости можно добавить здесь синхронизацию с другими источниками данных
    }
    
    /**
     * Наблюдает за объектами недвижимости с определенным статусом
     */
    override fun observePropertiesByStatus(status: PropertyStatus): Flow<List<Property>> {
        return propertyDao.getPropertiesByStatus(status.name)
            .map { entities -> entities.map { propertyMapper.mapToDomain(it) } }
            .catch { e -> 
                Log.e(TAG, "Ошибка при наблюдении за объектами недвижимости со статусом ${status.name}", e)
                throw e
            }
            .flowOn(Dispatchers.IO)
    }
    
    /**
     * Обновляет статус объекта недвижимости
     */
    override suspend fun updatePropertyStatus(propertyId: String, status: PropertyStatus): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Обновление статуса объекта недвижимости с ID: $propertyId на ${status.name}")
            propertyDao.updatePropertyStatus(propertyId, status.name)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при обновлении статуса объекта недвижимости", e)
            Result.failure(e)
        }
    }
    
    /**
     * Наблюдает за объектами недвижимости с активными бронированиями
     */
    override fun observePropertiesWithActiveBookings(): Flow<List<Property>> {
        return propertyDao.getPropertiesWithActiveBookings()
            .map { entities -> entities.map { propertyMapper.mapToDomain(it) } }
            .catch { e -> 
                Log.e(TAG, "Ошибка при наблюдении за объектами недвижимости с активными бронированиями", e)
                throw e
            }
            .flowOn(Dispatchers.IO)
    }
    
    /**
     * Наблюдает за объектами недвижимости с предстоящими бронированиями
     */
    override fun observePropertiesWithUpcomingBookings(): Flow<List<Property>> {
        return propertyDao.getPropertiesWithUpcomingBookings()
            .map { entities -> entities.map { propertyMapper.mapToDomain(it) } }
            .catch { e -> 
                Log.e(TAG, "Ошибка при наблюдении за объектами недвижимости с предстоящими бронированиями", e)
                throw e
            }
            .flowOn(Dispatchers.IO)
    }
    
    /**
     * Получает список объектов недвижимости, доступных для бронирования в указанном диапазоне дат
     */
    override fun getAvailableProperties(startDate: Long, endDate: Long): Flow<List<Property>> {
        return propertyDao.getAvailableProperties(startDate, endDate)
            .map { entities -> entities.map { propertyMapper.mapToDomain(it) } }
            .catch { e -> 
                Log.e(TAG, "Ошибка при получении доступных объектов недвижимости", e)
                throw e
            }
            .flowOn(Dispatchers.IO)
    }
    
    /**
     * Обновляет статусы всех объектов недвижимости на основе текущих бронирований
     */
    override suspend fun updateAllPropertyStatuses(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Обновление статусов всех объектов недвижимости")
            
            // Получаем все объекты недвижимости
            val allProperties = propertyDao.getAllProperties().firstOrNull() ?: return@withContext Result.success(Unit)
            
            // Получаем все активные бронирования (на текущую дату)
            val currentDate = System.currentTimeMillis()
            val activeBookings = bookingDao.getBookingsInDateRange(currentDate, currentDate).firstOrNull() ?: emptyList()
            
            // Получаем предстоящие бронирования (забронировано, но еще не наступила дата)
            val futureBookings = bookingDao.getUpcomingBookings(currentDate).firstOrNull() ?: emptyList()
            
            // Получаем ID объектов с активными бронированиями
            val activePropertyIds = activeBookings
                .filter { it.status == BookingStatus.ACTIVE.name }
                .map { it.propertyId }
                .toSet()
                
            // Получаем ID объектов с подтвержденными бронированиями
            val reservedPropertyIds = activeBookings
                .filter { it.status == BookingStatus.CONFIRMED.name }
                .map { it.propertyId }
                .toSet() +
                futureBookings
                .filter { it.status == BookingStatus.CONFIRMED.name || it.status == BookingStatus.PENDING.name }
                .map { it.propertyId }
                .toSet()
            
            // Обновляем статусы всех объектов
            allProperties.forEach { property ->
                val newStatus = when {
                    // Если объект занят сейчас (кто-то заселился)
                    property.id in activePropertyIds -> PropertyStatus.OCCUPIED.name
                    
                    // Если объект зарезервирован (бронь подтверждена, но еще не заехали)
                    property.id in reservedPropertyIds -> PropertyStatus.RESERVED.name
                    
                    // Иначе объект доступен
                    else -> PropertyStatus.AVAILABLE.name
                }
                
                // Обновляем статус только если он изменился
                if (property.status != newStatus) {
                    propertyDao.updatePropertyStatus(property.id, newStatus)
                }
            }
            
            Log.d(TAG, "Статусы объектов недвижимости успешно обновлены")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при обновлении статусов объектов недвижимости", e)
            Result.failure(e)
        }
    }
} 