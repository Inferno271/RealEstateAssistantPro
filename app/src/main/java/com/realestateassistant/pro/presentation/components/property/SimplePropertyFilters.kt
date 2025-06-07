package com.realestateassistant.pro.presentation.components.property

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.domain.model.PropertyFilter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.realestateassistant.pro.domain.model.PropertyConstants
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize
import com.realestateassistant.pro.presentation.viewmodel.OptionsViewModel

/**
 * Компонент для отображения активных фильтров
 */
@Composable
fun ActiveFiltersRow(
    propertyFilter: PropertyFilter,
    onFilterChange: (PropertyFilter) -> Unit,
    onClearFilters: () -> Unit
) {
    // Получаем количество активных фильтров из модели
    val activeFiltersCount = propertyFilter.getActiveFiltersCount()
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Активные фильтры: $activeFiltersCount",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        if (activeFiltersCount > 0) {
            TextButton(
                onClick = onClearFilters,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("Сбросить")
            }
        }
    }
}

/**
 * Упрощенная панель фильтров для объектов недвижимости
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PropertyFilterPanel(
    propertyFilter: PropertyFilter,
    onFilterChange: (PropertyFilter) -> Unit,
    onClearFilters: () -> Unit,
    onClose: () -> Unit,
    optionsViewModel: OptionsViewModel
) {
    // Получаем списки типов недвижимости и районов из OptionsViewModel
    val propertyTypes by optionsViewModel.propertyTypes.collectAsState()
    val districts by optionsViewModel.districts.collectAsState()
    
    // Локальные состояния для работы с фильтрами
    var selectedTypes by remember { mutableStateOf(propertyFilter.propertyTypes) }
    var minRoomsText by remember { mutableStateOf(propertyFilter.minRooms?.toString() ?: "") }
    var maxRoomsText by remember { mutableStateOf(propertyFilter.maxRooms?.toString() ?: "") }
    var minPriceText by remember { mutableStateOf(propertyFilter.minPrice?.toString() ?: "") }
    var maxPriceText by remember { mutableStateOf(propertyFilter.maxPrice?.toString() ?: "") }
    var selectedDistricts by remember { mutableStateOf(propertyFilter.districts) }
    
    // Состояния для выпадающих списков
    var expandedMinRooms by remember { mutableStateOf(false) }
    var expandedMaxRooms by remember { mutableStateOf(false) }
    var expandedMinPrice by remember { mutableStateOf(false) }
    var expandedMaxPrice by remember { mutableStateOf(false) }
    
    // Размеры для выпадающих списков
    var minRoomsFieldSize by remember { mutableStateOf(Size.Zero) }
    var maxRoomsFieldSize by remember { mutableStateOf(Size.Zero) }
    var minPriceFieldSize by remember { mutableStateOf(Size.Zero) }
    var maxPriceFieldSize by remember { mutableStateOf(Size.Zero) }
    
    // Локальное получение плотности пикселей
    val density = LocalDensity.current
    
    // Функция применения фильтров
    val applyFilters = {
        // Проверка и преобразование числовых значений
        val minRoomsInt = try {
            minRoomsText.toIntOrNull()
        } catch (e: NumberFormatException) {
            println("DEBUG: Ошибка при преобразовании минимального количества комнат: ${e.message}")
            null
        }
        
        val maxRoomsInt = try {
            maxRoomsText.toIntOrNull()
        } catch (e: NumberFormatException) {
            println("DEBUG: Ошибка при преобразовании максимального количества комнат: ${e.message}")
            null
        }
        
        val minPriceDouble = try {
            minPriceText.toDoubleOrNull()
        } catch (e: NumberFormatException) {
            println("DEBUG: Ошибка при преобразовании минимальной цены: ${e.message}")
            null
        }
        
        val maxPriceDouble = try {
            maxPriceText.toDoubleOrNull()
        } catch (e: NumberFormatException) {
            println("DEBUG: Ошибка при преобразовании максимальной цены: ${e.message}")
            null
        }
        
        // Создаем новый фильтр
        val newFilter = propertyFilter.copy(
            propertyTypes = selectedTypes,
            minRooms = minRoomsInt,
            maxRooms = maxRoomsInt,
            minPrice = minPriceDouble,
            maxPrice = maxPriceDouble,
            districts = selectedDistricts
        )
        
        // Выводим информацию о фильтре для отладки
        println("DEBUG: Применяем новый фильтр: $newFilter")
        println("DEBUG: Типы недвижимости: ${newFilter.propertyTypes}")
        println("DEBUG: Комнаты: от ${newFilter.minRooms} до ${newFilter.maxRooms}")
        println("DEBUG: Цена: от ${newFilter.minPrice} до ${newFilter.maxPrice}")
        println("DEBUG: Районы: ${newFilter.districts}")
        
        // Применяем фильтр и закрываем панель
        onFilterChange(newFilter)
        onClose()
    }

    // Обеспечиваем достаточный отступ снизу для кнопок
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 72.dp) // Добавляем большой отступ снизу для нижней навигации
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // Заголовок
                Text(
                    text = "Фильтры объектов",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // Основное содержимое с возможностью скроллинга
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Тип недвижимости - выбор с чипсами
                    Text(
                        text = "Тип недвижимости",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        maxItemsInEachRow = 3
                    ) {
                        propertyTypes.forEach { type ->
                            val isSelected = selectedTypes.contains(type)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedTypes = if (isSelected) {
                                        selectedTypes - type
                                    } else {
                                        selectedTypes + type
                                    }
                                },
                                label = { Text(type) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Комнаты - выпадающие списки
                    Text(
                        text = "Количество комнат",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Минимальное количество комнат
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = minRoomsText,
                                onValueChange = { minRoomsText = it },
                                label = { Text("От") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onGloballyPositioned { coordinates ->
                                        minRoomsFieldSize = coordinates.size.toSize()
                                    },
                                trailingIcon = {
                                    IconButton(onClick = { expandedMinRooms = !expandedMinRooms }) {
                                        Icon(
                                            imageVector = if (expandedMinRooms) 
                                                Icons.Filled.KeyboardArrowUp 
                                            else 
                                                Icons.Filled.KeyboardArrowDown,
                                            contentDescription = "Выбрать"
                                        )
                                    }
                                }
                            )
                            
                            DropdownMenu(
                                expanded = expandedMinRooms,
                                onDismissRequest = { expandedMinRooms = false },
                                modifier = Modifier.width(with(density) { minRoomsFieldSize.width.toDp() })
                            ) {
                                PropertyConstants.MIN_ROOMS_OPTIONS.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text("$option") },
                                        onClick = {
                                            minRoomsText = option.toString()
                                            expandedMinRooms = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        // Максимальное количество комнат
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = maxRoomsText,
                                onValueChange = { maxRoomsText = it },
                                label = { Text("До") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onGloballyPositioned { coordinates ->
                                        maxRoomsFieldSize = coordinates.size.toSize()
                                    },
                                trailingIcon = {
                                    IconButton(onClick = { expandedMaxRooms = !expandedMaxRooms }) {
                                        Icon(
                                            imageVector = if (expandedMaxRooms) 
                                                Icons.Filled.KeyboardArrowUp 
                                            else 
                                                Icons.Filled.KeyboardArrowDown,
                                            contentDescription = "Выбрать"
                                        )
                                    }
                                }
                            )
                            
                            DropdownMenu(
                                expanded = expandedMaxRooms,
                                onDismissRequest = { expandedMaxRooms = false },
                                modifier = Modifier.width(with(density) { maxRoomsFieldSize.width.toDp() })
                            ) {
                                PropertyConstants.MAX_ROOMS_OPTIONS.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text("$option") },
                                        onClick = {
                                            maxRoomsText = option.toString()
                                            expandedMaxRooms = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Цена - выпадающие списки
                    Text(
                        text = "Цена",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Минимальная цена
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = minPriceText,
                                onValueChange = { minPriceText = it },
                                label = { Text("От") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onGloballyPositioned { coordinates ->
                                        minPriceFieldSize = coordinates.size.toSize()
                                    },
                                trailingIcon = {
                                    IconButton(onClick = { expandedMinPrice = !expandedMinPrice }) {
                                        Icon(
                                            imageVector = if (expandedMinPrice) 
                                                Icons.Filled.KeyboardArrowUp 
                                            else 
                                                Icons.Filled.KeyboardArrowDown,
                                            contentDescription = "Выбрать"
                                        )
                                    }
                                }
                            )
                            
                            DropdownMenu(
                                expanded = expandedMinPrice,
                                onDismissRequest = { expandedMinPrice = false },
                                modifier = Modifier.width(with(density) { minPriceFieldSize.width.toDp() })
                            ) {
                                PropertyConstants.PRICE_OPTIONS.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text("${option.toInt()} ₽") },
                                        onClick = {
                                            minPriceText = option.toString()
                                            expandedMinPrice = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        // Максимальная цена
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = maxPriceText,
                                onValueChange = { maxPriceText = it },
                                label = { Text("До") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onGloballyPositioned { coordinates ->
                                        maxPriceFieldSize = coordinates.size.toSize()
                                    },
                                trailingIcon = {
                                    IconButton(onClick = { expandedMaxPrice = !expandedMaxPrice }) {
                                        Icon(
                                            imageVector = if (expandedMaxPrice) 
                                                Icons.Filled.KeyboardArrowUp 
                                            else 
                                                Icons.Filled.KeyboardArrowDown,
                                            contentDescription = "Выбрать"
                                        )
                                    }
                                }
                            )
                            
                            DropdownMenu(
                                expanded = expandedMaxPrice,
                                onDismissRequest = { expandedMaxPrice = false },
                                modifier = Modifier.width(with(density) { maxPriceFieldSize.width.toDp() })
                            ) {
                                PropertyConstants.PRICE_OPTIONS.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text("${option.toInt()} ₽") },
                                        onClick = {
                                            maxPriceText = option.toString()
                                            expandedMaxPrice = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Район - выбор с чипсами
                    Text(
                        text = "Район",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        maxItemsInEachRow = 3
                    ) {
                        districts.forEach { district ->
                            val isSelected = selectedDistricts.contains(district)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedDistricts = if (isSelected) {
                                        selectedDistricts - district
                                    } else {
                                        selectedDistricts + district
                                    }
                                },
                                label = { Text(district) }
                            )
                        }
                    }
                    
                    // Добавляем дополнительный отступ внизу скроллируемой области
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Разделитель перед кнопками
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // Кнопки действий вне скроллируемой области с минимальной высотой
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Более компактные кнопки
                    TextButton(
                        onClick = {
                            // Сброс всех локальных состояний
                            selectedTypes = emptySet()
                            minRoomsText = ""
                            maxRoomsText = ""
                            minPriceText = ""
                            maxPriceText = ""
                            selectedDistricts = emptySet()
                            // Вызов функции сброса
                            onClearFilters()
                        },
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Сбросить")
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Button(
                        onClick = { applyFilters() },
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Применить")
                    }
                }
            }
        }
    }
} 