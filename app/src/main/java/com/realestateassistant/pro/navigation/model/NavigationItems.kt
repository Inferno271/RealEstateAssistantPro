package com.realestateassistant.pro.navigation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import com.realestateassistant.pro.navigation.routes.AppRoutes

/**
 * Элементы навигации для бокового меню
 */
object NavigationItems {
    // Основные пункты меню
    val mainItems = listOf(
        NavigationItem(
            route = AppRoutes.PROPERTIES,
            title = "Объекты",
            icon = Icons.Default.Home,
            contentDescription = "Список объектов недвижимости"
        ),
        NavigationItem(
            route = AppRoutes.CLIENTS,
            title = "Клиенты",
            icon = Icons.Default.Person,
            contentDescription = "Список клиентов"
        ),
        NavigationItem(
            route = AppRoutes.APPOINTMENTS,
            title = "Встречи",
            icon = Icons.Default.DateRange,
            contentDescription = "Список встреч"
        )
    )
    
    // Дополнительные пункты меню (нижняя часть drawer)
    val otherItems = listOf(
        NavigationItem(
            route = AppRoutes.SETTINGS,
            title = "Настройки",
            icon = Icons.Default.Settings,
            contentDescription = "Настройки приложения"
        ),
        NavigationItem(
            route = AppRoutes.HELP,
            title = "Помощь",
            icon = Icons.Default.Info,
            contentDescription = "Помощь и инструкции"
        ),
        NavigationItem(
            route = AppRoutes.ABOUT,
            title = "О приложении",
            icon = Icons.Default.Info,
            contentDescription = "Информация о приложении"
        )
    )
} 