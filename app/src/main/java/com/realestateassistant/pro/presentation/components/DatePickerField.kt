package com.realestateassistant.pro.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * Компонент для выбора даты с календарем.
 *
 * @param value Текущее значение даты в миллисекундах
 * @param onValueChange Функция обратного вызова при изменении даты
 * @param label Название поля
 * @param modifier Модификатор для настройки внешнего вида
 * @param isRequired Флаг, указывающий является ли поле обязательным
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    value: Long?,
    onValueChange: (Long?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false
) {
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    
    // Форматируем дату для отображения
    val displayDate = if (value != null) dateFormat.format(Date(value)) else ""
    
    // Создаем состояние выбранной даты для DatePicker
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = value ?: System.currentTimeMillis()
    )
    
    // Показываем диалог выбора даты при необходимости
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { onValueChange(it) }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
    
    // Поле для отображения выбранной даты
    OutlinedTextField(
        value = displayDate,
        onValueChange = { /* Игнорируем прямой ввод */ },
        label = { 
            Text(
                if (isRequired) "$label *" else label
            )
        },
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Default.DateRange, contentDescription = "Выбрать дату")
            }
        },
        readOnly = true,
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDatePicker = true },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
} 