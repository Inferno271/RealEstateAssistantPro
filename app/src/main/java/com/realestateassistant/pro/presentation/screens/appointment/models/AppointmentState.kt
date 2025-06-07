package com.realestateassistant.pro.presentation.screens.appointment.models

import com.realestateassistant.pro.domain.model.Appointment
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.model.Property
import java.time.LocalDate
import com.realestateassistant.pro.presentation.screens.appointment.models.CalendarView

/**
 * Состояние экрана встреч
 *
 * @param isLoading флаг загрузки
 * @param error сообщение об ошибке
 * @param appointments список всех встреч для текущего представления
 * @param allAppointments список всех встреч
 * @param appointmentsForSelectedDate список встреч для выбранной даты
 * @param availableClients список доступных клиентов
 * @param availableProperties список доступных объектов недвижимости
 * @param selectedDate выбранная дата
 * @param selectedWeek выбранная неделя (первый день недели)
 * @param selectedMonth выбранный месяц (пара год-месяц)
 * @param calendarView текущее представление календаря
 * @param showAppointmentDialog флаг отображения диалога встречи
 * @param isEditMode режим редактирования встречи
 * @param formState состояние формы встречи
 * @param appointmentCountByDate количество встреч по датам
 * @param isFiltered флаг фильтрации
 * @param filterClientId ID клиента для фильтрации
 * @param filterPropertyId ID объекта для фильтрации
 * @param filterStatus статус для фильтрации
 * @param filterType тип для фильтрации
 * @param searchQuery текст поиска
 * @param showFilterDialog флаг отображения диалога фильтров
 * @param saveSuccess флаг успешного сохранения встречи
 */
data class AppointmentState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val appointments: List<Appointment> = emptyList(),
    val allAppointments: List<Appointment> = emptyList(),
    val appointmentsForSelectedDate: List<Appointment> = emptyList(),
    val availableClients: List<Client> = emptyList(),
    val availableProperties: List<Property> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedWeek: LocalDate = LocalDate.now(),
    val selectedMonth: Pair<Int, Int> = LocalDate.now().year to LocalDate.now().monthValue,
    val calendarView: CalendarView = CalendarView.MONTH,
    val showAppointmentDialog: Boolean = false,
    val isEditMode: Boolean = false,
    val formState: AppointmentFormState = AppointmentFormState(),
    val appointmentCountByDate: Map<LocalDate, Int> = emptyMap(),
    val isFiltered: Boolean = false,
    val filterClientId: String? = null,
    val filterPropertyId: String? = null,
    val filterStatus: String? = null,
    val filterType: String? = null,
    val searchQuery: String = "",
    val showFilterDialog: Boolean = false,
    val saveSuccess: Boolean = false
) {
    /**
     * Получает количество встреч для указанной даты
     */
    fun getAppointmentCountForDate(date: LocalDate): Int {
        return appointmentCountByDate[date]?.let { it } ?: 0
    }
    
    /**
     * Возвращает отфильтрованный список встреч
     */
    fun getFilteredAppointments(): List<Appointment> {
        return if (isFiltered) {
            appointments
        } else {
            allAppointments
        }
    }
    
    /**
     * Проверяет, применены ли какие-либо фильтры
     */
    fun hasActiveFilters(): Boolean {
        return filterClientId != null || 
               filterPropertyId != null || 
               filterStatus != null || 
               filterType != null || 
               searchQuery.isNotEmpty()
    }
} 