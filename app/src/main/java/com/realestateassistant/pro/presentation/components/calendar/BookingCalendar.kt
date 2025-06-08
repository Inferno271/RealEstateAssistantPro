package com.realestateassistant.pro.presentation.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.realestateassistant.pro.domain.model.Booking
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/**
 * Компонент календаря бронирований
 * 
 * @param bookings Список бронирований для отображения
 * @param selectedDate Выбранная дата
 * @param onDateSelected Обработчик выбора даты
 * @param onBookingSelected Обработчик выбора бронирования
 * @param modifier Модификатор
 */
@Composable
fun BookingCalendar(
    bookings: List<Booking>,
    selectedDate: LocalDate? = null,
    onDateSelected: (LocalDate) -> Unit,
    onBookingSelected: (Booking) -> Unit,
    modifier: Modifier = Modifier
) {
    // Определяем текущую дату и диапазон календаря (текущий месяц + 11 месяцев)
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(1) }
    val endMonth = remember { currentMonth.plusMonths(11) }
    val today = remember { LocalDate.now() }

    // Состояние календаря
    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek().first()
    )

    Column(modifier = modifier) {
        // Заголовки дней недели
        val daysOfWeek = remember { daysOfWeek() }
        Row(modifier = Modifier.fillMaxWidth()) {
            for (dayOfWeek in daysOfWeek) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Календарь
        HorizontalCalendar(
            state = calendarState,
            dayContent = { calendarDay ->
                // Получаем состояние дня на основе бронирований
                val dayState = getDayState(calendarDay, today, selectedDate, bookings)
                
                // Отображаем день календаря
                if (calendarDay.position == DayPosition.MonthDate) {
                    BookingCalendarDay(
                        date = calendarDay.date,
                        state = dayState,
                        onClick = {
                            if (dayState != BookingDayState.Disabled) {
                                onDateSelected(calendarDay.date)
                            }
                            
                            // Если день забронирован, вызываем обработчик выбора бронирования
                            if (dayState is BookingDayState.Booked) {
                                val booking = bookings.find { booking ->
                                    val bookingStartDate = LocalDate.ofEpochDay(booking.startDate / 86400000)
                                    val bookingEndDate = LocalDate.ofEpochDay(booking.endDate / 86400000)
                                    calendarDay.date in bookingStartDate..bookingEndDate
                                }
                                booking?.let { onBookingSelected(it) }
                            }
                        },
                    )
                } else {
                    // Отображаем пустую ячейку для дней из соседних месяцев
                    Box {}
                }
            },
            monthHeader = { month ->
                // Заголовок месяца
                MonthHeader(month)
            },
        )
    }
}

@Composable
private fun MonthHeader(month: CalendarMonth) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = month.yearMonth.format(
                java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
            ),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )
    }
}

/**
 * Определяет состояние дня в зависимости от бронирований
 */
private fun getDayState(
    calendarDay: CalendarDay,
    today: LocalDate,
    selectedDate: LocalDate?,
    bookings: List<Booking>
): BookingDayState {
    // Если дата в прошлом, она недоступна
    if (calendarDay.date.isBefore(today)) {
        return BookingDayState.Disabled
    }
    
    // Если дата выбрана
    if (selectedDate == calendarDay.date) {
        return BookingDayState.Selected
    }
    
    // Проверяем, есть ли бронирования на эту дату
    for (booking in bookings) {
        val startDate = LocalDate.ofEpochDay(booking.startDate / 86400000)
        val endDate = LocalDate.ofEpochDay(booking.endDate / 86400000)
        
        // Если дата входит в диапазон бронирования
        if ((calendarDay.date.isEqual(startDate) || calendarDay.date.isAfter(startDate)) && 
            (calendarDay.date.isEqual(endDate) || calendarDay.date.isBefore(endDate))) {
            return BookingDayState.Booked(booking.status, booking.id)
        }
    }
    
    // По умолчанию день доступен
    return BookingDayState.Available
} 