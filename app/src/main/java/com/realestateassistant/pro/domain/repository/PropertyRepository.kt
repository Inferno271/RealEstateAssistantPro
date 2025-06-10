package com.realestateassistant.pro.domain.repository

import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.model.PropertyStatus
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    /**
     * Добавляет новый объект недвижимости.
     * Возвращает добавленный объект с обновленным id, если операция успешна.
     */
    suspend fun addProperty(property: Property): Result<Property>

    /**
     * Обновляет существующий объект недвижимости.
     */
    suspend fun updateProperty(property: Property): Result<Unit>

    /**
     * Удаляет объект недвижимости по его id.
     */
    suspend fun deleteProperty(propertyId: String): Result<Unit>

    /**
     * Получает объект недвижимости по его id.
     */
    suspend fun getProperty(propertyId: String): Result<Property>

    /**
     * Получает список всех объектов недвижимости.
     */
    suspend fun getAllProperties(): Result<List<Property>>
    
    /**
     * Наблюдает за всеми объектами недвижимости.
     * Возвращает Flow, который будет обновляться при изменении данных.
     */
    fun observeAllProperties(): Flow<List<Property>>
    
    /**
     * Наблюдает за объектами недвижимости определенного типа.
     * Возвращает Flow, который будет обновляться при изменении данных.
     */
    fun observePropertiesByType(type: String): Flow<List<Property>>
    
    /**
     * Синхронизирует данные между локальной базой данных и Firebase.
     */
    suspend fun syncProperties()
    
    /**
     * Наблюдает за объектами недвижимости с определенным статусом.
     * Возвращает Flow, который будет обновляться при изменении данных.
     */
    fun observePropertiesByStatus(status: PropertyStatus): Flow<List<Property>>
    
    /**
     * Обновляет статус объекта недвижимости.
     */
    suspend fun updatePropertyStatus(propertyId: String, status: PropertyStatus): Result<Unit>
    
    /**
     * Наблюдает за объектами недвижимости с активными бронированиями на текущую дату.
     */
    fun observePropertiesWithActiveBookings(): Flow<List<Property>>
    
    /**
     * Наблюдает за объектами недвижимости с предстоящими бронированиями.
     */
    fun observePropertiesWithUpcomingBookings(): Flow<List<Property>>
    
    /**
     * Получает список объектов недвижимости, доступных для бронирования в указанном диапазоне дат.
     */
    fun getAvailableProperties(startDate: Long, endDate: Long): Flow<List<Property>>
    
    /**
     * Обновляет статусы всех объектов недвижимости на основе текущих бронирований.
     */
    suspend fun updateAllPropertyStatuses(): Result<Unit>
} 