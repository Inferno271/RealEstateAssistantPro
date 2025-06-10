package com.realestateassistant.pro.data.local.dao

import androidx.room.*
import com.realestateassistant.pro.data.local.entity.PropertyEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO для работы с объектами недвижимости
 */
@Dao
interface PropertyDao {
    /**
     * Вставляет новый объект недвижимости
     * Если объект с таким id уже существует, он будет заменен
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: PropertyEntity)

    /**
     * Вставляет список объектов недвижимости
     * Если объект с таким id уже существует, он будет заменен
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperties(properties: List<PropertyEntity>)

    /**
     * Обновляет существующий объект недвижимости
     */
    @Update
    suspend fun updateProperty(property: PropertyEntity)

    /**
     * Удаляет объект недвижимости по его id
     */
    @Query("DELETE FROM properties WHERE id = :propertyId")
    suspend fun deleteProperty(propertyId: String)

    /**
     * Получает объект недвижимости по его id
     */
    @Query("SELECT * FROM properties WHERE id = :propertyId")
    suspend fun getProperty(propertyId: String): PropertyEntity?

    /**
     * Получает список всех объектов недвижимости
     */
    @Query("SELECT * FROM properties")
    fun getAllProperties(): Flow<List<PropertyEntity>>

    /**
     * Получает количество объектов недвижимости в базе данных
     */
    @Query("SELECT COUNT(*) FROM properties")
    suspend fun getPropertyCount(): Int
    
    /**
     * Получает список объектов недвижимости по типу
     */
    @Query("SELECT * FROM properties WHERE propertyType = :type")
    fun getPropertiesByType(type: String): Flow<List<PropertyEntity>>
    
    /**
     * Получает список объектов недвижимости, которые не синхронизированы
     */
    @Query("SELECT * FROM properties WHERE isSynced = 0")
    suspend fun getUnsyncedProperties(): List<PropertyEntity>

    /**
     * Обновляет статус синхронизации объекта недвижимости
     */
    @Query("UPDATE properties SET isSynced = :isSynced WHERE id = :propertyId")
    suspend fun updateSyncStatus(propertyId: String, isSynced: Boolean)
    
    /**
     * Получает список объектов недвижимости по статусу
     */
    @Query("SELECT * FROM properties WHERE status = :status")
    fun getPropertiesByStatus(status: String): Flow<List<PropertyEntity>>
    
    /**
     * Обновляет статус объекта недвижимости
     */
    @Query("UPDATE properties SET status = :status, updatedAt = :updatedAt WHERE id = :propertyId")
    suspend fun updatePropertyStatus(propertyId: String, status: String, updatedAt: Long = System.currentTimeMillis())
    
    /**
     * Получает список объектов недвижимости с активными бронированиями на указанную дату
     */
    @Query("SELECT p.* FROM properties p JOIN bookings b ON p.id = b.propertyId " +
           "WHERE b.status IN ('CONFIRMED', 'ACTIVE') AND " +
           "(:currentDate BETWEEN b.startDate AND b.endDate)")
    fun getPropertiesWithActiveBookings(currentDate: Long = System.currentTimeMillis()): Flow<List<PropertyEntity>>
    
    /**
     * Получает список объектов недвижимости с предстоящими бронированиями
     */
    @Query("SELECT p.* FROM properties p JOIN bookings b ON p.id = b.propertyId " +
           "WHERE b.status IN ('CONFIRMED', 'PENDING') AND " +
           "b.startDate > :currentDate")
    fun getPropertiesWithUpcomingBookings(currentDate: Long = System.currentTimeMillis()): Flow<List<PropertyEntity>>
    
    /**
     * Получает список объектов недвижимости без активных бронирований
     */
    @Query("SELECT * FROM properties WHERE id NOT IN " +
           "(SELECT propertyId FROM bookings WHERE status IN ('CONFIRMED', 'ACTIVE', 'PENDING') " +
           "AND (startDate <= :endDate AND endDate >= :startDate))")
    fun getAvailableProperties(startDate: Long, endDate: Long): Flow<List<PropertyEntity>>
} 