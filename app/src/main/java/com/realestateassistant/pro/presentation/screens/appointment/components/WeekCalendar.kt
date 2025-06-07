package com.realestateassistant.pro.presentation.screens.appointment.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realestateassistant.pro.domain.model.Appointment
import com.realestateassistant.pro.domain.model.AppointmentStatus
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
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
 * Компонент для отображения недельного представления календаря
 *
 * @param selectedDate выбранная дата
 * @param appointments список встреч для текущей недели
 * @param clients список клиентов
 * @param properties список объектов недвижимости
 * @param onDateClick обработчик выбора даты
 * @param onAppointmentClick обработчик выбора встречи
 */
@Composable
fun WeekCalendar(
    selectedDate: LocalDate,
    appointments: List<Appointment> = emptyList(),
    clients: List<Client> = emptyList(),
    properties: List<Property> = emptyList(),
    onDateClick: (LocalDate) -> Unit = {},
    onAppointmentClick: (String) -> Unit = {}
) {
    val weekFields = WeekFields.of(Locale.getDefault())
    
    // Находим первый день недели (понедельник по умолчанию для большинства стран)
    val firstDayOfWeek = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    
    // Создаем список дней недели
    val daysOfWeek = (0..6).map { firstDayOfWeek.plusDays(it.toLong()) }
    
    // Группируем встречи по дате
    val appointmentsByDate = appointments.groupBy { appointment ->
        appointment.startDateTime.toLocalDate()
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Отображаем заголовок с днями недели
        WeekHeader(
            days = daysOfWeek,
            selectedDate = selectedDate,
            onDaySelected = onDateClick
        )
        
        // Отображаем список встреч для выбранной даты
        val appointmentsForSelectedDate = appointmentsByDate[selectedDate] ?: emptyList()
        
        if (appointmentsForSelectedDate.isEmpty()) {
            EmptyAppointmentList(
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            AppointmentList(
                appointments = appointmentsForSelectedDate,
                clients = clients,
                properties = properties,
                onAppointmentClick = onAppointmentClick,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

/**
 * Заголовок недельного календаря с днями недели
 */
@Composable
private fun WeekHeader(
    days: List<LocalDate>,
    selectedDate: LocalDate,
    onDaySelected: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEach { date ->
            WeekDay(
                date = date,
                isSelected = date.isEqual(selectedDate),
                onClick = { onDaySelected(date) }
            )
        }
    }
}

/**
 * Отображает день недели
 */
@Composable
private fun WeekDay(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val today = LocalDate.now()
    val isToday = date.isEqual(today)
    
    Column(
        modifier = Modifier
            .width(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // День недели (Пн, Вт, и т.д.)
        Text(
            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru")),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Число месяца
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isSelected -> MaterialTheme.colorScheme.primary
                        isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        else -> Color.Transparent
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    isToday -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface
                },
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

/**
 * Отображает пустое представление недели
 */
@Composable
private fun EmptyWeekView() {
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
 * Отображает элемент встречи в недельном календаре
 */
@Composable
private fun WeekAppointmentItem(
    appointment: Appointment,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Индикатор статуса
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(56.dp)
                    .background(getStatusColor(appointment.status))
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appointment.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Время встречи
                val timeText = if (appointment.isAllDay) {
                    "Весь день"
                } else {
                    val formatter = DateTimeFormatter.ofPattern("HH:mm")
                    val startTime = appointment.startDateTime.format(formatter)
                    val endTime = appointment.endDateTime.format(formatter)
                    "$startTime - $endTime"
                }
                
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                // Клиент
                if (appointment.clientName != null) {
                    Text(
                        text = appointment.clientName,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Бейдж статуса
            Box(
                modifier = Modifier
                    .background(
                        color = getStatusColor(appointment.status).copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = getStatusText(appointment.status),
                    style = MaterialTheme.typography.bodySmall,
                    color = getStatusColor(appointment.status)
                )
            }
        }
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