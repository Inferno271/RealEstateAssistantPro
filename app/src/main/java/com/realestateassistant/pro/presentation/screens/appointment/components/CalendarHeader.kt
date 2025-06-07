package com.realestateassistant.pro.presentation.screens.appointment.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.presentation.screens.appointment.models.CalendarView
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * Заголовок календаря с кнопками навигации и датой
 */
@Composable
fun CalendarHeader(
    selectedDate: LocalDate,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    calendarView: CalendarView,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Предыдущий период"
            )
        }
        
        // Заголовок с датой/месяцем в зависимости от текущего представления
        Row(
            modifier = Modifier
                .weight(1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            
            Text(
                text = formatHeaderDate(selectedDate, calendarView),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        
        IconButton(onClick = onNextClick) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Следующий период"
            )
        }
    }
}

/**
 * Форматирует дату для заголовка в зависимости от текущего представления
 */
private fun formatHeaderDate(date: LocalDate, calendarView: CalendarView): String {
    return when (calendarView) {
        CalendarView.DAY -> {
            val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("ru"))
            val day = date.dayOfMonth
            val month = date.month.getDisplayName(TextStyle.FULL, Locale("ru"))
            "$dayOfWeek, $day $month"
        }
        CalendarView.WEEK -> {
            val firstDayOfWeek = date.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
            val lastDayOfWeek = firstDayOfWeek.plusDays(6)
            
            val firstDayMonth = firstDayOfWeek.month.getDisplayName(TextStyle.SHORT, Locale("ru"))
            val lastDayMonth = lastDayOfWeek.month.getDisplayName(TextStyle.SHORT, Locale("ru"))
            
            if (firstDayOfWeek.month == lastDayOfWeek.month) {
                "${firstDayOfWeek.dayOfMonth} - ${lastDayOfWeek.dayOfMonth} $firstDayMonth"
            } else {
                "${firstDayOfWeek.dayOfMonth} $firstDayMonth - ${lastDayOfWeek.dayOfMonth} $lastDayMonth"
            }
        }
        CalendarView.MONTH -> {
            val month = date.month.getDisplayName(TextStyle.FULL, Locale("ru"))
            val year = date.year
            "$month $year"
        }
        else -> {
            val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("ru"))
            date.format(formatter)
        }
    }
} 