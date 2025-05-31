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
 * Компонент для отображения юридических предпочтений клиента.
 */
@Composable
fun ClientLegalPreferences(client: Client) {
    // Проверяем, есть ли данные для отображения
    val hasData = client.needsOfficialAgreement || !client.preferredTaxOption.isNullOrEmpty()
    
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
                    text = "Юридические предпочтения",
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
            
            // Официальный договор
            if (client.needsOfficialAgreement) {
                PreferenceRow(
                    icon = Icons.Outlined.Description,
                    label = "Официальный договор:",
                    value = "Необходим"
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Предпочтительный вариант налогообложения
            client.preferredTaxOption?.let {
                if (it.isNotEmpty()) {
                    PreferenceRow(
                        icon = Icons.Outlined.Receipt,
                        label = "Вариант налогообложения:",
                        value = it
                    )
                }
            }
        }
    }
} 