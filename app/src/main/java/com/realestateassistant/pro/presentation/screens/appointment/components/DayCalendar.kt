package com.realestateassistant.pro.presentation.screens.appointment.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.domain.model.Appointment
import com.realestateassistant.pro.domain.model.AppointmentStatus
import com.realestateassistant.pro.domain.model.AppointmentType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import com.realestateassistant.pro.ui.theme.appointmentScheduled
import com.realestateassistant.pro.ui.theme.appointmentConfirmed
import com.realestateassistant.pro.ui.theme.appointmentInProgress
import com.realestateassistant.pro.ui.theme.appointmentCompleted
import com.realestateassistant.pro.ui.theme.appointmentCancelled
import com.realestateassistant.pro.ui.theme.appointmentRescheduled
import com.realestateassistant.pro.ui.theme.appointmentNoShow
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.model.Property

/**
 * Компонент для отображения календаря на день
 */
@Composable
fun DayCalendar(
    selectedDate: LocalDate,
    appointments: List<Appointment>,
    clients: List<Client>,
    properties: List<Property>,
    onAppointmentClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок с выбранной датой
        Text(
            text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale("ru"))),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Список встреч
        AppointmentList(
            appointments = appointments,
            clients = clients,
            properties = properties,
            onAppointmentClick = onAppointmentClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Отображение пустого дня (без встреч)
 */
@Composable
private fun EmptyDayView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "На выбранный день нет запланированных встреч",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

/**
 * Элемент встречи в списке
 */
@Composable
private fun AppointmentItem(
    appointment: Appointment,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Индикатор статуса
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(64.dp)
                    .background(getStatusColor(appointment.status))
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appointment.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = formatTimeRange(appointment),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                if (appointment.clientName != null) {
                    Text(
                        text = appointment.clientName,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Отображаем тип встречи
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = getAppointmentTypeText(appointment.type),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                
                if (appointment.location != null) {
                    Text(
                        text = appointment.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            StatusBadge(status = appointment.status)
        }
    }
}

/**
 * Форматирует временной диапазон встречи
 */
private fun formatTimeRange(appointment: Appointment): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val startTime = appointment.startDateTime.format(formatter)
    val endTime = appointment.endDateTime.format(formatter)
    
    return if (appointment.isAllDay) {
        "Весь день"
    } else {
        "$startTime - $endTime"
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
 * Компонент для отображения статуса встречи
 */
@Composable
private fun StatusBadge(status: AppointmentStatus) {
    Box(
        modifier = Modifier
            .background(
                color = getStatusColor(status).copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = getStatusText(status),
            style = MaterialTheme.typography.bodySmall,
            color = getStatusColor(status)
        )
    }
}

/**
 * Возвращает текстовое представление статуса встречи
 */
private fun getStatusText(status: AppointmentStatus): String {
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