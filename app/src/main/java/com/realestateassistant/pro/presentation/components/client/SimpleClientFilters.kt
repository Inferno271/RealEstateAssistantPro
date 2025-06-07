package com.realestateassistant.pro.presentation.components.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.domain.model.ClientFilter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.realestateassistant.pro.domain.model.ClientConstants
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
fun ActiveClientFiltersRow(
    clientFilter: ClientFilter,
    onFilterChange: (ClientFilter) -> Unit,
    onClearFilters: () -> Unit
) {
    // Получаем количество активных фильтров из модели
    val activeFiltersCount = clientFilter.getActiveFiltersCount()
    
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
 * Упрощенная панель фильтров для клиентов
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ClientFilterPanel(
    clientFilter: ClientFilter,
    onFilterChange: (ClientFilter) -> Unit,
    onClearFilters: () -> Unit,
    onClose: () -> Unit,
    optionsViewModel: OptionsViewModel
) {
    // Получаем данные из OptionsViewModel
    val propertyTypes by optionsViewModel.propertyTypes.collectAsState()
    val rentalTypes by optionsViewModel.rentalTypes.collectAsState()
    val familyCompositions by optionsViewModel.familyCompositions.collectAsState()
    val urgencyLevels by optionsViewModel.urgencyLevels.collectAsState()
    val peopleCountRanges by optionsViewModel.peopleCountRanges.collectAsState()
    val longTermBudgetOptions by optionsViewModel.longTermBudgetOptions.collectAsState()
    val shortTermBudgetOptions by optionsViewModel.shortTermBudgetOptions.collectAsState()
    val districts by optionsViewModel.districts.collectAsState()
    
    // Локальные состояния для работы с фильтрами
    var selectedRentalTypes by remember { mutableStateOf(clientFilter.rentalTypes) }
    var selectedFamilyCompositions by remember { mutableStateOf(clientFilter.familyCompositions) }
    var selectedUrgencyLevels by remember { mutableStateOf(clientFilter.urgencyLevels) }
    var selectedPropertyTypes by remember { mutableStateOf(clientFilter.propertyTypes) }
    var selectedPeopleCountRanges by remember { mutableStateOf(clientFilter.peopleCountRanges) }
    var selectedDistricts by remember { mutableStateOf(clientFilter.districts) }
    
    // Состояния для бюджета длительной аренды
    var minLongTermBudgetText by remember { mutableStateOf("") }
    var maxLongTermBudgetText by remember { mutableStateOf("") }
    
    // Состояния для бюджета посуточной аренды
    var minShortTermBudgetText by remember { mutableStateOf("") }
    var maxShortTermBudgetText by remember { mutableStateOf("") }
    
    // Состояния для выпадающих списков
    var expandedMinLongTermBudget by remember { mutableStateOf(false) }
    var expandedMaxLongTermBudget by remember { mutableStateOf(false) }
    var expandedMinShortTermBudget by remember { mutableStateOf(false) }
    var expandedMaxShortTermBudget by remember { mutableStateOf(false) }
    
    // Размеры для выпадающих списков
    var minLongTermBudgetFieldSize by remember { mutableStateOf(Size.Zero) }
    var maxLongTermBudgetFieldSize by remember { mutableStateOf(Size.Zero) }
    var minShortTermBudgetFieldSize by remember { mutableStateOf(Size.Zero) }
    var maxShortTermBudgetFieldSize by remember { mutableStateOf(Size.Zero) }
    
    // Локальное получение плотности пикселей
    val density = LocalDensity.current
    
    // Получаем реальные списки данных или используем константы как запасной вариант
    val effectiveRentalTypes = if (rentalTypes.isNotEmpty()) rentalTypes else ClientConstants.RENTAL_TYPES
    val effectiveFamilyCompositions = if (familyCompositions.isNotEmpty()) familyCompositions else ClientConstants.FAMILY_COMPOSITIONS
    val effectiveUrgencyLevels = if (urgencyLevels.isNotEmpty()) urgencyLevels else ClientConstants.URGENCY_LEVELS
    val effectivePropertyTypes = if (propertyTypes.isNotEmpty()) propertyTypes else ClientConstants.PROPERTY_TYPES
    val effectivePeopleCountRanges = if (peopleCountRanges.isNotEmpty()) peopleCountRanges else ClientConstants.PEOPLE_COUNT_RANGES
    val effectiveLongTermBudgetOptions = if (longTermBudgetOptions.isNotEmpty()) longTermBudgetOptions.map { it.toDouble() } else ClientConstants.LONG_TERM_BUDGET_OPTIONS
    val effectiveShortTermBudgetOptions = if (shortTermBudgetOptions.isNotEmpty()) shortTermBudgetOptions.map { it.toDouble() } else ClientConstants.SHORT_TERM_BUDGET_OPTIONS
    
    // Функция применения фильтров
    val applyFilters = {
        // Проверка и преобразование значений бюджета
        val minLongTermBudgetValue = try {
            minLongTermBudgetText.toDoubleOrNull()
        } catch (e: NumberFormatException) {
            println("DEBUG: Ошибка при преобразовании минимального бюджета для длительной аренды: ${e.message}")
            null
        }
        
        val maxLongTermBudgetValue = try {
            maxLongTermBudgetText.toDoubleOrNull()
        } catch (e: NumberFormatException) {
            println("DEBUG: Ошибка при преобразовании максимального бюджета для длительной аренды: ${e.message}")
            null
        }
        
        val minShortTermBudgetValue = try {
            minShortTermBudgetText.toDoubleOrNull()
        } catch (e: NumberFormatException) {
            println("DEBUG: Ошибка при преобразовании минимального бюджета для посуточной аренды: ${e.message}")
            null
        }
        
        val maxShortTermBudgetValue = try {
            maxShortTermBudgetText.toDoubleOrNull()
        } catch (e: NumberFormatException) {
            println("DEBUG: Ошибка при преобразовании максимального бюджета для посуточной аренды: ${e.message}")
            null
        }
        
        // Создаем новый фильтр с учетом всех параметров
        val newFilter = clientFilter.copy(
            rentalTypes = selectedRentalTypes,
            familyCompositions = selectedFamilyCompositions,
            urgencyLevels = selectedUrgencyLevels,
            propertyTypes = selectedPropertyTypes,
            peopleCountRanges = selectedPeopleCountRanges,
            districts = selectedDistricts,
            minLongTermBudget = minLongTermBudgetValue,
            maxLongTermBudget = maxLongTermBudgetValue,
            minShortTermBudget = minShortTermBudgetValue,
            maxShortTermBudget = maxShortTermBudgetValue
        )
        
        // Выводим информацию о фильтре для отладки
        println("DEBUG: Применяем новый фильтр: $newFilter")
        println("DEBUG: Типы аренды: ${newFilter.rentalTypes}")
        println("DEBUG: Составы семьи: ${newFilter.familyCompositions}")
        println("DEBUG: Уровни срочности: ${newFilter.urgencyLevels}")
        println("DEBUG: Типы недвижимости: ${newFilter.propertyTypes}")
        println("DEBUG: Количество проживающих: ${newFilter.peopleCountRanges}")
        println("DEBUG: Районы: ${newFilter.districts}")
        println("DEBUG: Бюджет для длительной аренды: от ${newFilter.minLongTermBudget} до ${newFilter.maxLongTermBudget}")
        println("DEBUG: Бюджет для посуточной аренды: от ${newFilter.minShortTermBudget} до ${newFilter.maxShortTermBudget}")
        
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
                    text = "Фильтры клиентов",
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
                    // Тип аренды - выбор с чипсами
                    Text(
                        text = "Тип аренды",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        maxItemsInEachRow = 3
                    ) {
                        effectiveRentalTypes.forEach { type ->
                            val isSelected = selectedRentalTypes.contains(type)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedRentalTypes = if (isSelected) {
                                        selectedRentalTypes - type
                                    } else {
                                        selectedRentalTypes + type
                                    }
                                },
                                label = { 
                                    Text(
                                        when (type) {
                                            "длительная" -> "Длительная"
                                            "посуточная" -> "Посуточная"
                                            else -> type
                                        }
                                    ) 
                                }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Тип недвижимости - выбор с чипсами
                    Text(
                        text = "Тип недвижимости",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        maxItemsInEachRow = 3
                    ) {
                        effectivePropertyTypes.forEach { type ->
                            val isSelected = selectedPropertyTypes.contains(type)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedPropertyTypes = if (isSelected) {
                                        selectedPropertyTypes - type
                                    } else {
                                        selectedPropertyTypes + type
                                    }
                                },
                                label = { Text(type) }
                            )
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
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Количество проживающих - выбор с чипсами
                    Text(
                        text = "Количество проживающих",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        maxItemsInEachRow = 3
                    ) {
                        effectivePeopleCountRanges.forEach { range ->
                            val isSelected = selectedPeopleCountRanges.contains(range)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedPeopleCountRanges = if (isSelected) {
                                        selectedPeopleCountRanges - range
                                    } else {
                                        selectedPeopleCountRanges + range
                                    }
                                },
                                label = { Text(range) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Состав семьи - выбор с чипсами
                    Text(
                        text = "Состав семьи",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        maxItemsInEachRow = 3
                    ) {
                        effectiveFamilyCompositions.forEach { composition ->
                            val isSelected = selectedFamilyCompositions.contains(composition)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedFamilyCompositions = if (isSelected) {
                                        selectedFamilyCompositions - composition
                                    } else {
                                        selectedFamilyCompositions + composition
                                    }
                                },
                                label = { Text(composition) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Уровень срочности - выбор с чипсами
                    Text(
                        text = "Уровень срочности",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        maxItemsInEachRow = 3
                    ) {
                        effectiveUrgencyLevels.forEach { level ->
                            val isSelected = selectedUrgencyLevels.contains(level)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedUrgencyLevels = if (isSelected) {
                                        selectedUrgencyLevels - level
                                    } else {
                                        selectedUrgencyLevels + level
                                    }
                                },
                                label = { Text(level) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Бюджет для длительной аренды - выпадающие списки
                    if (selectedRentalTypes.contains("длительная") || selectedRentalTypes.isEmpty()) {
                        Text(
                            text = "Бюджет (длительная аренда)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Минимальный бюджет
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = minLongTermBudgetText,
                                    onValueChange = { minLongTermBudgetText = it },
                                    label = { Text("От") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onGloballyPositioned { coordinates ->
                                            minLongTermBudgetFieldSize = coordinates.size.toSize()
                                        },
                                    trailingIcon = {
                                        IconButton(onClick = { expandedMinLongTermBudget = !expandedMinLongTermBudget }) {
                                            Icon(
                                                imageVector = if (expandedMinLongTermBudget) 
                                                    Icons.Filled.KeyboardArrowUp 
                                                else 
                                                    Icons.Filled.KeyboardArrowDown,
                                                contentDescription = "Выбрать"
                                            )
                                        }
                                    }
                                )
                                
                                DropdownMenu(
                                    expanded = expandedMinLongTermBudget,
                                    onDismissRequest = { expandedMinLongTermBudget = false },
                                    modifier = Modifier.width(with(density) { minLongTermBudgetFieldSize.width.toDp() })
                                ) {
                                    effectiveLongTermBudgetOptions.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text("${option.toInt()} ₽/мес") },
                                            onClick = {
                                                minLongTermBudgetText = option.toString()
                                                expandedMinLongTermBudget = false
                                            }
                                        )
                                    }
                                }
                            }
                            
                            // Максимальный бюджет
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = maxLongTermBudgetText,
                                    onValueChange = { maxLongTermBudgetText = it },
                                    label = { Text("До") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onGloballyPositioned { coordinates ->
                                            maxLongTermBudgetFieldSize = coordinates.size.toSize()
                                        },
                                    trailingIcon = {
                                        IconButton(onClick = { expandedMaxLongTermBudget = !expandedMaxLongTermBudget }) {
                                            Icon(
                                                imageVector = if (expandedMaxLongTermBudget) 
                                                    Icons.Filled.KeyboardArrowUp 
                                                else 
                                                    Icons.Filled.KeyboardArrowDown,
                                                contentDescription = "Выбрать"
                                            )
                                        }
                                    }
                                )
                                
                                DropdownMenu(
                                    expanded = expandedMaxLongTermBudget,
                                    onDismissRequest = { expandedMaxLongTermBudget = false },
                                    modifier = Modifier.width(with(density) { maxLongTermBudgetFieldSize.width.toDp() })
                                ) {
                                    effectiveLongTermBudgetOptions.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text("${option.toInt()} ₽/мес") },
                                            onClick = {
                                                maxLongTermBudgetText = option.toString()
                                                expandedMaxLongTermBudget = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    // Бюджет для посуточной аренды - выпадающие списки
                    if (selectedRentalTypes.contains("посуточная") || selectedRentalTypes.isEmpty()) {
                        Text(
                            text = "Бюджет (посуточная аренда)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Минимальный бюджет
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = minShortTermBudgetText,
                                    onValueChange = { minShortTermBudgetText = it },
                                    label = { Text("От") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onGloballyPositioned { coordinates ->
                                            minShortTermBudgetFieldSize = coordinates.size.toSize()
                                        },
                                    trailingIcon = {
                                        IconButton(onClick = { expandedMinShortTermBudget = !expandedMinShortTermBudget }) {
                                            Icon(
                                                imageVector = if (expandedMinShortTermBudget) 
                                                    Icons.Filled.KeyboardArrowUp 
                                                else 
                                                    Icons.Filled.KeyboardArrowDown,
                                                contentDescription = "Выбрать"
                                            )
                                        }
                                    }
                                )
                                
                                DropdownMenu(
                                    expanded = expandedMinShortTermBudget,
                                    onDismissRequest = { expandedMinShortTermBudget = false },
                                    modifier = Modifier.width(with(density) { minShortTermBudgetFieldSize.width.toDp() })
                                ) {
                                    effectiveShortTermBudgetOptions.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text("${option.toInt()} ₽/сутки") },
                                            onClick = {
                                                minShortTermBudgetText = option.toString()
                                                expandedMinShortTermBudget = false
                                            }
                                        )
                                    }
                                }
                            }
                            
                            // Максимальный бюджет
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = maxShortTermBudgetText,
                                    onValueChange = { maxShortTermBudgetText = it },
                                    label = { Text("До") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onGloballyPositioned { coordinates ->
                                            maxShortTermBudgetFieldSize = coordinates.size.toSize()
                                        },
                                    trailingIcon = {
                                        IconButton(onClick = { expandedMaxShortTermBudget = !expandedMaxShortTermBudget }) {
                                            Icon(
                                                imageVector = if (expandedMaxShortTermBudget) 
                                                    Icons.Filled.KeyboardArrowUp 
                                                else 
                                                    Icons.Filled.KeyboardArrowDown,
                                                contentDescription = "Выбрать"
                                            )
                                        }
                                    }
                                )
                                
                                DropdownMenu(
                                    expanded = expandedMaxShortTermBudget,
                                    onDismissRequest = { expandedMaxShortTermBudget = false },
                                    modifier = Modifier.width(with(density) { maxShortTermBudgetFieldSize.width.toDp() })
                                ) {
                                    effectiveShortTermBudgetOptions.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text("${option.toInt()} ₽/сутки") },
                                            onClick = {
                                                maxShortTermBudgetText = option.toString()
                                                expandedMaxShortTermBudget = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
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
                            selectedRentalTypes = emptySet()
                            selectedFamilyCompositions = emptySet()
                            selectedUrgencyLevels = emptySet()
                            selectedPropertyTypes = emptySet()
                            selectedPeopleCountRanges = emptySet()
                            selectedDistricts = emptySet()
                            minLongTermBudgetText = ""
                            maxLongTermBudgetText = ""
                            minShortTermBudgetText = ""
                            maxShortTermBudgetText = ""
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