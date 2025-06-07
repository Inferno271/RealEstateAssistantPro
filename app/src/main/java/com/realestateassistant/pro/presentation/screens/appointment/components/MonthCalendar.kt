package com.realestateassistant.pro.presentation.screens.appointment.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.ui.theme.appointmentScheduled
import com.realestateassistant.pro.ui.theme.appointmentConfirmed
import com.realestateassistant.pro.ui.theme.appointmentInProgress
import com.realestateassistant.pro.ui.theme.appointmentCompleted
import com.realestateassistant.pro.ui.theme.appointmentCancelled
import com.realestateassistant.pro.ui.theme.appointmentRescheduled
import com.realestateassistant.pro.ui.theme.appointmentNoShow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * Компонент для отображения месячного календаря
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthCalendar(
    selectedDate: LocalDate,
    appointments: List<Appointment>,
    clients: List<Client>,
    properties: List<Property>,
    onDateSelected: (LocalDate) -> Unit,
    onAppointmentClick: (String) -> Unit,
    appointmentsForDate: List<Appointment>,
    countByDate: Map<LocalDate, Int>,
    isWeekStartsOnMonday: Boolean = true,
    modifier: Modifier = Modifier
) {
    // Группируем встречи по дате
    val appointmentsByDate = appointments.groupBy { appointment ->
        appointment.startDateTime.toLocalDate()
    }
    
    // Получаем встречи для выбранной даты
    val appointmentsForSelectedDate = appointmentsByDate[selectedDate] ?: emptyList()
    
    val currentMonth = YearMonth.of(selectedDate.year, selectedDate.month)
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1)
    
    // Правильный расчет первого дня недели с учетом настройки isWeekStartsOnMonday
    val firstDayOfWeek = if (isWeekStartsOnMonday) {
        (firstDayOfMonth.dayOfWeek.value - 1) % 7 // 0 - понедельник, 6 - воскресенье
    } else {
        firstDayOfMonth.dayOfWeek.value % 7 // 0 - воскресенье, 6 - суббота
    }
    
    val today = LocalDate.now()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Названия дней недели
            DaysOfWeekHeader(isWeekStartsOnMonday = isWeekStartsOnMonday)
            
            // Сетка календаря
            val weeks = (daysInMonth + firstDayOfWeek + 6) / 7 // Количество недель в месяце
            
            for (week in 0 until weeks) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (day in 0 until 7) {
                        val dayOfMonth = week * 7 + day - firstDayOfWeek + 1
                        
                        if (dayOfMonth in 1..daysInMonth) {
                            val date = LocalDate.of(currentMonth.year, currentMonth.month, dayOfMonth)
                            val isSelected = date == selectedDate
                            val isToday = date == today
                            val appointmentsCount = countByDate[date] ?: 0
                            
                            CalendarDay(
                                date = date,
                                isSelected = isSelected,
                                isToday = isToday,
                                appointmentsCount = appointmentsCount,
                                onDateSelected = onDateSelected,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            // Пустая ячейка
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Заголовок с выбранной датой
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy, EEEE", Locale("ru"))),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Список встреч для выбранного дня
        Text(
            text = "Встречи на ${selectedDate.format(DateTimeFormatter.ofPattern("d MMMM", Locale("ru")))}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (appointmentsForDate.isEmpty()) {
            EmptyAppointmentList(
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            AppointmentList(
                appointments = appointmentsForDate,
                clients = clients,
                properties = properties,
                onAppointmentClick = onAppointmentClick,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

/**
 * Заголовок с названиями дней недели
 */
@Composable
private fun DaysOfWeekHeader(isWeekStartsOnMonday: Boolean = true) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Определяем порядок дней недели
        val daysOfWeek = if (isWeekStartsOnMonday) {
        // Начинаем с понедельника (1) и заканчиваем воскресеньем (7)
            (1..7).map { DayOfWeek.of(it) }
        } else {
            // Начинаем с воскресенья (7) и заканчиваем субботой (6)
            listOf(DayOfWeek.SUNDAY) + (1..6).map { DayOfWeek.of(it) }
        }
        
        for (dayOfWeek in daysOfWeek) {
            Text(
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru")).uppercase(),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Ячейка дня календаря
 */
@Composable
private fun CalendarDay(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    appointmentsCount: Int,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    else -> Color.Transparent
                }
            )
            .border(
                width = if (isToday && !isSelected) 1.dp else 0.dp,
                color = if (isToday && !isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onDateSelected(date) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.onSurface
                },
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
            )
            
            if (appointmentsCount > 0) {
                Badge(
                    modifier = Modifier.padding(top = 2.dp),
                    containerColor = if (isSelected) 
                        MaterialTheme.colorScheme.onPrimary 
                    else 
                        MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = appointmentsCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

/**
 * Отображение пустого дня (без встреч)
 */
@Composable
private fun EmptyMonthDayView() {
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
 * Отображает элемент встречи в месячном календаре
 */
@Composable
private fun MonthAppointmentItem(
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Цветной индикатор статуса
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(getStatusColor(appointment.status))
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Информация о встрече
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = appointment.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = formatTimeRange(appointment),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                if (appointment.clientName != null) {
                    Text(
                        text = appointment.clientName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
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
            
            // Статус встречи
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
            .clip(RoundedCornerShape(4.dp))
            .background(getStatusColor(status).copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.name.lowercase().replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.bodySmall,
            color = getStatusColor(status)
        )
    }
} 