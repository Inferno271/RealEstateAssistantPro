package com.realestateassistant.pro.presentation.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.domain.model.Booking
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Упрощенная версия календаря без использования библиотеки Kizitonwose
 * 
 * @param bookings Список бронирований для отображения
 * @param selectedDate Выбранная дата
 * @param selectedEndDate Выбранная дата окончания бронирования
 * @param onDateSelected Обработчик выбора даты
 * @param onBookingSelected Обработчик выбора бронирования
 * @param modifier Модификатор
 */
@Composable
fun BookingCalendarCompat(
    bookings: List<Booking>,
    selectedDate: LocalDate? = null,
    selectedEndDate: LocalDate? = null,
    onDateSelected: (LocalDate) -> Unit,
    onBookingSelected: (Booking) -> Unit,
    modifier: Modifier = Modifier
) {
    // Определяем текущий месяц и год
    val currentMonth = remember { YearMonth.now() }
    val today = remember { LocalDate.now() }
    
    var displayedMonth by remember { mutableStateOf(currentMonth) }
    
    Column(modifier = modifier) {
        // Заголовок месяца с кнопками навигации
        MonthNavigationHeader(
            yearMonth = displayedMonth,
            onPreviousMonth = { displayedMonth = displayedMonth.minusMonths(1) },
            onNextMonth = { displayedMonth = displayedMonth.plusMonths(1) }
        )
        
        // Заголовки дней недели (Пн, Вт, Ср...)
        DaysOfWeekHeader()
        
        // Отображаем сетку дней текущего месяца
        MonthDaysGrid(
            yearMonth = displayedMonth,
            today = today,
            bookings = bookings,
            selectedStartDate = selectedDate,
            selectedEndDate = selectedEndDate,
            onDateSelected = onDateSelected,
            onBookingSelected = onBookingSelected
        )
        
        // Информационная панель с легендой статусов бронирования
        BookingStatusLegend()
    }
}

@Composable
private fun MonthNavigationHeader(
    yearMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Кнопка предыдущего месяца
        IconButton(onClick = onPreviousMonth) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Предыдущий месяц",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        // Заголовок текущего месяца
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            // Переводим первую букву названия месяца в верхний регистр
            val monthName = yearMonth.month.getDisplayName(
                java.time.format.TextStyle.FULL_STANDALONE, 
                Locale.getDefault()
            ).let { name ->
                name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            }
            
            val year = yearMonth.year
            
            Text(
                text = "$monthName $year",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        
        // Кнопка следующего месяца
        IconButton(onClick = onNextMonth) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Следующий месяц",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun DaysOfWeekHeader() {
    val daysOfWeek = remember { 
        listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс") 
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 2.dp)
    ) {
        for (dayOfWeek in daysOfWeek) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dayOfWeek,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (dayOfWeek in listOf("Сб", "Вс")) FontWeight.Bold else FontWeight.Normal,
                    color = if (dayOfWeek in listOf("Сб", "Вс")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun MonthDaysGrid(
    yearMonth: YearMonth,
    today: LocalDate,
    bookings: List<Booking>,
    selectedStartDate: LocalDate?,
    selectedEndDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onBookingSelected: (Booking) -> Unit
) {
    // Получаем первый день месяца
    val firstOfMonth = yearMonth.atDay(1)
    
    // Получаем номер дня недели для первого дня месяца (1 = Понедельник ... 7 = Воскресенье)
    val firstDayOfWeekValue = firstOfMonth.dayOfWeek.value  
    
    // Количество дней в месяце
    val daysInMonth = yearMonth.lengthOfMonth()
    
    // Количество недель для отображения
    val weeksCount = (daysInMonth + firstDayOfWeekValue - 1 + 6) / 7
    
    Column(modifier = Modifier.fillMaxWidth()) {
        for (weekIndex in 0 until weeksCount) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                // Для каждого дня недели (1 = Пн, 2 = Вт, ..., 7 = Вс)
                for (dayOfWeek in 1..7) {
                    val day = weekIndex * 7 + dayOfWeek - firstDayOfWeekValue + 1
                    
                    if (day in 1..daysInMonth) {
                        // Это день текущего месяца
                        val date = yearMonth.atDay(day)
                        
                        // Определяем состояние дня
                        val dayState = getDayState(date, today, selectedStartDate, selectedEndDate, bookings)
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                        ) {
                            BookingCalendarDay(
                                date = date,
                                state = dayState,
                                onClick = {
                                    if (dayState != BookingDayState.Disabled) {
                                        onDateSelected(date)
                                    }
                                    
                                    // Если день забронирован, вызываем обработчик выбора бронирования
                                    if (dayState is BookingDayState.Booked) {
                                        val booking = bookings.find { booking ->
                                            val bookingStartDate = LocalDate.ofEpochDay(booking.startDate / 86400000)
                                            val bookingEndDate = LocalDate.ofEpochDay(booking.endDate / 86400000)
                                            date in bookingStartDate..bookingEndDate
                                        }
                                        booking?.let { onBookingSelected(it) }
                                    }
                                }
                            )
                        }
                    } else {
                        // Пустая ячейка для дней вне текущего месяца
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingStatusLegend() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Статусы бронирования:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        val statuses = listOf(
            "Доступно" to Color.Transparent,
            "Выбрано" to MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            "Ожидает" to Color(0xFFFFF9C4),
            "Подтверждено" to Color(0xFFE3F2FD),
            "Активно" to Color(0xFFE8F5E9),
            "Отменено" to Color(0xFFFFEBEE)
        )
        
        // Отображаем статусы в две колонки
        Column(modifier = Modifier.fillMaxWidth()) {
            for (i in statuses.indices step 2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    // Левая колонка
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                                .background(
                                    color = statuses[i].second,
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                        Text(
                            text = statuses[i].first,
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    // Правая колонка (если есть)
                    if (i + 1 < statuses.size) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                                    .background(
                                        color = statuses[i + 1].second,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            )
                            Text(
                                text = statuses[i + 1].first,
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * Определяет состояние дня в зависимости от бронирований
 */
private fun getDayState(
    date: LocalDate,
    today: LocalDate,
    selectedStartDate: LocalDate?,
    selectedEndDate: LocalDate?,
    bookings: List<Booking>
): BookingDayState {
    // Если дата в прошлом, она недоступна
    if (date.isBefore(today)) {
        return BookingDayState.Disabled
    }
    
    // Если дата выбрана как начало или конец периода
    if (selectedStartDate == date || selectedEndDate == date) {
        return BookingDayState.Selected
    }
    
    // Если дата находится в диапазоне выбора
    if (selectedStartDate != null && selectedEndDate != null && 
        date.isAfter(selectedStartDate) && date.isBefore(selectedEndDate)) {
        return BookingDayState.InSelectionRange
    }
    
    // Проверяем, есть ли бронирования на эту дату
    for (booking in bookings) {
        val startDate = LocalDate.ofEpochDay(booking.startDate / 86400000)
        val endDate = LocalDate.ofEpochDay(booking.endDate / 86400000)
        
        // Если дата входит в диапазон бронирования
        if ((date.isEqual(startDate) || date.isAfter(startDate)) && 
            (date.isEqual(endDate) || date.isBefore(endDate))) {
            return BookingDayState.Booked(booking.status, booking.id)
        }
    }
    
    // По умолчанию день доступен
    return BookingDayState.Available
} 