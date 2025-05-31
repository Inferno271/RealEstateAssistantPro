package com.realestateassistant.pro.presentation.components.client

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
 * Компонент для отображения специфических предпочтений клиента по недвижимости.
 */
@Composable
fun ClientSpecificPropertyPreferences(client: Client) {
    // Проверяем, есть ли данные для отображения
    val hasData = client.needsYard || 
                  client.preferredYardArea != null ||
                  client.needsGarage || 
                  client.preferredGarageSpaces != null ||
                  client.needsBathhouse || 
                  client.needsPool
    
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Специальные требования",
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
            
            // Участок
            if (client.needsYard) {
                val yardText = client.preferredYardArea?.let { 
                    "Необходим (площадь: $it соток)" 
                } ?: "Необходим"
                
                PreferenceRow(
                    icon = Icons.Outlined.Yard,
                    label = "Участок:",
                    value = yardText
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Гараж
            if (client.needsGarage) {
                val garageText = client.preferredGarageSpaces?.let { 
                    "Необходим (мест: $it)" 
                } ?: "Необходим"
                
                PreferenceRow(
                    icon = Icons.Outlined.Garage,
                    label = "Гараж:",
                    value = garageText
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Баня
            if (client.needsBathhouse) {
                PreferenceRow(
                    icon = Icons.Outlined.Spa,
                    label = "Баня/сауна:",
                    value = "Необходима"
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Бассейн
            if (client.needsPool) {
                PreferenceRow(
                    icon = Icons.Outlined.Pool,
                    label = "Бассейн:",
                    value = "Необходим"
                )
            }
        }
    }
} 