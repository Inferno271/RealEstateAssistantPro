package com.realestateassistant.pro.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.realestateassistant.pro.domain.model.AppointmentStatus
import com.realestateassistant.pro.domain.model.AppointmentType
import com.realestateassistant.pro.domain.model.dashboard.DashboardSummary
import com.realestateassistant.pro.domain.model.dashboard.QuickAction
import com.realestateassistant.pro.domain.model.dashboard.UpcomingEvent
import com.realestateassistant.pro.navigation.routes.AppRoutes
import com.realestateassistant.pro.presentation.utils.parseColor
import com.realestateassistant.pro.presentation.viewmodels.DashboardViewModel
import com.realestateassistant.pro.ui.theme.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Экран панели управления, используемый в навигации
 */
@Composable
fun DashboardScreen(
    navController: NavController? = null,
    viewModel: DashboardViewModel = hiltViewModel(),
    drawerState: DrawerState? = null
) {
    // Используем переданный навигационный контроллер или создаем новый, если не передан
    val navControllerToUse = navController ?: rememberNavController()
    
    // Обновляем данные при каждом открытии экрана
    LaunchedEffect(key1 = Unit) {
        viewModel.loadDashboardData()
    }
    
    DashboardContent(
        viewModel = viewModel,
        drawerState = drawerState,
        onNavigate = { route ->
            when {
                route == "add_property" -> navControllerToUse.navigate(AppRoutes.ADD_PROPERTY)
                route == "add_client" -> navControllerToUse.navigate(AppRoutes.ADD_CLIENT)
                route == "add_appointment" -> navControllerToUse.navigate(AppRoutes.ADD_APPOINTMENT)
                route == "calendar" -> navControllerToUse.navigate(AppRoutes.APPOINTMENTS)
                route.startsWith("property_") -> navControllerToUse.navigate(route)
                route.startsWith("client_") -> navControllerToUse.navigate(route)
                route.startsWith("appointment_") -> navControllerToUse.navigate(route)
                else -> navControllerToUse.navigate(route)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    viewModel: DashboardViewModel,
    drawerState: DrawerState? = null,
    onNavigate: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Показываем сообщение об ошибке, если есть
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Панель управления") },
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
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                // Показываем индикатор загрузки
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Основное содержимое
                DashboardMainContent(
                    dashboardSummary = state.dashboardSummary,
                    onActionClick = onNavigate
                )
            }
        }
    }
}

@Composable
private fun DashboardMainContent(
    dashboardSummary: DashboardSummary,
    onActionClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Секция быстрых действий
        Text(
            text = "Быстрые действия",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Сетка быстрых действий
        QuickActionsSection(
            quickActions = dashboardSummary.quickActions,
            onActionClick = onActionClick
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Разделитель
        Divider()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Секция предстоящих событий
        Text(
            text = "Предстоящие события",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Список предстоящих событий
        if (dashboardSummary.upcomingEvents.isEmpty()) {
            EmptyEventsMessage()
        } else {
            UpcomingEventsSection(
                events = dashboardSummary.upcomingEvents,
                onEventClick = onActionClick
            )
        }
    }
}

@Composable
fun QuickActionsSection(
    quickActions: List<QuickAction>,
    onActionClick: (String) -> Unit
) {
    val actionIcons = mapOf(
        "new_property" to Icons.Default.Home,
        "new_client" to Icons.Default.Person,
        "new_appointment" to Icons.Default.DateRange,
        "calendar" to Icons.Default.DateRange
    )
    
    val actionsToShow = if (quickActions.isNotEmpty()) quickActions else listOf(
        QuickAction("new_property", "Новый объект", 0, "add_property"),
        QuickAction("new_client", "Новый клиент", 0, "add_client"),
        QuickAction("new_appointment", "Новая встреча", 0, "add_appointment"),
        QuickAction("calendar", "Календарь", 0, "calendar")
    )
    
    Column(modifier = Modifier.fillMaxWidth()) {
        // Первая строка с двумя карточками
        Row(modifier = Modifier.fillMaxWidth()) {
            actionsToShow.getOrNull(0)?.let { action ->
                QuickActionCard(
                    title = action.title,
                    icon = actionIcons[action.id] ?: Icons.Default.Add,
                    onClick = { onActionClick(action.route) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
            }
            
            actionsToShow.getOrNull(1)?.let { action ->
                QuickActionCard(
                    title = action.title,
                    icon = actionIcons[action.id] ?: Icons.Default.Add,
                    onClick = { onActionClick(action.route) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Вторая строка с двумя карточками
        Row(modifier = Modifier.fillMaxWidth()) {
            actionsToShow.getOrNull(2)?.let { action ->
                QuickActionCard(
                    title = action.title,
                    icon = actionIcons[action.id] ?: Icons.Default.Add,
                    onClick = { onActionClick(action.route) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
            }
            
            actionsToShow.getOrNull(3)?.let { action ->
                QuickActionCard(
                    title = action.title,
                    icon = actionIcons[action.id] ?: Icons.Default.Add,
                    onClick = { onActionClick(action.route) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyEventsMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "У вас нет предстоящих событий",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun UpcomingEventsSection(
    events: List<UpcomingEvent>,
    onEventClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        events.forEach { event ->
            EventCard(
                event = event,
                onClick = { appointmentId -> 
                    // Передаем маршрут для навигации к конкретной встрече
                    onEventClick(AppRoutes.appointmentDetail(appointmentId))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventCard(
    event: UpcomingEvent,
    onClick: (String) -> Unit
) {
    val appointment = event.appointment
    
    val startDateTime = Instant.ofEpochMilli(appointment.startTime)
        .atZone(ZoneId.systemDefault())
    
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    
    val formattedTime = startDateTime.format(timeFormatter)
    val formattedDate = if (event.isToday) {
        "Сегодня"
    } else {
        startDateTime.format(dateFormatter)
    }
    
    val statusColor = when (appointment.status) {
        AppointmentStatus.SCHEDULED -> appointmentScheduled
        AppointmentStatus.CONFIRMED -> appointmentConfirmed
        AppointmentStatus.IN_PROGRESS -> appointmentInProgress
        AppointmentStatus.COMPLETED -> appointmentCompleted
        AppointmentStatus.CANCELLED -> appointmentCancelled
        AppointmentStatus.RESCHEDULED -> appointmentRescheduled
        AppointmentStatus.NO_SHOW -> appointmentNoShow
    }
    
    val appointmentColor = parseColor(appointment.color ?: "#1976D2")
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onClick(appointment.id) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Заголовок и статус
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Цветовая метка
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(appointmentColor)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Заголовок
                Text(
                    text = appointment.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                // Статус встречи
                Surface(
                    color = statusColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = when(appointment.status) {
                            AppointmentStatus.SCHEDULED -> "Запланирована"
                            AppointmentStatus.CONFIRMED -> "Подтверждена"
                            AppointmentStatus.IN_PROGRESS -> "В процессе"
                            AppointmentStatus.COMPLETED -> "Завершена"
                            AppointmentStatus.CANCELLED -> "Отменена"
                            AppointmentStatus.RESCHEDULED -> "Перенесена"
                            AppointmentStatus.NO_SHOW -> "Не явились"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            // Тип встречи
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = "Тип:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = getAppointmentTypeText(appointment.type),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            // Время и дата
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Время
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = if (appointment.isAllDay) "Весь день" else formattedTime,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                // Дата
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // Информация о клиенте и объекте
            if (!appointment.clientName.isNullOrEmpty() || !appointment.propertyAddress.isNullOrEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Клиент
                    if (!appointment.clientName.isNullOrEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(4.dp))
                            
                            Text(
                                text = appointment.clientName,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    
                    // Объект
                    if (!appointment.propertyAddress.isNullOrEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = if (!appointment.clientName.isNullOrEmpty()) Modifier.weight(1f) else Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(4.dp))
                            
                            Text(
                                text = appointment.propertyAddress,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            
            // Место встречи, если отличается от адреса объекта
            if (!appointment.location.isNullOrBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = appointment.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Поле "Обновлено"
            if (appointment.updatedAt > 0) {
                val updatedDateTime = Instant.ofEpochMilli(appointment.updatedAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                
                val updateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Update,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "Обновлено: ${updatedDateTime.format(updateTimeFormatter)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

/**
 * Возвращает текстовое представление типа встречи
 */
private fun getAppointmentTypeText(type: com.realestateassistant.pro.domain.model.AppointmentType): String {
    return when (type) {
        com.realestateassistant.pro.domain.model.AppointmentType.SHOWING -> "Показ"
        com.realestateassistant.pro.domain.model.AppointmentType.CLIENT_MEETING -> "Встреча с клиентом"
        com.realestateassistant.pro.domain.model.AppointmentType.PROPERTY_INSPECTION -> "Осмотр"
        com.realestateassistant.pro.domain.model.AppointmentType.CONTRACT_SIGNING -> "Подписание договора"
        com.realestateassistant.pro.domain.model.AppointmentType.KEY_HANDOVER -> "Передача ключей"
        com.realestateassistant.pro.domain.model.AppointmentType.OWNER_MEETING -> "Встреча с собственником"
        com.realestateassistant.pro.domain.model.AppointmentType.SIGNING -> "Подписание документов"
        com.realestateassistant.pro.domain.model.AppointmentType.INSPECTION -> "Инспекция объекта"
        com.realestateassistant.pro.domain.model.AppointmentType.PHOTO_SESSION -> "Фотосессия объекта"
        com.realestateassistant.pro.domain.model.AppointmentType.MAINTENANCE -> "Техническое обслуживание"
        com.realestateassistant.pro.domain.model.AppointmentType.OTHER -> "Другое"
    }
} 