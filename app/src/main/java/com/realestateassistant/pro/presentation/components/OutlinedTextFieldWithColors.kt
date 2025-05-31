package com.realestateassistant.pro.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

/**
 * Компонент текстового поля с настраиваемыми цветами и поддержкой отображения ошибок.
 * 
 * @param value Текущее значение поля
 * @param onValueChange Функция обратного вызова при изменении значения
 * @param label Метка поля
 * @param placeholder Содержимое плейсхолдера
 * @param keyboardOptions Опции клавиатуры
 * @param visualTransformation Трансформация визуального отображения текста
 * @param minLines Минимальное количество строк
 * @param maxLines Максимальное количество строк
 * @param isError Флаг, указывающий на наличие ошибки
 * @param errorMessage Сообщение об ошибке
 * @param readOnly Флаг только для чтения
 * @param isRequired Флаг, указывающий что поле обязательное
 */
@Composable
fun OutlinedTextFieldWithColors(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    minLines: Int = 1,
    maxLines: Int = 1,
    isError: Boolean = false,
    errorMessage: String? = null,
    readOnly: Boolean = false,
    isRequired: Boolean = false
) {
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
        onValueChange = onValueChange,
        label = { Text(labelText) },
        placeholder = placeholder,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        minLines = minLines,
        maxLines = maxLines,
        colors = colors,
        isError = isError,
        supportingText = if (isError && errorMessage != null) {
            { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
        } else null,
        readOnly = readOnly,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
} 