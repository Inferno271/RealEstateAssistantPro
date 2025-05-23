package com.realestateassistant.pro.data.local.dao

import androidx.room.*
import com.realestateassistant.pro.data.local.entity.AppointmentEntity
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
    @Query("SELECT * FROM appointments")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    /**
     * Получает список встреч для клиента
     */
    @Query("SELECT * FROM appointments WHERE clientId = :clientId")
    fun getAppointmentsForClient(clientId: String): Flow<List<AppointmentEntity>>
    
    /**
     * Получает список встреч для объекта недвижимости
     */
    @Query("SELECT * FROM appointments WHERE propertyId = :propertyId")
    fun getAppointmentsForProperty(propertyId: String): Flow<List<AppointmentEntity>>
    
    /**
     * Получает список встреч для агента
     */
    @Query("SELECT * FROM appointments WHERE agentId = :agentId")
    fun getAppointmentsForAgent(agentId: String): Flow<List<AppointmentEntity>>
    
    /**
     * Получает список встреч на определенную дату
     */
    @Query("SELECT * FROM appointments WHERE appointmentTime BETWEEN :startTime AND :endTime")
    fun getAppointmentsForDateRange(startTime: Long, endTime: Long): Flow<List<AppointmentEntity>>
    
    /**
     * Получает список встреч с определенным статусом
     */
    @Query("SELECT * FROM appointments WHERE status = :status")
    fun getAppointmentsByStatus(status: String): Flow<List<AppointmentEntity>>
    
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
} 