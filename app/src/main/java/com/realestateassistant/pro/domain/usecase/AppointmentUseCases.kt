package com.realestateassistant.pro.domain.usecase

import com.realestateassistant.pro.domain.model.Appointment
import com.realestateassistant.pro.domain.model.AppointmentStatus
import com.realestateassistant.pro.domain.model.AppointmentType
import com.realestateassistant.pro.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateAppointment @Inject constructor(private val repository: AppointmentRepository) {
    suspend operator fun invoke(appointment: Appointment): Result<Appointment> {
        return repository.createAppointment(appointment)
    }
}

class UpdateAppointment @Inject constructor(private val repository: AppointmentRepository) {
    suspend operator fun invoke(appointment: Appointment): Result<Unit> {
        return repository.updateAppointment(appointment)
    }
}

class DeleteAppointment @Inject constructor(private val repository: AppointmentRepository) {
    suspend operator fun invoke(appointmentId: String): Result<Unit> {
        return repository.deleteAppointment(appointmentId)
    }
}

class GetAppointment @Inject constructor(private val repository: AppointmentRepository) {
    suspend operator fun invoke(appointmentId: String): Result<Appointment> {
        return repository.getAppointment(appointmentId)
    }
}

class GetAllAppointments @Inject constructor(private val repository: AppointmentRepository) {
    suspend operator fun invoke(): Result<List<Appointment>> {
        return repository.getAllAppointments()
    }
}

class ObserveAllAppointments @Inject constructor(private val repository: AppointmentRepository) {
    operator fun invoke(): Flow<List<Appointment>> {
        return repository.observeAllAppointments()
    }
}

class GetAppointmentsByProperty @Inject constructor(private val repository: AppointmentRepository) {
    suspend operator fun invoke(propertyId: String): Result<List<Appointment>> {
        return repository.getAppointmentsByProperty(propertyId)
    }
}

class ObserveAppointmentsByProperty @Inject constructor(private val repository: AppointmentRepository) {
    operator fun invoke(propertyId: String): Flow<List<Appointment>> {
        return repository.observeAppointmentsByProperty(propertyId)
    }
}

class GetAppointmentsByClient @Inject constructor(private val repository: AppointmentRepository) {
    suspend operator fun invoke(clientId: String): Result<List<Appointment>> {
        return repository.getAppointmentsByClient(clientId)
    }
}

class ObserveAppointmentsByClient @Inject constructor(private val repository: AppointmentRepository) {
    operator fun invoke(clientId: String): Flow<List<Appointment>> {
        return repository.observeAppointmentsByClient(clientId)
    }
}

class GetAppointmentsByDate @Inject constructor(private val repository: AppointmentRepository) {
    suspend operator fun invoke(date: Long): Result<List<Appointment>> {
        return repository.getAppointmentsByDate(date)
    }
}

class ObserveAppointmentsByDate @Inject constructor(private val repository: AppointmentRepository) {
    operator fun invoke(date: Long): Flow<List<Appointment>> {
        return repository.observeAppointmentsByDate(date)
    }
}

class GetAppointmentsByDateRange @Inject constructor(private val repository: AppointmentRepository) {
    suspend operator fun invoke(startDate: Long, endDate: Long): Result<List<Appointment>> {
        return repository.getAppointmentsByDateRange(startDate, endDate)
    }
}

class ObserveAppointmentsByDateRange @Inject constructor(private val repository: AppointmentRepository) {
    operator fun invoke(startDate: Long, endDate: Long): Flow<List<Appointment>> {
        return repository.observeAppointmentsByDateRange(startDate, endDate)
    }
}

class GetAppointmentsByStatus @Inject constructor(private val repository: AppointmentRepository) {
    suspend operator fun invoke(status: AppointmentStatus): Result<List<Appointment>> {
        return repository.getAppointmentsByStatus(status)
    }
}

class ObserveAppointmentsByStatus @Inject constructor(private val repository: AppointmentRepository) {
    operator fun invoke(status: AppointmentStatus): Flow<List<Appointment>> {
        return repository.observeAppointmentsByStatus(status)
    }
}

class GetAppointmentsByType @Inject constructor(private val repository: AppointmentRepository) {
    suspend operator fun invoke(type: AppointmentType): Result<List<Appointment>> {
        return repository.getAppointmentsByType(type)
    }
}

class ObserveAppointmentsByType @Inject constructor(private val repository: AppointmentRepository) {
    operator fun invoke(type: AppointmentType): Flow<List<Appointment>> {
        return repository.observeAppointmentsByType(type)
    }
}

class GetAppointmentsCountForDay @Inject constructor(private val repository: AppointmentRepository) {
    suspend operator fun invoke(date: Long): Result<Int> {
        return repository.getAppointmentsCountForDay(date)
    }
}

class GetOverlappingAppointments @Inject constructor(private val repository: AppointmentRepository) {
    suspend operator fun invoke(startTime: Long, endTime: Long, excludeId: String = ""): Result<List<Appointment>> {
        return repository.getOverlappingAppointments(startTime, endTime, excludeId)
    }
}

class SearchAppointments @Inject constructor(private val repository: AppointmentRepository) {
    operator fun invoke(query: String): Flow<List<Appointment>> {
        return repository.searchAppointments(query)
    }
}

class ObserveUpcomingAppointments @Inject constructor(private val repository: AppointmentRepository) {
    operator fun invoke(): Flow<List<Appointment>> {
        return repository.observeUpcomingAppointments()
    }
}

class ObserveAppointmentsForMonth @Inject constructor(private val repository: AppointmentRepository) {
    operator fun invoke(year: Int, month: Int): Flow<List<Appointment>> {
        return repository.observeAppointmentsForMonth(year, month)
    }
}

class ObserveAppointmentsForWeek @Inject constructor(private val repository: AppointmentRepository) {
    operator fun invoke(startOfWeek: Long, endOfWeek: Long): Flow<List<Appointment>> {
        return repository.observeAppointmentsForWeek(startOfWeek, endOfWeek)
    }
}

// Собираем все use case в один контейнер для удобства
data class AppointmentUseCases @Inject constructor(
    val createAppointment: CreateAppointment,
    val updateAppointment: UpdateAppointment,
    val deleteAppointment: DeleteAppointment,
    val getAppointment: GetAppointment,
    val getAllAppointments: GetAllAppointments,
    val observeAllAppointments: ObserveAllAppointments,
    val getAppointmentsByProperty: GetAppointmentsByProperty,
    val observeAppointmentsByProperty: ObserveAppointmentsByProperty,
    val getAppointmentsByClient: GetAppointmentsByClient,
    val observeAppointmentsByClient: ObserveAppointmentsByClient,
    val getAppointmentsByDate: GetAppointmentsByDate,
    val observeAppointmentsByDate: ObserveAppointmentsByDate,
    val getAppointmentsByDateRange: GetAppointmentsByDateRange,
    val observeAppointmentsByDateRange: ObserveAppointmentsByDateRange,
    val getAppointmentsByStatus: GetAppointmentsByStatus,
    val observeAppointmentsByStatus: ObserveAppointmentsByStatus,
    val getAppointmentsByType: GetAppointmentsByType,
    val observeAppointmentsByType: ObserveAppointmentsByType,
    val getAppointmentsCountForDay: GetAppointmentsCountForDay,
    val getOverlappingAppointments: GetOverlappingAppointments,
    val searchAppointments: SearchAppointments,
    val observeUpcomingAppointments: ObserveUpcomingAppointments,
    val observeAppointmentsForMonth: ObserveAppointmentsForMonth,
    val observeAppointmentsForWeek: ObserveAppointmentsForWeek
) 