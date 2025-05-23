package com.realestateassistant.pro.presentation.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Определение иконки в зависимости от типа объекта недвижимости
 */
@Composable
fun propertyTypeIcon(propertyType: String): ImageVector = when {
    propertyType.contains("Квартира", ignoreCase = true) -> Icons.Outlined.Apartment
    else -> Icons.Outlined.Home
} 