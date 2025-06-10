package com.realestateassistant.pro.presentation.screens.appointment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realestateassistant.pro.domain.model.Appointment
import com.realestateassistant.pro.domain.model.AppointmentStatus
import com.realestateassistant.pro.domain.model.AppointmentType
import com.realestateassistant.pro.domain.usecase.AppointmentUseCases
import com.realestateassistant.pro.domain.usecase.ClientUseCases
import com.realestateassistant.pro.domain.usecase.PropertyUseCases
import com.realestateassistant.pro.presentation.screens.appointment.models.AppointmentEvent
import com.realestateassistant.pro.presentation.screens.appointment.models.AppointmentFormState
import com.realestateassistant.pro.presentation.screens.appointment.models.AppointmentState
import com.realestateassistant.pro.presentation.screens.appointment.models.CalendarView
import com.realestateassistant.pro.presentation.screens.appointment.components.AppointmentDialogEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.Date

private const val TAG = "AppointmentViewModel"

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val appointmentUseCases: AppointmentUseCases,
    private val clientUseCases: ClientUseCases,
    private val propertyUseCases: PropertyUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(AppointmentState())
    val state: StateFlow<AppointmentState> = _state.asStateFlow()

    init {
        loadAppointmentsForCurrentMonth()
        observeAllAppointments()
        loadClientsList()
        loadPropertiesList()
    }
    
    /**
     * Загружает список всех клиентов
     */
    private fun loadClientsList() {
        viewModelScope.launch {
            try {
                val clients = clientUseCases.getAllClients()
                clients.onSuccess { clientsList ->
                    _state.update { it.copy(availableClients = clientsList) }
                }.onFailure { error ->
                    Log.e(TAG, "Error loading clients", error)
                    _state.update { it.copy(error = "Ошибка при загрузке клиентов: ${error.message}") }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading clients", e)
                _state.update { it.copy(error = "Ошибка при загрузке клиентов: ${e.message}") }
            }
        }
    }
    
    /**
     * Загружает список всех объектов недвижимости
     */
    private fun loadPropertiesList() {
        viewModelScope.launch {
            try {
                val properties = propertyUseCases.getAllProperties()
                properties.onSuccess { propertiesList ->
                    _state.update { it.copy(availableProperties = propertiesList) }
                }.onFailure { error ->
                    Log.e(TAG, "Error loading properties", error)
                    _state.update { it.copy(error = "Ошибка при загрузке объектов: ${error.message}") }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading properties", e)
                _state.update { it.copy(error = "Ошибка при загрузке объектов: ${e.message}") }
            }
        }
    }
    
    /**
     * Загружает информацию о конкретном клиенте
     */
    private fun loadClient(clientId: String) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                val clientResult = clientUseCases.getClient(clientId)
                clientResult.onSuccess { client ->
                    _state.update { state ->
                        // Добавляем клиента в список доступных, если его там нет
                        val updatedClients = if (state.availableClients.none { it.id == client.id }) {
                            state.availableClients + listOf(client)
                        } else {
                            // Заменяем существующего клиента обновленными данными
                            state.availableClients.map { if (it.id == client.id) client else it }
                        }
                        state.copy(availableClients = updatedClients, isLoading = false)
                    }
                }.onFailure { error ->
                    Log.e(TAG, "Error loading client with ID $clientId", error)
                    _state.update { it.copy(
                        isLoading = false,
                        error = "Ошибка при загрузке информации о клиенте: ${error.message}"
                    ) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading client with ID $clientId", e)
                _state.update { it.copy(
                    isLoading = false,
                    error = "Ошибка при загрузке информации о клиенте: ${e.message}"
                ) }
            }
        }
    }
    
    /**
     * Загружает информацию о конкретном объекте недвижимости
     */
    private fun loadProperty(propertyId: String) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                val propertyResult = propertyUseCases.getProperty(propertyId)
                propertyResult.onSuccess { property ->
                    _state.update { state ->
                        // Добавляем объект в список доступных, если его там нет
                        val updatedProperties = if (state.availableProperties.none { it.id == property.id }) {
                            state.availableProperties + listOf(property)
                        } else {
                            // Заменяем существующий объект обновленными данными
                            state.availableProperties.map { if (it.id == property.id) property else it }
                        }
                        state.copy(availableProperties = updatedProperties, isLoading = false)
                    }
                }.onFailure { error ->
                    Log.e(TAG, "Error loading property with ID $propertyId", error)
                    _state.update { it.copy(
                        isLoading = false,
                        error = "Ошибка при загрузке информации об объекте: ${error.message}"
                    ) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading property with ID $propertyId", e)
                _state.update { it.copy(
                    isLoading = false,
                    error = "Ошибка при загрузке информации об объекте: ${e.message}"
                ) }
            }
        }
    }

    /**
     * Обрабатывает события календаря
     */
    fun handleEvent(event: AppointmentEvent) {
        when (event) {
            is AppointmentEvent.ChangeCalendarView -> {
                _state.update { it.copy(calendarView = event.view) }
                // При смене представления календаря обновляем данные для нового представления
                when (event.view) {
                    CalendarView.DAY -> loadAppointmentsForDate(_state.value.selectedDate)
                    CalendarView.WEEK -> loadAppointmentsForWeek(_state.value.selectedDate)
                    CalendarView.MONTH -> {
                        loadAppointmentsForMonth(_state.value.selectedDate.year, _state.value.selectedDate.monthValue)
                        loadAppointmentsForDate(_state.value.selectedDate)
                    }
                    else -> {}
                }
            }
            is AppointmentEvent.NavigateToNext -> {
                navigateToNext()
            }
            is AppointmentEvent.NavigateToPrevious -> {
                navigateToPrevious()
            }
            is AppointmentEvent.NavigateToNextWeek -> {
                navigateToNextWeek()
            }
            is AppointmentEvent.NavigateToPreviousWeek -> {
                navigateToPreviousWeek()
            }
            is AppointmentEvent.NavigateToNextMonth -> {
                navigateToNextMonth()
            }
            is AppointmentEvent.NavigateToPreviousMonth -> {
                navigateToPreviousMonth()
            }
            is AppointmentEvent.SelectDate -> {
                selectDate(event.date)
            }
            is AppointmentEvent.ShowAppointmentDialog -> {
                _state.update { it.copy(
                    showAppointmentDialog = event.show,
                    isEditMode = event.isEditMode,
                    formState = event.formState ?: AppointmentFormState()
                ) }
            }
            is AppointmentEvent.ShowCreateDialog -> {
                showCreateDialog()
            }
            is AppointmentEvent.ShowEditDialog -> {
                loadAppointment(event.id)
            }
            is AppointmentEvent.OnDialogEvent -> {
                handleDialogEvent(event.event)
            }
            is AppointmentEvent.SaveAppointment -> {
                saveAppointment()
            }
            is AppointmentEvent.DeleteAppointment -> {
                deleteAppointment(event.id)
            }
            is AppointmentEvent.FilterByClient -> {
                filterByClient(event.clientId)
            }
            is AppointmentEvent.FilterByProperty -> {
                filterByProperty(event.propertyId)
            }
            is AppointmentEvent.FilterByType -> {
                val type = if (event.type != null) AppointmentType.valueOf(event.type) else null
                filterByType(type)
            }
            is AppointmentEvent.FilterByStatus -> {
                val status = if (event.status != null) AppointmentStatus.valueOf(event.status) else null
                filterByStatus(status)
            }
            is AppointmentEvent.SearchAppointments -> {
                searchAppointments(event.query)
            }
            is AppointmentEvent.ClearFilters -> {
                clearFilters()
            }
            is AppointmentEvent.Error -> {
                _state.update { it.copy(error = event.message) }
            }
            is AppointmentEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
            is AppointmentEvent.JumpToDate -> {
                jumpToDate(event.date)
            }
            is AppointmentEvent.LoadAppointment -> {
                loadAppointment(event.id)
            }
            is AppointmentEvent.LoadAppointmentForView -> {
                loadAppointmentForView(event.id)
            }
            is AppointmentEvent.LoadAppointmentsForDate -> {
                loadAppointmentsForDate(event.date)
            }
            is AppointmentEvent.LoadAppointmentsForMonth -> {
                loadAppointmentsForMonth(event.year, event.month)
            }
            is AppointmentEvent.LoadAppointmentsForWeek -> {
                loadAppointmentsForWeek(event.date)
            }
            is AppointmentEvent.LoadClient -> {
                loadClient(event.id)
            }
            is AppointmentEvent.LoadProperty -> {
                loadProperty(event.id)
            }
            is AppointmentEvent.LoadClients -> {
                loadClientsList()
            }
            is AppointmentEvent.LoadProperties -> {
                loadPropertiesList()
            }
            is AppointmentEvent.CancelEdit -> {
                _state.update { it.copy(
                    showAppointmentDialog = false,
                    isEditMode = false,
                    formState = AppointmentFormState()
                ) }
            }
            is AppointmentEvent.UpdateAppointmentStatus -> {
                updateAppointmentStatus(event.id, event.status)
            }
            is AppointmentEvent.UpdateAppointmentType -> {
                updateAppointmentType(event.id, event.type)
            }
            is AppointmentEvent.ShowFilterDialog -> {
                _state.update { it.copy(showFilterDialog = event.show) }
            }
            is AppointmentEvent.ApplyFilters -> {
                applyFilters(
                    clientId = event.clientId,
                    propertyId = event.propertyId,
                    status = event.status,
                    type = event.type,
                    searchQuery = event.searchQuery
                )
            }
            is AppointmentEvent.SetStartDate -> {
                // Обработка установки даты начала (используется LocalDate.ofEpochDay для преобразования Long в LocalDate)
                val localDate = LocalDate.ofEpochDay(event.date / (24 * 60 * 60 * 1000))
                _state.update { 
                    it.copy(formState = it.formState.copy(startDate = localDate))
                }
                validateEndDate()
                validateEndTime()
            }
            is AppointmentEvent.DuplicateAppointment,
            is AppointmentEvent.FilterByDateRange,
            is AppointmentEvent.AddAttachment,
            is AppointmentEvent.AddParticipant,
            is AppointmentEvent.RemoveAttachment,
            is AppointmentEvent.RemoveParticipant,
            is AppointmentEvent.SetAllDay,
            is AppointmentEvent.SetClient,
            is AppointmentEvent.SetColor,
            is AppointmentEvent.SetDescription,
            is AppointmentEvent.SetEndDate,
            is AppointmentEvent.SetEndTime,
            is AppointmentEvent.SetLocation,
            is AppointmentEvent.SetNotes,
            is AppointmentEvent.SetProperty,
            is AppointmentEvent.SetRecurring,
            is AppointmentEvent.SetRecurrenceRule,
            is AppointmentEvent.SetReminderTime,
            is AppointmentEvent.SetStartTime,
            is AppointmentEvent.SetStatus,
            is AppointmentEvent.ShowAttachmentPicker,
            is AppointmentEvent.ShowClientSelector,
            is AppointmentEvent.ShowColorPicker,
            is AppointmentEvent.ShowDatePicker,
            is AppointmentEvent.ShowParticipantDialog,
            is AppointmentEvent.ShowPropertySelector,
            is AppointmentEvent.ShowRecurrenceDialog,
            is AppointmentEvent.ShowReminderDialog,
            is AppointmentEvent.ShowTimePicker,
            is AppointmentEvent.UpdateParticipant,
            is AppointmentEvent.SetTitle,
            is AppointmentEvent.SetType -> {
                // TODO: Реализовать эти события
                _state.update { it.copy(error = "Эта функциональность в разработке") }
            }
        }
    }

    /**
     * Переходит к следующему периоду (дню/неделе/месяцу)
     */
    private fun navigateToNext() {
        when (_state.value.calendarView) {
            CalendarView.DAY -> {
                val nextDay = _state.value.selectedDate.plusDays(1)
                selectDate(nextDay)
            }
            CalendarView.WEEK -> {
                val nextWeek = _state.value.selectedDate.plusWeeks(1)
                selectWeek(nextWeek)
            }
            CalendarView.MONTH -> {
                val nextMonth = _state.value.selectedDate.plusMonths(1)
                selectMonth(nextMonth)
            }
            else -> {}
        }
    }

    /**
     * Переходит к предыдущему периоду (дню/неделе/месяцу)
     */
    private fun navigateToPrevious() {
        when (_state.value.calendarView) {
            CalendarView.DAY -> {
                val previousDay = _state.value.selectedDate.minusDays(1)
                selectDate(previousDay)
            }
            CalendarView.WEEK -> {
                val previousWeek = _state.value.selectedDate.minusWeeks(1)
                selectWeek(previousWeek)
            }
            CalendarView.MONTH -> {
                val previousMonth = _state.value.selectedDate.minusMonths(1)
                selectMonth(previousMonth)
            }
            else -> {}
        }
    }

    /**
     * Переходит к следующему месяцу
     */
    private fun navigateToNextMonth() {
        val nextMonth = _state.value.selectedDate.plusMonths(1)
        selectMonth(nextMonth)
    }

    /**
     * Переходит к предыдущему месяцу
     */
    private fun navigateToPreviousMonth() {
        val previousMonth = _state.value.selectedDate.minusMonths(1)
        selectMonth(previousMonth)
    }

    /**
     * Переходит к следующей неделе
     */
    private fun navigateToNextWeek() {
        val nextWeek = _state.value.selectedDate.plusWeeks(1)
        selectWeek(nextWeek)
    }

    /**
     * Переходит к предыдущей неделе
     */
    private fun navigateToPreviousWeek() {
        val previousWeek = _state.value.selectedDate.minusWeeks(1)
        selectWeek(previousWeek)
    }

    /**
     * Перейти к указанной дате
     */
    private fun jumpToDate(date: LocalDate) {
        when (_state.value.calendarView) {
            CalendarView.DAY -> selectDate(date)
            CalendarView.WEEK -> selectWeek(date)
            CalendarView.MONTH -> selectMonth(date)
            else -> selectDate(date)
        }
    }

    /**
     * Выбирает указанную дату
     */
    private fun selectDate(date: LocalDate) {
        _state.update { it.copy(selectedDate = date) }
        loadAppointmentsForDate(date)
        
        // Если текущее представление - месяц, обновляем отображение списка встреч для выбранной даты
        if (_state.value.calendarView == CalendarView.MONTH) {
            val appointmentsByDate = _state.value.appointments.groupBy { appointment ->
                appointment.startDateTime.toLocalDate()
            }
            val appointmentsForSelectedDate = appointmentsByDate[date] ?: emptyList()
            _state.update { it.copy(appointmentsForSelectedDate = appointmentsForSelectedDate) }
        }
    }

    /**
     * Выбирает неделю, содержащую указанную дату
     */
    private fun selectWeek(date: LocalDate) {
        _state.update { it.copy(selectedDate = date, selectedWeek = date) }
        loadAppointmentsForWeek(date)
    }

    /**
     * Выбирает месяц, содержащий указанную дату
     */
    private fun selectMonth(date: LocalDate) {
        _state.update { it.copy(selectedDate = date, selectedMonth = date.year to date.monthValue) }
        loadAppointmentsForMonth(date.year, date.monthValue)
        // После загрузки встреч для месяца, загружаем встречи для выбранного дня
        loadAppointmentsForDate(date)
    }

    /**
     * Показывает диалог создания новой встречи
     */
    private fun showCreateDialog() {
        // Устанавливаем начальные значения для новой встречи
        val now = LocalDate.now()
        val currentTime = LocalTime.now().withSecond(0).withNano(0)
        val startTime = currentTime.plusMinutes((15 - currentTime.minute % 15).toLong()) // Округляем до ближайших 15 минут
        val endTime = startTime.plusHours(1)
        
        val newFormState = AppointmentFormState(
            startDate = now,
            startTime = startTime,
            endDate = now,
            endTime = endTime
        )
        
        _state.update { 
            it.copy(
                isEditMode = false,
                showAppointmentDialog = true,
                formState = newFormState
            )
        }
    }

    /**
     * Обрабатывает события диалога встречи
     */
    private fun handleDialogEvent(event: AppointmentDialogEvent) {
        when (event) {
            is AppointmentDialogEvent.SetTitle -> {
                updateFormState { it.copy(title = event.title, titleError = null) }
            }
            is AppointmentDialogEvent.SetDescription -> {
                updateFormState { it.copy(description = event.description) }
            }
            is AppointmentDialogEvent.SetNotes -> {
                updateFormState { it.copy(notes = event.notes) }
            }
            is AppointmentDialogEvent.SetLocation -> {
                updateFormState { it.copy(location = event.location) }
            }
            is AppointmentDialogEvent.SetClient -> {
                updateFormState { 
                    it.copy(
                        clientId = event.id, 
                        clientName = event.name,
                        clientError = null,
                        showClientSelector = false
                    ) 
                }
            }
            is AppointmentDialogEvent.SetProperty -> {
                updateFormState { 
                    it.copy(
                        propertyId = event.id, 
                        propertyAddress = event.address,
                        propertyError = null,
                        showPropertySelector = false
                    ) 
                }
            }
            is AppointmentDialogEvent.ShowClientSelector -> {
                updateFormState { it.copy(showClientSelector = event.show) }
            }
            is AppointmentDialogEvent.ShowPropertySelector -> {
                updateFormState { it.copy(showPropertySelector = event.show) }
            }
            is AppointmentDialogEvent.SetStartDate -> {
                updateFormState { 
                    val currentState = it
                    val newStartDate = event.date
                    
                    // Базовое обновление даты начала
                    val newState = it.copy(
                        startDate = newStartDate,
                        startDateError = null,
                    showDatePicker = false
                    )
                    
                    // Если дата начала стала позже даты окончания, обновляем дату окончания
                    val updatedState = if (newStartDate.isAfter(currentState.endDate)) {
                        newState.copy(endDate = newStartDate)
                    } else {
                        newState
                    }
                    
                    // Дополнительно проверяем время, если даты совпадают
                    if (updatedState.startDate.isEqual(updatedState.endDate) && 
                        (updatedState.startTime.isAfter(updatedState.endTime) || updatedState.startTime.equals(updatedState.endTime))) {
                        // Если время начала позже или равно времени окончания, устанавливаем время окончания на час позже
                        updatedState.copy(endTime = updatedState.startTime.plusHours(1))
                    } else {
                        updatedState
                    }
                }
                validateEndDate()
                validateEndTime()
            }
            is AppointmentDialogEvent.SetEndDate -> {
                updateFormState { 
                    it.copy(
                        endDate = event.date,
                        endDateError = null,
                    showDatePicker = false
                    ) 
                }
                validateEndDate()
                validateEndTime()
            }
            is AppointmentDialogEvent.SetStartTime -> {
                updateFormState { 
                    val currentState = it
                    val newStartTime = event.time
                    val currentEndTime = it.endTime
                    
                    // Базовое обновление времени начала
                    val updatedState = it.copy(
                        startTime = newStartTime,
                    showTimePicker = false
                    ) 
                    
                    // Если время начала стало равным или позже времени окончания, 
                    // автоматически корректируем время окончания на 1 час позже времени начала
                    if (newStartTime.isAfter(currentEndTime) || newStartTime.equals(currentEndTime)) {
                        updatedState.copy(endTime = newStartTime.plusHours(1))
                    } else {
                        updatedState
                    }
                }
                validateEndTime()
            }
            is AppointmentDialogEvent.SetEndTime -> {
                updateFormState { 
                    it.copy(
                        endTime = event.time,
                    showTimePicker = false
                    ) 
                }
                validateEndTime()
            }
            is AppointmentDialogEvent.SetIsAllDay -> {
                updateFormState { 
                    it.copy(
                        isAllDay = event.isAllDay,
                        // Если включен режим "весь день", сбрасываем ошибку времени
                        timeError = if (event.isAllDay) null else it.timeError
                    ) 
                }
            }
            is AppointmentDialogEvent.ShowDatePicker -> {
                updateFormState { 
                    it.copy(
                    showDatePicker = event.show,
                    isStartDateSelection = event.isStartDate
                    ) 
                }
            }
            is AppointmentDialogEvent.ShowTimePicker -> {
                updateFormState { 
                    it.copy(
                    showTimePicker = event.show,
                    isStartTimeSelection = event.isStartTime
                    ) 
                }
            }
            is AppointmentDialogEvent.SetType -> {
                try {
                    val type = AppointmentType.valueOf(event.type)
                    updateFormState { it.copy(type = type) }
                } catch (e: Exception) {
                    _state.update { it.copy(error = "Неизвестный тип встречи") }
                }
            }
            is AppointmentDialogEvent.SetStatus -> {
                try {
                    val status = AppointmentStatus.valueOf(event.status)
                    updateFormState { it.copy(status = status) }
                } catch (e: Exception) {
                    _state.update { it.copy(error = "Неизвестный статус встречи") }
                }
            }
            is AppointmentDialogEvent.SaveAppointment -> {
                saveAppointment()
            }
            is AppointmentDialogEvent.CancelAppointment -> {
                _state.update { 
                    it.copy(
                        showAppointmentDialog = false,
                        isEditMode = false,
                        formState = AppointmentFormState()
                    ) 
                }
            }
        }
    }

    /**
     * Обновляет состояние формы
     */
    private fun updateFormState(update: (AppointmentFormState) -> AppointmentFormState) {
        _state.update { it.copy(formState = update(it.formState)) }
    }

    /**
     * Загружает встречу для редактирования
     */
    private fun loadAppointment(id: String) {
        viewModelScope.launch {
            try {
                val appointmentResult = appointmentUseCases.getAppointment(id)
                appointmentResult.onSuccess { appointment ->
                    _state.update { 
                        it.copy(
                            isEditMode = true,
                            showAppointmentDialog = true,
                            formState = AppointmentFormState.fromAppointment(appointment)
                        )
                    }
                }.onFailure { error ->
                    _state.update { it.copy(error = "Ошибка загрузки встречи: ${error.message}") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Ошибка загрузки встречи: ${e.message}") }
            }
        }
    }

    /**
     * Сохраняет встречу
     */
    private fun saveAppointment() {
        val formState = _state.value.formState
        val isEditMode = _state.value.isEditMode
        
        // Проверяем валидность формы
        if (!validateForm()) {
            // Если есть ошибка времени, явно показываем пользователю предупреждение
            if (formState.timeError != null) {
                _state.update { it.copy(error = formState.timeError) }
            }
            return
        }
        
        viewModelScope.launch {
            try {
                if (isEditMode) {
                    // Получаем текущую встречу, чтобы сохранить createdAt
                    val appointmentId = formState.id
                    val existingAppointmentResult = appointmentUseCases.getAppointment(appointmentId)
                    
                    if (existingAppointmentResult.isSuccess) {
                        val existingAppointment = existingAppointmentResult.getOrNull()
                        if (existingAppointment != null) {
                            // Создаем новую встречу с сохранением createdAt из существующей
                            val updatedAppointment = formState.toAppointment().copy(
                                createdAt = existingAppointment.createdAt,
                                updatedAt = System.currentTimeMillis()
                            )
                            appointmentUseCases.updateAppointment(updatedAppointment)
                } else {
                            // Если почему-то не нашли существующую встречу, просто обновляем как есть
                            appointmentUseCases.updateAppointment(formState.toAppointment())
                        }
                    } else {
                        // При ошибке получения существующей встречи, просто обновляем как есть
                        appointmentUseCases.updateAppointment(formState.toAppointment())
                    }
                } else {
                    // Создание новой встречи
                    appointmentUseCases.createAppointment(formState.toAppointment())
                }
                
                // Закрываем диалог, сбрасываем форму и устанавливаем флаг успешного сохранения
                _state.update { 
                    it.copy(
                        isEditMode = false,
                        showAppointmentDialog = false,
                        formState = AppointmentFormState(),
                        saveSuccess = true
                    )
                }
                
                // Обновляем данные
                loadAppointmentsForCurrentMonth()
            } catch (e: Exception) {
                _state.update { it.copy(error = "Ошибка сохранения встречи: ${e.message}") }
            }
        }
    }

    /**
     * Валидирует форму
     */
    private fun validateForm(): Boolean {
        var isValid = true
        val formState = _state.value.formState
        
        // Проверяем заголовок
        if (formState.title.isBlank()) {
            _state.update { 
                it.copy(formState = it.formState.copy(
                    titleError = "Заголовок не может быть пустым"
                ))
            }
            isValid = false
        } else {
            _state.update { it.copy(formState = it.formState.copy(titleError = null)) }
        }
        
        // Проверяем клиента
        if (formState.clientId.isBlank()) {
            _state.update { 
                it.copy(formState = it.formState.copy(
                    clientError = "Необходимо выбрать клиента"
                ))
            }
            isValid = false
        } else {
            _state.update { it.copy(formState = it.formState.copy(clientError = null)) }
        }
        
        // Проверяем объект недвижимости
        if (formState.propertyId.isBlank()) {
            _state.update { 
                it.copy(formState = it.formState.copy(
                    propertyError = "Необходимо выбрать объект недвижимости"
                ))
            }
            isValid = false
        } else {
            _state.update { it.copy(formState = it.formState.copy(propertyError = null)) }
        }
        
        // Проверяем даты
        if (formState.endDate.isBefore(formState.startDate)) {
            _state.update { 
                it.copy(formState = it.formState.copy(
                    endDateError = "Дата окончания не может быть раньше даты начала"
                ))
            }
            isValid = false
        } else {
            _state.update { it.copy(formState = it.formState.copy(endDateError = null)) }
        }
        
        // Проверяем время (только если не весь день и даты совпадают)
        if (!formState.isAllDay && formState.startDate.isEqual(formState.endDate)) {
            if (formState.endTime.isBefore(formState.startTime) || formState.endTime.equals(formState.startTime)) {
                _state.update { 
                    it.copy(formState = it.formState.copy(
                        timeError = "⚠️ ВНИМАНИЕ! Время окончания должно быть позже времени начала"
                    ))
                }
                isValid = false
            } else {
                _state.update { it.copy(formState = it.formState.copy(timeError = null)) }
            }
        }
        
        return isValid
    }

    /**
     * Проверяет корректность даты окончания
     */
    private fun validateEndDate() {
        val formState = _state.value.formState
        
        if (formState.endDate.isBefore(formState.startDate)) {
            updateFormState { it.copy(
                endDateError = "Дата окончания должна быть позже даты начала"
            ) }
        } else {
            updateFormState { it.copy(endDateError = null) }
        }
    }

    /**
     * Проверяет корректность времени окончания
     */
    private fun validateEndTime() {
        val formState = _state.value.formState
        
        // Проверяем только если это не на весь день
        if (!formState.isAllDay) {
            // Если дата начала и окончания совпадают, то проверяем время
            if (formState.startDate.isEqual(formState.endDate) && 
                (formState.endTime.isBefore(formState.startTime) || formState.endTime.equals(formState.startTime))) {
                updateFormState { 
                    it.copy(timeError = "⚠️ ВНИМАНИЕ! Время окончания должно быть позже времени начала")
                }
            } else {
                updateFormState { it.copy(timeError = null) }
            }
        }
    }

    /**
     * Удаляет встречу
     */
    private fun deleteAppointment(id: String) {
        viewModelScope.launch {
            try {
                appointmentUseCases.deleteAppointment(id)
                _state.update { 
                    it.copy(
                        isEditMode = false,
                        showAppointmentDialog = false,
                        formState = AppointmentFormState()
                    )
                }
                
                // Обновляем данные
                loadAppointmentsForCurrentMonth()
            } catch (e: Exception) {
                _state.update { it.copy(error = "Ошибка удаления встречи: ${e.message}") }
            }
        }
    }

    /**
     * Загружает встречи для текущего месяца
     */
    private fun loadAppointmentsForCurrentMonth() {
        val selectedDate = _state.value.selectedDate
        loadAppointmentsForMonth(selectedDate.year, selectedDate.monthValue)
    }

    /**
     * Загружает встречи для указанного месяца
     */
    private fun loadAppointmentsForMonth(year: Int, month: Int) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                
                // Создаем временные метки для начала и конца месяца
                val startOfMonth = LocalDate.of(year, month, 1)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
                
                val endOfMonth = LocalDate.of(year, month, 1)
                    .plusMonths(1)
                    .minusDays(1)
                    .atTime(23, 59, 59)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
                
                // Получаем встречи за месяц
                val appointmentsResult = appointmentUseCases.getAppointmentsByDateRange(startOfMonth, endOfMonth)
                appointmentsResult.onSuccess { appointments ->
                    _state.update { it.copy(
                        appointments = appointments, 
                        appointmentsForSelectedDate = appointments.filter { appointment -> 
                            appointment.startDate.isEqual(_state.value.selectedDate)
                        },
                        isLoading = false
                    ) }
                    
                    // Подсчитываем количество встреч по датам
                    val countByDate = appointments.groupBy { appointment ->
                        appointment.startDate
                    }.mapValues { it.value.size }
                    
                    _state.update { it.copy(appointmentCountByDate = countByDate) }
                }.onFailure { error ->
                    _state.update { it.copy(error = "Ошибка загрузки встреч: ${error.message}", isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Ошибка загрузки встреч: ${e.message}", isLoading = false) }
            }
        }
    }

    /**
     * Загружает встречи для указанной недели
     */
    private fun loadAppointmentsForWeek(date: LocalDate) {
        viewModelScope.launch {
            try {
                val weekStart = date.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1)
                val weekEnd = weekStart.plusDays(6)
                
                val startOfWeek = weekStart.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val endOfWeek = weekEnd.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
                
                val appointmentsResult = appointmentUseCases.getAppointmentsByDateRange(startOfWeek, endOfWeek)
                appointmentsResult.onSuccess { appointments ->
                    _state.update { it.copy(
                        appointments = appointments,
                        appointmentsForSelectedDate = appointments.filter { appointment -> 
                            appointment.startDate.isEqual(_state.value.selectedDate)
                        }
                    ) }
                }.onFailure { error ->
                    _state.update { it.copy(error = "Ошибка загрузки встреч: ${error.message}") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Ошибка загрузки встреч: ${e.message}") }
            }
        }
    }

    /**
     * Загружает встречи для указанной даты
     */
    private fun loadAppointmentsForDate(date: LocalDate) {
        viewModelScope.launch {
            try {
                val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
                
                val appointmentsResult = appointmentUseCases.getAppointmentsByDateRange(startOfDay, endOfDay)
                appointmentsResult.onSuccess { appointments ->
                    _state.update { it.copy(appointmentsForSelectedDate = appointments) }
                }.onFailure { error ->
                    _state.update { it.copy(error = "Ошибка загрузки встреч: ${error.message}") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Ошибка загрузки встреч: ${e.message}") }
            }
        }
    }

    /**
     * Наблюдает за всеми встречами
     */
    private fun observeAllAppointments() {
        viewModelScope.launch {
            appointmentUseCases.observeAllAppointments()
                .collect { appointments ->
                    _state.update { it.copy(allAppointments = appointments) }
                    
                    // Обновляем данные для текущего представления
                    when (_state.value.calendarView) {
                        CalendarView.DAY -> loadAppointmentsForDate(_state.value.selectedDate)
                        CalendarView.WEEK -> loadAppointmentsForWeek(_state.value.selectedDate)
                        CalendarView.MONTH -> loadAppointmentsForCurrentMonth()
                        else -> loadAppointmentsForCurrentMonth()
                    }
                }
        }
    }

    /**
     * Фильтрует встречи по клиенту
     */
    private fun filterByClient(clientId: String?) {
        viewModelScope.launch {
            try {
                if (clientId == null) {
                    clearFilters()
                    return@launch
                }
                
                val appointmentsResult = appointmentUseCases.getAppointmentsByClient(clientId)
                appointmentsResult.onSuccess { appointments ->
                    _state.update { it.copy(appointments = appointments, isFiltered = true) }
                }.onFailure { error ->
                    _state.update { it.copy(error = "Ошибка фильтрации встреч: ${error.message}") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Ошибка фильтрации встреч: ${e.message}") }
            }
        }
    }

    /**
     * Фильтрует встречи по объекту недвижимости
     */
    private fun filterByProperty(propertyId: String?) {
        viewModelScope.launch {
            try {
                if (propertyId == null) {
                    clearFilters()
                    return@launch
                }
                
                val appointmentsResult = appointmentUseCases.getAppointmentsByProperty(propertyId)
                appointmentsResult.onSuccess { appointments ->
                    _state.update { it.copy(appointments = appointments, isFiltered = true) }
                }.onFailure { error ->
                    _state.update { it.copy(error = "Ошибка фильтрации встреч: ${error.message}") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Ошибка фильтрации встреч: ${e.message}") }
            }
        }
    }

    /**
     * Фильтрует встречи по типу
     */
    private fun filterByType(type: AppointmentType?) {
        viewModelScope.launch {
            try {
                if (type == null) {
                    clearFilters()
                    return@launch
                }
                
                val appointmentsResult = appointmentUseCases.getAppointmentsByType(type)
                appointmentsResult.onSuccess { appointments ->
                    _state.update { it.copy(appointments = appointments, isFiltered = true) }
                }.onFailure { error ->
                    _state.update { it.copy(error = "Ошибка фильтрации встреч: ${error.message}") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Ошибка фильтрации встреч: ${e.message}") }
            }
        }
    }

    /**
     * Фильтрует встречи по статусу
     */
    private fun filterByStatus(status: AppointmentStatus?) {
        viewModelScope.launch {
            try {
                if (status == null) {
                    clearFilters()
                    return@launch
                }
                
                val appointmentsResult = appointmentUseCases.getAppointmentsByStatus(status)
                appointmentsResult.onSuccess { appointments ->
                    _state.update { it.copy(appointments = appointments, isFiltered = true) }
                }.onFailure { error ->
                    _state.update { it.copy(error = "Ошибка фильтрации встреч: ${error.message}") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Ошибка фильтрации встреч: ${e.message}") }
            }
        }
    }

    /**
     * Поиск встреч по запросу
     */
    private fun searchAppointments(query: String) {
        viewModelScope.launch {
            try {
                appointmentUseCases.searchAppointments(query).collect { appointments ->
                    _state.update { it.copy(appointments = appointments, isFiltered = true) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Ошибка поиска встреч: ${e.message}") }
            }
        }
    }

    /**
     * Очищает фильтры
     */
    private fun clearFilters() {
        _state.update { it.copy(
            isFiltered = false,
            filterClientId = null,
            filterPropertyId = null,
            filterStatus = null,
            filterType = null,
            searchQuery = ""
        ) }
        loadAppointmentsForCurrentMonth()
    }

    /**
     * Загружает список клиентов
     */
    private fun loadClients() {
        loadClientsList()
    }

    /**
     * Загружает список объектов недвижимости
     */
    private fun loadProperties() {
        loadPropertiesList()
    }

    /**
     * Загружает встречу для просмотра (без открытия диалога редактирования)
     */
    private fun loadAppointmentForView(id: String) {
        viewModelScope.launch {
            try {
                val appointmentResult = appointmentUseCases.getAppointment(id)
                appointmentResult.onSuccess { appointment ->
                    _state.update { currentState ->
                        // Добавляем встречу в список, если её там нет
                        val updatedAppointments = if (currentState.appointments.none { it.id == appointment.id }) {
                            currentState.appointments + appointment
                        } else {
                            // Заменяем существующую встречу на новую
                            currentState.appointments.map { 
                                if (it.id == appointment.id) appointment else it 
                            }
                        }
                        
                        currentState.copy(
                            appointments = updatedAppointments,
                            isLoading = false,
                            error = null
                        )
                    }
                }.onFailure { error ->
                    _state.update { it.copy(error = "Ошибка загрузки встречи: ${error.message}") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Ошибка загрузки встречи: ${e.message}") }
            }
        }
    }

    /**
     * Обновляет статус встречи
     */
    private fun updateAppointmentStatus(id: String, status: AppointmentStatus) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                
                // Получаем существующую встречу
                val appointmentResult = appointmentUseCases.getAppointment(id)
                appointmentResult.onSuccess { appointment ->
                    // Обновляем только статус и время обновления
                    val updatedAppointment = appointment.copy(
                        status = status,
                        updatedAt = System.currentTimeMillis()
                    )
                    
                    // Сохраняем обновленную встречу
                    val updateResult = appointmentUseCases.updateAppointment(updatedAppointment)
                    updateResult.onSuccess {
                        _state.update { currentState ->
                            // Обновляем встречу в текущем списке
                            val updatedAppointments = currentState.appointments.map { 
                                if (it.id == id) updatedAppointment else it 
                            }
                            
                            // Обновляем встречу в списке для выбранной даты
                            val updatedAppointmentsForSelectedDate = currentState.appointmentsForSelectedDate.map {
                                if (it.id == id) updatedAppointment else it
                            }
                            
                            currentState.copy(
                                appointments = updatedAppointments,
                                appointmentsForSelectedDate = updatedAppointmentsForSelectedDate,
                                isLoading = false,
                                error = null
                            )
                        }
                    }.onFailure { error ->
                        _state.update { it.copy(
                            isLoading = false,
                            error = "Ошибка при обновлении статуса встречи: ${error.message}"
                        ) }
                    }
                }.onFailure { error ->
                    _state.update { it.copy(
                        isLoading = false,
                        error = "Ошибка при загрузке встречи: ${error.message}"
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "Ошибка при обновлении статуса встречи: ${e.message}"
                ) }
            }
        }
    }

    /**
     * Обновляет состояние фильтров
     */
    private fun updateFilterState(showFilterDialog: Boolean) {
        _state.update { it.copy(showFilterDialog = showFilterDialog) }
    }

    /**
     * Применяет фильтры
     */
    private fun applyFilters(clientId: String?, propertyId: String?, status: String?, type: String?, searchQuery: String) {
        viewModelScope.launch {
            try {
                // Обновляем состояние фильтров
                _state.update { it.copy(
                    filterClientId = clientId,
                    filterPropertyId = propertyId,
                    filterStatus = status,
                    filterType = type,
                    searchQuery = searchQuery,
                    showFilterDialog = false
                )}
                
                // Если нет активных фильтров, очищаем фильтрацию
                if (clientId == null && propertyId == null && status == null && type == null && searchQuery.isEmpty()) {
                    clearFilters()
                    return@launch
                }
                
                // Получаем все встречи
                val allAppointmentsResult = appointmentUseCases.getAllAppointments()
                allAppointmentsResult.onSuccess { allAppointments ->
                    // Применяем все фильтры к списку встреч
                    var filteredAppointments = allAppointments
                    
                    // Фильтр по клиенту
                    if (clientId != null) {
                        filteredAppointments = filteredAppointments.filter { it.clientId == clientId }
                    }
                    
                    // Фильтр по объекту
                    if (propertyId != null) {
                        filteredAppointments = filteredAppointments.filter { it.propertyId == propertyId }
                    }
                    
                    // Фильтр по статусу
                    if (status != null) {
                        try {
                            val statusEnum = AppointmentStatus.valueOf(status)
                            filteredAppointments = filteredAppointments.filter { it.status == statusEnum }
                        } catch (e: Exception) {
                            _state.update { it.copy(error = "Неизвестный статус: $status") }
                        }
                    }
                    
                    // Фильтр по типу
                    if (type != null) {
                        try {
                            val typeEnum = AppointmentType.valueOf(type)
                            filteredAppointments = filteredAppointments.filter { it.type == typeEnum }
                        } catch (e: Exception) {
                            _state.update { it.copy(error = "Неизвестный тип: $type") }
                        }
                    }
                    
                    // Поиск по тексту
                    if (searchQuery.isNotEmpty()) {
                        val query = searchQuery.lowercase()
                        filteredAppointments = filteredAppointments.filter { appointment ->
                            appointment.title.lowercase().contains(query) ||
                            appointment.description?.lowercase()?.contains(query) == true ||
                            appointment.clientName?.lowercase()?.contains(query) == true ||
                            appointment.propertyAddress?.lowercase()?.contains(query) == true ||
                            appointment.notes?.lowercase()?.contains(query) == true ||
                            // Расширенный поиск
                            formatDateString(appointment.startTime)?.lowercase()?.contains(query) == true ||
                            appointment.startTimeOfDay.toString().lowercase().contains(query) ||
                            (appointment.endTime - appointment.startTime).toString().contains(query) ||
                            appointment.type.toString().lowercase().contains(query) ||
                            appointment.status.toString().lowercase().contains(query) ||
                            appointment.participants.any { it.name.lowercase().contains(query) } ||
                            appointment.location?.lowercase()?.contains(query) == true ||
                            // Дополнительные поля
                            appointment.color.lowercase().contains(query) ||
                            appointment.attachments.any { it.lowercase().contains(query) }
                        }
                    }
                    
                    // Обновляем состояние с отфильтрованными встречами
                    _state.update { it.copy(
                        appointments = filteredAppointments,
                        isFiltered = true
                    )}
                    
                    // Выводим отладочную информацию
                    println("DEBUG: Применены фильтры. Найдено ${filteredAppointments.size} встреч.")
                    
                }.onFailure { error ->
                    _state.update { it.copy(error = "Ошибка при получении встреч: ${error.message}") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Ошибка при применении фильтров: ${e.message}") }
            }
        }
    }

    /**
     * Обновляет тип встречи
     */
    private fun updateAppointmentType(id: String, type: AppointmentType) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                
                // Получаем существующую встречу
                val appointmentResult = appointmentUseCases.getAppointment(id)
                appointmentResult.onSuccess { appointment ->
                    // Обновляем только тип и время обновления
                    val updatedAppointment = appointment.copy(
                        type = type,
                        updatedAt = System.currentTimeMillis()
                    )
                    
                    // Сохраняем обновленную встречу
                    val updateResult = appointmentUseCases.updateAppointment(updatedAppointment)
                    updateResult.onSuccess {
                        _state.update { currentState ->
                            // Обновляем встречу в текущем списке
                            val updatedAppointments = currentState.appointments.map { 
                                if (it.id == id) updatedAppointment else it 
                            }
                            
                            // Обновляем встречу в списке для выбранной даты
                            val updatedAppointmentsForSelectedDate = currentState.appointmentsForSelectedDate.map {
                                if (it.id == id) updatedAppointment else it
                            }
                            
                            currentState.copy(
                                appointments = updatedAppointments,
                                appointmentsForSelectedDate = updatedAppointmentsForSelectedDate,
                                isLoading = false,
                                error = null
                            )
                        }
                    }.onFailure { error ->
                        _state.update { it.copy(
                            isLoading = false,
                            error = "Ошибка при обновлении типа встречи: ${error.message}"
                        ) }
                    }
                }.onFailure { error ->
                    _state.update { it.copy(
                        isLoading = false,
                        error = "Ошибка при загрузке встречи: ${error.message}"
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "Ошибка при обновлении типа встречи: ${e.message}"
                ) }
            }
        }
    }

    /**
     * Форматирует дату в строку для поиска
     */
    private fun formatDateString(timestamp: Long?): String? {
        if (timestamp == null) return null
        val date = Date(timestamp)
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return formatter.format(date)
    }
} 