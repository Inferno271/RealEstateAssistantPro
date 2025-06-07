package com.realestateassistant.pro.presentation.components.property

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.util.Log
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.network.YandexGeocoderService
import androidx.compose.ui.graphics.Color
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

private const val TAG = "PropertyAddressSection"

@Composable
fun PropertyAddressSection(
    property: Property, 
    modifier: Modifier = Modifier
) {
    val address = property.address
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState = lifecycleOwner.lifecycle.currentState
    val context = LocalContext.current
    
    // Используем координаты из свойства, если они есть, иначе применяем геокодирование
    var latitude by remember { mutableStateOf(property.latitude ?: 55.751225) } // Москва по умолчанию
    var longitude by remember { mutableStateOf(property.longitude ?: 37.62954) }
    var isLoading by remember { mutableStateOf(property.latitude == null && property.longitude == null) }
    var showMap by rememberSaveable { mutableStateOf(false) }
    
    // Отслеживаем состояние жизненного цикла для управления картой
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    Log.d(TAG, "onResume: восстановление карты")
                    showMap = true
                }
                Lifecycle.Event.ON_PAUSE -> {
                    Log.d(TAG, "onPause: скрытие карты")
                    showMap = false
                }
                Lifecycle.Event.ON_DESTROY -> {
                    Log.d(TAG, "onDestroy: уничтожение карты")
                    showMap = false
                }
                else -> { /* Ничего не делаем */ }
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            Log.d(TAG, "onDispose: компонент удален")
            showMap = false
        }
    }
    
    // Показать карту после первой композиции, если состояние жизненного цикла активно
    LaunchedEffect(Unit) {
        if (lifecycleState == Lifecycle.State.RESUMED || lifecycleState == Lifecycle.State.STARTED) {
            Log.d(TAG, "Инициализация карты")
            showMap = true
        }
    }
    
    // Выполняем геокодирование только если координаты отсутствуют
    LaunchedEffect(address) {
        if (property.latitude == null && property.longitude == null && address.isNotEmpty()) {
            try {
                Log.d(TAG, "Запускаем геокодирование для адреса: $address")
                val coordinates = YandexGeocoderService.getCoordinates(address)
                coordinates?.let {
                    latitude = it.first
                    longitude = it.second
                    Log.d(TAG, "Координаты получены: $latitude, $longitude")
                }
            } catch (e: Exception) {
                // В случае ошибки оставляем координаты по умолчанию
                Log.e(TAG, "Ошибка при геокодировании адреса: $address", e)
            } finally {
                isLoading = false
            }
        } else {
            isLoading = false
            if (property.latitude != null && property.longitude != null) {
                Log.d(TAG, "Используем координаты из свойства: ${property.latitude}, ${property.longitude}")
            }
        }
    }
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Адрес",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
                
                // Кнопка "Открыть карту" для навигации
                if (property.latitude != null && property.longitude != null) {
                    IconButton(
                        onClick = { 
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("geo:${property.latitude},${property.longitude}?q=${property.latitude},${property.longitude}(${property.address})")
                            }
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Открыть карту",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 2.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = property.address,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            if (property.district.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Row {
                    Spacer(modifier = Modifier.width(32.dp))
                    
                    Text(
                        text = "Район: ${property.district}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Отображаем координаты для отладки
            if (property.latitude != null && property.longitude != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Spacer(modifier = Modifier.width(32.dp))
                    Text(
                        text = "Координаты: ${String.format("%.6f", property.latitude)}, ${String.format("%.6f", property.longitude)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Всегда отображаем контейнер карты одинакового размера
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentAlignment = Alignment.Center
            ) {
                // Отображаем карту только если showMap=true и экран активен
                if (showMap && (lifecycleState == Lifecycle.State.RESUMED || lifecycleState == Lifecycle.State.STARTED)) {
                    if (!isLoading) {
                        // Используем key, чтобы пересоздавать компонент карты при изменении адреса
                        key(property.id, address, latitude, longitude) {
                            PropertyMapView(
                                address = property.address,
                                latitude = latitude,
                                longitude = longitude,
                                markerColor = Color.Red
                            )
                        }
                    } else {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
} 