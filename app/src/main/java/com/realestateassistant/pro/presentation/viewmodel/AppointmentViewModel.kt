package com.realestateassistant.pro.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realestateassistant.pro.domain.model.Appointment
import com.realestateassistant.pro.domain.usecase.AppointmentUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val appointmentUseCases: AppointmentUseCases
) : ViewModel() {

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> get() = _appointments

    init {
        loadAllAppointments()
    }

    fun loadAllAppointments() {
        viewModelScope.launch {
            appointmentUseCases.getAllAppointments().onSuccess { list ->
                _appointments.value = list
            }.onFailure { exception ->
                // Обработка ошибки, например, вывод сообщения пользователю
            }
        }
    }

    fun createAppointment(appointment: Appointment) {
        viewModelScope.launch {
            appointmentUseCases.createAppointment(appointment).onSuccess { createdAppointment ->
                _appointments.value = _appointments.value + createdAppointment
            }.onFailure { exception ->
                // Обработка ошибки
            }
        }
    }

    fun updateAppointment(appointment: Appointment) {
        viewModelScope.launch {
            appointmentUseCases.updateAppointment(appointment).onSuccess {
                // После обновления можно перезагрузить список
                loadAllAppointments()
            }.onFailure { exception ->
                // Обработка ошибки
            }
        }
    }

    fun deleteAppointment(appointmentId: String) {
        viewModelScope.launch {
            appointmentUseCases.deleteAppointment(appointmentId).onSuccess {
                _appointments.value = _appointments.value.filter { it.id != appointmentId }
            }.onFailure { exception ->
                // Обработка ошибки
            }
        }
    }

    fun loadAppointmentsByProperty(propertyId: String) {
        viewModelScope.launch {
            appointmentUseCases.getAppointmentsByProperty(propertyId).onSuccess { list ->
                _appointments.value = list
            }.onFailure { exception ->
                // Обработка ошибки
            }
        }
    }

    fun loadAppointmentsByClient(clientId: String) {
        viewModelScope.launch {
            appointmentUseCases.getAppointmentsByClient(clientId).onSuccess { list ->
                _appointments.value = list
            }.onFailure { exception ->
                // Обработка ошибки
            }
        }
    }

    fun loadAppointmentsByDate(date: Long) {
        viewModelScope.launch {
            appointmentUseCases.getAppointmentsByDate(date).onSuccess { list ->
                _appointments.value = list
            }.onFailure { exception ->
                // Обработка ошибки
            }
        }
    }

    fun loadAppointmentsByDateRange(startDate: Long, endDate: Long) {
        viewModelScope.launch {
            appointmentUseCases.getAppointmentsByDateRange(startDate, endDate).onSuccess { list ->
                _appointments.value = list
            }.onFailure { exception ->
                // Обработка ошибки
            }
        }
    }
} 