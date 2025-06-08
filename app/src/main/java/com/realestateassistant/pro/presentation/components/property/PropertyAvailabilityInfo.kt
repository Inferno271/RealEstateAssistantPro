package com.realestateassistant.pro.presentation.components.property

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.domain.model.Booking
import com.realestateassistant.pro.domain.model.BookingStatus
import com.realestateassistant.pro.domain.model.Property
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Компонент для отображения информации о доступности объекта недвижимости
 * Показывает текущий статус (занят/свободен), а также кто арендует (если объект занят) и дату освобождения
 *
 * @param property Объект недвижимости
 * @param currentBookings Список текущих бронирований объекта
 * @param modifier Модификатор
 */
@Composable
fun PropertyAvailabilityInfo(
    property: Property,
    currentBookings: List<Booking>,
    modifier: Modifier = Modifier
) {
    // Находим активное бронирование (если есть)
    val activeBooking = currentBookings.find { 
        it.status == BookingStatus.ACTIVE || 
        it.status == BookingStatus.CONFIRMED 
    }
    
    // Определяем, свободен ли объект
    val isAvailable = activeBooking == null
    
    // Определяем цвет фона в зависимости от доступности
    val backgroundColor = if (isAvailable) {
        Color(0xFFE8F5E9) // Зеленоватый оттенок для свободного
    } else {
        Color(0xFFFFF3E0) // Оранжеватый оттенок для занятого
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Заголовок: статус доступности
            Text(
                text = if (isAvailable) "Свободен для бронирования" else "Занят",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (!isAvailable && activeBooking != null) {
                // Информация о текущем клиенте
                activeBooking.clientId?.let { clientId ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "Арендует: ${activeBooking.clientName ?: "Клиент ID: $clientId"}",
                            style = MaterialTheme.typography.bodyMedium,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }
                
                // Информация о дате освобождения
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    val endDate = Instant.ofEpochMilli(activeBooking.endDate)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    
                    Text(
                        text = "Освободится: ${endDate.format(formatter)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                // Если объект свободен
                Text(
                    text = "Нет активных бронирований",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Расширение на Booking для получения имени клиента (если известно)
 */
val Booking.clientName: String?
    get() = null // Здесь должна быть логика получения имени клиента, если она доступна 