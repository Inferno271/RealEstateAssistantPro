package com.realestateassistant.pro.presentation.components.property.details

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Pool
import androidx.compose.material.icons.outlined.HotTub
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.model.PropertyTypeCharacteristics

/**
 * Компонент для отображения специфичных для типа недвижимости характеристик
 */
@Composable
fun PropertyTypeSpecificDetails(property: Property) {
    // Получаем конфигурацию характеристик для данного типа недвижимости
    val config = PropertyTypeCharacteristics.getCharacteristicsForType(property.propertyType)
    
    // Если нет специфичных характеристик для этого типа, не отображаем секцию
    val hasSpecificDetails = config.hasLevels || config.hasLandSquare || 
                           config.hasGarage || config.hasBathhouse || config.hasPool
    
    if (!hasSpecificDetails) return
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Особенности типа недвижимости",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
                
            // Количество уровней
            if (config.hasLevels && property.levelsCount > 0) {
                FeatureRow(
                    icon = Icons.Outlined.Apartment,
                    label = "Количество уровней:",
                    value = property.levelsCount.toString()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Площадь участка
            if (config.hasLandSquare && property.landArea > 0) {
                FeatureRow(
                    icon = Icons.Outlined.Landscape,
                    label = "Площадь участка:",
                    value = "${property.landArea} соток"
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Гараж
            if (config.hasGarage && property.hasGarage) {
                FeatureRow(
                    icon = Icons.Outlined.DirectionsCar,
                    label = "Гараж:",
                    value = "Да"
                )
                
                if (property.garageSpaces > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    FeatureRow(
                        label = "Машиномест:",
                        value = property.garageSpaces.toString(),
                        indented = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Баня/сауна
            if (config.hasBathhouse && property.hasBathhouse) {
                FeatureRow(
                    icon = Icons.Outlined.HotTub,
                    label = "Баня/Сауна:",
                    value = "Да"
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Бассейн
            if (config.hasPool && property.hasPool) {
                FeatureRow(
                    icon = Icons.Outlined.Pool,
                    label = "Бассейн:",
                    value = if (property.poolType.isNotEmpty()) property.poolType else "Да"
                )
            }
        }
    }
}

@Composable
private fun FeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    label: String,
    value: String,
    indented: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null && !indented) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else if (indented) {
            Spacer(modifier = Modifier.width(28.dp))
        }
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
} 