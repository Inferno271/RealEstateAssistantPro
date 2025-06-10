package com.realestateassistant.pro.data.local.dao

import androidx.room.*
import com.realestateassistant.pro.data.local.entity.AppointmentEntity
import com.realestateassistant.pro.domain.model.AppointmentStatus
import com.realestateassistant.pro.domain.model.AppointmentType
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * DAO для работы с встречами
 */
@Dao
interface AppointmentDao {
    /**
     * Вставляет новую встречу
     * Если встреча с таким id уже существует, она будет заменена
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity)

    /**
     * Вставляет список встреч
     * Если встреча с таким id уже существует, она будет заменена
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointments(appointments: List<AppointmentEntity>)

    /**
     * Обновляет существующую встречу
     */
    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)

    /**
     * Удаляет встречу по ее id
     */
    @Query("DELETE FROM appointments WHERE id = :appointmentId")
    suspend fun deleteAppointment(appointmentId: String)

    /**
     * Получает встречу по ее id
     */
    @Query("SELECT * FROM appointments WHERE id = :appointmentId")
    suspend fun getAppointment(appointmentId: String): AppointmentEntity?

    /**
     * Получает список всех встреч
     */
    @Query("SELECT * FROM appointments ORDER BY startTime ASC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    /**
     * Получает список встреч для клиента
     */
    @Query("SELECT * FROM appointments WHERE clientId = :clientId ORDER BY startTime ASC")
    fun getAppointmentsForClient(clientId: String): Flow<List<AppointmentEntity>>
    
    /**
     * Получает список встреч для объекта недвижимости
     */
    @Query("SELECT * FROM appointments WHERE propertyId = :propertyId ORDER BY startTime ASC")
    fun getAppointmentsForProperty(propertyId: String): Flow<List<AppointmentEntity>>
    
    /**
     * Получает список встреч на определенную дату
     */
    @Query("SELECT * FROM appointments WHERE startTime BETWEEN :startTime AND :endTime ORDER BY startTime ASC")
    fun getAppointmentsForDateRange(startTime: Long, endTime: Long): Flow<List<AppointmentEntity>>
    
    /**
     * Получает список встреч с определенным статусом
     */
    @Query("SELECT * FROM appointments WHERE status = :status ORDER BY startTime ASC")
    fun getAppointmentsByStatus(status: AppointmentStatus): Flow<List<AppointmentEntity>>

    /**
     * Получает список встреч определенного типа
     */
    @Query("SELECT * FROM appointments WHERE type = :type ORDER BY startTime ASC")
    fun getAppointmentsByType(type: AppointmentType): Flow<List<AppointmentEntity>>
    
    /**
     * Получает список встреч, которые не синхронизированы
     */
    @Query("SELECT * FROM appointments WHERE isSynced = 0")
    suspend fun getUnsyncedAppointments(): List<AppointmentEntity>

    /**
     * Обновляет статус синхронизации встречи
     */
    @Query("UPDATE appointments SET isSynced = :isSynced WHERE id = :appointmentId")
    suspend fun updateSyncStatus(appointmentId: String, isSynced: Boolean)
    
    /**
     * Получает количество встреч на определенную дату
     */
    @Query("SELECT COUNT(*) FROM appointments WHERE startTime BETWEEN :startOfDay AND :endOfDay")
    suspend fun getAppointmentsCountForDay(startOfDay: Long, endOfDay: Long): Int
    
    /**
     * Получает список встреч с пересечением по времени
     */
    @Query("""
        SELECT * FROM appointments 
        WHERE (
            (startTime <= :endTime AND endTime >= :startTime) OR
            (startTime >= :startTime AND startTime <= :endTime) OR
            (endTime >= :startTime AND endTime <= :endTime)
        )
        AND id != :excludeId
        ORDER BY startTime ASC
    """)
    suspend fun getOverlappingAppointments(startTime: Long, endTime: Long, excludeId: String = ""): List<AppointmentEntity>
    
    /**
     * Поиск встреч по заголовку или описанию
     */
    @Query("""
        SELECT * FROM appointments 
        WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'
        ORDER BY startTime ASC
    """)
    fun searchAppointments(query: String): Flow<List<AppointmentEntity>>
    
    /**
     * Получает список предстоящих встреч
     */
    @Query("""
        SELECT * FROM appointments 
        WHERE startTime >= :currentTime AND status NOT IN ('COMPLETED', 'CANCELLED')
        ORDER BY startTime ASC
    """)
    fun getUpcomingAppointments(currentTime: Long): Flow<List<AppointmentEntity>>
    
    /**
     * Получает список встреч, для которых нужно отправить напоминание
     */
    @Query("""
        SELECT * FROM appointments 
        WHERE reminderTime IS NOT NULL AND reminderTime <= :currentTime AND startTime > :currentTime
        AND status NOT IN ('COMPLETED', 'CANCELLED')
    """)
    suspend fun getAppointmentsForReminder(currentTime: Long): List<AppointmentEntity>
    
    /**
     * Получает список встреч для календаря по месяцам
     */
    @Query("""
        SELECT * FROM appointments 
        WHERE startTime >= :startOfMonth AND startTime <= :endOfMonth
        ORDER BY startTime ASC
    """)
    fun getAppointmentsForMonth(startOfMonth: Long, endOfMonth: Long): Flow<List<AppointmentEntity>>
    
    /**
     * Получает список встреч для календаря по неделям
     */
    @Query("""
        SELECT * FROM appointments 
        WHERE startTime >= :startOfWeek AND startTime <= :endOfWeek
        ORDER BY startTime ASC
    """)
    fun getAppointmentsForWeek(startOfWeek: Long, endOfWeek: Long): Flow<List<AppointmentEntity>>
} 