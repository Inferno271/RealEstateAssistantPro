package com.realestateassistant.pro.presentation.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.realestateassistant.pro.domain.model.Booking
import com.realestateassistant.pro.domain.model.BookingStatus
import com.realestateassistant.pro.domain.model.Client
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Диалог для отображения информации о бронировании
 * 
 * @param booking Бронирование для отображения
 * @param client Клиент, создавший бронирование
 * @param onClose Колбек для закрытия диалога
 * @param onEdit Колбек для редактирования бронирования
 * @param onDelete Колбек для удаления бронирования
 * @param onStatusChange Колбек для изменения статуса бронирования
 */
@Composable
fun BookingInfoDialog(
    booking: Booking,
    client: Client?,
    onClose: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onStatusChange: (BookingStatus) -> Unit
) {
    // Конвертация дат из timestamp в LocalDate
    val startDate = Instant.ofEpochMilli(booking.startDate)
        .atZone(ZoneId.systemDefault()).toLocalDate()
    
    val endDate = Instant.ofEpochMilli(booking.endDate)
        .atZone(ZoneId.systemDefault()).toLocalDate()
    
    // Количество ночей
    val nightsCount = ChronoUnit.DAYS.between(startDate, endDate)
    
    // Форматтер для дат
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    
    // Форматтер валюты
    val currencyFormat = NumberFormat.getCurrencyInstance()
    
    // Выбор статуса бронирования
    var showStatusSelector by remember { mutableStateOf(false) }
    
    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Заголовок и кнопка закрытия
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Информация о бронировании",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Закрыть"
                        )
                    }
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // Статус бронирования (с возможностью изменения)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showStatusSelector = true }
                            .background(
                                color = when(booking.status) {
                                    BookingStatus.PENDING -> Color(0xFFFFF9C4)
                                    BookingStatus.CONFIRMED -> Color(0xFFE3F2FD)
                                    BookingStatus.ACTIVE -> Color(0xFFE8F5E9)
                                    BookingStatus.COMPLETED -> Color(0xFFEFEBE9)
                                    BookingStatus.CANCELLED -> Color(0xFFFFEBEE)
                                    BookingStatus.EXPIRED -> Color(0xFFF5F5F5)
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when(booking.status) {
                                BookingStatus.PENDING -> "Ожидает подтверждения"
                                BookingStatus.CONFIRMED -> "Подтверждено"
                                BookingStatus.ACTIVE -> "Активно"
                                BookingStatus.COMPLETED -> "Завершено"
                                BookingStatus.CANCELLED -> "Отменено"
                                BookingStatus.EXPIRED -> "Просрочено"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    IconButton(
                        onClick = { showStatusSelector = true },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Изменить статус",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // Даты бронирования
                InfoItem(
                    icon = Icons.Outlined.CalendarMonth,
                    label = "Даты",
                    value = "${startDate.format(dateFormatter)} - ${endDate.format(dateFormatter)} ($nightsCount ${getNightsString(nightsCount.toInt())})"
                )
                
                // Клиент
                InfoItem(
                    icon = Icons.Outlined.Person,
                    label = "Клиент",
                    value = client?.fullName ?: "Не указан"
                )
                
                // Сумма бронирования
                InfoItem(
                    icon = Icons.Outlined.AttachMoney,
                    label = "Сумма",
                    value = currencyFormat.format(booking.totalAmount.toDouble())
                )
                
                // Количество гостей
                InfoItem(
                    icon = Icons.Outlined.Group,
                    label = "Количество гостей",
                    value = booking.guestsCount?.toString() ?: "Не указано"
                )
                
                // Примечания
                if (!booking.notes.isNullOrBlank()) {
                    InfoItem(
                        icon = Icons.Outlined.Notes,
                        label = "Примечания",
                        value = booking.notes
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Кнопки действий
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Кнопка удаления
                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(text = "Удалить")
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Кнопка редактирования
                    Button(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(text = "Редактировать")
                    }
                }
                
                // Селектор статуса (показывается при необходимости)
                if (showStatusSelector) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Text(
                        text = "Выберите новый статус",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Отображаем только подходящие статусы для изменения
                        val availableStatuses = when (booking.status) {
                            BookingStatus.PENDING -> listOf(
                                BookingStatus.CONFIRMED,
                                BookingStatus.CANCELLED
                            )
                            BookingStatus.CONFIRMED -> listOf(
                                BookingStatus.ACTIVE,
                                BookingStatus.CANCELLED
                            )
                            BookingStatus.ACTIVE -> listOf(
                                BookingStatus.COMPLETED,
                                BookingStatus.CANCELLED
                            )
                            BookingStatus.COMPLETED -> listOf(
                                BookingStatus.ACTIVE
                            )
                            BookingStatus.CANCELLED -> listOf(
                                BookingStatus.PENDING,
                                BookingStatus.CONFIRMED
                            )
                            BookingStatus.EXPIRED -> listOf(
                                BookingStatus.PENDING,
                                BookingStatus.CONFIRMED
                            )
                        }
                        
                        availableStatuses.forEach { status ->
                            StatusItem(
                                status = status,
                                onClick = {
                                    onStatusChange(status)
                                    showStatusSelector = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Элемент информации о бронировании
 */
@Composable
private fun InfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 16.dp)
        )
        
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Элемент для выбора статуса бронирования
 */
@Composable
private fun StatusItem(
    status: BookingStatus,
    onClick: () -> Unit
) {
    val statusColor = when(status) {
        BookingStatus.PENDING -> Color(0xFFFFF9C4)
        BookingStatus.CONFIRMED -> Color(0xFFE3F2FD)
        BookingStatus.ACTIVE -> Color(0xFFE8F5E9)
        BookingStatus.COMPLETED -> Color(0xFFEFEBE9)
        BookingStatus.CANCELLED -> Color(0xFFFFEBEE)
        BookingStatus.EXPIRED -> Color(0xFFF5F5F5)
    }
    
    val statusText = when(status) {
        BookingStatus.PENDING -> "Ожидает подтверждения"
        BookingStatus.CONFIRMED -> "Подтверждено"
        BookingStatus.ACTIVE -> "Активно"
        BookingStatus.COMPLETED -> "Завершено"
        BookingStatus.CANCELLED -> "Отменено"
        BookingStatus.EXPIRED -> "Просрочено"
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(statusColor.copy(alpha = 0.3f))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = statusText,
            style = MaterialTheme.typography.bodyLarge
        )
        
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * Возвращает правильное склонение слова "ночь" в зависимости от числа
 */
private fun getNightsString(count: Int): String {
    return when {
        count % 10 == 1 && count % 100 != 11 -> "ночь"
        count % 10 in 2..4 && count % 100 !in 12..14 -> "ночи"
        else -> "ночей"
    }
} 