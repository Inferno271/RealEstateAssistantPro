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
 * Компонент для отображения основной информации о клиенте.
 */
@Composable
fun ClientInfoCard(client: Client) {
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
                    text = "Информация о клиенте",
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
            
            // Состав семьи
            client.familyComposition?.let {
                if (it.isNotEmpty()) {
                    ClientInfoRow(
                        icon = Icons.Outlined.People,
                        label = "Состав семьи:",
                        value = it
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            // Количество проживающих
            client.peopleCount?.let {
                ClientInfoRow(
                    icon = Icons.Outlined.Group,
                    label = "Количество проживающих:",
                    value = it.toString()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Дети
            client.childrenCount?.let { count ->
                if (count > 0) {
                    val childrenText = if (client.childrenAges.isNotEmpty()) {
                        "$count (возраст: ${client.childrenAges.joinToString(", ")} лет)"
                    } else {
                        count.toString()
                    }
                    ClientInfoRow(
                        icon = Icons.Outlined.ChildCare,
                        label = "Дети:",
                        value = childrenText
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            // Домашние животные
            if (client.hasPets) {
                val petsText = if (client.petTypes.isNotEmpty()) {
                    "${client.petCount ?: "неизвестно"} (${client.petTypes.joinToString(", ")})"
                } else {
                    client.petCount?.toString() ?: "Есть"
                }
                ClientInfoRow(
                    icon = Icons.Outlined.Pets,
                    label = "Домашние животные:",
                    value = petsText
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Род занятий
            client.occupation?.let {
                if (it.isNotEmpty()) {
                    ClientInfoRow(
                        icon = Icons.Outlined.Work,
                        label = "Род занятий:",
                        value = it
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            // Курение
            ClientInfoRow(
                icon = Icons.Outlined.SmokingRooms,
                label = "Курит:",
                value = if (client.isSmokingClient) "Да" else "Нет"
            )
        }
    }
}

/**
 * Строка с информацией о клиенте.
 */
@Composable
fun ClientInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal
        )
    }
} 