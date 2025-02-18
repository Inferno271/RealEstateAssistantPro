package com.realestateassistant.pro.data.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.realestateassistant.pro.data.remote.FirebaseDatabaseManager
import com.realestateassistant.pro.domain.model.Appointment
import com.realestateassistant.pro.domain.repository.AppointmentRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentRepositoryImpl @Inject constructor(
    private val firebaseDatabaseManager: FirebaseDatabaseManager
) : AppointmentRepository {

    private val appointmentsRef: DatabaseReference
        get() = firebaseDatabaseManager.getAppointmentsReference()

    override suspend fun createAppointment(appointment: Appointment): Result<Appointment> {
        return try {
            val newRef = appointmentsRef.push()
            val newId = newRef.key
            if (newId == null) {
                return Result.failure(Exception("Не удалось сгенерировать новый ключ для встречи."))
            }
            val updatedAppointment = appointment.copy(id = newId)
            newRef.setValue(updatedAppointment).await()
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
            appointmentsRef.child(appointment.id).setValue(appointment).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAppointment(appointmentId: String): Result<Unit> {
        return try {
            appointmentsRef.child(appointmentId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAppointment(appointmentId: String): Result<Appointment> {
        return try {
            val snapshot = appointmentsRef.child(appointmentId).get().await()
            val appointment = snapshot.getValue(Appointment::class.java)
            if (appointment != null) {
                Result.success(appointment)
            } else {
                Result.failure(Exception("Встреча не найдена."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllAppointments(): Result<List<Appointment>> {
        return try {
            val snapshot = appointmentsRef.get().await()
            val appointmentList = mutableListOf<Appointment>()
            snapshot.children.forEach { child ->
                val appointment = child.getValue(Appointment::class.java)
                if (appointment != null) {
                    appointmentList.add(appointment)
                }
            }
            Result.success(appointmentList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAppointmentsByProperty(propertyId: String): Result<List<Appointment>> {
        return try {
            val query: Query = appointmentsRef.orderByChild("propertyId").equalTo(propertyId)
            val snapshot = query.get().await()
            val appointmentList = mutableListOf<Appointment>()
            snapshot.children.forEach { child ->
                val appointment = child.getValue(Appointment::class.java)
                if (appointment != null) {
                    appointmentList.add(appointment)
                }
            }
            Result.success(appointmentList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAppointmentsByClient(clientId: String): Result<List<Appointment>> {
        return try {
            val query: Query = appointmentsRef.orderByChild("clientId").equalTo(clientId)
            val snapshot = query.get().await()
            val appointmentList = mutableListOf<Appointment>()
            snapshot.children.forEach { child ->
                val appointment = child.getValue(Appointment::class.java)
                if (appointment != null) {
                    appointmentList.add(appointment)
                }
            }
            Result.success(appointmentList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAppointmentsByDate(date: Long): Result<List<Appointment>> {
        return try {
            // Получаем все встречи за указанные сутки
            // Предполагаем, что date - это начало дня (00:00:00)
            val endDate = date + 24 * 60 * 60 * 1000 // + 24 часа в миллисекундах
            val query: Query = appointmentsRef
                .orderByChild("appointmentTime")
                .startAt(date.toDouble())
                .endAt(endDate.toDouble())
            
            val snapshot = query.get().await()
            val appointmentList = mutableListOf<Appointment>()
            snapshot.children.forEach { child ->
                val appointment = child.getValue(Appointment::class.java)
                if (appointment != null) {
                    appointmentList.add(appointment)
                }
            }
            Result.success(appointmentList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAppointmentsByDateRange(
        startDate: Long,
        endDate: Long
    ): Result<List<Appointment>> {
        return try {
            val query: Query = appointmentsRef
                .orderByChild("appointmentTime")
                .startAt(startDate.toDouble())
                .endAt(endDate.toDouble())
            
            val snapshot = query.get().await()
            val appointmentList = mutableListOf<Appointment>()
            snapshot.children.forEach { child ->
                val appointment = child.getValue(Appointment::class.java)
                if (appointment != null) {
                    appointmentList.add(appointment)
                }
            }
            Result.success(appointmentList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 