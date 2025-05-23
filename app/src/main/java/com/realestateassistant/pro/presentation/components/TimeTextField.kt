package com.realestateassistant.pro.presentation.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue

/**
 * Компонент для ввода времени в формате ЧЧ:ММ с дополнительными опциями форматирования
 */
@Composable
fun TimeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    // Используем TextFieldValue для контроля позиции курсора
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(value, TextRange(value.length)))
    }
    
    // Обновляем TextFieldValue при изменении входящего value
    if (textFieldValue.text != value) {
        textFieldValue = TextFieldValue(value, TextRange(value.length))
    }
    
    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            // Сохраняем текущую позицию курсора
            val cursorPosition = newValue.selection.start
            
            // Удаляем все нецифровые символы, кроме двоеточия
            val filtered = newValue.text.filter { it.isDigit() || it == ':' }
            
            // Обрабатываем ввод в зависимости от длины
            val (resultText, newCursorPosition) = formatTimeInputWithCursor(
                filtered, 
                textFieldValue.text, 
                cursorPosition
            )
            
            // Обновляем TextFieldValue с правильной позицией курсора
            textFieldValue = TextFieldValue(
                text = resultText,
                selection = TextRange(newCursorPosition.coerceIn(0, resultText.length))
            )
            
            // Вызываем onValueChange только если текст изменился
            if (resultText != value) {
                onValueChange(resultText)
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier,
        placeholder = { Text("ЧЧ:ММ") },
        leadingIcon = { 
            Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        singleLine = true,
        supportingText = { Text("Введите время в формате ЧЧ:ММ") }
    )
}

/**
 * Форматирует ввод времени и возвращает новую строку и позицию курсора
 */
private fun formatTimeInputWithCursor(
    input: String, 
    oldValue: String, 
    cursorPosition: Int
): Pair<String, Int> {
    // Удаляем все нецифровые символы для работы с чистыми цифрами
    val digitsOnly = input.filter { it.isDigit() }
    
    // Если стираем символы (новое значение короче старого)
    if (input.length < oldValue.length) {
        return Pair(input, cursorPosition)
    }
    
    // Если добавляем двоеточие
    val addedColon = input.length > oldValue.length && input.contains(':') && !oldValue.contains(':')
    
    return when {
        // Если ввели только 1 или 2 цифры (часы)
        digitsOnly.length == 1 -> Pair(digitsOnly, cursorPosition)
        digitsOnly.length == 2 -> {
            val hours = digitsOnly.toInt()
            val validHours = if (hours > 23) "23" else digitsOnly
            Pair(validHours, cursorPosition)
        }
        
        // Если ввели 3 или 4 цифры - форматируем как ЧЧ:ММ
        digitsOnly.length >= 3 -> {
            val hours = digitsOnly.substring(0, 2).toInt().coerceAtMost(23)
            val hoursText = hours.toString().padStart(2, '0')
            
            // Берем оставшиеся цифры для минут
            val minutesStr = digitsOnly.substring(2, minOf(digitsOnly.length, 4))
            
            // Проверяем, не превышают ли минуты 59
            val minutes = if (minutesStr.length == 2) {
                minutesStr.toInt().coerceAtMost(59)
            } else {
                minutesStr.toInt()
            }
            
            // Форматируем минуты
            val minutesText = if (minutesStr.length == 2) {
                minutes.toString().padStart(2, '0')
            } else {
                minutes.toString()
            }
            
            // Собираем финальную строку времени
            val formattedTime = "$hoursText:$minutesText"
            
            // Определяем новую позицию курсора
            val newCursorPosition = if (cursorPosition > 2 && !oldValue.contains(':')) {
                // Если добавляем двоеточие и курсор был после второй цифры
                cursorPosition + 1
            } else {
                cursorPosition
            }
            
            Pair(formattedTime, newCursorPosition)
        }
        
        // Для всех других случаев - возвращаем входную строку
        else -> Pair(input, cursorPosition)
    }
} 