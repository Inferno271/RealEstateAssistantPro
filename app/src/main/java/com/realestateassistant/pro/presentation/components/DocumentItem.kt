package com.realestateassistant.pro.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.io.File

/**
 * Элемент для отображения документа в списке с возможностью удаления
 *
 * @param documentUri URI или путь документа
 * @param onDeleteClick Действие при нажатии на кнопку удаления
 * @param index Индекс документа в списке (для отображения)
 */
@Composable
fun DocumentItem(
    documentUri: String,
    onDeleteClick: () -> Unit,
    index: Int
) {
    // Получаем имя файла для отображения
    val displayName = remember(documentUri) {
        if (documentUri.startsWith("content://") || documentUri.startsWith("file://")) {
            // Для URI формата, которые не используются в финальной версии
            "Внешний документ"
        } else {
            // Для файлов, сохраненных в хранилище приложения
            val file = File(documentUri)
            file.name
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Description,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Документ ${index + 1}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            IconButton(
                onClick = onDeleteClick
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить документ",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
} 