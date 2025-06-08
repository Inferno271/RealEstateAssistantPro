package com.realestateassistant.pro.navigation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
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
            route = AppRoutes.DASHBOARD,
            title = "Панель управления",
            icon = Icons.Default.Dashboard,
            contentDescription = "Панель управления"
        ),
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
            route = AppRoutes.BOOKINGS,
            title = "Бронирования",
            icon = Icons.Default.BookOnline,
            contentDescription = "Список бронирований"
        ),
        NavigationItem(
            route = AppRoutes.APPOINTMENTS,
            title = "Встречи",
            icon = Icons.Default.DateRange,
            contentDescription = "Список встреч"
        ),
        NavigationItem(
            route = AppRoutes.NOTIFICATIONS,
            title = "Уведомления",
            icon = Icons.Default.Notifications,
            contentDescription = "Уведомления",
            badgeCount = 5 // Пример отображения количества уведомлений
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
            icon = Icons.Default.Help,
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