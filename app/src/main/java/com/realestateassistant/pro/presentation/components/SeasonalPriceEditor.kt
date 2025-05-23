package com.realestateassistant.pro.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.domain.model.SeasonalPrice
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonalPriceEditor(
    seasonalPrices: List<SeasonalPrice>,
    onSeasonalPricesChanged: (List<SeasonalPrice>) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Сезонные цены (${seasonalPrices.size})",
                style = MaterialTheme.typography.titleMedium
            )
            
            IconButton(onClick = { showAddDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить сезонную цену",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        if (seasonalPrices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет сезонных цен. Нажмите + чтобы добавить.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .padding(vertical = 8.dp)
            ) {
                items(seasonalPrices) { seasonalPrice ->
                    SeasonalPriceItem(
                        seasonalPrice = seasonalPrice,
                        onDelete = {
                            onSeasonalPricesChanged(seasonalPrices.filter { it != seasonalPrice })
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
    
    if (showAddDialog) {
        AddSeasonalPriceDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { startDate, endDate, price ->
                val newSeasonalPrice = SeasonalPrice(
                    startDate = startDate,
                    endDate = endDate,
                    price = price
                )
                onSeasonalPricesChanged(seasonalPrices + newSeasonalPrice)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun SeasonalPriceItem(
    seasonalPrice: SeasonalPrice,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val startDateStr = remember(seasonalPrice.startDate) {
        dateFormat.format(Date(seasonalPrice.startDate))
    }
    val endDateStr = remember(seasonalPrice.endDate) {
        dateFormat.format(Date(seasonalPrice.endDate))
    }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$startDateStr - $endDateStr",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AttachMoney,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${seasonalPrice.price} ₽/сутки",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            IconButton(
                onClick = onDelete,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSeasonalPriceDialog(
    onDismiss: () -> Unit,
    onConfirm: (startDate: Long, endDate: Long, price: Double) -> Unit
) {
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var priceStr by remember { mutableStateOf("") }
    
    // Состояния для DatePicker
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    
    // Для отображения выбранных дат
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val startDateFormatted = startDate?.let { dateFormat.format(Date(it)) } ?: ""
    val endDateFormatted = endDate?.let { dateFormat.format(Date(it)) } ?: ""
    
    // Создаем DatePicker для начальной даты
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState()
        
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        startDate = it
                    }
                    showStartDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // Создаем DatePicker для конечной даты
    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState()
        
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        endDate = it
                    }
                    showEndDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить сезонную цену") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Поле выбора даты начала
                OutlinedTextField(
                    value = startDateFormatted,
                    onValueChange = { /* Пустой обработчик, т.к. значение меняется через DatePicker */ },
                    label = { Text("Дата начала") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showStartDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Outlined.DateRange,
                                contentDescription = "Выбрать дату начала"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Поле выбора даты окончания
                OutlinedTextField(
                    value = endDateFormatted,
                    onValueChange = { /* Пустой обработчик, т.к. значение меняется через DatePicker */ },
                    label = { Text("Дата окончания") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showEndDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Outlined.DateRange,
                                contentDescription = "Выбрать дату окончания"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                NumericTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = "Цена за сутки",
                    allowDecimal = true,
                    suffix = " ₽"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceStr.toDoubleOrNull() ?: 0.0
                    
                    if (startDate != null && endDate != null && price > 0) {
                        onConfirm(startDate!!, endDate!!, price)
                    }
                },
                enabled = startDate != null && endDate != null && priceStr.toDoubleOrNull() != null && priceStr.toDoubleOrNull()!! > 0
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
} 