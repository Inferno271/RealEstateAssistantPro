package com.realestateassistant.pro.domain.model.dashboard

import com.realestateassistant.pro.domain.model.Appointment

/**
 * Модель, содержащая данные для экрана панели управления
 *
 * @property quickActions список быстрых действий
 * @property upcomingEvents список ближайших событий
 */
data class DashboardSummary(
    val quickActions: List<QuickAction> = emptyList(),
    val upcomingEvents: List<UpcomingEvent> = emptyList()
)

/**
 * Модель для быстрых действий
 *
 * @property id уникальный идентификатор действия
 * @property title заголовок действия
 * @property iconResId идентификатор ресурса иконки
 * @property route маршрут навигации для данного действия
 */
data class QuickAction(
    val id: String,
    val title: String,
    val iconResId: Int,
    val route: String
)

/**
 * Модель для ближайшего события (встречи)
 *
 * @property appointment информация о встрече
 * @property isToday флаг, указывающий на то, что встреча запланирована на сегодня
 */
data class UpcomingEvent(
    val appointment: Appointment,
    val isToday: Boolean = false
) 