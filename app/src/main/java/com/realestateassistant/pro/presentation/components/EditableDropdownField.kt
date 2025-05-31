package com.realestateassistant.pro.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Редактируемое поле выпадающего списка с возможностью добавления новых элементов.
 *
 * @param value Текущее выбранное значение
 * @param onValueChange Функция обратного вызова для изменения значения
 * @param label Метка поля
 * @param options Список опций для выбора
 * @param onOptionsChange Функция обратного вызова для изменения списка опций
 * @param modifier Модификатор для настройки внешнего вида
 * @param placeholder Опциональный placeholder текст (показывается, когда поле пустое)
 * @param isError Флаг, указывающий на наличие ошибки
 * @param errorMessage Сообщение об ошибке
 * @param isRequired Флаг, указывающий что поле обязательное
 */
@Composable
fun EditableDropdownField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    options: List<String>,
    onOptionsChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    isRequired: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }
    
    val filteredOptions = remember(searchQuery, options) {
        if (searchQuery.isEmpty()) {
            options
        } else {
            options.filter { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        val colors = if (isError) {
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.error,
                unfocusedBorderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                focusedLabelColor = MaterialTheme.colorScheme.error,
                unfocusedLabelColor = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                cursorColor = MaterialTheme.colorScheme.error
            )
        } else {
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        }

        val labelText = buildAnnotatedString {
            append(label)
            if (isRequired) {
                append(" ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                    append("*")
                }
            }
        }

        OutlinedTextField(
            value = if (isExpanded) searchQuery else value,
            onValueChange = { newValue ->
                searchQuery = newValue
                if (!isExpanded) isExpanded = true
            },
            label = { Text(labelText) },
            placeholder = placeholder,
            trailingIcon = {
                Row {
                    if (isError) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Ошибка",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit, 
                            "Редактировать список",
                            tint = if (isError) 
                                MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                            else
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    }
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = if (isExpanded) "Свернуть" else "Раскрыть",
                            tint = if (isError) 
                                MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                            else
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            modifier = Modifier.rotate(if (isExpanded) 180f else 0f)
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = colors,
            singleLine = true,
            isError = isError,
            supportingText = if (isError && errorMessage != null) {
                { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
            } else null
        )

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(animationSpec = tween(durationMillis = 200)),
            exit = shrinkVertically(animationSpec = tween(durationMillis = 200))
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                shadowElevation = 4.dp,
                shape = MaterialTheme.shapes.small
            ) {
                LazyColumn {
                    items(filteredOptions) { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onValueChange(option)
                                searchQuery = ""
                                isExpanded = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                    if (searchQuery.isNotEmpty() && !options.contains(searchQuery)) {
                        item {
                            DropdownMenuItem(
                                text = { 
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Add, 
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            "Добавить \"$searchQuery\"",
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                onClick = {
                                    onOptionsChange(options + searchQuery)
                                    onValueChange(searchQuery)
                                    searchQuery = ""
                                    isExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        EditOptionsDialogForDropdown(
            options = options,
            onOptionsChange = onOptionsChange,
            onDismiss = { showEditDialog = false }
        )
    }
}

/**
 * Вариант EditableDropdownField с текстовым placeholder вместо композитного.
 * Упрощает создание выпадающего списка со строковым placeholder.
 *
 * @param value Текущее выбранное значение
 * @param onValueChange Функция обратного вызова для изменения значения
 * @param label Метка поля
 * @param options Список опций для выбора
 * @param onOptionsChange Функция обратного вызова для изменения списка опций
 * @param modifier Модификатор для настройки внешнего вида
 * @param placeholderText Текст placeholder (показывается, когда поле пустое)
 * @param isError Флаг, указывающий на наличие ошибки
 * @param errorMessage Сообщение об ошибке
 * @param isRequired Флаг, указывающий что поле обязательное
 */
@Composable
fun EditableDropdownFieldWithText(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    options: List<String>,
    onOptionsChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "",
    isError: Boolean = false,
    errorMessage: String? = null,
    isRequired: Boolean = false
) {
    EditableDropdownField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        options = options,
        onOptionsChange = onOptionsChange,
        modifier = modifier,
        placeholder = if (placeholderText.isNotEmpty()) {
            { Text(placeholderText, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) }
        } else null,
        isError = isError,
        errorMessage = errorMessage,
        isRequired = isRequired
    )
}

@Composable
private fun EditOptionsDialogForDropdown(
    options: List<String>,
    onOptionsChange: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Редактировать список",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
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
                            .heightIn(max = 300.dp),
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

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { onOptionsChange(emptyList()) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Очистить все")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Закрыть")
                    }
                }
            }
        }
    }
} 