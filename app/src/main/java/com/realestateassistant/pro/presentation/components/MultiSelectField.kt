package com.realestateassistant.pro.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun MultiSelectField(
    label: String,
    options: List<String>,
    selectedOptions: List<String>,
    onSelectionChange: (List<String>) -> Unit,
    onOptionsChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }
    
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
                    .heightIn(min = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(
                    items = selectedOptions,
                    key = { it } // Используем сам элемент как ключ для стабильных перерисовок
                ) { option ->
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.clickable { onSelectionChange(selectedOptions - option) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Удалить",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
            IconButton(
                onClick = { showEditDialog = true }
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Редактировать список")
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        
        // Отображаем доступные опции только если они есть
        val availableOptions = options.filter { it !in selectedOptions }
        
        if (availableOptions.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .wrapContentHeight()
                    .heightIn(min = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(
                    items = availableOptions,
                    key = { it } // Используем сам элемент как ключ
                ) { option ->
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { onSelectionChange(selectedOptions + option) }
                    ) {
                        Text(
                            text = option,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        EditOptionsDialog(
            options = options,
            onOptionsChange = onOptionsChange,
            onDismiss = { showEditDialog = false }
        )
    }
}

@Composable
fun EditOptionsDialog(
    options: List<String>,
    onOptionsChange: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                "Редактировать список",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var newItemText by remember { mutableStateOf("") }
                
                OutlinedTextField(
                    value = newItemText,
                    onValueChange = { newItemText = it },
                    label = { Text("Новый элемент") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (newItemText.isNotEmpty()) {
                                    onOptionsChange(options + newItemText)
                                    newItemText = ""
                                }
                            },
                            enabled = newItemText.isNotEmpty()
                        ) {
                            Icon(
                                Icons.Default.Add, 
                                "Добавить",
                                tint = if (newItemText.isNotEmpty())
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                
                HorizontalDivider()
                
                if (options.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Список пуст",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(max = 300.dp, min = 50.dp)
                            .wrapContentHeight(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(options) { option ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = {
                                            onOptionsChange(options - option)
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close, 
                                            "Удалить", 
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Закрыть")
            }
        }
    )
}

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalGap: Dp = 0.dp,
    verticalGap: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val horizontalGapPx = with(density) { horizontalGap.toPx().toInt() }
        val verticalGapPx = with(density) { verticalGap.toPx().toInt() }
        
        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
        
        var width = 0
        var height = 0
        var rowWidth = 0
        var rowHeight = 0
        var rowItemCount = 0
        
        // Координаты для размещения элементов
        val positions = mutableListOf<IntArray>()
        
        placeables.forEach { placeable ->
            // Если элемент не помещается в строку, начинаем новую строку
            if (rowWidth + placeable.width > constraints.maxWidth && rowItemCount > 0) {
                width = width.coerceAtLeast(rowWidth)
                height += rowHeight + verticalGapPx
                rowWidth = 0
                rowHeight = 0
                rowItemCount = 0
            }
            
            // Добавляем позицию для текущего элемента
            positions.add(intArrayOf(rowWidth, height))
            
            // Обновляем данные строки
            rowWidth += placeable.width + horizontalGapPx
            rowHeight = rowHeight.coerceAtLeast(placeable.height)
            rowItemCount++
        }
        
        // Учитываем последнюю строку
        width = width.coerceAtLeast(rowWidth)
        height += rowHeight
        
        // Возвращаем компоновку
        layout(
            width = if (width > 0) width - horizontalGapPx else 0,
            height = height
        ) {
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(
                    x = positions[index][0],
                    y = positions[index][1]
                )
            }
        }
    }
}

@Composable
fun SelectedOptionChip(
    option: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 8.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
        ) {
            Text(
                text = option,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Удалить",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
} 