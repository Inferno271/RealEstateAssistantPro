package com.realestateassistant.pro.presentation.components.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.domain.model.Client

/**
 * Компонент для отображения жилищных предпочтений клиента.
 */
@Composable
fun ClientHousingPreferences(client: Client) {
    // Проверяем, есть ли данные для отображения
    val hasData = !client.preferredRepairState.isNullOrEmpty() || 
                  client.preferredFloorMin != null ||
                  client.preferredFloorMax != null ||
                  client.needsElevator ||
                  client.preferredBalconiesCount != null ||
                  client.preferredBathroomsCount != null ||
                  !client.preferredBathroomType.isNullOrEmpty() ||
                  !client.preferredHeatingType.isNullOrEmpty() ||
                  client.needsParking ||
                  !client.preferredParkingType.isNullOrEmpty()
    
    if (!hasData) return
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Заголовок раздела
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Жилищные предпочтения",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    thickness = 0.5.dp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Группа: Базовые характеристики
            PreferenceGroup(title = "Расположение и этажность") {
                // Этаж
                if (client.preferredFloorMin != null || client.preferredFloorMax != null) {
                    val floorText = when {
                        client.preferredFloorMin != null && client.preferredFloorMax != null -> 
                            "с ${client.preferredFloorMin} по ${client.preferredFloorMax}"
                        client.preferredFloorMin != null -> 
                            "от ${client.preferredFloorMin}"
                        else -> 
                            "до ${client.preferredFloorMax}"
                    }
                    
                    PreferenceRow(
                        icon = Icons.Outlined.Stairs,
                        label = "Этаж:",
                        value = floorText,
                        highlighted = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Лифт
                if (client.needsElevator) {
                    PreferenceRow(
                        icon = Icons.Outlined.Elevator,
                        label = "Наличие лифта:",
                        value = "Обязательно",
                        highlighted = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Группа: Ремонт и обустройство
            PreferenceGroup(title = "Ремонт и обустройство") {
                // Состояние ремонта
                client.preferredRepairState?.let {
                    if (it.isNotEmpty()) {
                        PreferenceRow(
                            icon = Icons.Outlined.Handyman,
                            label = "Состояние ремонта:",
                            value = it
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                // Мебель
                PreferenceRow(
                    icon = Icons.Outlined.Chair,
                    label = "Мебель:",
                    value = if (client.needsFurniture) "Необходима" else "Не требуется",
                    highlighted = client.needsFurniture
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Бытовая техника
                PreferenceRow(
                    icon = Icons.Outlined.Kitchen,
                    label = "Бытовая техника:",
                    value = if (client.needsAppliances) "Необходима" else "Не требуется",
                    highlighted = client.needsAppliances
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Отопление
                client.preferredHeatingType?.let {
                    if (it.isNotEmpty()) {
                        PreferenceRow(
                            icon = Icons.Outlined.Thermostat,
                            label = "Тип отопления:",
                            value = it
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Группа: Дополнительные помещения
            PreferenceGroup(title = "Дополнительные помещения") {
                // Балконы
                client.preferredBalconiesCount?.let {
                    PreferenceRow(
                        icon = Icons.Outlined.Balcony,
                        label = "Балконов/лоджий:",
                        value = it.toString()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Санузлы
                client.preferredBathroomsCount?.let {
                    PreferenceRow(
                        icon = Icons.Outlined.Bathroom,
                        label = "Количество санузлов:",
                        value = it.toString()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Тип санузла
                client.preferredBathroomType?.let {
                    if (it.isNotEmpty()) {
                        PreferenceRow(
                            icon = Icons.Outlined.Bathroom,
                            label = "Тип санузла:",
                            value = it
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Группа: Инфраструктура
            if (client.needsParking) {
                PreferenceGroup(title = "Инфраструктура") {
                    // Парковка
                    val parkingText = client.preferredParkingType?.let { 
                        "Необходима ($it)" 
                    } ?: "Необходима"
                    
                    PreferenceRow(
                        icon = Icons.Outlined.LocalParking,
                        label = "Парковка:",
                        value = parkingText,
                        highlighted = true
                    )
                }
            }
        }
    }
}

/**
 * Группа предпочтений с заголовком и контентом.
 */
@Composable
private fun PreferenceGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            content()
        }
    }
} 