package com.realestateassistant.pro.presentation.components.property

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.domain.model.Property

@Composable
fun PropertyAdditionalFeatures(property: Property) {
    // Упрощаем проверку наличия данных
    val hasData = property.description?.isNotEmpty() == true || 
                  property.bedsCount != null || 
                  property.bathroomsCount != null || 
                  property.bathroomType.isNotEmpty() || 
                  property.repairState.isNotEmpty() || 
                  property.heatingType.isNotEmpty() || 
                  property.balconiesCount > 0 || 
                  property.elevatorsCount != null ||
                  !property.noFurniture ||
                  property.hasAppliances ||
                  property.hasParking ||
                  property.parkingType != null ||
                  property.parkingSpaces != null ||
                  property.layout.isNotEmpty() ||
                  property.isStudio
    
    if (!hasData) return
    
    PropertySectionCard(title = "Дополнительные характеристики") {
        // Описание объекта
        property.description?.let { description ->
            if (description.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = "Описание",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 36.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        
        // Планировка
        if (property.layout.isNotEmpty() || property.isStudio) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Apartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Планировка",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = if (property.isStudio) "Студия" else property.layout,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 36.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Спальные места и ванные комнаты
        if (property.bedsCount != null || property.bathroomsCount != null || property.bathroomType.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Bed,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Спальные места и санузел",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(
                modifier = Modifier.padding(start = 36.dp)
            ) {
                property.bedsCount?.let { bedsCount ->
                    FeatureRow(
                        label = "Спальных мест:",
                        value = "$bedsCount"
                    )
                }
                
                property.bathroomsCount?.let { bathroomsCount ->
                    Spacer(modifier = Modifier.height(8.dp))
                    FeatureRow(
                        label = "Ванных комнат:",
                        value = "$bathroomsCount"
                    )
                }
                
                if (property.bathroomType.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    FeatureRow(
                        label = "Тип санузла:",
                        value = property.bathroomType
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Состояние и отопление
        if (property.repairState.isNotEmpty() || property.heatingType.isNotEmpty()) {
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
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Состояние и отопление",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(
                modifier = Modifier.padding(start = 36.dp)
            ) {
                if (property.repairState.isNotEmpty()) {
                    FeatureRow(
                        label = "Состояние ремонта:",
                        value = property.repairState
                    )
                }
                
                if (property.heatingType.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    FeatureRow(
                        label = "Тип отопления:",
                        value = property.heatingType
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Мебель и техника
        if (!property.noFurniture || property.hasAppliances) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Apartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Мебель и техника",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(
                modifier = Modifier.padding(start = 36.dp)
            ) {
                FeatureRow(
                    label = "Мебель:",
                    value = if (property.noFurniture) "Отсутствует" else "Есть"
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                FeatureRow(
                    label = "Бытовая техника:",
                    value = if (property.hasAppliances) "Есть" else "Отсутствует"
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Балконы и лифты
        if (property.balconiesCount > 0 || property.elevatorsCount != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Apartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Балконы и лифты",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(
                modifier = Modifier.padding(start = 36.dp)
            ) {
                if (property.balconiesCount > 0) {
                    FeatureRow(
                        label = "Балконы:",
                        value = "${property.balconiesCount}"
                    )
                }
                
                property.elevatorsCount?.let { elevatorsCount ->
                    Spacer(modifier = Modifier.height(8.dp))
                    FeatureRow(
                        label = "Лифты:",
                        value = "$elevatorsCount"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Парковка
        if (property.hasParking) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Business,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Парковка",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(
                modifier = Modifier.padding(start = 36.dp)
            ) {
                FeatureRow(
                    label = "Статус:",
                    value = "Есть"
                )
                
                property.parkingType?.let { type ->
                    Spacer(modifier = Modifier.height(8.dp))
                    FeatureRow(
                        label = "Тип:",
                        value = type
                    )
                }
                
                property.parkingSpaces?.let { spaces ->
                    Spacer(modifier = Modifier.height(8.dp))
                    FeatureRow(
                        label = "Количество мест:",
                        value = "$spaces"
                    )
                }
            }
        }
    }
} 