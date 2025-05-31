package com.realestateassistant.pro.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realestateassistant.pro.presentation.model.RentalType

/**
 * Компонент для выбора типа аренды.
 * Используется для выбора между длительной и посуточной арендой.
 *
 * @param rentalType Текущий выбранный тип аренды
 * @param onRentalTypeChange Обработчик изменения типа аренды
 * @param modifier Модификатор компонента
 */
@Composable
fun RentalTypeSelector(
    rentalType: RentalType,
    onRentalTypeChange: (RentalType) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Кнопка "Длительная"
            RentalTypeButton(
                selected = rentalType == RentalType.LONG_TERM,
                onClick = { onRentalTypeChange(RentalType.LONG_TERM) },
                text = RentalType.LONG_TERM.getDisplayName(),
                modifier = Modifier.weight(1f)
            )
            
            Spacer(modifier = Modifier.width(6.dp))
            
            // Кнопка "Посуточная"
            RentalTypeButton(
                selected = rentalType == RentalType.SHORT_TERM,
                onClick = { onRentalTypeChange(RentalType.SHORT_TERM) },
                text = RentalType.SHORT_TERM.getDisplayName(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Кнопка выбора типа аренды с анимацией
 *
 * @param selected Выбрана ли кнопка
 * @param onClick Обработчик нажатия
 * @param text Текст кнопки
 * @param modifier Модификатор компонента
 */
@Composable
private fun RentalTypeButton(
    selected: Boolean,
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        animationSpec = tween(durationMillis = 300),
        label = "Background color animation"
    )
    
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
        animationSpec = tween(durationMillis = 300),
        label = "Text color animation"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (selected) 4.dp else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "Elevation animation"
    )
    
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = elevation
        )
    ) {
        Text(
            text = text, 
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
} 