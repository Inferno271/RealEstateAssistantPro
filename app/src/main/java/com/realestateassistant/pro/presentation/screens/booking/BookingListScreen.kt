package com.realestateassistant.pro.presentation.screens.booking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.domain.model.Booking
import com.realestateassistant.pro.domain.model.BookingStatus
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.presentation.components.calendar.BookingInfoDialog
import com.realestateassistant.pro.presentation.viewmodels.BookingCalendarEvent
import com.realestateassistant.pro.presentation.viewmodels.BookingCalendarViewModel
import com.realestateassistant.pro.presentation.viewmodel.ClientViewModel
import com.realestateassistant.pro.presentation.viewmodel.PropertyViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

// Add extension property for displayName
val BookingStatus.displayName: String
    get() = when(this) {
        BookingStatus.PENDING -> "Ожидает подтверждения"
        BookingStatus.CONFIRMED -> "Подтверждено"
        BookingStatus.ACTIVE -> "Активно"
        BookingStatus.COMPLETED -> "Завершено"
        BookingStatus.CANCELLED -> "Отменено"
        BookingStatus.EXPIRED -> "Просрочено"
    }

/**
 * Типы поиска для бронирований
 */
enum class SearchType {
    ALL, PROPERTY, CLIENT, DATES
}

/**
 * Экран списка бронирований с фильтрацией, сортировкой и быстрыми действиями
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreateBooking: (String) -> Unit,
    onNavigateToBookingDetails: (String) -> Unit,
    onNavigateToPropertyDetails: (String) -> Unit,
    onNavigateToClientDetails: (String) -> Unit,
    onNavigateToCalendar: (String) -> Unit,
    bookingViewModel: BookingCalendarViewModel = hiltViewModel(),
    propertyViewModel: PropertyViewModel = hiltViewModel(),
    clientViewModel: ClientViewModel = hiltViewModel()
) {
    val bookingState by bookingViewModel.state.collectAsState()
    val propertyState by propertyViewModel.properties.collectAsState()
    val clientState by clientViewModel.clients.collectAsState()
    
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Состояние для диалога с деталями бронирования
    var showBookingDialog by remember { mutableStateOf(false) }
    var selectedBooking by remember { mutableStateOf<Booking?>(null) }
    
    // Состояние для фильтров и сортировки
    var showFilterMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var selectedPropertyId by remember { mutableStateOf<String?>(null) }
    var selectedStatuses by remember { mutableStateOf<Set<BookingStatus>>(emptySet()) }
    var sortOption by remember { mutableStateOf("dateDesc") }
    
    // Состояние для поиска
    var searchQuery by remember { mutableStateOf("") }
    var searchType by remember { mutableStateOf(SearchType.ALL) }
    
    // Отображаем сообщение об ошибке, если оно есть
    LaunchedEffect(bookingState.error) {
        bookingState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }
    
    // Загружаем данные при первом открытии экрана
    LaunchedEffect(Unit) {
        propertyViewModel.loadProperties()
        clientViewModel.loadClients()
        
        // Загружаем все бронирования используя специальный метод для списка
        bookingViewModel.onEvent(BookingCalendarEvent.LoadBookingsList)
        
        // Обновляем статусы бронирований автоматически
        bookingViewModel.onEvent(BookingCalendarEvent.UpdateBookingStatusesAutomatically)
    }
    
    // Добавляем диагностическую информацию о количестве загруженных бронирований
    LaunchedEffect(bookingState) {
        println("DEBUG [BookingListScreen]: Текущее состояние:")
        println("- bookings: ${bookingState.bookings.size}")
        println("- allBookings: ${bookingState.allBookings.size}")
        println("- filteredBookings: ${bookingState.filteredBookings.size}")
        println("- selectedPropertyId: ${bookingState.selectedPropertyId}")
        println("- isLoading: ${bookingState.isLoading}")
        println("- error: ${bookingState.error}")
    }
    
    // Применяем фильтры к бронированиям
    val filteredBookings = remember(
        bookingState.allBookings,
        selectedPropertyId,
        selectedStatuses,
        searchQuery,
        searchType
    ) {
        bookingState.allBookings
            .filter { booking ->
                // Фильтрация по объекту недвижимости
                (selectedPropertyId == null || selectedPropertyId.toString().isEmpty() || 
                 booking.propertyId == selectedPropertyId)
            }
            .filter { booking ->
                // Фильтрация по статусу
                if (selectedStatuses.isEmpty()) true else booking.status in selectedStatuses
            }
            .filter { booking ->
                // Фильтрация по поисковому запросу
                if (searchQuery.isBlank()) return@filter true
                
                when (searchType) {
                    SearchType.PROPERTY -> {
                        val property = propertyState.find { it.id == booking.propertyId }
                        property?.address?.contains(searchQuery, ignoreCase = true) ?: 
                        booking.propertyId.contains(searchQuery, ignoreCase = true)
                    }
                    SearchType.CLIENT -> {
                        val client = clientState.find { it.id == booking.clientId }
                        client?.fullName?.contains(searchQuery, ignoreCase = true) ?: 
                        (booking.clientId?.contains(searchQuery, ignoreCase = true) ?: false)
                    }
                    SearchType.DATES -> {
                        val startDate = Instant.ofEpochMilli(booking.startDate)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                        val endDate = Instant.ofEpochMilli(booking.endDate)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                        
                        startDate.format(dateFormatter).contains(searchQuery, ignoreCase = true) || 
                        endDate.format(dateFormatter).contains(searchQuery, ignoreCase = true)
                    }
                    SearchType.ALL -> {
                        // Поиск по всем полям
                        val property = propertyState.find { it.id == booking.propertyId }
                        val client = clientState.find { it.id == booking.clientId }
                        val startDate = Instant.ofEpochMilli(booking.startDate)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                        val endDate = Instant.ofEpochMilli(booking.endDate)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                        
                        property?.address?.contains(searchQuery, ignoreCase = true) ?: false ||
                        booking.propertyId.contains(searchQuery, ignoreCase = true) ||
                        client?.fullName?.contains(searchQuery, ignoreCase = true) ?: false ||
                        (booking.clientId?.contains(searchQuery, ignoreCase = true) ?: false) ||
                        startDate.format(dateFormatter).contains(searchQuery, ignoreCase = true) || 
                        endDate.format(dateFormatter).contains(searchQuery, ignoreCase = true) ||
                        booking.id.contains(searchQuery, ignoreCase = true) ||
                        booking.notes?.contains(searchQuery, ignoreCase = true) ?: false
                    }
                }
            }
    }
    
    val sortedBookings = remember(filteredBookings, sortOption) {
        when (sortOption) {
            "dateAsc" -> filteredBookings.sortedBy { it.startDate }
            "dateDesc" -> filteredBookings.sortedByDescending { it.startDate }
            "priceAsc" -> filteredBookings.sortedBy { it.totalAmount }
            "priceDesc" -> filteredBookings.sortedByDescending { it.totalAmount }
            else -> filteredBookings
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Бронирования") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    // Кнопка сортировки
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Сортировка")
                        }
                        
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("По дате (сначала новые)") },
                                onClick = { 
                                    sortOption = "dateDesc"
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("По дате (сначала старые)") },
                                onClick = { 
                                    sortOption = "dateAsc"
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("По цене (сначала дорогие)") },
                                onClick = { 
                                    sortOption = "priceDesc"
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("По цене (сначала дешевые)") },
                                onClick = { 
                                    sortOption = "priceAsc"
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                    
                    // Кнопка фильтрации
                    IconButton(onClick = { showFilterMenu = !showFilterMenu }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Фильтры")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { 
                    // При нажатии на кнопку создания, сначала нужно выбрать объект недвижимости
                    if (propertyState.isNotEmpty()) {
                        onNavigateToCreateBooking(propertyState.first().id)
                    } else {
                        // Если объектов нет, показываем сообщение
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Сначала добавьте объект недвижимости")
                        }
                    }
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Создать") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Секция фильтров по статусу
            AnimatedVisibility(
                visible = showFilterMenu,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Фильтры по статусу",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Чипсы с фильтрами по статусу
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        // Чип "Все"
                        item {
                            FilterChip(
                                selected = selectedStatuses.isEmpty(),
                                onClick = { selectedStatuses = emptySet() },
                                label = { Text("Все") },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                        
                        // Чипы для каждого статуса
                        BookingStatus.values().forEach { status ->
                            item {
                                FilterChip(
                                    selected = status in selectedStatuses,
                                    onClick = {
                                        selectedStatuses = if (status in selectedStatuses) {
                                            selectedStatuses - status
                                        } else {
                                            selectedStatuses + status
                                        }
                                    },
                                    label = { Text(status.displayName) },
                                    modifier = Modifier.padding(horizontal = 4.dp),
                                    leadingIcon = {
                                        val color = when(status) {
                                            BookingStatus.PENDING -> Color(0xFFFBC02D)
                                            BookingStatus.CONFIRMED -> Color(0xFF2196F3)
                                            BookingStatus.ACTIVE -> Color(0xFF4CAF50)
                                            BookingStatus.COMPLETED -> Color(0xFF795548)
                                            BookingStatus.CANCELLED -> Color(0xFFF44336)
                                            BookingStatus.EXPIRED -> Color(0xFF9E9E9E)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                        )
                                    }
                                )
                            }
                        }
                    }
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )
                }
            }
            
            // Селектор объекта недвижимости
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Объект:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(end = 8.dp)
                )
                
                var showPropertySelector by remember { mutableStateOf(false) }
                
                OutlinedButton(
                    onClick = { showPropertySelector = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = propertyState.find { it.id == selectedPropertyId }?.address 
                            ?: "Все объекты"
                    )
                }
                
                // Выпадающее меню для выбора объекта недвижимости
                DropdownMenu(
                    expanded = showPropertySelector,
                    onDismissRequest = { showPropertySelector = false }
                ) {
                    // Опция "Все объекты"
                    DropdownMenuItem(
                        text = { Text("Все объекты") },
                        onClick = {
                            selectedPropertyId = null
                            showPropertySelector = false
                        }
                    )
                    
                    // Список объектов недвижимости
                    propertyState.forEach { property ->
                        DropdownMenuItem(
                            text = { Text(property.address) },
                            onClick = {
                                selectedPropertyId = property.id
                                showPropertySelector = false
                            }
                        )
                    }
                }
            }
            
            // Поле для поиска и выбора типа поиска
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Поиск...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Поиск"
                        )
                    },
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Выпадающее меню для выбора типа поиска
                var showSearchTypeMenu by remember { mutableStateOf(false) }
                
                Box {
                    IconButton(onClick = { showSearchTypeMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterAlt,
                            contentDescription = "Тип поиска"
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showSearchTypeMenu,
                        onDismissRequest = { showSearchTypeMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Везде") },
                            onClick = {
                                searchType = SearchType.ALL
                                showSearchTypeMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("По объекту") },
                            onClick = {
                                searchType = SearchType.PROPERTY
                                showSearchTypeMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("По клиенту") },
                            onClick = {
                                searchType = SearchType.CLIENT
                                showSearchTypeMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("По датам") },
                            onClick = {
                                searchType = SearchType.DATES
                                showSearchTypeMenu = false
                            }
                        )
                    }
                }
            }
            
            // Список бронирований
            if (bookingState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (sortedBookings.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Нет бронирований",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = if (selectedStatuses.isEmpty()) 
                                "Создайте новое бронирование с помощью кнопки внизу экрана" 
                            else 
                                "Измените фильтры для отображения бронирований",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        // Диагностическая информация для отладки
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Диагностика: В state загружено бронирований: ${bookingState.bookings.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Всего бронирований: ${bookingState.allBookings.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Отфильтрованных: ${filteredBookings.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { 
                                bookingViewModel.onEvent(BookingCalendarEvent.LoadBookingsList)
                            }
                        ) {
                            Text("Обновить список")
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sortedBookings) { booking ->
                        BookingListItem(
                            booking = booking,
                            property = propertyState.find { it.id == booking.propertyId },
                            client = clientState.find { it.id == booking.clientId },
                            onClick = {
                                selectedBooking = booking
                                showBookingDialog = true
                            },
                            onPropertyClick = { propertyId ->
                                onNavigateToPropertyDetails(propertyId)
                            },
                            onClientClick = { clientId ->
                                onNavigateToClientDetails(clientId)
                            },
                            onCalendarClick = { propertyId ->
                                onNavigateToCalendar(propertyId)
                            },
                            snackbarHostState = snackbarHostState,
                            coroutineScope = coroutineScope
                        )
                    }
                }
            }
        }
    }
    
    // Диалог информации о бронировании
    if (showBookingDialog && selectedBooking != null) {
        BookingInfoDialog(
            booking = selectedBooking!!,
            client = clientState.find { it.id == selectedBooking!!.clientId },
            onClose = {
                showBookingDialog = false
                selectedBooking = null
            },
            onEdit = {
                onNavigateToBookingDetails(selectedBooking!!.id)
                showBookingDialog = false
            },
            onDelete = {
                bookingViewModel.onEvent(BookingCalendarEvent.DeleteBooking(selectedBooking!!.id))
                showBookingDialog = false
            },
            onStatusChange = { newStatus ->
                bookingViewModel.onEvent(BookingCalendarEvent.UpdateBookingStatus(selectedBooking!!.id, newStatus))
            }
        )
    }
}

/**
 * Элемент списка бронирований
 */
@Composable
fun BookingListItem(
    booking: Booking,
    property: Property?,
    client: Client?,
    onClick: () -> Unit,
    onPropertyClick: (String) -> Unit,
    onClientClick: (String) -> Unit,
    onCalendarClick: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    // Форматтер для дат - выносим наружу для оптимизации
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    
    // Преобразуем даты из timestamp в LocalDate
    val startDate = remember(booking.startDate) {
        Instant.ofEpochMilli(booking.startDate)
            .atZone(ZoneId.systemDefault()).toLocalDate()
    }
    
    val endDate = remember(booking.endDate) {
        Instant.ofEpochMilli(booking.endDate)
            .atZone(ZoneId.systemDefault()).toLocalDate()
    }
    
    // Определяем цвета в зависимости от статуса - используем remember для оптимизации
    val indicatorColor = remember(booking.status) {
        when(booking.status) {
            BookingStatus.PENDING -> Color(0xFFFBC02D)   // Желтый
            BookingStatus.CONFIRMED -> Color(0xFF2196F3) // Синий
            BookingStatus.ACTIVE -> Color(0xFF4CAF50)    // Зеленый
            BookingStatus.COMPLETED -> Color(0xFF795548) // Коричневый
            BookingStatus.CANCELLED -> Color(0xFFF44336) // Красный
            BookingStatus.EXPIRED -> Color(0xFF9E9E9E)   // Серый
        }
    }
    
    // Подготавливаем обработчики навигации заранее
    val handlePropertyClick = remember(booking.propertyId) {
        {
            coroutineScope.launch {
                onPropertyClick(booking.propertyId)
            }
            Unit // Возвращаем Unit для соответствия типу
        }
    }
    
    val handleClientClick = remember(booking.clientId) {
        {
            booking.clientId?.let { clientId ->
                coroutineScope.launch {
                    onClientClick(clientId)
                }
            }
            Unit // Возвращаем Unit для соответствия типу
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Цветной индикатор статуса
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(indicatorColor)
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Верхняя строка: статус и сумма
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Статус как компактный кружок с текстом
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(indicatorColor)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = booking.status.displayName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = indicatorColor
                        )
                    }
                    
                    // Цена
                    Text(
                        text = "${booking.totalAmount} ₽",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Информация о датах
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "${startDate.format(dateFormatter)} - ${endDate.format(dateFormatter)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Информация о объекте недвижимости с эффектом нажатия
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = handlePropertyClick) // Используем подготовленный обработчик
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Column {
                        Text(
                            text = "Объект",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = property?.address ?: "Объект: ${booking.propertyId}",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                // Информация о клиенте, если есть
                booking.clientId?.let { clientId ->
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = handleClientClick) // Используем подготовленный обработчик
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Column {
                            Text(
                                text = "Клиент",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Text(
                                text = client?.fullName ?: "Клиент: $clientId",
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
} 