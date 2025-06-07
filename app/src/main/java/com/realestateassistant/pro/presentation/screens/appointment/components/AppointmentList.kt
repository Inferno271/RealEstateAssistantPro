package com.realestateassistant.pro.presentation.screens.appointment.components

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.domain.model.Appointment
import com.realestateassistant.pro.domain.model.AppointmentStatus
import com.realestateassistant.pro.domain.model.AppointmentType
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.ui.theme.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.realestateassistant.pro.presentation.screens.appointment.AppointmentViewModel
import com.realestateassistant.pro.presentation.screens.appointment.models.AppointmentEvent
import com.realestateassistant.pro.presentation.utils.parseColor

/**
 * Компонент для отображения списка встреч
 */
@Composable
fun AppointmentList(
    appointments: List<Appointment>,
    clients: List<Client>,
    properties: List<Property>,
    onAppointmentClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (appointments.isEmpty()) {
        EmptyAppointmentList(modifier)
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(appointments) { appointment ->
                val client = clients.find { it.id == appointment.clientId }
                val property = properties.find { it.id == appointment.propertyId }
                
                AppointmentItem(
                    appointment = appointment,
                    client = client,
                    property = property,
                    onClick = { onAppointmentClick(appointment.id) }
                )
                
                Divider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

/**
 * Компонент для отображения пустого списка встреч
 */
@Composable
fun EmptyAppointmentList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Нет запланированных встреч",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Нажмите + чтобы создать новую встречу",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

/**
 * Элемент списка встреч
 */
@Composable
fun AppointmentItem(
    appointment: Appointment,
    client: Client?,
    property: Property?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val startTime = remember(appointment.startTime) {
        Instant.ofEpochMilli(appointment.startTime)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    }
    
    val endTime = remember(appointment.endTime) {
        Instant.ofEpochMilli(appointment.endTime)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    }
    
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    
    val statusColor = when(appointment.status) {
        AppointmentStatus.SCHEDULED -> appointmentScheduled
        AppointmentStatus.CONFIRMED -> appointmentConfirmed
        AppointmentStatus.IN_PROGRESS -> appointmentInProgress
        AppointmentStatus.COMPLETED -> appointmentCompleted
        AppointmentStatus.CANCELLED -> appointmentCancelled
        AppointmentStatus.RESCHEDULED -> appointmentRescheduled
        AppointmentStatus.NO_SHOW -> appointmentNoShow
    }
    
    val appointmentColor = parseColor(appointment.color)
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Заголовок и информация о встрече
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
            
            // Основная информация
            Column(
                modifier = Modifier.weight(1f)
            ) {
                    // Заголовок
                Text(
                    text = appointment.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                }
                
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
                
                Spacer(modifier = Modifier.width(8.dp))
                
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
                        text = when(appointment.type) {
                            AppointmentType.SHOWING -> "Показ"
                            AppointmentType.CLIENT_MEETING -> "Встреча с клиентом"
                            AppointmentType.PROPERTY_INSPECTION -> "Осмотр"
                            AppointmentType.CONTRACT_SIGNING -> "Подписание договора"
                            AppointmentType.KEY_HANDOVER -> "Передача ключей"
                            AppointmentType.OWNER_MEETING -> "Встреча с собственником"
                            AppointmentType.SIGNING -> "Подписание"
                            AppointmentType.INSPECTION -> "Инспекция"
                            AppointmentType.PHOTO_SESSION -> "Фотосессия"
                            AppointmentType.MAINTENANCE -> "Обслуживание"
                            AppointmentType.OTHER -> "Другое"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            // Время и дата встречи
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
                        
                    val timeStr = startTime.format(timeFormatter)
                    Text(
                        text = if (appointment.isAllDay) "Весь день" else timeStr,
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
                    
                    val dateStr = startTime.format(dateFormatter)
                    Text(
                        text = dateStr,
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
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
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
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Информация об обновлении
            if (appointment.updatedAt > 0) {
                val updatedDateTime = remember(appointment.updatedAt) {
                    Instant.ofEpochMilli(appointment.updatedAt)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                }
                
                val updateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
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
                    val statusColor = when(status) {
                        AppointmentStatus.SCHEDULED -> appointmentScheduled
                        AppointmentStatus.CONFIRMED -> appointmentConfirmed
                        AppointmentStatus.IN_PROGRESS -> appointmentInProgress
                        AppointmentStatus.COMPLETED -> appointmentCompleted
                        AppointmentStatus.CANCELLED -> appointmentCancelled
                        AppointmentStatus.RESCHEDULED -> appointmentRescheduled
                        AppointmentStatus.NO_SHOW -> appointmentNoShow
                    }
                    val statusText = when(status) {
                        AppointmentStatus.SCHEDULED -> "Запланирована"
                        AppointmentStatus.CONFIRMED -> "Подтверждена"
                        AppointmentStatus.IN_PROGRESS -> "В процессе"
                        AppointmentStatus.COMPLETED -> "Завершена"
                        AppointmentStatus.CANCELLED -> "Отменена"
                        AppointmentStatus.RESCHEDULED -> "Перенесена"
                        AppointmentStatus.NO_SHOW -> "Не явились"
                    }
                    
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