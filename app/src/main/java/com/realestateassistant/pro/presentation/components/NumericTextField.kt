package com.realestateassistant.pro.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun NumericTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    allowDecimal: Boolean = true,
    maxValue: Double? = null,
    suffix: String = "",
    isRequired: Boolean = false
) {
    val pattern = if (allowDecimal) {
        Regex("^\\d*\\.?\\d*$") // Разрешает десятичные числа
    } else {
        Regex("^\\d*$") // Только целые числа
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
            Text(
                if (isRequired) "$label *" else label
            ) 
        },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (allowDecimal) KeyboardType.Decimal else KeyboardType.Number
        ),
        suffix = if (suffix.isNotEmpty()) { { Text(suffix) } } else null
    )
} 