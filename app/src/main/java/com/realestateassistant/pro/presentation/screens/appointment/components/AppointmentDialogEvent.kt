package com.realestateassistant.pro.presentation.screens.appointment.components

import java.time.LocalDate
import java.time.LocalTime

/**
 * События диалога встречи
 */
sealed class AppointmentDialogEvent {
    // Основные поля
    data class SetTitle(val title: String) : AppointmentDialogEvent()
    data class SetDescription(val description: String) : AppointmentDialogEvent()
    data class SetNotes(val notes: String) : AppointmentDialogEvent()
    data class SetLocation(val location: String) : AppointmentDialogEvent()
    
    // Клиент и объект
    data class SetClient(val id: String, val name: String) : AppointmentDialogEvent()
    data class SetProperty(val id: String, val address: String) : AppointmentDialogEvent()
    data class ShowClientSelector(val show: Boolean) : AppointmentDialogEvent()
    data class ShowPropertySelector(val show: Boolean) : AppointmentDialogEvent()
    
    // Дата и время
    data class SetStartDate(val date: LocalDate) : AppointmentDialogEvent()
    data class SetEndDate(val date: LocalDate) : AppointmentDialogEvent()
    data class SetStartTime(val time: LocalTime) : AppointmentDialogEvent()
    data class SetEndTime(val time: LocalTime) : AppointmentDialogEvent()
    data class SetIsAllDay(val isAllDay: Boolean) : AppointmentDialogEvent()
    data class ShowDatePicker(val show: Boolean, val isStartDate: Boolean) : AppointmentDialogEvent()
    data class ShowTimePicker(val show: Boolean, val isStartTime: Boolean) : AppointmentDialogEvent()
    
    // Тип и статус
    data class SetType(val type: String) : AppointmentDialogEvent()
    data class SetStatus(val status: String) : AppointmentDialogEvent()
    
    // Действия
    data object SaveAppointment : AppointmentDialogEvent()
    data object CancelAppointment : AppointmentDialogEvent()
} 