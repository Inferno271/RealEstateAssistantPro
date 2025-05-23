package com.realestateassistant.pro.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import android.util.Log
import com.realestateassistant.pro.network.YandexSuggestService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GeocodedAddress(
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class AddressSuggestion(
    val value: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)

@Composable
fun AddressTextField(
    value: GeocodedAddress,
    onValueChange: (GeocodedAddress) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Адрес"
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }
    var showDropdown by remember { mutableStateOf(false) }
    var suggestions by remember { mutableStateOf<List<AddressSuggestion>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // Создаем состояние для дебаунсинга ввода
    val inputFlow = remember { MutableStateFlow("") }
    var debouncedJob: Job? by remember { mutableStateOf(null) }
    
    // Настраиваем дебаунсинг ввода
    LaunchedEffect(Unit) {
        val scope = CoroutineScope(Dispatchers.Main)
        debouncedJob = scope.launch {
            inputFlow
                .debounce(500) // Задержка в 500 мс перед запросом
                .collect { input ->
                    if (input.length >= 3) {
                        isLoading = true
                        error = null
                        try {
                            val result = withContext(Dispatchers.IO) {
                                val apiSuggestions = YandexSuggestService.getSuggestions(input, context)
                                // Преобразуем API тип в наш собственный
                                apiSuggestions.map { apiSuggestion ->
                                    AddressSuggestion(
                                        value = apiSuggestion.fullAddress,
                                        latitude = apiSuggestion.latitude,
                                        longitude = apiSuggestion.longitude
                                    )
                                }
                            }
                            suggestions = result
                            showDropdown = suggestions.isNotEmpty()
                        } catch (e: Exception) {
                            error = "Ошибка загрузки подсказок: ${e.message}"
                            Log.e("AddressTextField", "Error loading address suggestions", e)
                        } finally {
                            isLoading = false
                        }
                    } else {
                        suggestions = emptyList()
                        showDropdown = false
                    }
                }
        }
    }
    
    // Очищаем ресурсы при уходе с экрана
    DisposableEffect(Unit) {
        onDispose {
            debouncedJob?.cancel()
        }
    }
    
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value.address,
            onValueChange = { newValue ->
                onValueChange(GeocodedAddress(address = newValue))
                inputFlow.value = newValue
            },
            label = { Text(label) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                    if (isFocused && value.address.length >= 3) {
                        // При фокусе показываем ранее загруженные подсказки
                        showDropdown = suggestions.isNotEmpty()
                    } else {
                        showDropdown = false
                    }
                },
            trailingIcon = {
                if (value.address.isNotEmpty()) {
                    IconButton(onClick = { 
                        onValueChange(GeocodedAddress())
                        suggestions = emptyList()
                        showDropdown = false
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Очистить")
                    }
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                showDropdown = false
            }),
            isError = error != null,
            supportingText = error?.let { { Text(it) } }
        )
        
        // Отображаем координаты (опционально, для отладки)
        if (value.latitude != null && value.longitude != null) {
            Text(
                text = "Координаты: ${String.format("%.6f", value.latitude)}, ${String.format("%.6f", value.longitude)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
        
        if (showDropdown && isFocused) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.height(24.dp).width(24.dp))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().height(250.dp)
                    ) {
                        items(suggestions) { suggestion ->
                            SuggestionItem(
                                suggestion = suggestion,
                                onClick = {
                                    // Сохраняем адрес и координаты
                                    val geocodedAddress = GeocodedAddress(
                                        address = suggestion.value,
                                        latitude = suggestion.latitude,
                                        longitude = suggestion.longitude
                                    )
                                    onValueChange(geocodedAddress)
                                    
                                    Log.d("AddressTextField", "Выбран адрес: ${suggestion.value}, координаты: ${suggestion.latitude}, ${suggestion.longitude}")
                                    
                                    // Очищаем фокус и скрываем подсказки
                                    focusManager.clearFocus()
                                    showDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SuggestionItem(
    suggestion: AddressSuggestion,
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
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = suggestion.value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
} 