package com.realestateassistant.pro.data.repository

import android.util.Log
import com.realestateassistant.pro.data.local.dao.AppointmentDao
import com.realestateassistant.pro.data.local.entity.AppointmentEntity
import com.realestateassistant.pro.domain.model.Appointment
import com.realestateassistant.pro.domain.model.AppointmentStatus
import com.realestateassistant.pro.domain.model.AppointmentType
import com.realestateassistant.pro.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "AppointmentRepository"

@Singleton
class AppointmentRepositoryImpl @Inject constructor(
    private val appointmentDao: AppointmentDao
) : AppointmentRepository {

    override suspend fun createAppointment(appointment: Appointment): Result<Appointment> {
        return try {
            val newId = appointment.id.ifEmpty { UUID.randomUUID().toString() }
            val updatedAppointment = appointment.copy(id = newId)
            val appointmentEntity = mapToEntity(updatedAppointment)
            appointmentDao.insertAppointment(appointmentEntity)
            Log.d(TAG, "Встреча создана: ${updatedAppointment.id}")
            Result.success(updatedAppointment)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при создании встречи", e)
            Result.failure(e)
        }
    }

    override suspend fun updateAppointment(appointment: Appointment): Result<Unit> {
        return try {
            if (appointment.id.isEmpty()) {
                Log.e(TAG, "Ошибка при обновлении встречи: ID встречи пустой")
                return Result.failure(Exception("ID встречи пустой."))
            }
            val appointmentEntity = mapToEntity(appointment)
            appointmentDao.updateAppointment(appointmentEntity)
            Log.d(TAG, "Встреча обновлена: ${appointment.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при обновлении встречи", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteAppointment(appointmentId: String): Result<Unit> {
        return try {
            appointmentDao.deleteAppointment(appointmentId)
            Log.d(TAG, "Встреча удалена: $appointmentId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при удалении встречи", e)
            Result.failure(e)
        }
    }

    override suspend fun getAppointment(appointmentId: String): Result<Appointment> {
        return try {
            val appointmentEntity = appointmentDao.getAppointment(appointmentId)
            if (appointmentEntity != null) {
                Log.d(TAG, "Встреча получена: $appointmentId")
                Result.success(mapFromEntity(appointmentEntity))
            } else {
                Log.e(TAG, "Ошибка при получении встречи: Встреча не найдена")
                Result.failure(Exception("Встреча не найдена."))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении встречи", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllAppointments(): Result<List<Appointment>> {
        return try {
            val appointmentEntities = appointmentDao.getAllAppointments().first()
            val appointments = appointmentEntities.map { mapFromEntity(it) }
            Log.d(TAG, "Получено встреч: ${appointments.size}")
            Result.success(appointments)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении всех встреч", e)
            Result.failure(e)
        }
    }
    
    override fun observeAllAppointments(): Flow<List<Appointment>> {
        return appointmentDao.getAllAppointments()
            .map { entities -> entities.map { mapFromEntity(it) } }
            .catch { e ->
                Log.e(TAG, "Ошибка при наблюдении за встречами", e)
                emitAll(flow { emit(emptyList<Appointment>()) })
            }
    }

    override suspend fun getAppointmentsByProperty(propertyId: String): Result<List<Appointment>> {
        return try {
            val appointmentEntities = appointmentDao.getAppointmentsForProperty(propertyId).first()
            val appointments = appointmentEntities.map { mapFromEntity(it) }
            Log.d(TAG, "Получено встреч для объекта $propertyId: ${appointments.size}")
            Result.success(appointments)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении встреч для объекта", e)
            Result.failure(e)
        }
    }
    
    override fun observeAppointmentsByProperty(propertyId: String): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsForProperty(propertyId)
            .map { entities -> entities.map { mapFromEntity(it) } }
            .catch { e ->
                Log.e(TAG, "Ошибка при наблюдении за встречами для объекта", e)
                emitAll(flow { emit(emptyList<Appointment>()) })
            }
    }

    override suspend fun getAppointmentsByClient(clientId: String): Result<List<Appointment>> {
        return try {
            val appointmentEntities = appointmentDao.getAppointmentsForClient(clientId).first()
            val appointments = appointmentEntities.map { mapFromEntity(it) }
            Log.d(TAG, "Получено встреч для клиента $clientId: ${appointments.size}")
            Result.success(appointments)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении встреч для клиента", e)
            Result.failure(e)
        }
    }
    
    override fun observeAppointmentsByClient(clientId: String): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsForClient(clientId)
            .map { entities -> entities.map { mapFromEntity(it) } }
            .catch { e ->
                Log.e(TAG, "Ошибка при наблюдении за встречами для клиента", e)
                emitAll(flow { emit(emptyList<Appointment>()) })
            }
    }

    override suspend fun getAppointmentsByDate(date: Long): Result<List<Appointment>> {
        return try {
            // Получаем все встречи за указанные сутки
            val calendar = Calendar.getInstance().apply {
                timeInMillis = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfDay = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val endOfDay = calendar.timeInMillis - 1
            
            val appointmentEntities = appointmentDao.getAppointmentsForDateRange(startOfDay, endOfDay).first()
            val appointments = appointmentEntities.map { mapFromEntity(it) }
            Log.d(TAG, "Получено встреч на дату $date: ${appointments.size}")
            Result.success(appointments)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении встреч на дату", e)
            Result.failure(e)
        }
    }
    
    override fun observeAppointmentsByDate(date: Long): Flow<List<Appointment>> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis - 1
        
        return appointmentDao.getAppointmentsForDateRange(startOfDay, endOfDay)
            .map { entities -> entities.map { mapFromEntity(it) } }
            .catch { e ->
                Log.e(TAG, "Ошибка при наблюдении за встречами на дату", e)
                emitAll(flow { emit(emptyList<Appointment>()) })
            }
    }

    override suspend fun getAppointmentsByDateRange(
        startDate: Long,
        endDate: Long
    ): Result<List<Appointment>> {
        return try {
            val appointmentEntities = appointmentDao.getAppointmentsForDateRange(startDate, endDate).first()
            val appointments = appointmentEntities.map { mapFromEntity(it) }
            Log.d(TAG, "Получено встреч за период: ${appointments.size}")
            Result.success(appointments)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении встреч за период", e)
            Result.failure(e)
        }
    }
    
    override fun observeAppointmentsByDateRange(startDate: Long, endDate: Long): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsForDateRange(startDate, endDate)
            .map { entities -> entities.map { mapFromEntity(it) } }
            .catch { e ->
                Log.e(TAG, "Ошибка при наблюдении за встречами за период", e)
                emitAll(flow { emit(emptyList()) })
            }
    }
    
    override suspend fun getAppointmentsByStatus(status: AppointmentStatus): Result<List<Appointment>> {
        return try {
            val appointmentEntities = appointmentDao.getAppointmentsByStatus(status).first()
            val appointments = appointmentEntities.map { mapFromEntity(it) }
            Log.d(TAG, "Получено встреч со статусом $status: ${appointments.size}")
            Result.success(appointments)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении встреч по статусу", e)
            Result.failure(e)
        }
    }
    
    override fun observeAppointmentsByStatus(status: AppointmentStatus): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsByStatus(status)
            .map { entities -> entities.map { mapFromEntity(it) } }
            .catch { e ->
                Log.e(TAG, "Ошибка при наблюдении за встречами по статусу", e)
                emitAll(flow { emit(emptyList()) })
            }
    }
    
    override suspend fun getAppointmentsByType(type: AppointmentType): Result<List<Appointment>> {
        return try {
            val appointmentEntities = appointmentDao.getAppointmentsByType(type).first()
            val appointments = appointmentEntities.map { mapFromEntity(it) }
            Log.d(TAG, "Получено встреч типа $type: ${appointments.size}")
            Result.success(appointments)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении встреч по типу", e)
            Result.failure(e)
        }
    }
    
    override fun observeAppointmentsByType(type: AppointmentType): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsByType(type)
            .map { entities -> entities.map { mapFromEntity(it) } }
            .catch { e ->
                Log.e(TAG, "Ошибка при наблюдении за встречами по типу", e)
                emitAll(flow { emit(emptyList()) })
            }
    }
    
    override suspend fun getAppointmentsCountForDay(date: Long): Result<Int> {
        return try {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfDay = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val endOfDay = calendar.timeInMillis - 1
            
            val count = appointmentDao.getAppointmentsCountForDay(startOfDay, endOfDay)
            Log.d(TAG, "Количество встреч на дату $date: $count")
            Result.success(count)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении количества встреч на дату", e)
            Result.failure(e)
        }
    }
    
    override suspend fun getOverlappingAppointments(
        startTime: Long,
        endTime: Long,
        excludeId: String
    ): Result<List<Appointment>> {
        return try {
            val appointmentEntities = appointmentDao.getOverlappingAppointments(startTime, endTime, excludeId)
            val appointments = appointmentEntities.map { mapFromEntity(it) }
            Log.d(TAG, "Найдено пересекающихся встреч: ${appointments.size}")
            Result.success(appointments)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при поиске пересекающихся встреч", e)
            Result.failure(e)
        }
    }
    
    override fun searchAppointments(query: String): Flow<List<Appointment>> {
        return appointmentDao.searchAppointments(query)
            .map { entities -> entities.map { mapFromEntity(it) } }
            .catch { e ->
                Log.e(TAG, "Ошибка при поиске встреч", e)
                emitAll(flow { emit(emptyList()) })
            }
    }
    
    override fun observeUpcomingAppointments(): Flow<List<Appointment>> {
        val currentTime = System.currentTimeMillis()
        return appointmentDao.getUpcomingAppointments(currentTime)
            .map { entities -> entities.map { mapFromEntity(it) } }
            .catch { e ->
                Log.e(TAG, "Ошибка при наблюдении за предстоящими встречами", e)
                emit(emptyList())
            }
    }
    
    override fun observeAppointmentsForMonth(year: Int, month: Int): Flow<List<Appointment>> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1) // Calendar.MONTH начинается с 0
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfMonth = calendar.timeInMillis
        
        calendar.add(Calendar.MONTH, 1)
        val endOfMonth = calendar.timeInMillis - 1
        
        return appointmentDao.getAppointmentsForMonth(startOfMonth, endOfMonth)
            .map { entities -> entities.map { mapFromEntity(it) } }
            .catch { e ->
                Log.e(TAG, "Ошибка при наблюдении за встречами за месяц", e)
                emit(emptyList())
            }
    }
    
    override fun observeAppointmentsForWeek(startOfWeek: Long, endOfWeek: Long): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsForWeek(startOfWeek, endOfWeek)
            .map { entities -> entities.map { mapFromEntity(it) } }
            .catch { e ->
                Log.e(TAG, "Ошибка при наблюдении за встречами за неделю", e)
                emit(emptyList())
            }
    }

    private fun mapToEntity(appointment: Appointment): AppointmentEntity {
        return AppointmentEntity(
            id = appointment.id,
            clientId = appointment.clientId,
            propertyId = appointment.propertyId,
            title = appointment.title,
            description = appointment.description,
            startTime = appointment.startTime,
            endTime = appointment.endTime,
            status = appointment.status,
            type = appointment.type,
            notes = appointment.notes,
            reminderTime = appointment.reminderTime,
            location = appointment.location,
            isAllDay = appointment.isAllDay,
            isRecurring = appointment.isRecurring,
            recurrenceRule = appointment.recurrenceRule,
            color = appointment.color,
            attachments = appointment.attachments,
            participants = appointment.participants,
            clientName = appointment.clientName,
            propertyAddress = appointment.propertyAddress,
            createdAt = appointment.createdAt,
            updatedAt = appointment.updatedAt,
            isSynced = false
        )
    }

    private fun mapFromEntity(entity: AppointmentEntity): Appointment {
        return Appointment(
            id = entity.id,
            clientId = entity.clientId,
            propertyId = entity.propertyId,
            title = entity.title,
            description = entity.description,
            startTime = entity.startTime,
            endTime = entity.endTime,
            status = entity.status,
            type = entity.type,
            notes = entity.notes,
            reminderTime = entity.reminderTime,
            location = entity.location,
            isAllDay = entity.isAllDay,
            isRecurring = entity.isRecurring,
            recurrenceRule = entity.recurrenceRule,
            color = entity.color,
            attachments = entity.attachments,
            participants = entity.participants,
            clientName = entity.clientName,
            propertyAddress = entity.propertyAddress,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
} 