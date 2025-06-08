package com.realestateassistant.pro.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.model.PropertyFilter
import com.realestateassistant.pro.domain.usecase.ClientUseCases
import com.realestateassistant.pro.domain.usecase.recommendation.GetPropertyRecommendationsUseCase
import com.realestateassistant.pro.presentation.model.PropertyRecommendation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для управления рекомендациями объектов недвижимости.
 */
@HiltViewModel
class PropertyRecommendationsViewModel @Inject constructor(
    private val getPropertyRecommendationsUseCase: GetPropertyRecommendationsUseCase,
    private val clientUseCases: ClientUseCases
) : ViewModel() {
    
    // Состояние загрузки
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // Состояние ошибки
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    // Текущий клиент
    private val _client = MutableStateFlow<Client?>(null)
    val client: StateFlow<Client?> = _client
    
    // Список рекомендаций
    private val _recommendations = MutableStateFlow<List<PropertyRecommendation>>(emptyList())
    val recommendations: StateFlow<List<PropertyRecommendation>> = _recommendations
    
    // Фильтр для рекомендаций
    private val _filter = MutableStateFlow(PropertyFilter())
    val filter: StateFlow<PropertyFilter> = _filter
    
    // Отфильтрованные рекомендации
    private val _filteredRecommendations = MutableStateFlow<List<PropertyRecommendation>>(emptyList())
    val filteredRecommendations: StateFlow<List<PropertyRecommendation>> = _filteredRecommendations
    
    /**
     * Загружает клиента по ID и получает рекомендации для него.
     * 
     * @param clientId ID клиента
     */
    fun loadRecommendationsForClient(clientId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Загружаем клиента
                val clientResult = clientUseCases.getClient(clientId)
                
                if (clientResult.isFailure) {
                    _error.value = "Не удалось загрузить данные клиента"
                    _isLoading.value = false
                    return@launch
                }
                
                val client = clientResult.getOrNull()
                if (client == null) {
                    _error.value = "Клиент не найден"
                    _isLoading.value = false
                    return@launch
                }
                
                _client.value = client
                
                // Получаем рекомендации
                val recommendationsResult = getPropertyRecommendationsUseCase(client)
                
                if (recommendationsResult.isFailure) {
                    _error.value = "Не удалось получить рекомендации: ${recommendationsResult.exceptionOrNull()?.message}"
                    _isLoading.value = false
                    return@launch
                }
                
                val propertyScores = recommendationsResult.getOrNull() ?: emptyList()
                
                // Преобразуем в модель представления
                val recommendations = propertyScores.map { (property, score) ->
                    PropertyRecommendation(property, score)
                }
                
                _recommendations.value = recommendations
                applyFilter() // Применяем текущий фильтр
                
            } catch (e: Exception) {
                _error.value = "Произошла ошибка: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Обновляет фильтр и применяет его к рекомендациям.
     * 
     * @param filter Новый фильтр
     */
    fun updateFilter(filter: PropertyFilter) {
        _filter.value = filter
        applyFilter()
    }
    
    /**
     * Очищает текущий фильтр.
     */
    fun clearFilter() {
        _filter.value = PropertyFilter()
        applyFilter()
    }
    
    /**
     * Применяет текущий фильтр к списку рекомендаций.
     */
    private fun applyFilter() {
        val currentFilter = _filter.value
        val allRecommendations = _recommendations.value
        
        // Если нет активных фильтров, показываем все рекомендации
        if (!currentFilter.hasActiveFilters()) {
            _filteredRecommendations.value = allRecommendations
            return
        }
        
        // Применяем фильтры
        val filtered = allRecommendations.filter { recommendation ->
            val property = recommendation.property
            
            // Фильтр по типу недвижимости
            val matchesPropertyType = currentFilter.propertyTypes.isEmpty() || 
                currentFilter.propertyTypes.contains(property.propertyType)
            
            // Фильтр по району
            val matchesDistrict = currentFilter.districts.isEmpty() || 
                currentFilter.districts.contains(property.district)
            
            // Фильтр по количеству комнат
            val matchesRooms = (currentFilter.minRooms == null || property.roomsCount >= currentFilter.minRooms) &&
                (currentFilter.maxRooms == null || property.roomsCount <= currentFilter.maxRooms)
            
            // Фильтр по цене для длительной аренды
            val matchesPrice = if (property.monthlyRent != null) {
                (currentFilter.minPrice == null || property.monthlyRent >= currentFilter.minPrice) &&
                (currentFilter.maxPrice == null || property.monthlyRent <= currentFilter.maxPrice)
            } else if (property.dailyPrice != null) {
                (currentFilter.minPrice == null || property.dailyPrice >= currentFilter.minPrice) &&
                (currentFilter.maxPrice == null || property.dailyPrice <= currentFilter.maxPrice)
            } else {
                true
            }
            
            // Поиск по запросу
            val matchesSearch = currentFilter.searchQuery.isEmpty() ||
                property.address.contains(currentFilter.searchQuery, ignoreCase = true) ||
                property.description?.contains(currentFilter.searchQuery, ignoreCase = true) == true ||
                property.district.contains(currentFilter.searchQuery, ignoreCase = true) ||
                property.propertyType.contains(currentFilter.searchQuery, ignoreCase = true)
            
            // Объединяем условия
            matchesPropertyType && matchesDistrict && matchesRooms && matchesPrice && matchesSearch
        }
        
        _filteredRecommendations.value = filtered
    }
    
    /**
     * Обновляет поисковый запрос для фильтрации рекомендаций.
     * 
     * @param query Поисковый запрос
     */
    fun updateSearchQuery(query: String) {
        _filter.update { it.copy(searchQuery = query) }
        applyFilter()
    }
} 