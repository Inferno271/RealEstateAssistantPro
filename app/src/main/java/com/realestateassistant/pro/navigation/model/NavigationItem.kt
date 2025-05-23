package com.realestateassistant.pro.navigation.model

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Модель для представления элемента навигации в боковом меню
 * 
 * @param route Маршрут для навигации
 * @param title Заголовок пункта меню
 * @param icon Иконка для пункта меню
 * @param contentDescription Описание элемента для accessibility
 */
data class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val contentDescription: String? = null,
    val badgeCount: Int? = null
) 