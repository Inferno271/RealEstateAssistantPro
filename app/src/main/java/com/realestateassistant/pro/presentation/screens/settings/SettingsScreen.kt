package com.realestateassistant.pro.presentation.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Экран настроек приложения
 */
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    var enableNotifications by remember { mutableStateOf(true) }
    var enableDarkTheme by remember { mutableStateOf(false) }
    var syncInBackground by remember { mutableStateOf(true) }
    var showPrices by remember { mutableStateOf(true) }
    var useLocationService by remember { mutableStateOf(true) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Настройки",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        SettingsCategory(title = "Основные")
        
        SettingsSwitch(
            title = "Уведомления",
            description = "Получать уведомления о новых событиях",
            checked = enableNotifications,
            onCheckedChange = { enableNotifications = it }
        )
        
        SettingsSwitch(
            title = "Темная тема",
            description = "Использовать темную тему оформления",
            checked = enableDarkTheme,
            onCheckedChange = { enableDarkTheme = it }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        SettingsCategory(title = "Синхронизация")
        
        SettingsSwitch(
            title = "Фоновая синхронизация",
            description = "Синхронизировать данные в фоновом режиме",
            checked = syncInBackground,
            onCheckedChange = { syncInBackground = it }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        SettingsCategory(title = "Отображение")
        
        SettingsSwitch(
            title = "Отображать цены",
            description = "Показывать цены объектов в списке",
            checked = showPrices,
            onCheckedChange = { showPrices = it }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        SettingsCategory(title = "Службы геолокации")
        
        SettingsSwitch(
            title = "Использовать геолокацию",
            description = "Определять местоположение для поиска объектов поблизости",
            checked = useLocationService,
            onCheckedChange = { useLocationService = it }
        )
    }
}

/**
 * Категория настроек
 */
@Composable
private fun SettingsCategory(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.height(16.dp))
}

/**
 * Настройка с переключателем
 */
@Composable
private fun SettingsSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
    
    Spacer(modifier = Modifier.height(8.dp))
} 