package com.realestateassistant.pro.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.util.PhoneUtils
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Компонент для ввода международного телефонного номера с кодом страны
 */
@Composable
fun PhoneInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var phoneNumber by remember { mutableStateOf("") }
    var countryCode by remember { mutableStateOf("+7") }
    
    // Разбор входного значения при инициализации
    LaunchedEffect(value) {
        if (value.isNotEmpty()) {
            if (value.startsWith("+")) {
                // Находим код страны
                val matchedCountry = (popularCountries + otherCountries).find { country ->
                    value.startsWith(country.dialCode)
                }
                
                if (matchedCountry != null) {
                    countryCode = matchedCountry.dialCode
                    phoneNumber = value.substring(matchedCountry.dialCode.length)
                        .replace(Regex("[^\\d]"), "") // Оставляем только цифры
                } else {
                    // Если код страны не найден, но номер начинается с +
                    // Пытаемся определить код страны по первым цифрам
                    val plusIndex = value.indexOf('+')
                    var digitIndex = plusIndex + 1
                    
                    // Находим конец кода страны (обычно 1-3 цифры после +)
                    while (digitIndex < value.length && 
                           value[digitIndex].isDigit() &&
                           digitIndex - plusIndex <= 4) {
                        digitIndex++
                    }
                    
                    if (digitIndex > plusIndex + 1) {
                        countryCode = value.substring(plusIndex, digitIndex)
                        phoneNumber = value.substring(digitIndex)
                            .replace(Regex("[^\\d]"), "") // Оставляем только цифры
                    } else {
                        // Если не удалось определить структуру, просто используем +7
                        countryCode = "+7"
                        phoneNumber = value.replace("+7", "")
                            .replace(Regex("[^\\d]"), "") // Оставляем только цифры
                    }
                }
            } else {
                // Если номер без кода страны, предполагаем +7
                countryCode = "+7"
                phoneNumber = value.replace(Regex("[^\\d]"), "") // Оставляем только цифры
            }
        } else {
            // Для пустого значения
            countryCode = "+7"
            phoneNumber = ""
        }
    }
    
    // Определение валидности телефонного номера
    val isPhoneValid = remember(phoneNumber, countryCode) {
        if (phoneNumber.isEmpty()) true // Пустой номер не считаем ошибкой в UI
        else PhoneUtils.isValidPhoneNumber("$countryCode$phoneNumber")
    }
    
    // Формат номера для подсказки
    val phoneFormat = remember(countryCode) {
        when (countryCode) {
            "+7" -> "Введите 10 цифр номера: (XXX) XXX-XX-XX"
            "+1" -> "Введите 10 цифр номера: (XXX) XXX-XXXX"
            else -> "Введите номер без кода страны"
        }
    }
    
    // Максимальная длина номера
    val maxLength = remember(countryCode) {
        when (countryCode) {
            "+7", "+1" -> 10  // Для России и США ограничиваем 10 цифрами
            else -> 15        // Для других стран допускаем до 15 цифр
        }
    }
    
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Строка для ввода номера с кодом страны
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Компонент выбора кода страны
            CountryCodePicker(
                selectedCountryCode = countryCode,
                onCountryCodeSelected = { code, _ ->
                    countryCode = code
                    // Формируем полный номер в E.164 формате
                    val fullNumber = "$countryCode$phoneNumber"
                    onValueChange(fullNumber)
                },
                modifier = Modifier.wrapContentWidth()
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Поле для ввода номера телефона
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { input ->
                    // Фильтруем только цифры
                    val filtered = input.filter { it.isDigit() }
                    
                    // Ограничиваем длину в зависимости от кода страны
                    if (filtered.length <= maxLength) {
                        phoneNumber = filtered
                        
                        // Формируем полный номер с кодом страны
                        val fullNumber = "$countryCode$filtered"
                        onValueChange(fullNumber)
                    }
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = isError || (!phoneNumber.isEmpty() && !isPhoneValid),
                placeholder = { 
                    // Динамический placeholder с форматом
                    Text(when (countryCode) {
                        "+7" -> "(XXX) XXX-XX-XX"
                        "+1" -> "(XXX) XXX-XXXX"
                        else -> "XXX XXX..."
                    })
                 },
                visualTransformation = when (countryCode) {
                    "+7" -> RussianPhoneVisualTransformation()
                    "+1" -> USPhoneVisualTransformation()
                    else -> DefaultPhoneVisualTransformation()
                }
            )
        }
        
        // Сообщение об ошибке или подсказка о формате
        Spacer(modifier = Modifier.height(4.dp))
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        } else if (!isPhoneValid && phoneNumber.isNotEmpty()) {
            Text(
                text = when (countryCode) {
                    "+7" -> "Введите 10 цифр номера"
                    "+1" -> "Введите 10 цифр номера"
                    else -> "Введите минимум 7 цифр номера"
                },
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        } else {
            Text(
                text = phoneFormat,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * Визуальная трансформация для российских номеров: (XXX) XXX-XX-XX
 */
class RussianPhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digitsOnly = text.text.filter { it.isDigit() }
        
        val formattedText = buildString {
            digitsOnly.forEachIndexed { index, c ->
                when (index) {
                    0 -> append("(").append(c)
                    1, 2 -> append(c)
                    3 -> append(") ").append(c)
                    4, 5 -> append(c)
                    6 -> append("-").append(c)
                    7 -> append(c)
                    8 -> append("-").append(c)
                    9 -> append(c)
                }
            }
        }
        
        // Создаем карту соответствия между исходными и отформатированными индексами
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                if (offset <= 3) return offset + 1 // (XXX
                if (offset <= 6) return offset + 3 // (XXX) XXX
                if (offset <= 8) return offset + 4 // (XXX) XXX-XX
                if (offset <= 10) return offset + 5 // (XXX) XXX-XX-XX
                return formattedText.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                if (offset <= 1) return 0      // (
                if (offset <= 4) return offset - 1 // (XXX
                if (offset <= 5) return 3      // ) 
                if (offset <= 9) return offset - 3 // (XXX) XXX
                if (offset <= 10) return 6     // -
                if (offset <= 12) return offset - 4 // (XXX) XXX-XX
                if (offset <= 13) return 8     // -
                if (offset <= 15) return offset - 5 // (XXX) XXX-XX-XX
                return digitsOnly.length
            }
        }

        return TransformedText(
            text = AnnotatedString(formattedText),
            offsetMapping = offsetMapping
        )
    }
}

/**
 * Визуальная трансформация для американских номеров: (XXX) XXX-XXXX
 */
class USPhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digitsOnly = text.text.filter { it.isDigit() }
        
        val formattedText = buildString {
            digitsOnly.forEachIndexed { index, c ->
                when (index) {
                    0 -> append("(").append(c)
                    1, 2 -> append(c)
                    3 -> append(") ").append(c)
                    4, 5 -> append(c)
                    6 -> append("-").append(c)
                    else -> append(c) // 7, 8, 9
                }
            }
        }
        
        // Создаем карту соответствия между исходными и отформатированными индексами
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                if (offset <= 3) return offset + 1 // (XXX
                if (offset <= 6) return offset + 3 // (XXX) XXX
                if (offset <= 10) return offset + 4 // (XXX) XXX-XXXX
                return formattedText.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                if (offset <= 1) return 0      // (
                if (offset <= 4) return offset - 1 // (XXX
                if (offset <= 5) return 3      // ) 
                if (offset <= 9) return offset - 3 // (XXX) XXX
                if (offset <= 10) return 6     // -
                if (offset <= 14) return offset - 4 // (XXX) XXX-XXXX
                return digitsOnly.length
            }
        }

        return TransformedText(
            text = AnnotatedString(formattedText),
            offsetMapping = offsetMapping
        )
    }
}

/**
 * Визуальная трансформация для номеров других стран без особого форматирования
 * (Простая группировка цифр)
 */
class DefaultPhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Группировка цифр по 3 или 2
        val digitsOnly = text.text.filter { it.isDigit() }
        
        val formattedText = buildString {
            digitsOnly.forEachIndexed { index, c ->
                append(c)
                if ((index + 1) % 3 == 0 && index < digitsOnly.length - 1) {
                    append(" ")
                }
            }
        }
        
        // Создаем карту соответствия между исходными и отформатированными индексами
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                 val spaces = (offset - 1).coerceAtLeast(0) / 3
                 return offset + spaces
            }

            override fun transformedToOriginal(offset: Int): Int {
                 val spaces = (offset - 1).coerceAtLeast(0) / 4
                 return offset - spaces
            }
        }

        return TransformedText(
            text = AnnotatedString(formattedText),
            offsetMapping = offsetMapping
        )
    }
} 