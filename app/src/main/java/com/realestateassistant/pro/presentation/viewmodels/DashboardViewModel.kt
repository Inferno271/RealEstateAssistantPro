package com.realestateassistant.pro.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realestateassistant.pro.domain.usecase.dashboard.GetDashboardSummaryUseCase
import com.realestateassistant.pro.presentation.state.dashboard.DashboardState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel для экрана панели управления
 *
 * Управляет состоянием экрана и обработкой данных
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardSummaryUseCase: GetDashboardSummaryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state

    init {
        loadDashboardData()
    }

    /**
     * Загружает данные для панели управления
     */
    fun loadDashboardData() {
        getDashboardSummaryUseCase()
            .onStart {
                _state.update { it.copy(isLoading = true) }
            }
            .onEach { dashboardSummary ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        dashboardSummary = dashboardSummary,
                        error = null
                    )
                }
            }
            .catch { e ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка загрузки данных"
                    )
                }
            }
            .launchIn(viewModelScope)
    }
} 