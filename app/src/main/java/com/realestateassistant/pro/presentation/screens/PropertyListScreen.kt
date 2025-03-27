package com.realestateassistant.pro.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.presentation.viewmodel.PropertyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyListScreen(
    onNavigateToAddProperty: () -> Unit,
    propertyViewModel: PropertyViewModel = hiltViewModel()
) {
    val propertiesState = propertyViewModel.properties.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Объекты недвижимости",
                        style = MaterialTheme.typography.titleLarge
                    ) 
                },
                actions = {
                    IconButton(onClick = { /* TODO: Реализовать поиск */ }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Поиск",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddProperty,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить объект")
            }
        }
    ) { paddingValues ->
        if (propertiesState.value.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Нет объектов недвижимости",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Нажмите + чтобы добавить новый объект",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(propertiesState.value) { property ->
                    PropertyItem(property = property)
                }
            }
        }
    }
}

@Composable
fun PropertyItem(property: Property) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Заголовок карточки с типом и общей информацией
            Text(
                text = "${property.propertyType}, ${property.area} м²",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Адрес объекта
            Text(
                text = property.address, 
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Информация о комнатах и расположении
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${property.roomsCount} ${getRoomsText(property.roomsCount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                
                property.floor?.let { floor ->
                    Text(
                        text = "${floor} этаж ${if (property.totalFloors > 0) "из ${property.totalFloors}" else ""}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // Цена аренды
            property.monthlyRent?.let { price ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${formatPrice(price)} ₽/мес",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            property.dailyPrice?.let { price ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${formatPrice(price)} ₽/сутки",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            
            // Контактная информация
            if (property.contactName.isNotEmpty()) {
                Text(
                    text = property.contactName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            if (property.contactPhone.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatPhoneForDisplay(property.contactPhone.first()),
                    style = MaterialTheme.typography.bodyMedium
                )
                
                if (property.contactPhone.size > 1) {
                    Text(
                        text = "И ещё ${property.contactPhone.size - 1} номер(а)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// Склонение слова "комната" в зависимости от числа
fun getRoomsText(roomsCount: Int): String {
    return when {
        roomsCount % 10 == 1 && roomsCount % 100 != 11 -> "комната"
        roomsCount % 10 in 2..4 && (roomsCount % 100 < 10 || roomsCount % 100 >= 20) -> "комнаты"
        else -> "комнат"
    }
}

// Форматирование цены с разделением тысяч
fun formatPrice(price: Double): String {
    return String.format("%,d", price.toInt()).replace(",", " ")
}

fun formatPhoneForDisplay(phone: String): String {
    if (phone.length != 10) return phone
    
    return "+7 (${phone.substring(0, 3)}) ${phone.substring(3, 6)}-${phone.substring(6, 8)}-${phone.substring(8, 10)}"
} 