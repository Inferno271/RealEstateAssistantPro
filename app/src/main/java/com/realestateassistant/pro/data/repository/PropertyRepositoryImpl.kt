package com.realestateassistant.pro.data.repository

import com.realestateassistant.pro.data.local.dao.PropertyDao
import com.realestateassistant.pro.data.mapper.PropertyMapper
import com.realestateassistant.pro.domain.model.Property
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

/**
 * Реализация репозитория для работы с объектами недвижимости
 */
class PropertyRepositoryImpl @Inject constructor(
    private val propertyDao: PropertyDao,
    private val propertyMapper: PropertyMapper
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
} 