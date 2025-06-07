package com.realestateassistant.pro.presentation.screens.appointment

import com.realestateassistant.pro.domain.model.Appointment
import com.realestateassistant.pro.presentation.screens.appointment.components.AppointmentDialogEvent
import com.realestateassistant.pro.presentation.screens.appointment.models.AppointmentFormState
import com.realestateassistant.pro.presentation.screens.appointment.models.CalendarView
import java.time.LocalDate

/**
 * События экрана встреч
 */
sealed class AppointmentEvent {
    // События навигации
    data class ChangeCalendarView(val view: CalendarView) : AppointmentEvent()
    data object NextDay : AppointmentEvent()
    data object PreviousDay : AppointmentEvent()
    data object NextWeek : AppointmentEvent()
    data object PreviousWeek : AppointmentEvent()
    data object NextMonth : AppointmentEvent()
    data object PreviousMonth : AppointmentEvent()
    data class SelectDate(val date: LocalDate) : AppointmentEvent()
    
    // События диалога
    data class ShowAppointmentDialog(
        val show: Boolean, 
        val isEditMode: Boolean = false, 
        val formState: AppointmentFormState? = null
    ) : AppointmentEvent()
    
    data class OnDialogEvent(val event: AppointmentDialogEvent) : AppointmentEvent()
    data object SaveAppointment : AppointmentEvent()
    data class DeleteAppointment(val id: String) : AppointmentEvent()
    
    // События фильтрации
    data class FilterByClient(val clientId: String?) : AppointmentEvent()
    data class FilterByProperty(val propertyId: String?) : AppointmentEvent()
    data class FilterByType(val type: String?) : AppointmentEvent()
    data class FilterByStatus(val status: String?) : AppointmentEvent()
    data class SearchAppointments(val query: String) : AppointmentEvent()
    data object ClearFilters : AppointmentEvent()
    
    // События обработки ошибок
    data class Error(val message: String) : AppointmentEvent()
    data object ClearError : AppointmentEvent()
} 