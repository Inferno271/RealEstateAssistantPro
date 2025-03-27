package com.realestateassistant.pro.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.realestateassistant.pro.network.YandexSuggestService
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@OptIn(FlowPreview::class)
@Composable
fun AddressTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Адрес",
    placeholder: String = "Введите адрес"
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }
    var showSuggestions by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Используем Flow для дебаунса ввода, чтобы не делать запросы при каждом нажатии клавиши
    val searchTextFlow = remember { MutableStateFlow("") }
    val suggestions = remember { mutableStateListOf<YandexSuggestService.AddressSuggestion>() }
    
    // Наблюдаем за текстом и делаем запрос с задержкой
    LaunchedEffect(searchTextFlow) {
        searchTextFlow
            .debounce(300) // Задержка 300 мс
            .filter { it.length >= 3 } // Фильтр на минимальную длину
            .distinctUntilChanged() // Только при изменении
            .collectLatest { query ->
                if (query.length >= 3) {
                    isLoading = true
                    errorMessage = null
                    try {
                        val result = YandexSuggestService.getSuggestions(query, context)
                        suggestions.clear()
                        suggestions.addAll(result)
                        showSuggestions = result.isNotEmpty() && isFocused
                    } catch (e: YandexSuggestService.NoInternetException) {
                        errorMessage = e.message ?: "Отсутствует подключение к интернету"
                    } catch (e: YandexSuggestService.ApiException) {
                        errorMessage = e.message ?: "Ошибка при обращении к серверу"
                    } catch (e: YandexSuggestService.ParsingException) {
                        errorMessage = e.message ?: "Ошибка при обработке ответа"
                    } catch (e: Exception) {
                        errorMessage = "Произошла ошибка: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            }
    }
    
    // При изменении значения обновляем Flow
    LaunchedEffect(value) {
        if (value != searchTextFlow.value) {
            searchTextFlow.value = value
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .zIndex(if (showSuggestions) 1f else 0f)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { 
                onValueChange(it)
                if (it.isEmpty()) {
                    suggestions.clear()
                    showSuggestions = false
                    errorMessage = null
                } else {
                    showSuggestions = it.length >= 3 && isFocused && suggestions.isNotEmpty()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                    if (focusState.isFocused && value.length >= 3 && suggestions.isNotEmpty()) {
                        showSuggestions = true
                    } else {
                        showSuggestions = false
                    }
                },
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else if (value.isNotEmpty()) {
                    IconButton(onClick = { 
                        onValueChange("")
                        suggestions.clear()
                        showSuggestions = false
                        errorMessage = null
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Очистить"
                        )
                    }
                }
            },
            isError = errorMessage != null
        )
        
        // Сообщение об ошибке
        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 60.dp)
            )
        }
        
        // Выпадающий список с подсказками
        if (showSuggestions) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp)
                    .padding(top = 60.dp) // Смещение вниз от поля ввода
                    .background(
                        MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                items(suggestions) { suggestion ->
                    AddressSuggestionItem(
                        suggestion = suggestion,
                        onClick = {
                            onValueChange(suggestion.fullAddress)
                            suggestions.clear()
                            showSuggestions = false
                            focusManager.clearFocus()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AddressSuggestionItem(
    suggestion: YandexSuggestService.AddressSuggestion,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = suggestion.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            if (suggestion.fullAddress != suggestion.title) {
                Text(
                    text = suggestion.fullAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
} 