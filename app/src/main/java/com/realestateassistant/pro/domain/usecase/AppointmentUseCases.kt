package com.realestateassistant.pro.domain.usecase

import com.realestateassistant.pro.domain.model.Appointment
import com.realestateassistant.pro.domain.repository.AppointmentRepository

class CreateAppointment(private val repository: AppointmentRepository) {
    suspend operator fun invoke(appointment: Appointment): Result<Appointment> {
        return repository.createAppointment(appointment)
    }
}

class UpdateAppointment(private val repository: AppointmentRepository) {
    suspend operator fun invoke(appointment: Appointment): Result<Unit> {
        return repository.updateAppointment(appointment)
    }
}

class DeleteAppointment(private val repository: AppointmentRepository) {
    suspend operator fun invoke(appointmentId: String): Result<Unit> {
        return repository.deleteAppointment(appointmentId)
    }
}

class GetAppointment(private val repository: AppointmentRepository) {
    suspend operator fun invoke(appointmentId: String): Result<Appointment> {
        return repository.getAppointment(appointmentId)
    }
}

class GetAllAppointments(private val repository: AppointmentRepository) {
    suspend operator fun invoke(): Result<List<Appointment>> {
        return repository.getAllAppointments()
    }
}

class GetAppointmentsByProperty(private val repository: AppointmentRepository) {
    suspend operator fun invoke(propertyId: String): Result<List<Appointment>> {
        return repository.getAppointmentsByProperty(propertyId)
    }
}

class GetAppointmentsByClient(private val repository: AppointmentRepository) {
    suspend operator fun invoke(clientId: String): Result<List<Appointment>> {
        return repository.getAppointmentsByClient(clientId)
    }
}

class GetAppointmentsByDate(private val repository: AppointmentRepository) {
    suspend operator fun invoke(date: Long): Result<List<Appointment>> {
        return repository.getAppointmentsByDate(date)
    }
}

class GetAppointmentsByDateRange(private val repository: AppointmentRepository) {
    suspend operator fun invoke(startDate: Long, endDate: Long): Result<List<Appointment>> {
        return repository.getAppointmentsByDateRange(startDate, endDate)
    }
}

// Собираем все use case в один контейнер для удобства
data class AppointmentUseCases(
    val createAppointment: CreateAppointment,
    val updateAppointment: UpdateAppointment,
    val deleteAppointment: DeleteAppointment,
    val getAppointment: GetAppointment,
    val getAllAppointments: GetAllAppointments,
    val getAppointmentsByProperty: GetAppointmentsByProperty,
    val getAppointmentsByClient: GetAppointmentsByClient,
    val getAppointmentsByDate: GetAppointmentsByDate,
    val getAppointmentsByDateRange: GetAppointmentsByDateRange
) 