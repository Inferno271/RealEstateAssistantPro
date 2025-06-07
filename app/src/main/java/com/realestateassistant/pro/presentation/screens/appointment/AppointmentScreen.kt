package com.realestateassistant.pro.presentation.screens.appointment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.domain.model.Appointment
import com.realestateassistant.pro.presentation.components.ErrorMessage
import com.realestateassistant.pro.presentation.screens.appointment.components.*
import com.realestateassistant.pro.presentation.screens.appointment.models.AppointmentEvent
import com.realestateassistant.pro.presentation.screens.appointment.models.CalendarView
import com.realestateassistant.pro.ui.theme.Primary
import com.realestateassistant.pro.ui.theme.PrimaryContainer
import com.realestateassistant.pro.ui.theme.OnPrimaryContainer
import java.time.LocalDate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlinx.coroutines.launch

/**
 * Экран управления встречами
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentScreen(
    modifier: Modifier = Modifier,
    viewModel: AppointmentViewModel = hiltViewModel(),
    onNavigateToAppointmentDetail: (String) -> Unit = {},
    onNavigateToAddAppointment: () -> Unit = {},
    drawerState: DrawerState? = null
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(key1 = Unit) {
        // При каждом переходе на экран обновляем встречи для текущей даты
        viewModel.handleEvent(AppointmentEvent.LoadAppointmentsForDate(state.selectedDate))
        
        // Только при первом запуске загружаем данные для текущего дня
        if (state.selectedDate == LocalDate.now()) {
        viewModel.handleEvent(AppointmentEvent.JumpToDate(LocalDate.now()))
        }
    }
    
    // Загружаем информацию о клиентах и объектах недвижимости
    LaunchedEffect(key1 = Unit) {
        viewModel.handleEvent(AppointmentEvent.LoadClients)
        viewModel.handleEvent(AppointmentEvent.LoadProperties)
    }
    
    // Загружаем данные для выбранного месяца только когда меняется месяц
    LaunchedEffect(key1 = state.selectedDate.year, key2 = state.selectedDate.monthValue) {
        viewModel.handleEvent(AppointmentEvent.LoadAppointmentsForMonth(
            year = state.selectedDate.year,
            month = state.selectedDate.monthValue
        ))
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Встречи",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    // Добавляем кнопку меню только если drawerState не null
                    drawerState?.let {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Меню",
                                tint = OnPrimaryContainer
                            )
                        }
                    }
                },
                actions = {
                    // Кнопки переключения вида календаря
                    IconButton(
                        onClick = { viewModel.handleEvent(AppointmentEvent.ChangeCalendarView(CalendarView.DAY)) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarViewDay,
                            contentDescription = "Дневной вид",
                            tint = if (state.calendarView == CalendarView.DAY) 
                                   Primary else Color.Gray
                        )
                    }
                    IconButton(
                        onClick = { viewModel.handleEvent(AppointmentEvent.ChangeCalendarView(CalendarView.WEEK)) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarViewWeek,
                            contentDescription = "Недельный вид",
                            tint = if (state.calendarView == CalendarView.WEEK) 
                                   Primary else Color.Gray
                        )
                    }
                    IconButton(
                        onClick = { viewModel.handleEvent(AppointmentEvent.ChangeCalendarView(CalendarView.MONTH)) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Месячный вид",
                            tint = if (state.calendarView == CalendarView.MONTH) 
                                   Primary else Color.Gray
                        )
                    }
                    IconButton(
                        onClick = { 
                            viewModel.handleEvent(AppointmentEvent.ShowFilterDialog(true))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Фильтры"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryContainer,
                    titleContentColor = OnPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddAppointment,
                containerColor = Primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить встречу",
                    tint = Color.White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Заголовок календаря с кнопками навигации
                CalendarHeader(
                    selectedDate = state.selectedDate,
                    onPreviousClick = { viewModel.handleEvent(AppointmentEvent.NavigateToPrevious) },
                    onNextClick = { viewModel.handleEvent(AppointmentEvent.NavigateToNext) },
                    calendarView = state.calendarView
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Календарь в зависимости от выбранного режима
                when (state.calendarView) {
                    CalendarView.DAY -> {
                        DayCalendar(
                            selectedDate = state.selectedDate,
                            appointments = state.appointmentsForSelectedDate,
                            clients = state.availableClients,
                            properties = state.availableProperties,
                            onAppointmentClick = { appointmentId ->
                                onNavigateToAppointmentDetail(appointmentId)
                            }
                        )
                    }
                    CalendarView.WEEK -> {
                        WeekCalendar(
                            selectedDate = state.selectedDate,
                            appointments = state.appointments,
                            clients = state.availableClients,
                            properties = state.availableProperties,
                            onDateClick = { date ->
                                viewModel.handleEvent(AppointmentEvent.SelectDate(date))
                            },
                            onAppointmentClick = { appointmentId ->
                                onNavigateToAppointmentDetail(appointmentId)
                            }
                        )
                    }
                    CalendarView.MONTH -> {
                        MonthCalendar(
                            selectedDate = state.selectedDate,
                            appointments = state.appointments,
                            clients = state.availableClients,
                            properties = state.availableProperties,
                            onDateSelected = { date ->
                                viewModel.handleEvent(AppointmentEvent.SelectDate(date))
                            },
                            onAppointmentClick = { appointmentId ->
                                onNavigateToAppointmentDetail(appointmentId)
                            },
                            appointmentsForDate = state.appointmentsForSelectedDate,
                            countByDate = state.appointmentCountByDate,
                            isWeekStartsOnMonday = true
                        )
                    }
                    else -> {
                        // На будущее, для реализации других видов
                        Text(text = "Этот вид календаря в разработке")
                    }
                }
            }
            
            // Показываем индикатор загрузки
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            // Показываем сообщение об ошибке
            state.error?.let { error ->
                ErrorMessage(
                    message = error,
                    onDismiss = { viewModel.handleEvent(AppointmentEvent.ClearError) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                )
            }
            
            // Диалог создания/редактирования встречи
            if (state.showAppointmentDialog) {
                AppointmentDialog(
                    isEditMode = state.isEditMode,
                    formState = state.formState,
                    onEvent = { dialogEvent -> 
                        viewModel.handleEvent(AppointmentEvent.OnDialogEvent(dialogEvent))
                    },
                    clients = state.availableClients,
                    properties = state.availableProperties,
                    onDismiss = { viewModel.handleEvent(AppointmentEvent.ShowAppointmentDialog(false)) }
                )
            }
            
            // Диалог фильтров
            if (state.showFilterDialog) {
                FilterDialog(
                    onEvent = viewModel::handleEvent,
                    clients = state.availableClients,
                    properties = state.availableProperties,
                    currentClientId = state.filterClientId,
                    currentPropertyId = state.filterPropertyId,
                    currentStatus = state.filterStatus,
                    currentType = state.filterType,
                    currentSearchQuery = state.searchQuery,
                    onDismiss = { viewModel.handleEvent(AppointmentEvent.ShowFilterDialog(false)) }
                )
            }
        }
    }
} 