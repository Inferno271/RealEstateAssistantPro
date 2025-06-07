package com.realestateassistant.pro.presentation.screens.appointment.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.domain.model.Appointment
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Компонент для отображения дневного расписания встреч
 */
@Composable
fun DaySchedule(
    date: LocalDate,
    appointments: List<Appointment>,
    onAppointmentClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    emptyContent: @Composable () -> Unit = { EmptyDaySchedule() }
) {
    val timeSlots = remember {
        (0..23).map { hour ->
            LocalTime.of(hour, 0)
        }
    }

    val appointmentsByHour = remember(appointments, date) {
        appointments.groupBy { appointment ->
            val startTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(appointment.startTime),
                ZoneId.systemDefault()
            )
            startTime.hour
        }
    }
    
    val allDayAppointments = remember(appointments, date) {
        appointments.filter { it.isAllDay }
    }
    
    if (appointments.isEmpty()) {
        emptyContent()
    } else {
        LazyColumn(modifier = modifier) {
            // Отображение встреч на весь день
            if (allDayAppointments.isNotEmpty()) {
                item {
                    Column {
                        Text(
                            text = "Весь день",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                        )
                        
                        allDayAppointments.forEach { appointment ->
                            AllDayAppointmentItem(
                                appointment = appointment,
                                onClick = { onAppointmentClick(appointment.id) }
                            )
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
            
            // Отображение часовых слотов
            items(timeSlots) { time ->
                TimeSlotItem(
                    time = time,
                    appointments = appointmentsByHour[time.hour] ?: emptyList(),
                    onAppointmentClick = onAppointmentClick
                )
            }
        }
    }
}

/**
 * Компонент для отображения пустого дневного расписания
 */
@Composable
fun EmptyDaySchedule() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Нет запланированных встреч на этот день",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Элемент временного слота
 */
@Composable
fun TimeSlotItem(
    time: LocalTime,
    appointments: List<Appointment>,
    onAppointmentClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Отображение времени
        Text(
            text = timeFormatter.format(time),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .width(50.dp)
                .padding(start = 8.dp, end = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // Разделитель
        Divider(
            modifier = Modifier
                .height(if (appointments.isEmpty()) 24.dp else 1.dp)
                .width(1.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        
        // Отображение встреч для этого временного слота
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            appointments.forEach { appointment ->
                AppointmentTimeSlotItem(
                    appointment = appointment,
                    onClick = { onAppointmentClick(appointment.id) }
                )
                
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

/**
 * Преобразует строку с HEX-кодом цвета в объект Color
 */
private fun parseColor(colorString: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorString))
    } catch (e: Exception) {
        Color.Gray // Цвет по умолчанию, если не удалось разобрать строку
    }
}

/**
 * Элемент встречи для временного слота
 */
@Composable
fun AppointmentTimeSlotItem(
    appointment: Appointment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val startTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(appointment.startTime),
        ZoneId.systemDefault()
    )
    val endTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(appointment.endTime),
        ZoneId.systemDefault()
    )
    
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val timeRange = "${timeFormatter.format(startTime)} - ${timeFormatter.format(endTime)}"
    
    val appointmentColor = parseColor(appointment.color)
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 8.dp),
        shape = RoundedCornerShape(4.dp),
        color = appointmentColor.copy(alpha = 0.2f),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(appointmentColor)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = appointment.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = timeRange,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                appointment.location?.let { loc ->
                    if (loc.isNotBlank()) {
                        Text(
                            text = loc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Элемент встречи на весь день
 */
@Composable
fun AllDayAppointmentItem(
    appointment: Appointment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appointmentColor = parseColor(appointment.color)
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        shape = RoundedCornerShape(4.dp),
        color = appointmentColor.copy(alpha = 0.2f),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(appointmentColor)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = appointment.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
} 