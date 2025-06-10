package com.realestateassistant.pro.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realestateassistant.pro.domain.usecase.PopulateTestDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

/**
 * ViewModel для экрана настроек
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val populateTestDataUseCase: PopulateTestDataUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    /**
     * Заполняет базу данных тестовыми данными
     */
    fun populateTestData(propertiesCount: Int, clientsCount: Int) {
        _state.update { it.copy(isLoading = true) }
        
        Log.d("SettingsViewModel", "Начинаем генерацию тестовых данных: объекты=$propertiesCount, клиенты=$clientsCount")
        
        viewModelScope.launch {
            try {
                populateTestDataUseCase(propertiesCount, clientsCount)
                    .onSuccess { result ->
                        Log.d("SettingsViewModel", "Успешно создано: объекты=${result.propertiesAdded}, клиенты=${result.clientsAdded}")
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                testDataResult = result,
                                error = null
                            ) 
                        }
                    }
                    .onFailure { error ->
                        Log.e("SettingsViewModel", "Ошибка при создании тестовых данных: ${error.message}")
                        error.printStackTrace()
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                error = "Ошибка при создании тестовых данных: ${error.message}"
                            ) 
                        }
                    }
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Исключение при создании тестовых данных", e)
                e.printStackTrace()
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = "Ошибка: ${e.message}"
                    ) 
                }
            }
        }
    }

    /**
     * Сбрасывает результаты и ошибки
     */
    fun resetResult() {
        _state.update { 
            it.copy(
                testDataResult = null,
                error = null
            ) 
        }
    }
}

/**
 * Состояние экрана настроек
 */
data class SettingsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val testDataResult: PopulateTestDataUseCase.TestDataResult? = null
) 