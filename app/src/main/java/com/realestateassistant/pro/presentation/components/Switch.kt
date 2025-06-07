package com.realestateassistant.pro.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch as MaterialSwitch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Компонент переключателя (Switch)
 * 
 * @param checked состояние переключателя (включен/выключен)
 * @param onCheckedChange обработчик изменения состояния
 * @param modifier модификатор для настройки внешнего вида
 * @param enabled доступность компонента для взаимодействия
 */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    MaterialSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        thumbContent = null
    )
} 