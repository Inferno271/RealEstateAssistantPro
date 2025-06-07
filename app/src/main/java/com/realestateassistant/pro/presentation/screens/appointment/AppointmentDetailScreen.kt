package com.realestateassistant.pro.presentation.screens.appointment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.realestateassistant.pro.R
import com.realestateassistant.pro.domain.model.Appointment
import com.realestateassistant.pro.domain.model.AppointmentStatus
import com.realestateassistant.pro.domain.model.AppointmentType
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.presentation.screens.appointment.models.AppointmentEvent
import com.realestateassistant.pro.presentation.utils.PropertyDisplayUtils.formatPhoneForDisplay
import com.realestateassistant.pro.presentation.utils.parseColor
import com.realestateassistant.pro.ui.theme.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.BorderStroke
import com.realestateassistant.pro.presentation.screens.appointment.components.AppointmentDialog

/**
 * Экран детальной информации о встрече
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailScreen(
    appointmentId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit,
    viewModel: AppointmentViewModel = hiltViewModel(),
    isEditMode: Boolean = false
) {
    val state by viewModel.state.collectAsState()
    
    // Загружаем данные о встрече при первом отображении экрана
    LaunchedEffect(key1 = appointmentId) {
        if (isEditMode) {
        viewModel.handleEvent(AppointmentEvent.LoadAppointment(appointmentId))
        } else {
            viewModel.handleEvent(AppointmentEvent.LoadAppointmentForView(appointmentId))
        }
    }
    
    // Если это режим редактирования, показываем диалог
    LaunchedEffect(key1 = isEditMode, key2 = appointmentId) {
        if (isEditMode) {
            viewModel.handleEvent(AppointmentEvent.ShowEditDialog(appointmentId))
        }
    }
    
    // Получаем информацию о текущей встрече
    val appointment = state.appointments.find { it.id == appointmentId }
    
    // Состояние для диалога удаления
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = appointment?.title ?: "Детали встречи",
                    style = MaterialTheme.typography.titleLarge
                ) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    // Кнопка редактирования (не показываем в режиме редактирования)
                    if (!isEditMode) {
                    IconButton(onClick = onNavigateToEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit, 
                            contentDescription = "Редактировать",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        }
                    }
                    
                    // Кнопка удаления
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete, 
                            contentDescription = "Удалить",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (appointment == null) {
                // Показываем индикатор загрузки или сообщение об ошибке
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("Встреча не найдена")
                }
            } else {
                // Показываем информацию о встрече
                AppointmentDetailContent(appointment = appointment)
            }
        }
        
        // Диалог подтверждения удаления
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Подтверждение удаления") },
                text = { Text("Вы уверены, что хотите удалить эту встречу?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.handleEvent(AppointmentEvent.DeleteAppointment(appointmentId))
                            showDeleteDialog = false
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Удалить")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}

/**
 * Содержимое экрана детальной информации о встрече
 */
@Composable
fun AppointmentDetailContent(
    appointment: Appointment,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val viewModel: AppointmentViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    
    // Получаем клиента и объект
    val client = state.availableClients.find { it.id == appointment.clientId }
    val property = state.availableProperties.find { it.id == appointment.propertyId }
    
    // Загружаем клиента и объект при первом отображении контента
    LaunchedEffect(key1 = appointment.id) {
        if (client == null && appointment.clientId.isNotEmpty()) {
            viewModel.handleEvent(AppointmentEvent.LoadClient(appointment.clientId))
        }
        if (property == null && appointment.propertyId.isNotEmpty()) {
            viewModel.handleEvent(AppointmentEvent.LoadProperty(appointment.propertyId))
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // Основная информация о встрече
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Заголовок секции
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                Text(
                        text = "Детали встречи",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    thickness = 0.5.dp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
                // Заголовок встречи
                Text(
                    text = appointment.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Дата и время встречи
                val startDateTime = Instant.ofEpochMilli(appointment.startTime)
                    .atZone(ZoneId.systemDefault())
                val endDateTime = Instant.ofEpochMilli(appointment.endTime)
                    .atZone(ZoneId.systemDefault())
                
                val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                
                val formattedDate = startDateTime.format(dateFormatter)
                val timeText = if (appointment.isAllDay) {
                    "Весь день"
                } else {
                    "${startDateTime.format(timeFormatter)} - ${endDateTime.format(timeFormatter)}"
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = timeText,
                        style = MaterialTheme.typography.bodyLarge
                    )
            }
            
                // Статус встречи
            Spacer(modifier = Modifier.height(16.dp))
            
                // Статус встречи с возможностью изменения
                var showStatusDialog by remember { mutableStateOf(false) }
                val statusColor = getStatusColor(appointment.status)
                val statusText = getAppointmentStatusText(appointment.status)
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showStatusDialog = true }
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Статус:",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.width(80.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Surface(
                        color = statusColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(statusColor, CircleShape)
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Text(
                                    text = statusText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Изменить статус",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                if (showStatusDialog) {
                    StatusDialog(
                        currentStatus = appointment.status,
                        onStatusSelected = { newStatus ->
                            showStatusDialog = false
                            if (newStatus != appointment.status) {
                                viewModel.handleEvent(
                                    AppointmentEvent.UpdateAppointmentStatus(
                                        id = appointment.id,
                                        status = newStatus
                                    )
                                )
                            }
                        },
                        onDismiss = { showStatusDialog = false }
                    )
                }
                
                // Тип встречи
                Spacer(modifier = Modifier.height(16.dp))
                
                var showTypeDialog by remember { mutableStateOf(false) }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTypeDialog = true }
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Тип:",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.width(80.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Text(
                                    text = getAppointmentTypeText(appointment.type),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Изменить тип",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                
                if (showTypeDialog) {
                    TypeDialog(
                        currentType = appointment.type,
                        onTypeSelected = { newType ->
                            showTypeDialog = false
                            if (newType != appointment.type) {
                                viewModel.handleEvent(
                                    AppointmentEvent.UpdateAppointmentType(
                                        id = appointment.id,
                                        type = newType
                                    )
                                )
                            }
                        },
                        onDismiss = { showTypeDialog = false }
                    )
                }
                
                // Описание встречи
                if (!appointment.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Описание:",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = appointment.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
            }
            
                // Местоположение встречи
                if (!appointment.location.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            
                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                Icon(
                            imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                    Text(
                            text = appointment.location,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Информация о клиенте и объекте
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
                // Заголовок секции для клиента
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                Text(
                        text = "Клиент",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    thickness = 0.5.dp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Информация о клиенте
                if (client != null) {
                    // ФИО клиента
                    Text(
                        text = client.fullName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    
                    // Состав семьи клиента
                    if (client.familyComposition != null || client.peopleCount != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                    Text(
                            text = buildString {
                                if (client.familyComposition != null) {
                                    append(client.familyComposition)
                                    if (client.peopleCount != null) {
                                        append(" (${client.peopleCount} чел.)")
                                    }
                                } else if (client.peopleCount != null) {
                                    append("Состав семьи: ${client.peopleCount} чел.")
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Контактная информация клиента (телефоны)
                    if (client.phone.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                    Text(
                            text = "Контактная информация:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Column {
                            client.phone.forEachIndexed { index, phoneNumber ->
                                if (index > 0) Spacer(modifier = Modifier.height(8.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = formatPhoneForDisplay(phoneNumber),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    val context = LocalContext.current
                                    
                                    // Кнопка для звонка
                                    IconButton(
                                        onClick = { 
                                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:$phoneNumber")
                                            }
                                            context.startActivity(intent)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = "Позвонить",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    
                                    // Кнопка для WhatsApp
                                    IconButton(
                                        onClick = {
                                            try {
                                                // Формируем URL для открытия WhatsApp с этим номером
                                                val whatsappUrl = "https://api.whatsapp.com/send?phone=${phoneNumber.replace("+", "").replace(" ", "").replace("-", "")}"
                                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                                    data = Uri.parse(whatsappUrl)
                                                }
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                // Обработка случая, если WhatsApp не установлен
                                                Toast.makeText(context, "WhatsApp не установлен", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_whatsapp),
                                            contentDescription = "WhatsApp",
                                            tint = Color(0xFF25D366)
                                        )
                                    }
                                    
                                    // Кнопка для Telegram
                                    IconButton(
                                        onClick = {
                                            try {
                                                // Формируем URL для открытия Telegram с этим номером
                                                val formattedPhone = phoneNumber.filter { it.isDigit() }
                                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                                    data = Uri.parse("tg://resolve?phone=$formattedPhone")
                                                }
                                                try {
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    // Если приложение Telegram не установлено, открываем браузер
                                                    try {
                                                        val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                                                            data = Uri.parse("https://t.me/$formattedPhone")
                                                        }
                                                        context.startActivity(browserIntent)
                                                    } catch (e2: Exception) {
                                                        Toast.makeText(context, "Не удалось открыть Telegram", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Ошибка при открытии Telegram", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    ) {
                Icon(
                                            painter = painterResource(id = R.drawable.ic_telegram),
                                            contentDescription = "Telegram",
                                            tint = Color(0xFF0088CC)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Дополнительная информация о клиенте (комментарий)
                    if (!client.comment.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                    Text(
                            text = "Примечания:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    Text(
                            text = client.comment,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Text(
                        text = appointment.clientName ?: "Не указан",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Отдельная карточка для объекта недвижимости
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
                // Заголовок секции для объекта недвижимости
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                Text(
                        text = "Объект недвижимости",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    thickness = 0.5.dp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
                // Информация об объекте недвижимости
                if (property != null) {
                    // Адрес объекта
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = property.address,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            
                            // Отображаем район, если указан
                            if (property.district.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = property.district,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        // Кнопка навигации к объекту
                        if (property.latitude != null && property.longitude != null) {
                            val context = LocalContext.current
                            IconButton(
                                onClick = { 
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse("geo:${property.latitude},${property.longitude}?q=${property.latitude},${property.longitude}(${property.address})")
                                    }
                                    context.startActivity(intent)
                                }
                            ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Открыть карту",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    
                    // Основная информация об объекте
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Основная информация:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Тип объекта
                    if (property.propertyType.isNotEmpty()) {
                        Text(
                            text = property.propertyType,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Цена
                    val priceText = when {
                        property.monthlyRent != null -> formatPrice(property.monthlyRent) + " / мес."
                        property.dailyPrice != null -> formatPrice(property.dailyPrice) + " / сутки"
                        else -> null
                    }
                    
                    if (priceText != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = priceText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // Площадь и комнаты
                    val detailsText = buildString {
                        if (property.area > 0) {
                            append("${property.area} м²")
                            if (property.roomsCount > 0) {
                                append(", ")
                            }
                        }
                        if (property.roomsCount > 0) {
                            append("${property.roomsCount} комн.")
                        }
                    }
                    
                    if (detailsText.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = detailsText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Этаж
                    if (property.floor > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Этаж: ${property.floor}" + if (property.totalFloors > 0) " из ${property.totalFloors}" else "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Контактная информация объекта
                    if (property.contactName.isNotEmpty() || property.contactPhone.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Контактная информация объекта:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Контактное лицо
                        if (property.contactName.isNotEmpty()) {
                            Text(
                                text = property.contactName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        // Телефоны контактного лица объекта
                        if (property.contactPhone.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Column {
                                property.contactPhone.forEachIndexed { index, phoneNumber ->
                                    if (index > 0) Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = formatPhoneForDisplay(phoneNumber),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.weight(1f)
                                        )
                                        
                                        val context = LocalContext.current
                                        
                                        // Кнопка для звонка
                                        IconButton(
                                            onClick = { 
                                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                                    data = Uri.parse("tel:$phoneNumber")
                                                }
                                                context.startActivity(intent)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Phone,
                                                contentDescription = "Позвонить",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        
                                        // Кнопка для WhatsApp
                                        IconButton(
                                            onClick = {
                                                try {
                                                    // Формируем URL для открытия WhatsApp с этим номером
                                                    val whatsappUrl = "https://api.whatsapp.com/send?phone=${phoneNumber.replace("+", "").replace(" ", "").replace("-", "")}"
                                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                                        data = Uri.parse(whatsappUrl)
                                                    }
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    // Обработка случая, если WhatsApp не установлен
                                                    Toast.makeText(context, "WhatsApp не установлен", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_whatsapp),
                                                contentDescription = "WhatsApp",
                                                tint = Color(0xFF25D366)
                                            )
                                        }
                                        
                                        // Кнопка для Telegram
                                        IconButton(
                                            onClick = {
                                                try {
                                                    // Формируем URL для открытия Telegram с этим номером
                                                    val formattedPhone = phoneNumber.filter { it.isDigit() }
                                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                                        data = Uri.parse("tg://resolve?phone=$formattedPhone")
                                                    }
                                                    try {
                                                        context.startActivity(intent)
                                                    } catch (e: Exception) {
                                                        // Если приложение Telegram не установлено, открываем браузер
                                                        try {
                                                            val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                                                                data = Uri.parse("https://t.me/$formattedPhone")
                                                            }
                                                            context.startActivity(browserIntent)
                                                        } catch (e2: Exception) {
                                                            Toast.makeText(context, "Не удалось открыть Telegram", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Ошибка при открытии Telegram", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_telegram),
                                                contentDescription = "Telegram",
                                                tint = Color(0xFF0088CC)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = appointment.propertyAddress ?: "Не указан",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        
        // Если указано место встречи
        if (!appointment.location.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Заголовок секции для места встречи
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                        Text(
                    text = "Место встречи",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    thickness = 0.5.dp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
                        Text(
                            text = appointment.location,
                            style = MaterialTheme.typography.bodyLarge
                        )
                }
                
        // Дополнительная информация (описание, примечания)
        if (!appointment.description.isNullOrBlank() || !appointment.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Заголовок секции
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Дополнительная информация",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        HorizontalDivider(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                            thickness = 0.5.dp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
            
            // Описание
            if (!appointment.description.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Описание",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = appointment.description,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Примечания
            if (!appointment.notes.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Notes,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Примечания",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = appointment.notes,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

        // Служебная информация
        Spacer(modifier = Modifier.height(16.dp))
        
    Surface(
        modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Заголовок секции
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                Text(
                    text = "Служебная информация",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    thickness = 0.5.dp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                
                // Информация о создании и обновлении в отдельных карточках
                // Карточка с информацией о создании
                InfoItemCard(
                    icon = Icons.Default.AddCircleOutline,
                    label = "Создано",
                    value = Instant.ofEpochMilli(appointment.createdAt)
                .atZone(ZoneId.systemDefault())
                        .format(dateFormat),
                    iconTint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Карточка с информацией об обновлении
                InfoItemCard(
                    icon = Icons.Default.Update,
                    label = "Последнее обновление", 
                    value = Instant.ofEpochMilli(appointment.updatedAt)
                .atZone(ZoneId.systemDefault())
                        .format(dateFormat),
                    iconTint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * Карточка с элементом информации
 */
@Composable
private fun InfoItemCard(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color = MaterialTheme.colorScheme.primary
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка в цветном круге
            Surface(
                color = iconTint.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
            Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Форматирует цену для отображения
 */
private fun formatPrice(price: Double): String {
    return String.format("%,.0f ₽", price)
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

/**
 * Возвращает текстовое представление статуса встречи
 */
private fun getAppointmentStatusText(status: AppointmentStatus): String {
    return when (status) {
        AppointmentStatus.SCHEDULED -> "Запланирована"
        AppointmentStatus.CONFIRMED -> "Подтверждена"
        AppointmentStatus.CANCELLED -> "Отменена"
        AppointmentStatus.COMPLETED -> "Завершена"
        AppointmentStatus.RESCHEDULED -> "Перенесена"
        AppointmentStatus.NO_SHOW -> "Не явились"
        AppointmentStatus.IN_PROGRESS -> "В процессе"
    }
}

/**
 * Возвращает цвет для статуса встречи
 */
private fun getStatusColor(status: AppointmentStatus): Color {
    return when (status) {
        AppointmentStatus.SCHEDULED -> appointmentScheduled
        AppointmentStatus.CONFIRMED -> appointmentConfirmed
        AppointmentStatus.CANCELLED -> appointmentCancelled
        AppointmentStatus.COMPLETED -> appointmentCompleted
        AppointmentStatus.RESCHEDULED -> appointmentRescheduled
        AppointmentStatus.NO_SHOW -> appointmentNoShow
        AppointmentStatus.IN_PROGRESS -> appointmentInProgress
    }
}

/**
 * Диалог для выбора статуса встречи
 */
@Composable
private fun StatusDialog(
    currentStatus: AppointmentStatus,
    onStatusSelected: (AppointmentStatus) -> Unit,
    onDismiss: () -> Unit
) {
    val allStatuses = remember {
        listOf(
            AppointmentStatus.SCHEDULED,
            AppointmentStatus.CONFIRMED,
            AppointmentStatus.IN_PROGRESS,
            AppointmentStatus.COMPLETED,
            AppointmentStatus.RESCHEDULED,
            AppointmentStatus.CANCELLED,
            AppointmentStatus.NO_SHOW
        )
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите статус встречи") },
        text = {
            LazyColumn {
                items(allStatuses) { status ->
                    val isSelected = status == currentStatus
                    val statusColor = getStatusColor(status)
                    val statusText = getAppointmentStatusText(status)
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStatusSelected(status) }
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                            .background(
                                if (isSelected) statusColor.copy(alpha = 0.1f) else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(statusColor, CircleShape)
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Выбрано",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

/**
 * Раздел с контактной информацией клиента
 */
@Composable
fun ClientContactSection(client: Client) {
    val context = LocalContext.current
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Заголовок секции
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Контактная информация клиента",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    thickness = 0.5.dp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Имя клиента
            if (client.fullName.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(8.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = client.fullName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Состав семьи
            if (!client.familyComposition.isNullOrBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.People,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(8.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = "Состав семьи",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = client.familyComposition,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Количество человек
            if (client.peopleCount != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Group,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(8.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = "Количество проживающих",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${client.peopleCount} чел.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Телефоны с кнопками действий
            if (client.phone.isNotEmpty()) {
                client.phone.forEach { phone ->
                    PhoneContactRow(
                        phone = phone,
                        context = context
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

/**
 * Раздел с контактной информацией объекта недвижимости
 */
@Composable
fun PropertyContactSection(property: Property) {
    val context = LocalContext.current
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Заголовок секции
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Контактная информация объекта",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    thickness = 0.5.dp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Имя контакта
            if (property.contactName.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(8.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = property.contactName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Телефоны с кнопками действий
            if (property.contactPhone.isNotEmpty()) {
                property.contactPhone.forEach { phone ->
                    PhoneContactRow(
                        phone = phone,
                        context = context
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            // Дополнительная контактная информация
            property.additionalContactInfo?.let { info ->
                if (info.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                        thickness = 0.5.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Дополнительно",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = info,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

/**
 * Строка с телефоном и кнопками действий
 */
@Composable
private fun PhoneContactRow(
    phone: String,
    context: android.content.Context
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Phone,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = formatPhoneForDisplay(phone),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        
        // Кнопка звонка
        IconButton(
            onClick = { 
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phone")
                }
                context.startActivity(intent)
            },
            modifier = Modifier.size(36.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Позвонить",
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Кнопка WhatsApp
        Surface(
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            modifier = Modifier
                .size(36.dp)
                .clickable {
                    val formattedPhone = phone.filter { it.isDigit() }
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://wa.me/$formattedPhone")
                    }
                    context.startActivity(intent)
                },
            color = Color.Transparent
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_whatsapp),
                    contentDescription = "WhatsApp",
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF25D366)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Кнопка Telegram
        Surface(
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            modifier = Modifier
                .size(36.dp)
                .clickable {
                    val formattedPhone = phone.filter { it.isDigit() }
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("tg://resolve?phone=$formattedPhone")
                    }
                    try {
                    context.startActivity(intent)
                    } catch (e: Exception) {
                        // Если приложение Telegram не установлено, открываем браузер
                        try {
                            val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://t.me/$formattedPhone")
                            }
                            context.startActivity(browserIntent)
                        } catch (e2: Exception) {
                            Toast.makeText(context, "Не удалось открыть Telegram", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
            color = Color.Transparent
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_telegram),
                    contentDescription = "Telegram",
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF0088CC)
                )
            }
        }
    }
}

/**
 * Диалог для выбора типа встречи
 */
@Composable
private fun TypeDialog(
    currentType: com.realestateassistant.pro.domain.model.AppointmentType,
    onTypeSelected: (com.realestateassistant.pro.domain.model.AppointmentType) -> Unit,
    onDismiss: () -> Unit
) {
    val allTypes = remember {
        listOf(
            com.realestateassistant.pro.domain.model.AppointmentType.SHOWING,
            com.realestateassistant.pro.domain.model.AppointmentType.CLIENT_MEETING,
            com.realestateassistant.pro.domain.model.AppointmentType.PROPERTY_INSPECTION,
            com.realestateassistant.pro.domain.model.AppointmentType.CONTRACT_SIGNING,
            com.realestateassistant.pro.domain.model.AppointmentType.KEY_HANDOVER,
            com.realestateassistant.pro.domain.model.AppointmentType.OWNER_MEETING,
            com.realestateassistant.pro.domain.model.AppointmentType.SIGNING,
            com.realestateassistant.pro.domain.model.AppointmentType.INSPECTION,
            com.realestateassistant.pro.domain.model.AppointmentType.PHOTO_SESSION,
            com.realestateassistant.pro.domain.model.AppointmentType.MAINTENANCE,
            com.realestateassistant.pro.domain.model.AppointmentType.OTHER
        )
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите тип встречи") },
        text = {
            LazyColumn {
                items(allTypes) { type ->
                    val isSelected = type == currentType
                    val typeText = getAppointmentTypeText(type)
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTypeSelected(type) }
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = typeText,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Выбрано",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
} 