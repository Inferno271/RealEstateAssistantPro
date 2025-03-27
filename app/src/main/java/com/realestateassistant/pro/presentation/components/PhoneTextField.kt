package com.realestateassistant.pro.presentation.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.MaterialTheme

@Composable
fun PhoneTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var hasFocus by remember { mutableStateOf(false) }
    
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            // Фильтруем ввод - принимаем только цифры
            val filtered = newValue.filter { it.isDigit() }
            // Ограничиваем длину до 10 цифр (для формата +7 XXX XXX XX XX)
            if (filtered.length <= 10) {
                onValueChange(filtered)
            }
        },
        label = { Text(label) },
        visualTransformation = PhoneVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        modifier = modifier.onFocusChanged { hasFocus = it.isFocused },
        isError = isError,
        supportingText = if (isError && errorMessage != null) {
            { Text(errorMessage) }
        } else null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isError) Color.Red else MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = if (isError) Color.Red else MaterialTheme.colorScheme.outline
        )
    )
}

class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Добавляем форматирование: +7 (XXX) XXX-XX-XX
        val trimmed = if (text.text.length >= 10) text.text.substring(0..9) else text.text
        val formattedText = buildFormattedPhoneNumber(trimmed)
        
        // Создаем карту соответствия между исходными и отформатированными индексами
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // Карта соответствия индексов для корректной работы курсора
                return when {
                    offset <= 0 -> 4 // +7 ( - 4 символа в начале
                    offset <= 3 -> offset + 4 // +7 (XXX - добавляем 4 символа
                    offset <= 6 -> offset + 6 // +7 (XXX) XXX - добавляем 6 символов
                    offset <= 8 -> offset + 7 // +7 (XXX) XXX-XX - добавляем 7 символов
                    else -> offset + 8 // +7 (XXX) XXX-XX-XX - добавляем 8 символов
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 4 -> 0 // Всё до первой цифры после +7 (
                    offset <= 7 -> offset - 4 // Три цифры после +7 (
                    offset <= 12 -> offset - 6 // Три цифры после закрывающей скобки
                    offset <= 15 -> offset - 7 // Две цифры после первого дефиса
                    offset <= 18 -> offset - 8 // Две цифры после второго дефиса
                    else -> 10 // Всё что после
                }
            }
        }

        return TransformedText(
            text = AnnotatedString(formattedText),
            offsetMapping = offsetMapping
        )
    }
}

fun buildFormattedPhoneNumber(input: String): String {
    val builder = StringBuilder("+7 (")
    
    input.forEachIndexed { index, char ->
        builder.append(char)
        when (index) {
            2 -> builder.append(") ") // После третьей цифры добавляем ) и пробел
            5 -> builder.append("-") // После шестой цифры добавляем дефис
            7 -> builder.append("-") // После восьмой цифры добавляем дефис
        }
    }
    
    // Дополняем шаблон, если он не полностью заполнен
    return when (input.length) {
        0 -> "+7 ("
        1 -> "+7 (${input[0]}"
        2 -> "+7 (${input[0]}${input[1]}"
        3 -> "+7 (${input.substring(0, 3)})"
        4 -> "+7 (${input.substring(0, 3)}) ${input[3]}"
        5 -> "+7 (${input.substring(0, 3)}) ${input.substring(3, 5)}"
        6 -> "+7 (${input.substring(0, 3)}) ${input.substring(3, 6)}"
        7 -> "+7 (${input.substring(0, 3)}) ${input.substring(3, 6)}-${input[6]}"
        8 -> "+7 (${input.substring(0, 3)}) ${input.substring(3, 6)}-${input.substring(6, 8)}"
        9 -> "+7 (${input.substring(0, 3)}) ${input.substring(3, 6)}-${input.substring(6, 8)}-${input[8]}"
        10 -> "+7 (${input.substring(0, 3)}) ${input.substring(3, 6)}-${input.substring(6, 8)}-${input.substring(8, 10)}"
        else -> builder.toString()
    }
} 