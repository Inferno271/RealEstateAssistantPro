package com.realestateassistant.pro.presentation.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realestateassistant.pro.domain.model.BookingStatus
import java.time.LocalDate

/**
 * Состояние дня в календаре бронирований
 */
sealed class BookingDayState {
    /**
     * День свободен для бронирования
     */
    object Available : BookingDayState()
    
    /**
     * День забронирован
     * @param status Статус бронирования
     * @param bookingId ID бронирования
     */
    data class Booked(val status: BookingStatus, val bookingId: String) : BookingDayState()
    
    /**
     * День выбран пользователем (для создания нового бронирования)
     */
    object Selected : BookingDayState()
    
    /**
     * День находится в диапазоне выбора (между двумя выбранными днями)
     */
    object InSelectionRange : BookingDayState()
    
    /**
     * День недоступен для бронирования (прошедший день или заблокированный администратором)
     */
    object Disabled : BookingDayState()
}

/**
 * Компонент для отображения дня в календаре бронирований
 * 
 * @param date Дата
 * @param state Состояние дня
 * @param onClick Обработчик клика по дню
 * @param modifier Модификатор
 */
@Composable
fun BookingCalendarDay(
    date: LocalDate,
    state: BookingDayState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(1.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                when (state) {
                    is BookingDayState.Available -> Color.Transparent
                    is BookingDayState.Booked -> when (state.status) {
                        BookingStatus.PENDING -> Color(0xFFFFF9C4) // Светло-желтый
                        BookingStatus.CONFIRMED -> Color(0xFFE3F2FD) // Светло-синий
                        BookingStatus.ACTIVE -> Color(0xFFE8F5E9) // Светло-зеленый
                        BookingStatus.COMPLETED -> Color(0xFFEFEBE9) // Светло-серый
                        BookingStatus.CANCELLED -> Color(0xFFFFEBEE) // Светло-красный
                        BookingStatus.EXPIRED -> Color(0xFFF5F5F5) // Светло-серый
                    }
                    BookingDayState.Selected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    BookingDayState.InSelectionRange -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    BookingDayState.Disabled -> Color.LightGray.copy(alpha = 0.3f)
                }
            )
            .border(
                width = if (state is BookingDayState.Selected) 1.dp else 0.dp,
                color = if (state is BookingDayState.Selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(
                enabled = state !is BookingDayState.Disabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = when {
                state is BookingDayState.Selected -> MaterialTheme.colorScheme.primary
                state is BookingDayState.Disabled -> Color.Gray
                state is BookingDayState.Booked -> Color.Black
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
} 