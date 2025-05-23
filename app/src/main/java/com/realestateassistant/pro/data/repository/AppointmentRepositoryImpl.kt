package com.realestateassistant.pro.data.repository

import com.realestateassistant.pro.data.local.dao.AppointmentDao
import com.realestateassistant.pro.data.local.entity.AppointmentEntity
import com.realestateassistant.pro.domain.model.Appointment
import com.realestateassistant.pro.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

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
            Result.success(updatedAppointment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAppointment(appointment: Appointment): Result<Unit> {
        return try {
            if (appointment.id.isEmpty()) {
                return Result.failure(Exception("ID встречи пустой."))
            }
            val appointmentEntity = mapToEntity(appointment)
            appointmentDao.updateAppointment(appointmentEntity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAppointment(appointmentId: String): Result<Unit> {
        return try {
            appointmentDao.deleteAppointment(appointmentId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAppointment(appointmentId: String): Result<Appointment> {
        return try {
            val appointmentEntity = appointmentDao.getAppointment(appointmentId)
            if (appointmentEntity != null) {
                Result.success(mapFromEntity(appointmentEntity))
            } else {
                Result.failure(Exception("Встреча не найдена."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllAppointments(): Result<List<Appointment>> {
        return try {
            val appointmentEntities = appointmentDao.getAllAppointments().first()
            val appointments = appointmentEntities.map { mapFromEntity(it) }
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAppointmentsByProperty(propertyId: String): Result<List<Appointment>> {
        return try {
            val appointmentEntities = appointmentDao.getAppointmentsForProperty(propertyId).first()
            val appointments = appointmentEntities.map { mapFromEntity(it) }
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAppointmentsByClient(clientId: String): Result<List<Appointment>> {
        return try {
            val appointmentEntities = appointmentDao.getAppointmentsForClient(clientId).first()
            val appointments = appointmentEntities.map { mapFromEntity(it) }
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAppointmentsByDate(date: Long): Result<List<Appointment>> {
        return try {
            // Получаем все встречи за указанные сутки
            val endDate = date + 24 * 60 * 60 * 1000 // + 24 часа в миллисекундах
            val appointmentEntities = appointmentDao.getAppointmentsForDateRange(date, endDate).first()
            val appointments = appointmentEntities.map { mapFromEntity(it) }
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAppointmentsByDateRange(
        startDate: Long, 
        endDate: Long
    ): Result<List<Appointment>> {
        return try {
            val appointmentEntities = appointmentDao.getAppointmentsForDateRange(startDate, endDate).first()
            val appointments = appointmentEntities.map { mapFromEntity(it) }
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapToEntity(appointment: Appointment): AppointmentEntity {
        return AppointmentEntity(
            id = appointment.id,
            clientId = appointment.clientId,
            propertyId = appointment.propertyId,
            agentId = appointment.agentId,
            appointmentTime = appointment.appointmentTime,
            duration = appointment.duration,
            status = appointment.status,
            type = appointment.type,
            notes = appointment.notes,
            reminderTime = appointment.reminderTime,
            location = appointment.location,
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
            agentId = entity.agentId,
            appointmentTime = entity.appointmentTime,
            duration = entity.duration,
            status = entity.status,
            type = entity.type,
            notes = entity.notes,
            reminderTime = entity.reminderTime,
            location = entity.location,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
} 