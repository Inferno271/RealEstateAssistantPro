package com.realestateassistant.pro.presentation.components.property

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.domain.model.PropertyStatus

/**
 * Компонент для отображения статуса объекта недвижимости в виде цветного бейджа
 *
 * @param status Статус объекта недвижимости
 * @param modifier Модификатор компонента
 */
@Composable
fun PropertyStatusBadge(
    status: PropertyStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, contentColor, text) = when (status) {
        PropertyStatus.AVAILABLE -> Triple(
            Color(0xFF4CAF50).copy(alpha = 0.2f),
            Color(0xFF2E7D32),
            "Доступно"
        )
        PropertyStatus.RESERVED -> Triple(
            Color(0xFFFFB74D).copy(alpha = 0.2f),
            Color(0xFFE65100),
            "Зарезервировано"
        )
        PropertyStatus.OCCUPIED -> Triple(
            Color(0xFFEF5350).copy(alpha = 0.2f),
            Color(0xFFB71C1C),
            "Занято"
        )
        PropertyStatus.UNAVAILABLE -> Triple(
            Color(0xFF9E9E9E).copy(alpha = 0.2f),
            Color(0xFF424242),
            "Недоступно"
        )
        PropertyStatus.PENDING_REVIEW -> Triple(
            Color(0xFF90CAF9).copy(alpha = 0.2f),
            Color(0xFF1565C0),
            "На проверке"
        )
        PropertyStatus.ARCHIVED -> Triple(
            Color(0xFF9E9E9E).copy(alpha = 0.2f),
            Color(0xFF424242),
            "В архиве"
        )
    }

    Box(
        modifier = modifier
            .widthIn(min = 110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = contentColor,
            textAlign = TextAlign.Center
        )
    }
} 