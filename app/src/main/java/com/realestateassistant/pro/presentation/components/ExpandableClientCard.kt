package com.realestateassistant.pro.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.presentation.model.ClientSection

/**
 * Раскрывающаяся карточка для секций клиентской формы.
 * 
 * @param title Заголовок карточки
 * @param sectionKey Ключ секции для отслеживания состояния
 * @param expandedSections Карта состояний развернутости секций
 * @param isError Флаг, указывающий на наличие ошибки в секции
 * @param content Содержимое карточки
 */
@Composable
fun ExpandableClientCard(
    title: String,
    sectionKey: ClientSection,
    expandedSections: MutableMap<ClientSection, Boolean>,
    isError: Boolean = false,
    content: @Composable () -> Unit
) {
    // Получаем текущее состояние развёрнутости секции из карты
    val isExpanded = expandedSections[sectionKey] ?: false
    
    // Анимация поворота стрелки
    val arrowRotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)
    
    // Определяем цвета в зависимости от наличия ошибки
    val borderColor = if (isError) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    
    val backgroundColor = if (isError) {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }
    
    val contentColor = if (isError) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isError) 2.dp else 1.dp, 
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isError) 4.dp else 0.dp
        )
    ) {
        // Заголовок секции с возможностью раскрытия/скрытия
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { 
                    // Изменяем состояние в карте напрямую
                    expandedSections[sectionKey] = !isExpanded
                }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isError) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = "Ошибка",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Свернуть" else "Развернуть",
                modifier = Modifier.rotate(arrowRotation),
                tint = contentColor
            )
        }
        
        // Содержимое секции (отображается только если секция развернута)
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                content()
            }
        }
    }
} 