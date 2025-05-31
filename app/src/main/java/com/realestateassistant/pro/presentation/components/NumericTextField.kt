package com.realestateassistant.pro.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle

/**
 * Текстовое поле для ввода числовых значений.
 * 
 * @param value Текущее значение
 * @param onValueChange Функция обратного вызова при изменении значения
 * @param label Метка поля
 * @param modifier Модификатор для настройки внешнего вида
 * @param allowDecimal Разрешить ли ввод десятичных чисел
 * @param maxValue Максимальное допустимое значение
 * @param suffix Суффикс для отображения после числа
 * @param isRequired Является ли поле обязательным
 * @param isError Флаг, указывающий на наличие ошибки
 * @param errorMessage Сообщение об ошибке
 */
@Composable
fun NumericTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    allowDecimal: Boolean = true,
    maxValue: Double? = null,
    suffix: String = "",
    isRequired: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val pattern = if (allowDecimal) {
        Regex("^\\d*\\.?\\d*$") // Разрешает десятичные числа
    } else {
        Regex("^\\d*$") // Только целые числа
    }

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
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            cursorColor = MaterialTheme.colorScheme.primary
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

    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.isEmpty() || pattern.matches(newValue)) {
                val numericValue = newValue.toDoubleOrNull()
                if (maxValue == null || numericValue == null || numericValue <= maxValue) {
                    onValueChange(newValue)
                }
            }
        },
        label = { 
            Text(labelText) 
        },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (allowDecimal) KeyboardType.Decimal else KeyboardType.Number
        ),
        suffix = if (suffix.isNotEmpty()) { { Text(suffix) } } else null,
        isError = isError,
        supportingText = if (isError && errorMessage != null) {
            { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
        } else null,
        colors = colors
    )
} 