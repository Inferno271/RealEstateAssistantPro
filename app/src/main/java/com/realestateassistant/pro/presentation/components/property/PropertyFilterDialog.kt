package com.realestateassistant.pro.presentation.components.property

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.domain.model.PropertyFilter
import com.realestateassistant.pro.presentation.viewmodel.OptionsViewModel

/**
 * Диалог для фильтрации объектов недвижимости.
 *
 * @param currentFilter Текущий фильтр
 * @param onFilterApplied Функция обратного вызова при применении фильтра
 * @param onDismiss Функция обратного вызова при закрытии диалога
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PropertyFilterDialog(
    currentFilter: PropertyFilter,
    onFilterApplied: (PropertyFilter) -> Unit,
    onDismiss: () -> Unit,
    optionsViewModel: OptionsViewModel = hiltViewModel()
) {
    var propertyTypes by remember { mutableStateOf(currentFilter.propertyTypes) }
    var minRooms by remember { mutableStateOf(currentFilter.minRooms?.toString() ?: "") }
    var maxRooms by remember { mutableStateOf(currentFilter.maxRooms?.toString() ?: "") }
    var minPrice by remember { mutableStateOf(currentFilter.minPrice?.toString() ?: "") }
    var maxPrice by remember { mutableStateOf(currentFilter.maxPrice?.toString() ?: "") }
    var districts by remember { mutableStateOf(currentFilter.districts) }
    
    val availablePropertyTypes by optionsViewModel.propertyTypes.collectAsState()
    val availableDistricts by optionsViewModel.districts.collectAsState()
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Заголовок
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Фильтры",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Закрыть"
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Тип недвижимости
                Text(
                    text = "Тип недвижимости",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availablePropertyTypes.forEach { propertyType ->
                        val isSelected = propertyType in propertyTypes
                        
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                propertyTypes = if (isSelected) {
                                    propertyTypes - propertyType
                                } else {
                                    propertyTypes + propertyType
                                }
                            },
                            label = { Text(propertyType) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Количество комнат
                Text(
                    text = "Количество комнат",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = minRooms,
                        onValueChange = { minRooms = it.filter { char -> char.isDigit() } },
                        modifier = Modifier.weight(1f),
                        label = { Text("От") },
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = maxRooms,
                        onValueChange = { maxRooms = it.filter { char -> char.isDigit() } },
                        modifier = Modifier.weight(1f),
                        label = { Text("До") },
                        singleLine = true
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Цена
                Text(
                    text = "Цена",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = minPrice,
                        onValueChange = { minPrice = it.filter { char -> char.isDigit() || char == '.' || char == ',' } },
                        modifier = Modifier.weight(1f),
                        label = { Text("От") },
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = maxPrice,
                        onValueChange = { maxPrice = it.filter { char -> char.isDigit() || char == '.' || char == ',' } },
                        modifier = Modifier.weight(1f),
                        label = { Text("До") },
                        singleLine = true
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Район
                Text(
                    text = "Район",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableDistricts.forEach { district ->
                        val isSelected = district in districts
                        
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                districts = if (isSelected) {
                                    districts - district
                                } else {
                                    districts + district
                                }
                            },
                            label = { Text(district) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Кнопки
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            propertyTypes = emptySet()
                            minRooms = ""
                            maxRooms = ""
                            minPrice = ""
                            maxPrice = ""
                            districts = emptySet()
                        }
                    ) {
                        Text("Сбросить")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            val filter = PropertyFilter(
                                propertyTypes = propertyTypes,
                                minRooms = minRooms.toIntOrNull(),
                                maxRooms = maxRooms.toIntOrNull(),
                                minPrice = minPrice.replace(',', '.').toDoubleOrNull(),
                                maxPrice = maxPrice.replace(',', '.').toDoubleOrNull(),
                                districts = districts,
                                searchQuery = currentFilter.searchQuery
                            )
                            
                            onFilterApplied(filter)
                        }
                    ) {
                        Text("Применить")
                    }
                }
            }
        }
    }
}

 