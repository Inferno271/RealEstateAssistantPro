package com.realestateassistant.pro.presentation.components.property

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.domain.model.Property

@Composable
fun PropertyLivingConditions(property: Property) {
    // Оптимизируем проверку
    val hasData = property.childrenAllowed || 
                  property.petsAllowed || 
                  property.smokingAllowed || 
                  property.partiesAllowed || 
                  property.amenities.isNotEmpty()
    
    if (!hasData) return
    
    PropertySectionCard(title = "Условия проживания") {
        // Информация о разрешении детей
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
                text = "Правила проживания",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Column(
            modifier = Modifier.padding(start = 36.dp)
        ) {
            FeatureRow(
                label = "Можно с детьми:",
                value = if (property.childrenAllowed) "Да" else "Нет"
            )
            
            // Дополнительные детали о детях
            if (property.childrenAllowed) {
                property.minChildAge?.let { minAge ->
                    Spacer(modifier = Modifier.height(8.dp))
                    FeatureRow(
                        label = "Минимальный возраст:",
                        value = "$minAge лет"
                    )
                }
                
                property.maxChildrenCount?.let { maxCount ->
                    Spacer(modifier = Modifier.height(8.dp))
                    FeatureRow(
                        label = "Максимальное количество:",
                        value = "$maxCount"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Информация о разрешении животных
            FeatureRow(
                label = "Можно с животными:",
                value = if (property.petsAllowed) "Да" else "Нет"
            )
            
            // Дополнительные детали о животных
            if (property.petsAllowed) {
                property.maxPetsCount?.let { maxCount ->
                    Spacer(modifier = Modifier.height(8.dp))
                    FeatureRow(
                        label = "Максимальное количество:",
                        value = "$maxCount"
                    )
                }
                
                if (property.allowedPetTypes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    FeatureRow(
                        label = "Разрешены:",
                        value = property.allowedPetTypes.joinToString(", ")
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Информация о курении
            FeatureRow(
                label = "Можно курить:",
                value = if (property.smokingAllowed) "Да" else "Нет"
            )
            
            if (property.monthlyRent == null) { // Только для краткосрочной аренды
                Spacer(modifier = Modifier.height(16.dp))
                // Информация о вечеринках
                FeatureRow(
                    label = "Можно устраивать вечеринки:",
                    value = if (property.partiesAllowed) "Да" else "Нет"
                )
            }
        }
        
        // Удобства и услуги
        if (property.amenities.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(24.dp))
            
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
                    text = "Удобства и услуги",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(
                modifier = Modifier.padding(start = 36.dp)
            ) {
                property.amenities.forEach { amenity ->
                    Text(
                        text = "• $amenity",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
} 