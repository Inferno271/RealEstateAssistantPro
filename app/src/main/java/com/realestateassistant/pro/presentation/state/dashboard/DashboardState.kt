package com.realestateassistant.pro.presentation.state.dashboard

import com.realestateassistant.pro.domain.model.dashboard.DashboardSummary

/**
 * Состояние экрана панели управления
 *
 * @property isLoading флаг загрузки данных
 * @property dashboardSummary данные для отображения на экране
 * @property error сообщение об ошибке (если есть)
 */
data class DashboardState(
    val isLoading: Boolean = false,
    val dashboardSummary: DashboardSummary = DashboardSummary(),
    val error: String? = null
) 