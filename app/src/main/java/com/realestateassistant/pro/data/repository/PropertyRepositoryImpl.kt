package com.realestateassistant.pro.data.repository

import com.realestateassistant.pro.data.local.dao.PropertyDao
import com.realestateassistant.pro.data.mapper.PropertyMapper
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.repository.PropertyRepository
import com.realestateassistant.pro.network.YandexGeocoderService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import android.util.Log
import java.util.UUID
import javax.inject.Inject

/**
 * Реализация репозитория для работы с объектами недвижимости
 */
class PropertyRepositoryImpl @Inject constructor(
    private val propertyDao: PropertyDao,
    private val propertyMapper: PropertyMapper
) : PropertyRepository {
    
    override suspend fun addProperty(property: Property): Result<Property> {
        return try {
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
                        Log.e("PropertyRepositoryImpl", "Ошибка при геокодировании адреса: ${property.address}", e)
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
            
            // Возвращаем полный объект с присвоенным идентификатором
            Result.success(propertyWithId)
        } catch (e: Exception) {
            Log.e("PropertyRepositoryImpl", "Ошибка при добавлении объекта недвижимости", e)
            Result.failure(e)
        }
    }
    
    override suspend fun updateProperty(property: Property): Result<Unit> {
        return try {
            if (property.id.isEmpty()) {
                return Result.failure(Exception("Property id пустой."))
            }
            
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
                        Log.e("PropertyRepositoryImpl", "Ошибка при геокодировании адреса: ${property.address}", e)
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
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PropertyRepositoryImpl", "Ошибка при обновлении объекта недвижимости", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteProperty(propertyId: String): Result<Unit> {
        return try {
            // Удаляем из локальной базы данных
            propertyDao.deleteProperty(propertyId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProperty(propertyId: String): Result<Property> {
        return try {
            // Получаем из локальной базы данных
            val localEntity = propertyDao.getProperty(propertyId)
            
            if (localEntity != null) {
                // Если нашли в локальной базе, возвращаем
                val property = propertyMapper.mapToDomain(localEntity)
                Result.success(property)
            } else {
                Result.failure(Exception("Объект недвижимости не найден."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllProperties(): Result<List<Property>> {
        return try {
            // Получаем все объекты из локальной базы данных
            val localEntities = propertyDao.getAllProperties().first()
            
            // Преобразуем в список моделей
            val properties = localEntities.map { propertyMapper.mapToDomain(it) }
            
            Result.success(properties)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeAllProperties(): Flow<List<Property>> {
        return propertyDao.getAllProperties().map { entities ->
            entities.map { propertyMapper.mapToDomain(it) }
        }
    }
    
    override fun observePropertiesByType(type: String): Flow<List<Property>> {
        return propertyDao.getPropertiesByType(type).map { entities ->
            entities.map { propertyMapper.mapToDomain(it) }
        }
    }
    
    override suspend fun syncProperties() {
        // Метод оставлен для совместимости с интерфейсом, но логика с Firebase удалена
        // При необходимости можно добавить здесь синхронизацию с другими источниками данных
    }
} 