package com.realestateassistant.pro.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
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
 * @param isError Флаг, указывающий на наличие ошибки
 * @param errorMessage Сообщение об ошибке
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    value: Long?,
    onValueChange: (Long?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null
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
    
    // Настройка цветов в зависимости от наличия ошибки
    val colors = if (isError) {
        OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.error,
            unfocusedBorderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
            focusedLabelColor = MaterialTheme.colorScheme.error,
            unfocusedLabelColor = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
            cursorColor = MaterialTheme.colorScheme.error
        )
    } else {
        OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    val labelText = buildAnnotatedString {
        append(label)
        if (isRequired) {
            append(" ")
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                append("*")
            }
        }
    }
    
    // Поле для отображения выбранной даты
    OutlinedTextField(
        value = displayDate,
        onValueChange = { /* Игнорируем прямой ввод */ },
        label = { 
            Text(labelText)
        },
        trailingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isError) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = "Ошибка",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        Icons.Default.DateRange, 
                        contentDescription = "Выбрать дату",
                        tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        readOnly = true,
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDatePicker = true },
        colors = colors,
        isError = isError,
        supportingText = if (isError && errorMessage != null) {
            { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
        } else null
    )
} 