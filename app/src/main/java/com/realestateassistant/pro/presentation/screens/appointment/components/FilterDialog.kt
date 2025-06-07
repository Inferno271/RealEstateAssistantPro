package com.realestateassistant.pro.presentation.screens.appointment.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.realestateassistant.pro.domain.model.AppointmentStatus
import com.realestateassistant.pro.domain.model.AppointmentType
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.presentation.screens.appointment.models.AppointmentEvent

/**
 * Диалог фильтрации встреч
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    onEvent: (AppointmentEvent) -> Unit,
    clients: List<Client>,
    properties: List<Property>,
    onDismiss: () -> Unit,
    currentClientId: String? = null,
    currentPropertyId: String? = null,
    currentStatus: String? = null,
    currentType: String? = null,
    currentSearchQuery: String = ""
) {
    var selectedClientId by remember { mutableStateOf(currentClientId) }
    var selectedPropertyId by remember { mutableStateOf(currentPropertyId) }
    var selectedStatus by remember { mutableStateOf(currentStatus) }
    var selectedType by remember { mutableStateOf(currentType) }
    var searchQuery by remember { mutableStateOf(currentSearchQuery) }
    
    // Меню для выбора клиента
    var showClientMenu by remember { mutableStateOf(false) }
    // Меню для выбора объекта
    var showPropertyMenu by remember { mutableStateOf(false) }
    // Меню для выбора статуса
    var showStatusMenu by remember { mutableStateOf(false) }
    // Меню для выбора типа
    var showTypeMenu by remember { mutableStateOf(false) }
    
    // Получаем имя клиента по ID
    val selectedClientName = remember(selectedClientId, clients) {
        clients.find { it.id == selectedClientId }?.fullName ?: ""
    }
    
    // Получаем адрес объекта по ID
    val selectedPropertyAddress = remember(selectedPropertyId, properties) {
        properties.find { it.id == selectedPropertyId }?.address ?: ""
    }
    
    // Получаем текст статуса
    val selectedStatusText = remember(selectedStatus) {
        when (selectedStatus) {
            "SCHEDULED" -> "Запланирована"
            "CONFIRMED" -> "Подтверждена"
            "IN_PROGRESS" -> "В процессе"
            "COMPLETED" -> "Завершена"
            "CANCELLED" -> "Отменена"
            "RESCHEDULED" -> "Перенесена"
            "NO_SHOW" -> "Не явились"
            else -> "Все статусы"
        }
    }
    
    // Получаем текст типа
    val selectedTypeText = remember(selectedType) {
        when (selectedType) {
            "VIEWING" -> "Просмотр"
            "MEETING" -> "Встреча"
            "CALL" -> "Звонок"
            "NEGOTIATION" -> "Переговоры"
            "CONTRACT_SIGNING" -> "Подписание договора"
            "OTHER" -> "Другое"
            else -> "Все типы"
        }
    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Заголовок
                Text(
                    text = "Фильтр встреч",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Поле поиска
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Поиск по названию, описанию, дате, участникам...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Очистить")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Фильтр по клиенту
                Column {
                    Text(
                        text = "Клиент",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Box {
                        OutlinedTextField(
                            value = selectedClientName,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Выбрать клиента"
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showClientMenu = true }
                        )
                        
                        // Невидимый блок для обработки клика по всему полю
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showClientMenu = true }
                        )
                        
                        DropdownMenu(
                            expanded = showClientMenu,
                            onDismissRequest = { showClientMenu = false },
                            modifier = Modifier.heightIn(max = 300.dp)
                        ) {
                            // Опция "Все клиенты"
                            DropdownMenuItem(
                                text = { Text("Все клиенты") },
                                onClick = {
                                    selectedClientId = null
                                    showClientMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.People,
                                        contentDescription = null
                                    )
                                }
                            )
                            
                            // Список клиентов
                            clients.forEach { client ->
                                DropdownMenuItem(
                                    text = { Text(client.fullName) },
                                    onClick = {
                                        selectedClientId = client.id
                                        showClientMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null
                                        )
                                    },
                                    trailingIcon = {
                                        if (client.id == selectedClientId) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Выбрано"
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Фильтр по объекту
                Column {
                    Text(
                        text = "Объект недвижимости",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Box {
                        OutlinedTextField(
                            value = selectedPropertyAddress,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Выбрать объект"
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showPropertyMenu = true }
                        )
                        
                        // Невидимый блок для обработки клика по всему полю
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showPropertyMenu = true }
                        )
                        
                        DropdownMenu(
                            expanded = showPropertyMenu,
                            onDismissRequest = { showPropertyMenu = false },
                            modifier = Modifier.heightIn(max = 300.dp)
                        ) {
                            // Опция "Все объекты"
                            DropdownMenuItem(
                                text = { Text("Все объекты") },
                                onClick = {
                                    selectedPropertyId = null
                                    showPropertyMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.LocationCity,
                                        contentDescription = null
                                    )
                                }
                            )
                            
                            // Список объектов
                            properties.forEach { property ->
                                DropdownMenuItem(
                                    text = { Text(property.address) },
                                    onClick = {
                                        selectedPropertyId = property.id
                                        showPropertyMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Home,
                                            contentDescription = null
                                        )
                                    },
                                    trailingIcon = {
                                        if (property.id == selectedPropertyId) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Выбрано"
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Фильтр по статусу
                Column {
                    Text(
                        text = "Статус",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Box {
                        OutlinedTextField(
                            value = selectedStatusText,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Выбрать статус"
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showStatusMenu = true }
                        )
                        
                        // Невидимый блок для обработки клика по всему полю
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showStatusMenu = true }
                        )
                        
                        DropdownMenu(
                            expanded = showStatusMenu,
                            onDismissRequest = { showStatusMenu = false },
                            modifier = Modifier.heightIn(max = 300.dp)
                        ) {
                            // Опция "Все статусы"
                            DropdownMenuItem(
                                text = { Text("Все статусы") },
                                onClick = {
                                    selectedStatus = null
                                    showStatusMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Flag,
                                        contentDescription = null
                                    )
                                }
                            )
                            
                            // Список статусов
                            listOf(
                                "SCHEDULED" to "Запланирована",
                                "CONFIRMED" to "Подтверждена",
                                "IN_PROGRESS" to "В процессе",
                                "COMPLETED" to "Завершена",
                                "CANCELLED" to "Отменена",
                                "RESCHEDULED" to "Перенесена",
                                "NO_SHOW" to "Не явились"
                            ).forEach { (statusValue, statusText) ->
                                DropdownMenuItem(
                                    text = { Text(statusText) },
                                    onClick = {
                                        selectedStatus = statusValue
                                        showStatusMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Flag,
                                            contentDescription = null
                                        )
                                    },
                                    trailingIcon = {
                                        if (statusValue == selectedStatus) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Выбрано"
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Фильтр по типу
                Column {
                    Text(
                        text = "Тип",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Box {
                        OutlinedTextField(
                            value = selectedTypeText,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Выбрать тип"
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showTypeMenu = true }
                        )
                        
                        // Невидимый блок для обработки клика по всему полю
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showTypeMenu = true }
                        )
                        
                        DropdownMenu(
                            expanded = showTypeMenu,
                            onDismissRequest = { showTypeMenu = false },
                            modifier = Modifier.heightIn(max = 300.dp)
                        ) {
                            // Опция "Все типы"
                            DropdownMenuItem(
                                text = { Text("Все типы") },
                                onClick = {
                                    selectedType = null
                                    showTypeMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Category,
                                        contentDescription = null
                                    )
                                }
                            )
                            
                            // Список типов
                            listOf(
                                "VIEWING" to "Просмотр",
                                "MEETING" to "Встреча",
                                "CALL" to "Звонок",
                                "NEGOTIATION" to "Переговоры",
                                "CONTRACT_SIGNING" to "Подписание договора",
                                "OTHER" to "Другое"
                            ).forEach { (typeValue, typeText) ->
                                DropdownMenuItem(
                                    text = { Text(typeText) },
                                    onClick = {
                                        selectedType = typeValue
                                        showTypeMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Category,
                                            contentDescription = null
                                        )
                                    },
                                    trailingIcon = {
                                        if (typeValue == selectedType) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Выбрано"
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Кнопки
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Кнопка сброса
                    TextButton(
                        onClick = {
                            onEvent(AppointmentEvent.ClearFilters)
                            onDismiss()
                        }
                    ) {
                        Text("Сбросить")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Кнопка отмены
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Отмена")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Кнопка применения
                    Button(
                        onClick = {
                            onEvent(
                                AppointmentEvent.ApplyFilters(
                                    clientId = selectedClientId,
                                    propertyId = selectedPropertyId,
                                    status = selectedStatus,
                                    type = selectedType,
                                    searchQuery = searchQuery
                                )
                            )
                            onDismiss()
                        }
                    ) {
                        Text("Применить")
                    }
                }
            }
        }
    }
} 