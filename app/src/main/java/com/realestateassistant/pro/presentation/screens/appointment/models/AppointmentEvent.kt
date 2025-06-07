package com.realestateassistant.pro.presentation.screens.appointment.models

import com.realestateassistant.pro.domain.model.Appointment
import com.realestateassistant.pro.domain.model.AppointmentStatus
import com.realestateassistant.pro.domain.model.AppointmentType
import com.realestateassistant.pro.domain.model.Participant
import com.realestateassistant.pro.domain.model.RecurrenceRule
import com.realestateassistant.pro.presentation.screens.appointment.components.AppointmentDialogEvent
import java.time.LocalDate

/**
 * События для ViewModel встреч
 */
sealed class AppointmentEvent {
    // События формы
    data class SetTitle(val title: String) : AppointmentEvent()
    data class SetDescription(val description: String) : AppointmentEvent()
    data class SetProperty(val propertyId: String) : AppointmentEvent()
    data class SetClient(val clientId: String) : AppointmentEvent()
    data class SetAgent(val agentId: String) : AppointmentEvent()
    data class SetStartDate(val date: Long) : AppointmentEvent()
    data class SetStartTime(val time: Long) : AppointmentEvent()
    data class SetEndDate(val date: Long) : AppointmentEvent()
    data class SetEndTime(val time: Long) : AppointmentEvent()
    data class SetStatus(val status: AppointmentStatus) : AppointmentEvent()
    data class SetType(val type: AppointmentType) : AppointmentEvent()
    data class SetLocation(val location: String) : AppointmentEvent()
    data class SetNotes(val notes: String) : AppointmentEvent()
    data class SetReminderTime(val time: Long?) : AppointmentEvent()
    data class SetAllDay(val isAllDay: Boolean) : AppointmentEvent()
    data class SetRecurring(val isRecurring: Boolean) : AppointmentEvent()
    data class SetRecurrenceRule(val rule: RecurrenceRule?) : AppointmentEvent()
    data class SetColor(val color: String) : AppointmentEvent()
    data class AddAttachment(val attachment: String) : AppointmentEvent()
    data class RemoveAttachment(val attachment: String) : AppointmentEvent()
    data class AddParticipant(val participant: Participant) : AppointmentEvent()
    data class RemoveParticipant(val participant: Participant) : AppointmentEvent()
    data class UpdateParticipant(val oldParticipant: Participant, val newParticipant: Participant) : AppointmentEvent()
    
    // События управления диалогами
    object ShowCreateDialog : AppointmentEvent()
    data class ShowEditDialog(val id: String) : AppointmentEvent()
    data class ShowAppointmentDialog(val show: Boolean, val isEditMode: Boolean = false, val formState: AppointmentFormState? = null) : AppointmentEvent()
    data class ShowPropertySelector(val show: Boolean) : AppointmentEvent()
    data class ShowClientSelector(val show: Boolean) : AppointmentEvent()
    data class ShowDatePicker(val show: Boolean, val isStartDate: Boolean = true) : AppointmentEvent()
    data class ShowTimePicker(val show: Boolean, val isStartTime: Boolean = true) : AppointmentEvent()
    data class ShowColorPicker(val show: Boolean) : AppointmentEvent()
    data class ShowRecurrenceDialog(val show: Boolean) : AppointmentEvent()
    data class ShowReminderDialog(val show: Boolean) : AppointmentEvent()
    data class ShowAttachmentPicker(val show: Boolean) : AppointmentEvent()
    data class ShowParticipantDialog(val show: Boolean) : AppointmentEvent()
    
    // Событие для передачи событий диалога во ViewModel
    data class OnDialogEvent(val event: AppointmentDialogEvent) : AppointmentEvent()
    
    // События управления встречами
    data object SaveAppointment : AppointmentEvent()
    data object CancelEdit : AppointmentEvent()
    data class LoadAppointment(val id: String) : AppointmentEvent()
    data class LoadAppointmentForView(val id: String) : AppointmentEvent()
    data class DeleteAppointment(val id: String) : AppointmentEvent()
    data class LoadAppointmentsForDate(val date: LocalDate) : AppointmentEvent()
    data class LoadAppointmentsForMonth(val year: Int, val month: Int) : AppointmentEvent()
    data class LoadAppointmentsForWeek(val date: LocalDate) : AppointmentEvent()
    data class UpdateAppointmentStatus(val id: String, val status: AppointmentStatus) : AppointmentEvent()
    data class UpdateAppointmentType(val id: String, val type: AppointmentType) : AppointmentEvent()
    data class DuplicateAppointment(val appointment: Appointment) : AppointmentEvent()
    
    // События календаря
    data class SelectDate(val date: LocalDate) : AppointmentEvent()
    data class ChangeCalendarView(val view: CalendarView) : AppointmentEvent()
    data class JumpToDate(val date: LocalDate) : AppointmentEvent()
    object NavigateToPrevious : AppointmentEvent()
    object NavigateToNext : AppointmentEvent()
    object NavigateToPreviousMonth : AppointmentEvent()
    object NavigateToNextMonth : AppointmentEvent()
    object NavigateToPreviousWeek : AppointmentEvent()
    object NavigateToNextWeek : AppointmentEvent()
    object ClearError : AppointmentEvent()
    
    // События фильтрации
    data class FilterByClient(val clientId: String?) : AppointmentEvent()
    data class FilterByProperty(val propertyId: String?) : AppointmentEvent()
    data class FilterByStatus(val status: String?) : AppointmentEvent()
    data class FilterByType(val type: String?) : AppointmentEvent()
    data class FilterByDateRange(val startDate: Long, val endDate: Long) : AppointmentEvent()
    data class SearchAppointments(val query: String) : AppointmentEvent()
    object ClearFilters : AppointmentEvent()
    
    // Событие для отображения ошибок
    data class Error(val message: String) : AppointmentEvent()
    
    // События для загрузки связанных данных
    data class LoadClient(val id: String) : AppointmentEvent()
    data class LoadProperty(val id: String) : AppointmentEvent()
    object LoadClients : AppointmentEvent()
    object LoadProperties : AppointmentEvent()
    
    // События для управления фильтрами
    data class ShowFilterDialog(val show: Boolean) : AppointmentEvent()
    data class ApplyFilters(
        val clientId: String? = null,
        val propertyId: String? = null,
        val status: String? = null,
        val type: String? = null,
        val searchQuery: String = ""
    ) : AppointmentEvent()
} 