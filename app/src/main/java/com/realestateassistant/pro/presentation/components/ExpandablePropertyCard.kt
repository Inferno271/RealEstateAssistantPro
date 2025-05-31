package com.realestateassistant.pro.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.presentation.model.PropertySection

/**
 * Компонент расширяемой карточки с единообразной обработкой ошибок
 * 
 * @param title Заголовок секции
 * @param sectionKey Ключ секции для отслеживания состояния развернутости
 * @param expandedSections Карта состояний развернутости секций
 * @param modifier Модификатор для настройки внешнего вида
 * @param hasError Флаг наличия ошибки в секции
 * @param errorMessage Сообщение об ошибке для отображения
 * @param content Содержимое секции
 */
@Composable
fun ExpandablePropertyCard(
    title: String,
    sectionKey: PropertySection,
    expandedSections: MutableMap<PropertySection, Boolean>,
    modifier: Modifier = Modifier,
    hasError: Boolean = false,
    errorMessage: String? = null,
    content: @Composable () -> Unit
) {
    val isExpanded = expandedSections[sectionKey] ?: false
    
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "Rotation animation"
    )
    
    // Определяем цвета в зависимости от наличия ошибки
    val borderColor = if (hasError) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    
    val backgroundColor = if (hasError) {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }
    
    val contentColor = if (hasError) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (hasError) 2.dp else 1.dp, 
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (hasError) 4.dp else 0.dp
        )
    ) {
        // Заголовок с иконкой разворачивания
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedSections[sectionKey] = !isExpanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (hasError) {
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
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Свернуть" else "Развернуть",
                    modifier = Modifier.rotate(rotationState),
                    tint = contentColor
                )
            }
            
            // Отображаем сообщение об ошибке если оно есть и секция свернута
            if (hasError && errorMessage != null && !isExpanded) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
            
            // Анимированное содержимое
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    content()
                }
            }
        }
    }
} 