package com.realestateassistant.pro.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realestateassistant.pro.core.result.Result
import com.realestateassistant.pro.domain.model.Booking
import com.realestateassistant.pro.domain.model.BookingStatus
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.model.PropertyId
import com.realestateassistant.pro.domain.usecase.ClientUseCases
import com.realestateassistant.pro.domain.usecase.booking.BookingUseCases
import com.realestateassistant.pro.domain.usecase.PropertyUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject

/**
 * Состояние UI для экрана календаря бронирований
 */
data class BookingCalendarState(
    val selectedPropertyId: String? = null,
    val selectedProperty: Property? = null,
    val bookings: List<Booking> = emptyList(),
    val clients: List<Client> = emptyList(),
    val selectedBooking: Booking? = null,
    val selectedStartDate: LocalDate? = null,
    val selectedEndDate: LocalDate? = null,
    val selectedClient: Client? = null,
    val isSelectionMode: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isBookingDialogVisible: Boolean = false,
    val isInfoMode: Boolean = true, // Флаг для определения режима отображения (инфо или редактирование)
    val availableTimeSlots: List<AvailableTimeSlot> = emptyList(), // Доступные окна для бронирования
    val allBookings: List<Booking> = emptyList(),
    val filteredBookings: List<Booking> = emptyList(),
    val allProperties: List<PropertyId> = emptyList(), // Список всех идентификаторов объектов недвижимости
    val tempBookingAmount: Double? = null // Временное хранение рекомендуемой суммы для нового бронирования
)

/**
 * Класс, представляющий доступное окно для бронирования
 */
data class AvailableTimeSlot(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val durationDays: Int
)

/**
 * События UI для экрана календаря бронирований
 */
sealed class BookingCalendarEvent {
    data class SelectProperty(val propertyId: String) : BookingCalendarEvent()
    data class SelectDate(val date: LocalDate) : BookingCalendarEvent()
    data class SelectClient(val client: Client) : BookingCalendarEvent()
    data class SelectBooking(val booking: Booking) : BookingCalendarEvent()
    data class UpdateDates(val start: LocalDate, val end: LocalDate) : BookingCalendarEvent()
    data class CreateBooking(
        val propertyId: String,
        val clientId: String?,
        val startDate: LocalDate,
        val endDate: LocalDate,
        val amount: Double,
        val guestsCount: Int = 1,
        val notes: String? = null
    ) : BookingCalendarEvent()
    data class UpdateBooking(
        val bookingId: String,
        val clientId: String?,
        val startDate: LocalDate,
        val endDate: LocalDate,
        val amount: Double,
        val guestsCount: Int = 1,
        val notes: String? = null
    ) : BookingCalendarEvent()
    data class DeleteBooking(val bookingId: String) : BookingCalendarEvent()
    data class UpdateBookingStatus(val bookingId: String, val status: BookingStatus) : BookingCalendarEvent()
    object ShowBookingDialog : BookingCalendarEvent()
    object HideBookingDialog : BookingCalendarEvent()
    object SwitchToEditMode : BookingCalendarEvent()
    object LoadAllBookings : BookingCalendarEvent() // Загрузка всех бронирований
    object LoadBookingsList : BookingCalendarEvent() // Загрузка списка бронирований для экрана списка
    object UpdateBookingStatusesAutomatically : BookingCalendarEvent() // Автоматическое обновление статусов
    object ConfirmDateSelection : BookingCalendarEvent()
    object CancelDateSelection : BookingCalendarEvent()
}

@HiltViewModel
class BookingCalendarViewModel @Inject constructor(
    private val bookingUseCases: BookingUseCases,
    private val clientUseCases: ClientUseCases,
    private val propertyUseCases: PropertyUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _state = MutableStateFlow(BookingCalendarState())
    val state: StateFlow<BookingCalendarState> = _state.asStateFlow()
    
    init {
        loadClients()
        
        // Загружаем все бронирования при инициализации
        // Используем loadAllBookings, который подходит для календаря
        loadAllBookings()
        
        // Регулярное автоматическое обновление статусов бронирований
        updateBookingStatusesAutomatically()
    }
    
    fun onEvent(event: BookingCalendarEvent) {
        when (event) {
            is BookingCalendarEvent.SelectProperty -> {
                selectProperty(event.propertyId)
            }
            is BookingCalendarEvent.SelectDate -> {
                selectDate(event.date)
            }
            is BookingCalendarEvent.SelectClient -> {
                _state.update { it.copy(selectedClient = event.client) }
            }
            is BookingCalendarEvent.SelectBooking -> {
                _state.update { 
                    it.copy(
                        selectedBooking = event.booking,
                        isBookingDialogVisible = true,
                        isInfoMode = true
                    ) 
                }
            }
            is BookingCalendarEvent.UpdateDates -> {
                _state.update { 
                    it.copy(
                        selectedStartDate = event.start,
                        selectedEndDate = event.end
                    ) 
                }
            }
            is BookingCalendarEvent.CreateBooking -> {
                createBooking(
                    event.propertyId,
                    event.clientId,
                    event.startDate,
                    event.endDate,
                    event.amount,
                    event.guestsCount,
                    event.notes
                )
            }
            is BookingCalendarEvent.UpdateBooking -> {
                updateBooking(
                    event.bookingId,
                    event.clientId,
                    event.startDate,
                    event.endDate,
                    event.amount,
                    event.guestsCount,
                    event.notes
                )
            }
            is BookingCalendarEvent.DeleteBooking -> {
                deleteBooking(event.bookingId)
            }
            is BookingCalendarEvent.UpdateBookingStatus -> {
                updateBookingStatus(event.bookingId, event.status)
            }
            is BookingCalendarEvent.ShowBookingDialog -> {
                // Показываем диалог и рассчитываем предварительную цену
                _state.update { currentState ->
                    val startDate = currentState.selectedStartDate ?: LocalDate.now()
                    val endDate = currentState.selectedEndDate ?: startDate.plusDays(1)
                    val recommendedPrice = calculateRecommendedPrice(startDate, endDate, currentState.selectedProperty)

                    currentState.copy(
                        isBookingDialogVisible = true,
                        tempBookingAmount = recommendedPrice
                    )
                }
            }
            is BookingCalendarEvent.HideBookingDialog -> {
                _state.update { 
                    it.copy(
                        isBookingDialogVisible = false,
                        selectedBooking = null,
                        error = null,
                        tempBookingAmount = null
                    ) 
                }
            }
            is BookingCalendarEvent.SwitchToEditMode -> {
                _state.update { currentState ->
                    // Получаем текущее бронирование для редактирования
                    val bookingToEdit = currentState.selectedBooking
                    
                    if (bookingToEdit != null) {
                        // Конвертируем даты из timestamp в LocalDate
                        val startDate = Instant.ofEpochMilli(bookingToEdit.startDate)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                        val endDate = Instant.ofEpochMilli(bookingToEdit.endDate)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                        
                        // Находим клиента
                        val client = currentState.clients.find { it.id == bookingToEdit.clientId }
                        
                        // Обновляем состояние с информацией из выбранного бронирования
                        currentState.copy(
                            isInfoMode = false,
                            selectedStartDate = startDate,
                            selectedEndDate = endDate,
                            selectedClient = client
                        )
                    } else {
                        // Если бронирование не выбрано, просто меняем режим
                        currentState.copy(isInfoMode = false)
                    }
                }
            }
            is BookingCalendarEvent.LoadAllBookings -> {
                loadAllBookings()
            }
            is BookingCalendarEvent.LoadBookingsList -> {
                loadBookingsList()
            }
            is BookingCalendarEvent.UpdateBookingStatusesAutomatically -> {
                updateBookingStatusesAutomatically()
            }
            is BookingCalendarEvent.ConfirmDateSelection -> {
                _state.update { currentState -> 
                    val startDate = currentState.selectedStartDate ?: LocalDate.now()
                    val endDate = currentState.selectedEndDate ?: startDate.plusDays(1)
                    val recommendedPrice = calculateRecommendedPrice(startDate, endDate, currentState.selectedProperty)
                
                    currentState.copy(
                        isSelectionMode = false,
                        isBookingDialogVisible = true,
                        isInfoMode = false,
                        // Создаем временное бронирование с рекомендуемой ценой, но без ID
                        // Это не сохраняемое бронирование, а только для отображения в диалоге
                        selectedBooking = null, // Убираем временное бронирование, чтобы не вызывать UpdateBooking
                        // Храним рекомендуемую цену в другом месте
                        tempBookingAmount = recommendedPrice
                    )
                }
            }
            is BookingCalendarEvent.CancelDateSelection -> {
                _state.update { 
                    it.copy(
                        isSelectionMode = false,
                        selectedStartDate = null,
                        selectedEndDate = null
                    )
                }
            }
        }
    }
    
    private fun selectProperty(propertyId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedPropertyId = propertyId,
                    isLoading = true,
                    error = null
                )
            }
            
            try {
                // Загружаем информацию о выбранном объекте недвижимости
                val propertyResult = propertyUseCases.getProperty(propertyId)
                val property = propertyResult.getOrNull()
                if (property != null) {
                    _state.update {
                        it.copy(selectedProperty = property)
                    }
                } else {
                    println("DEBUG: Не удалось загрузить информацию об объекте: $propertyId")
                }
                
                bookingUseCases.observeBookingsByPropertyUseCase(propertyId)
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(5000),
                        initialValue = emptyList()
                    )
                    .collect { bookings ->
                        // Логируем полученные бронирования для отладки
                        println("DEBUG: Загружено ${bookings.size} бронирований")
                        if (bookings.isNotEmpty()) {
                            println("DEBUG: Первое бронирование: id=${bookings[0].id}, propertyId=${bookings[0].propertyId}, " +
                                    "startDate=${LocalDate.ofEpochDay(bookings[0].startDate / 86400000)}, " +
                                    "endDate=${LocalDate.ofEpochDay(bookings[0].endDate / 86400000)}, " +
                                    "status=${bookings[0].status}")
                        }
                        
                        _state.update { state ->
                            state.copy(
                                bookings = bookings,
                                isLoading = false,
                                availableTimeSlots = calculateAvailableTimeSlots(bookings)
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка загрузки бронирований: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Загружает все бронирования (основной метод для календаря)
     */
    private fun loadAllBookings() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val selectedPropertyId = _state.value.selectedPropertyId
                
                // Если выбран конкретный объект
                if (selectedPropertyId != null && selectedPropertyId.isNotEmpty()) {
                    bookingUseCases.observeBookingsByPropertyUseCase(selectedPropertyId)
                        .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5000),
                            initialValue = emptyList()
                        )
                        .collect { bookings ->
                            println("DEBUG [loadAllBookings]: Загружено ${bookings.size} бронирований")
                            if (bookings.isNotEmpty()) {
                                val firstBooking = bookings.first()
                                println("DEBUG [loadAllBookings]: Первое бронирование: id=${firstBooking.id}, propertyId=${firstBooking.propertyId}, " +
                                        "startDate=${LocalDate.ofEpochDay(firstBooking.startDate / 86400000)}, " +
                                        "endDate=${LocalDate.ofEpochDay(firstBooking.endDate / 86400000)}, " +
                                        "status=${firstBooking.status}")
                            }
                            
                            _state.update { state ->
                                state.copy(
                                    isLoading = false,
                                    bookings = bookings
                                )
                            }
                        }
                } else {
                    // Если объект не выбран, загружаем все бронирования
                    bookingUseCases.observeAllBookingsUseCase()
                        .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5000),
                            initialValue = emptyList()
                        )
                        .collect { bookings ->
                            println("DEBUG [loadAllBookings ALL]: Загружено ${bookings.size} бронирований")
                            if (bookings.isNotEmpty()) {
                                val firstBooking = bookings.first()
                                println("DEBUG [loadAllBookings ALL]: Первое бронирование: id=${firstBooking.id}, propertyId=${firstBooking.propertyId}, " +
                                        "startDate=${LocalDate.ofEpochDay(firstBooking.startDate / 86400000)}, " +
                                        "endDate=${LocalDate.ofEpochDay(firstBooking.endDate / 86400000)}, " +
                                        "status=${firstBooking.status}")
                            }
                            
                            _state.update { state ->
                                state.copy(
                                    isLoading = false,
                                    bookings = bookings
                                )
                            }
                        }
                }
            } catch (e: Exception) {
                println("DEBUG [loadAllBookings]: Ошибка: ${e.message}")
                e.printStackTrace()
                _state.update { 
                    it.copy(
                        error = "Ошибка загрузки бронирований: ${e.message}",
                        isLoading = false
                    ) 
                }
            }
        }
    }
    
    /**
     * Загружает список всех бронирований для экрана списка
     * Отличается от loadAllBookings тем, что явно сохраняет бронирования в allBookings и filteredBookings
     */
    private fun loadBookingsList() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // Используем observeAllBookingsUseCase для получения всех бронирований
                bookingUseCases.observeAllBookingsUseCase()
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(5000),
                        initialValue = emptyList()
                    )
                    .collect { bookings ->
                        // Логируем полученные бронирования
                        println("DEBUG [loadBookingsList]: Загружено ${bookings.size} бронирований")
                        if (bookings.isNotEmpty()) {
                            val firstBooking = bookings.first()
                            println("DEBUG [loadBookingsList]: Первое бронирование: id=${firstBooking.id}, " +
                                    "propertyId=${firstBooking.propertyId}, " +
                                    "startDate=${LocalDate.ofEpochDay(firstBooking.startDate / 86400000)}, " +
                                    "endDate=${LocalDate.ofEpochDay(firstBooking.endDate / 86400000)}, " +
                                    "status=${firstBooking.status}")
                        }
                        
                        _state.update { state ->
                            state.copy(
                                bookings = bookings,
                                allBookings = bookings,
                                filteredBookings = bookings,
                                isLoading = false
                            )
                        }
                    }
            } catch (e: Exception) {
                println("DEBUG [loadBookingsList]: Ошибка при загрузке: ${e.message}")
                e.printStackTrace()
                _state.update { 
                    it.copy(
                        error = "Ошибка загрузки списка бронирований: ${e.message}",
                        isLoading = false
                    ) 
                }
            }
        }
    }
    
    /**
     * Загружает все бронирования для всех объектов
     */
    private fun loadAllBookingsList() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // Используем observeAllBookingsUseCase для получения всех бронирований
                bookingUseCases.observeAllBookingsUseCase()
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(5000),
                        initialValue = emptyList()
                    )
                    .collect { bookings ->
                        // Логируем полученные бронирования
                        println("DEBUG [loadAllBookingsList]: Загружено ${bookings.size} бронирований")
                        if (bookings.isNotEmpty()) {
                            val firstBooking = bookings.first()
                            println("DEBUG [loadAllBookingsList]: Первое бронирование: id=${firstBooking.id}, " +
                                    "propertyId=${firstBooking.propertyId}, " +
                                    "startDate=${LocalDate.ofEpochDay(firstBooking.startDate / 86400000)}, " +
                                    "endDate=${LocalDate.ofEpochDay(firstBooking.endDate / 86400000)}, " +
                                    "status=${firstBooking.status}")
                        }
                        
                        // Обновляем состояние всех объектов недвижимости
                        _state.update { state ->
                            val propIds = bookings.map { it.propertyId }.distinct()
                            state.copy(
                                isLoading = false,
                                allBookings = bookings,
                                allProperties = propIds.map { PropertyId(it) }
                            )
                        }
                    }
            } catch (e: Exception) {
                println("DEBUG [loadAllBookingsList]: Ошибка: ${e.message}")
                e.printStackTrace()
                _state.update { 
                    it.copy(
                        error = "Ошибка загрузки бронирований: ${e.message}",
                        isLoading = false
                    ) 
                }
            }
        }
    }
    
    /**
     * Запускает автоматическое обновление статусов бронирований
     */
    private fun updateBookingStatusesAutomatically() {
        viewModelScope.launch {
            try {
                val result = bookingUseCases.updateBookingStatusesAutomaticallyUseCase()
                
                if (result.isSuccess) {
                    // Обновляем список бронирований, если требуется
                    if (_state.value.selectedPropertyId != null) {
                        selectProperty(_state.value.selectedPropertyId!!)
                    }
                }
            } catch (e: Exception) {
                // Логируем ошибку, но не обновляем состояние UI
                println("Ошибка автоматического обновления статусов: ${e.message}")
            }
        }
    }
    
    /**
     * Рассчитывает доступные окна для бронирования на основе существующих бронирований
     */
    private fun calculateAvailableTimeSlots(bookings: List<Booking>): List<AvailableTimeSlot> {
        // Отбираем только активные бронирования (не отмененные и не просроченные)
        val activeBookings = bookings.filter { 
            it.status == BookingStatus.PENDING || 
            it.status == BookingStatus.CONFIRMED || 
            it.status == BookingStatus.ACTIVE
        }
        
        // Если нет активных бронирований, то доступно всё время
        if (activeBookings.isEmpty()) {
            val today = LocalDate.now()
            val threeMonthsLater = today.plusMonths(3)
            return listOf(
                AvailableTimeSlot(
                    startDate = today,
                    endDate = threeMonthsLater,
                    durationDays = ChronoUnit.DAYS.between(today, threeMonthsLater).toInt()
                )
            )
        }
        
        // Сортируем бронирования по дате начала
        val sortedBookings = activeBookings.sortedBy { it.startDate }
        
        val slots = mutableListOf<AvailableTimeSlot>()
        val today = LocalDate.now()
        var lastEndDate = today
        
        // Находим промежутки между бронированиями
        for (booking in sortedBookings) {
            val bookingStartDate = LocalDate.ofEpochDay(booking.startDate / 86400000)
            
            // Если есть промежуток между последней датой окончания и датой начала текущего бронирования
            if (bookingStartDate.isAfter(lastEndDate)) {
                val durationDays = ChronoUnit.DAYS.between(lastEndDate, bookingStartDate).toInt()
                // Добавляем слот только если его длительность >= 1 дня
                if (durationDays >= 1) {
                    slots.add(
                        AvailableTimeSlot(
                            startDate = lastEndDate,
                            endDate = bookingStartDate.minusDays(1),
                            durationDays = durationDays
                        )
                    )
                }
            }
            
            // Обновляем последнюю дату окончания
            val bookingEndDate = LocalDate.ofEpochDay(booking.endDate / 86400000)
            if (bookingEndDate.isAfter(lastEndDate)) {
                lastEndDate = bookingEndDate.plusDays(1) // +1 день после окончания
            }
        }
        
        // Добавляем слот после последнего бронирования до +3 месяца
        val threeMonthsLater = today.plusMonths(3)
        if (lastEndDate.isBefore(threeMonthsLater)) {
            val durationDays = ChronoUnit.DAYS.between(lastEndDate, threeMonthsLater).toInt()
            slots.add(
                AvailableTimeSlot(
                    startDate = lastEndDate,
                    endDate = threeMonthsLater,
                    durationDays = durationDays
                )
            )
        }
        
        return slots
    }
    
    /**
     * Вычисляет рекомендуемую стоимость бронирования на основе выбранных дат и объекта недвижимости
     * 
     * @param startDate начальная дата бронирования
     * @param endDate конечная дата бронирования
     * @param property объект недвижимости
     * @return рекомендуемая стоимость бронирования
     */
    private fun calculateRecommendedPrice(startDate: LocalDate, endDate: LocalDate, property: Property?): Double {
        if (property == null) return 0.0
        
        // Рассчитываем количество ночей
        val nightsCount = ChronoUnit.DAYS.between(startDate, endDate)
        
        // Определяем, является ли бронирование долгосрочным (более 30 дней)
        val isLongTerm = nightsCount > 30
        
        if (isLongTerm) {
            // Для долгосрочной аренды используем месячную стоимость
            val monthlyPrice = property.monthlyRent ?: 0.0
            
            // Проверяем, есть ли сезонные цены для долгосрочной аренды
            val currentMonth = LocalDate.now().monthValue
            val isSummerSeason = currentMonth in 5..9 // Май-Сентябрь
            
            val adjustedMonthlyPrice = if (isSummerSeason && property.summerMonthlyRent != null) {
                property.summerMonthlyRent
            } else if (!isSummerSeason && property.winterMonthlyRent != null) {
                property.winterMonthlyRent
            } else {
                monthlyPrice
            }
            
            // Рассчитываем стоимость на основе количества месяцев
            val months = nightsCount / 30.0
            return adjustedMonthlyPrice * months
        } else {
            // Для краткосрочной аренды проверяем наличие сезонных цен
            var dailyPrice = property.dailyPrice ?: 0.0
            
            // Проверяем, попадает ли бронирование в период сезонной цены
            property.seasonalPrices.forEach { seasonalPrice ->
                val seasonStartDate = Instant.ofEpochMilli(seasonalPrice.startDate)
                    .atZone(ZoneId.systemDefault()).toLocalDate()
                val seasonEndDate = Instant.ofEpochMilli(seasonalPrice.endDate)
                    .atZone(ZoneId.systemDefault()).toLocalDate()
                
                // Если текущая дата находится в диапазоне сезона, используем сезонную цену
                val today = LocalDate.now()
                if ((today.isEqual(seasonStartDate) || today.isAfter(seasonStartDate)) &&
                    (today.isEqual(seasonEndDate) || today.isBefore(seasonEndDate))) {
                    dailyPrice = seasonalPrice.price
                }
            }
            
            // Применяем скидки для более длительного проживания, если они указаны
            val weeklyDiscount = property.weeklyDiscount ?: 0.0
            val monthlyDiscount = property.monthlyDiscount ?: 0.0
            
            val totalPrice = when {
                nightsCount >= 30 && monthlyDiscount > 0 -> {
                    dailyPrice * nightsCount * (1 - monthlyDiscount / 100)
                }
                nightsCount >= 7 && weeklyDiscount > 0 -> {
                    dailyPrice * nightsCount * (1 - weeklyDiscount / 100)
                }
                else -> {
                    dailyPrice * nightsCount
                }
            }
            
            return totalPrice
        }
    }
    
    private fun selectDate(date: LocalDate) {
        val currentState = _state.value
        
        if (currentState.isSelectionMode) {
            // Уже в режиме выбора, определяем, что делать с датой
            if (currentState.selectedStartDate == null) {
                // Выбираем начальную дату
                _state.update { it.copy(selectedStartDate = date) }
            } else if (currentState.selectedEndDate == null) {
                // Выбираем конечную дату, обеспечивая правильный порядок
                if (date.isBefore(currentState.selectedStartDate)) {
                    _state.update {
                        it.copy(
                            selectedStartDate = date,
                            selectedEndDate = currentState.selectedStartDate
                        )
                    }
                } else {
                    _state.update { it.copy(selectedEndDate = date) }
                }
            } else {
                // Сбрасываем выбор и начинаем заново
                _state.update {
                    it.copy(
                        selectedStartDate = date,
                        selectedEndDate = null
                    )
                }
            }
        } else {
            // Проверяем, есть ли бронирование на эту дату
            val booking = currentState.bookings.find { booking ->
                val bookingStartDate = LocalDate.ofEpochDay(booking.startDate / 86400000)
                val bookingEndDate = LocalDate.ofEpochDay(booking.endDate / 86400000)
                
                date.isEqual(bookingStartDate) || date.isEqual(bookingEndDate) ||
                        (date.isAfter(bookingStartDate) && date.isBefore(bookingEndDate))
            }
            
            if (booking != null) {
                // Есть бронирование, показываем информацию о нём
                _state.update {
                    it.copy(
                        selectedBooking = booking,
                        isBookingDialogVisible = true,
                        isInfoMode = true // Начинаем с режима информации
                    )
                }
            } else {
                // Нет бронирования, начинаем выбор для создания нового
                _state.update {
                    it.copy(
                        isSelectionMode = true,
                        selectedStartDate = date,
                        selectedEndDate = null
                    )
                }
            }
        }
    }
    
    private fun createBooking(
        propertyId: String,
        clientId: String?,
        startDate: LocalDate,
        endDate: LocalDate,
        amount: Double,
        guestsCount: Int,
        notes: String?
    ) {
        val currentState = _state.value
        
        if (currentState.selectedPropertyId == null || 
            currentState.selectedStartDate == null || 
            currentState.selectedEndDate == null) {
            _state.update {
                it.copy(
                    error = "Не выбран объект или даты бронирования"
                )
            }
            return
        }
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                // Преобразуем LocalDate в миллисекунды
                val startDateMillis = startDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
                val endDateMillis = endDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
                
                // Определяем, является ли бронирование долгосрочным (более 30 дней)
                val nightsCount = ChronoUnit.DAYS.between(startDate, endDate)
                val isLongTerm = nightsCount > 30
                
                // Создаем новое бронирование
                val newBooking = Booking(
                    id = UUID.randomUUID().toString(),
                    propertyId = propertyId,
                    clientId = clientId,
                    startDate = startDateMillis,
                    endDate = endDateMillis,
                    status = BookingStatus.PENDING,
                    totalAmount = amount,
                    guestsCount = guestsCount,
                    notes = notes,
                    
                    // Дополнительные поля для долгосрочной аренды
                    rentPeriodMonths = if (isLongTerm) (nightsCount / 30).toInt() else null,
                    monthlyPaymentAmount = if (isLongTerm) currentState.selectedProperty?.monthlyRent else null
                )
                
                val result = bookingUseCases.addBookingUseCase(newBooking)
                
                result.onSuccess { _ ->
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            isBookingDialogVisible = false,
                            selectedStartDate = null,
                            selectedEndDate = null
                        ) 
                    }
                    
                    // Перезагружаем список бронирований, чтобы обновить календарь
                    loadAllBookings()
                    
                }.onFailure { error ->
                    _state.update { 
                        it.copy(
                            error = "Ошибка создания бронирования: ${error.message}",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        error = "Ошибка создания бронирования: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun updateBooking(
        bookingId: String,
        clientId: String?,
        startDate: LocalDate,
        endDate: LocalDate,
        amount: Double,
        guestsCount: Int,
        notes: String?
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                val result = bookingUseCases.updateBookingUseCase(
                    Booking(
                        id = bookingId,
                        propertyId = _state.value.selectedPropertyId!!,
                        clientId = clientId,
                        startDate = startDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
                        endDate = endDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
                        status = BookingStatus.PENDING,
                        totalAmount = amount,
                        guestsCount = guestsCount,
                        notes = notes
                    )
                )
                
                result.onSuccess { _ ->
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            isBookingDialogVisible = false,
                            selectedBooking = null
                        ) 
                    }
                    
                    // Перезагружаем список бронирований, чтобы обновить календарь
                    loadAllBookings()
                    
                }.onFailure { error ->
                    _state.update { 
                        it.copy(
                            error = "Ошибка обновления бронирования: ${error.message}",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        error = "Ошибка обновления бронирования: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun deleteBooking(bookingId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                val result = bookingUseCases.deleteBookingUseCase(bookingId)
                
                result.onSuccess { _ ->
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            isBookingDialogVisible = false,
                            selectedBooking = null
                        ) 
                    }
                    
                    // Перезагружаем список бронирований, чтобы обновить календарь
                    loadAllBookings()
                }.onFailure { error ->
                    _state.update { 
                        it.copy(
                            error = "Ошибка удаления бронирования: ${error.message}",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        error = "Ошибка удаления бронирования: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun updateBookingStatus(bookingId: String, status: BookingStatus) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                val result = bookingUseCases.updateBookingStatusUseCase(bookingId, status)
                
                result.onSuccess { _ ->
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            isBookingDialogVisible = false,
                            selectedBooking = null
                        ) 
                    }
                    
                    // Перезагружаем список бронирований, чтобы обновить календарь
                    loadAllBookings()
                    
                }.onFailure { error ->
                    _state.update { 
                        it.copy(
                            error = "Ошибка при изменении статуса бронирования: ${error.message}",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        error = "Ошибка при изменении статуса бронирования: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * Загрузка списка клиентов
     */
    private fun loadClients() {
        viewModelScope.launch {
            try {
                val result = clientUseCases.getAllClients()
                result.onSuccess { clients ->
                    _state.update { it.copy(clients = clients) }
                }.onFailure { error ->
                    _state.update { it.copy(error = "Не удалось загрузить клиентов: ${error.message}") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Ошибка: ${e.message}") }
            }
        }
    }
} 