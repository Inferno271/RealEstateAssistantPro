package com.realestateassistant.pro.presentation.screens.booking

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.presentation.components.calendar.BookingCalendar
import com.realestateassistant.pro.presentation.components.calendar.BookingCalendarCompat
import com.realestateassistant.pro.presentation.components.calendar.BookingDialog
import com.realestateassistant.pro.presentation.components.calendar.BookingInfoDialog
import com.realestateassistant.pro.presentation.components.calendar.AvailableSlotsComponent
import com.realestateassistant.pro.presentation.viewmodels.BookingCalendarEvent
import com.realestateassistant.pro.presentation.viewmodels.BookingCalendarViewModel
import java.time.LocalDate

/**
 * Экран календаря бронирований
 *
 * @param propertyId ID объекта недвижимости
 * @param onNavigateBack Обратный вызов для навигации назад
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingCalendarScreen(
    propertyId: String,
    onNavigateBack: () -> Unit,
    viewModel: BookingCalendarViewModel = hiltViewModel()
) {
    // Получаем состояние UI из ViewModel
    val state by viewModel.state.collectAsState()
    
    // SnackbarHostState для отображения ошибок
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Отображаем сообщение об ошибке, если оно есть
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }
    
    // Инициализируем выбор объекта недвижимости, если он не выбран
    LaunchedEffect(propertyId) {
        if (state.selectedPropertyId != propertyId) {
            viewModel.onEvent(BookingCalendarEvent.SelectProperty(propertyId))
        }
        // Загружаем все бронирования после выбора объекта
        viewModel.onEvent(BookingCalendarEvent.LoadAllBookings)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Календарь бронирований") 
                        // Отображаем адрес объекта, если он загружен
                        val property = state.selectedProperty
                        if (property != null) {
                            Text(
                                text = property.address,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Основное содержимое
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Если данные загружаются, показываем индикатор загрузки
                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Если выбран объект недвижимости, показываем календарь
                    if (state.selectedPropertyId != null) {
                        // Инструкции для пользователя
                        if (state.isSelectionMode) {
                            Text(
                                text = if (state.selectedStartDate != null && state.selectedEndDate == null)
                                    "Выберите дату выезда"
                                else
                                    "Выберите даты бронирования",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        } else if (state.bookings.isEmpty()) {
                            Text(
                                text = "Нет бронирований. Выберите даты для создания нового бронирования.",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        } else {
                            Text(
                                text = "Загружено бронирований: ${state.bookings.size}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        
                        // Отображаем доступные окна для бронирования
                        if (!state.isSelectionMode) {
                            AvailableSlotsComponent(
                                availableSlots = state.availableTimeSlots,
                                onSlotSelected = { slot ->
                                    // При выборе окна устанавливаем начальную и конечную даты
                                    viewModel.onEvent(BookingCalendarEvent.SelectDate(slot.startDate))
                                    viewModel.onEvent(BookingCalendarEvent.SelectDate(slot.endDate))
                                },
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                        
                        // Отображаем календарь бронирований
                        BookingCalendarCompat(
                            bookings = state.bookings,
                            selectedDate = state.selectedStartDate,
                            selectedEndDate = state.selectedEndDate,
                            onDateSelected = { date ->
                                viewModel.onEvent(BookingCalendarEvent.SelectDate(date))
                            },
                            onBookingSelected = { booking ->
                                viewModel.onEvent(BookingCalendarEvent.SelectBooking(booking))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                        
                        // Кнопки действий
                        if (state.isSelectionMode && state.selectedStartDate != null && state.selectedEndDate != null) {
                            Button(
                                onClick = { viewModel.onEvent(BookingCalendarEvent.ConfirmDateSelection) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Text("Создать бронирование")
                            }
                        } else if (state.isSelectionMode) {
                            Button(
                                onClick = { viewModel.onEvent(BookingCalendarEvent.CancelDateSelection) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Text("Отменить выбор")
                            }
                        } else if (!state.isBookingDialogVisible) {
                            Button(
                                onClick = { viewModel.onEvent(BookingCalendarEvent.ShowBookingDialog) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Text("Создать новое бронирование")
                            }
                        }
                    } else {
                        // Если объект недвижимости не выбран
                        Text(
                            text = "Выберите объект недвижимости для отображения календаря бронирований",
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            // Диалог бронирования
            if (state.isBookingDialogVisible) {
                if (state.isInfoMode && state.selectedBooking != null) {
                    // Режим просмотра информации о бронировании
                    val booking = state.selectedBooking
                    if (booking != null) {
                        BookingInfoDialog(
                            booking = booking,
                            client = state.clients.find { it.id == booking.clientId },
                            onClose = { viewModel.onEvent(BookingCalendarEvent.HideBookingDialog) },
                            onEdit = { 
                                viewModel.onEvent(BookingCalendarEvent.SwitchToEditMode) 
                            },
                            onDelete = { 
                                viewModel.onEvent(BookingCalendarEvent.DeleteBooking(booking.id)) 
                            },
                            onStatusChange = { newStatus ->
                                viewModel.onEvent(
                                    BookingCalendarEvent.UpdateBookingStatus(
                                        booking.id, 
                                        newStatus
                                    )
                                )
                            }
                        )
                    }
                } else {
                    // Режим создания/редактирования бронирования
                    BookingDialog(
                        propertyId = propertyId,
                        startDate = state.selectedStartDate,
                        endDate = state.selectedEndDate,
                        clients = state.clients,
                        selectedClient = state.selectedClient,
                        onClientSelect = { client ->
                            viewModel.onEvent(BookingCalendarEvent.SelectClient(client))
                        },
                        onDateChange = { start, end ->
                            viewModel.onEvent(BookingCalendarEvent.UpdateDates(start, end))
                        },
                        onCancel = {
                            viewModel.onEvent(BookingCalendarEvent.HideBookingDialog)
                        },
                        onSave = { amount, guestsCount, notes ->
                            val booking = state.selectedBooking
                            if (booking != null) {
                                // Редактирование существующего бронирования
                                viewModel.onEvent(
                                    BookingCalendarEvent.UpdateBooking(
                                        bookingId = booking.id,
                                        clientId = state.selectedClient?.id,
                                        startDate = state.selectedStartDate ?: LocalDate.now(),
                                        endDate = state.selectedEndDate ?: LocalDate.now(),
                                        amount = amount,
                                        guestsCount = guestsCount,
                                        notes = notes
                                    )
                                )
                            } else {
                                // Создание нового бронирования
                                viewModel.onEvent(
                                    BookingCalendarEvent.CreateBooking(
                                        propertyId = propertyId,
                                        clientId = state.selectedClient?.id,
                                        startDate = state.selectedStartDate ?: LocalDate.now(),
                                        endDate = state.selectedEndDate ?: LocalDate.now(),
                                        amount = amount,
                                        guestsCount = guestsCount,
                                        notes = notes
                                    )
                                )
                            }
                        },
                        initialAmount = state.selectedBooking?.totalAmount,
                        initialGuestsCount = state.selectedBooking?.guestsCount ?: 1,
                        initialNotes = state.selectedBooking?.notes,
                        isEditing = state.selectedBooking != null && !state.isInfoMode
                    )
                }
            }
        }
    }
} 