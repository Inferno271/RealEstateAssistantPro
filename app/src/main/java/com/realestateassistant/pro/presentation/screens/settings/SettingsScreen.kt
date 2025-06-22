package com.realestateassistant.pro.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Экран настроек приложения
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showClearDatabaseDialog by remember { mutableStateOf(false) }
    var propertiesCount by remember { mutableStateOf(20) }
    var clientsCount by remember { mutableStateOf(15) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Общие настройки",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Основные настройки приложения
            SettingsSection(
                title = "Тема приложения",
                icon = Icons.Default.DarkMode
            ) {
                // Здесь можно добавить переключение темы
                Text("Системная тема", style = MaterialTheme.typography.bodyMedium)
            }
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            Text(
                text = "Разработка и тестирование",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Секция для заполнения тестовыми данными
            SettingsSection(
                title = "Заполнение тестовыми данными",
                icon = Icons.Default.DataObject
            ) {
                Column {
                    Text(
                        "Создание тестовых объектов и клиентов для демонстрации возможностей приложения",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { showConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Заполнить тестовыми данными")
                    }
                    
                    if (state.testDataResult != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                val result = state.testDataResult
                                Text(
                                    "Создано ${result?.propertiesAdded ?: 0} объектов недвижимости",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Создано ${result?.clientsAdded ?: 0} клиентов",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Всего добавлено: ${result?.totalDataItems ?: 0} записей",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    
                    if (state.isLoading) {
                        Spacer(modifier = Modifier.height(8.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    
                    if (state.error != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ошибка: ${state.error}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Секция для очистки базы данных
            SettingsSection(
                title = "Очистка базы данных",
                icon = Icons.Default.Delete
            ) {
                Column {
                    Text(
                        "Удаление всех данных из базы данных приложения",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { showClearDatabaseDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Очистить базу данных")
                    }
                    
                    if (state.clearDatabaseResult != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                val result = state.clearDatabaseResult
                                Text(
                                    "Удалено ${result?.propertiesDeleted ?: 0} объектов недвижимости",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Удалено ${result?.clientsDeleted ?: 0} клиентов",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Всего удалено: ${result?.totalItemsDeleted ?: 0} записей",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            Text(
                text = "О приложении",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            SettingsSection(
                title = "Версия приложения",
                icon = Icons.Default.Info
            ) {
                Text("1.0.0", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
    
    // Диалог подтверждения заполнения тестовыми данными
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Заполнение тестовыми данными") },
            text = {
                Column {
                    Text("Это действие создаст тестовые объекты недвижимости и клиентов в базе данных. Продолжить?")
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Количество объектов недвижимости:")
                    Slider(
                        value = propertiesCount.toFloat(),
                        onValueChange = { propertiesCount = it.toInt() },
                        valueRange = 5f..50f,
                        steps = 45,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text("$propertiesCount объектов")
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("Количество клиентов:")
                    Slider(
                        value = clientsCount.toFloat(),
                        onValueChange = { clientsCount = it.toInt() },
                        valueRange = 5f..30f,
                        steps = 25,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text("$clientsCount клиентов")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.populateTestData(propertiesCount, clientsCount)
                        showConfirmDialog = false
                    }
                ) {
                    Text("Создать данные")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
    
    // Диалог подтверждения очистки базы данных
    if (showClearDatabaseDialog) {
        AlertDialog(
            onDismissRequest = { showClearDatabaseDialog = false },
            title = { Text("Очистка базы данных") },
            text = {
                Text("Вы уверены, что хотите удалить все данные из базы данных? Это действие нельзя отменить.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearDatabase()
                        showClearDatabaseDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Удалить все данные")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showClearDatabaseDialog = false }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
    
    // Отображение ошибки, если она есть
    if (state.error != null) {
        AlertDialog(
            onDismissRequest = {
                viewModel.resetResult()
            },
            title = { Text("Ошибка") },
            text = { Text(state.error!!) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetResult()
                    }
                ) {
                    Text("ОК")
                }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            
            Box(modifier = Modifier.padding(start = 40.dp)) {
                content()
            }
        }
    }
} 