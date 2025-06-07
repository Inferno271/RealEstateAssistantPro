package com.realestateassistant.pro.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.model.PropertyCharacteristicsConfig
import com.realestateassistant.pro.domain.model.PropertyTypeCharacteristics
import com.realestateassistant.pro.domain.model.RentalFilter
import com.realestateassistant.pro.domain.model.PropertyFilter
import com.realestateassistant.pro.domain.usecase.PropertyUseCases
import com.realestateassistant.pro.presentation.model.PropertyFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PropertyViewModel @Inject constructor(
    private val propertyUseCases: PropertyUseCases
) : ViewModel() {
    
    // Состояние для всех объектов
    private val _properties = MutableStateFlow<List<Property>>(emptyList())
    val properties: StateFlow<List<Property>> get() = _properties
    
    // Состояние для отфильтрованных объектов
    private val _filteredProperties = MutableStateFlow<List<Property>>(emptyList())
    val filteredProperties: StateFlow<List<Property>> get() = _filteredProperties
    
    // Текущий выбранный фильтр типа аренды
    private val _currentFilter = MutableStateFlow(RentalFilter.LONG_TERM)
    val currentFilter: StateFlow<RentalFilter> get() = _currentFilter
    
    // Текущий фильтр для объектов недвижимости
    private val _propertyFilter = MutableStateFlow(PropertyFilter())
    val propertyFilter: StateFlow<PropertyFilter> get() = _propertyFilter
    
    // Состояние для текущего просматриваемого объекта
    private val _selectedProperty = MutableStateFlow<Property?>(null)
    val selectedProperty: StateFlow<Property?> get() = _selectedProperty
    
    // Состояние загрузки
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading
    
    // Состояние ошибки
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error
    
    // Переменная для хранения задания наблюдения за объектами
    private var observePropertiesJob: Job? = null
    
    // Состояние для текущей конфигурации характеристик в зависимости от типа недвижимости
    private val _currentCharacteristicsConfig = MutableStateFlow(PropertyCharacteristicsConfig.DEFAULT)
    val currentCharacteristicsConfig: StateFlow<PropertyCharacteristicsConfig> get() = _currentCharacteristicsConfig
    
    // Состояние формы редактирования
    private val _propertyFormState = MutableStateFlow(PropertyFormState())
    val propertyFormState: StateFlow<PropertyFormState> get() = _propertyFormState

    init {
        // Отложенный старт наблюдения за объектами, чтобы не блокировать UI при запуске
        viewModelScope.launch {
            kotlinx.coroutines.delay(500) // Задержка 500мс для запуска UI
            startObservingProperties()
        }
    }
    
    // Метод для начала наблюдения за объектами из базы данных
    private fun startObservingProperties() {
        observePropertiesJob?.cancel()
        observePropertiesJob = viewModelScope.launch {
            propertyUseCases.observeAllProperties().collectLatest { propertiesList ->
                _properties.value = propertiesList
                applyFilters()
            }
        }
    }

    // Метод для загрузки объектов при необходимости
    fun loadProperties() {
        // Если наблюдение активно, просто применим текущий фильтр снова
        applyFilters()
    }
    
    // Метод для изменения текущего фильтра
    fun setFilter(filter: RentalFilter) {
        _currentFilter.value = filter
        applyFilters()
    }
    
    // Применение фильтров к списку объектов
    private fun applyFilters() {
        val filter = _currentFilter.value
        val currentPropertyFilter = _propertyFilter.value
        
        // Проверяем, что у нас есть объекты для фильтрации
        if (_properties.value.isEmpty()) {
            _filteredProperties.value = emptyList()
            return
        }
        
        // Логируем начало фильтрации для отладки
        println("DEBUG: Начинаем фильтрацию. Всего объектов: ${_properties.value.size}")
        println("DEBUG: Текущий тип аренды: $filter")
        println("DEBUG: Активные фильтры: ${currentPropertyFilter.hasActiveFilters()}")
        
        // Сначала фильтруем по типу аренды
        var filtered = when (filter) {
            RentalFilter.LONG_TERM -> _properties.value.filter { 
                it.monthlyRent != null || it.winterMonthlyRent != null || it.summerMonthlyRent != null 
            }
            RentalFilter.SHORT_TERM -> _properties.value.filter { it.dailyPrice != null }
        }
        
        println("DEBUG: После фильтрации по типу аренды: ${filtered.size} объектов")
        
        // Применяем дополнительные фильтры объектов
        
        // Фильтр по типу недвижимости
        if (currentPropertyFilter.propertyTypes.isNotEmpty()) {
            filtered = filtered.filter { property ->
                currentPropertyFilter.propertyTypes.contains(property.propertyType)
            }
            println("DEBUG: После фильтрации по типу недвижимости: ${filtered.size} объектов")
        }
        
        // Фильтр по количеству комнат
        if (currentPropertyFilter.minRooms != null) {
            filtered = filtered.filter { property ->
                property.roomsCount >= currentPropertyFilter.minRooms
            }
            println("DEBUG: После фильтрации по мин. кол-ву комнат: ${filtered.size} объектов")
        }
        
        if (currentPropertyFilter.maxRooms != null) {
            filtered = filtered.filter { property ->
                property.roomsCount <= currentPropertyFilter.maxRooms
            }
            println("DEBUG: После фильтрации по макс. кол-ву комнат: ${filtered.size} объектов")
        }
        
        // Фильтр по цене
        if (currentPropertyFilter.minPrice != null) {
            filtered = filtered.filter { property ->
                val price = when (filter) {
                    RentalFilter.LONG_TERM -> property.monthlyRent ?: 0.0
                    RentalFilter.SHORT_TERM -> property.dailyPrice ?: 0.0
                }
                price >= currentPropertyFilter.minPrice
            }
            println("DEBUG: После фильтрации по мин. цене: ${filtered.size} объектов")
        }
        
        if (currentPropertyFilter.maxPrice != null) {
            filtered = filtered.filter { property ->
                val price = when (filter) {
                    RentalFilter.LONG_TERM -> property.monthlyRent ?: Double.MAX_VALUE
                    RentalFilter.SHORT_TERM -> property.dailyPrice ?: Double.MAX_VALUE
                }
                price <= currentPropertyFilter.maxPrice
            }
            println("DEBUG: После фильтрации по макс. цене: ${filtered.size} объектов")
        }
        
        // Фильтр по району
        if (currentPropertyFilter.districts.isNotEmpty()) {
            filtered = filtered.filter { property ->
                currentPropertyFilter.districts.contains(property.district)
            }
            println("DEBUG: После фильтрации по району: ${filtered.size} объектов")
        }
        
        // Поиск по запросу
        if (currentPropertyFilter.searchQuery.isNotEmpty()) {
            val query = currentPropertyFilter.searchQuery.lowercase()
            filtered = filtered.filter { property ->
                property.address.lowercase().contains(query) ||
                property.description?.lowercase()?.contains(query) == true ||
                property.contactName.lowercase().contains(query) ||
                property.district.lowercase().contains(query) ||
                property.propertyType.lowercase().contains(query) ||
                // Расширенные поля поиска
                (property.nearbyObjects.any { it.lowercase().contains(query) }) ||
                (property.views.any { it.lowercase().contains(query) }) ||
                (property.layout.lowercase().contains(query)) ||
                (property.repairState.lowercase().contains(query)) ||
                (property.bathroomType.lowercase().contains(query)) ||
                (property.heatingType.lowercase().contains(query)) ||
                (property.parkingType?.lowercase()?.contains(query) == true) ||
                (property.amenities.any { it.lowercase().contains(query) }) ||
                (property.allowedPetTypes.any { it.lowercase().contains(query) }) ||
                (property.minRentPeriod?.lowercase()?.contains(query) == true) ||
                (property.additionalExpenses?.lowercase()?.contains(query) == true) ||
                (property.longTermRules?.lowercase()?.contains(query) == true) ||
                (property.shortTermRules?.lowercase()?.contains(query) == true) ||
                (property.additionalServices?.lowercase()?.contains(query) == true) ||
                (property.cleaningDetails?.lowercase()?.contains(query) == true) ||
                (property.additionalComments?.lowercase()?.contains(query) == true) ||
                (property.additionalContactInfo?.lowercase()?.contains(query) == true) ||
                (property.poolType.lowercase().contains(query) && property.poolType.isNotEmpty())
            }
            println("DEBUG: После поиска по запросу: ${filtered.size} объектов")
        }
        
        // Обновляем список отфильтрованных объектов
        _filteredProperties.value = filtered
    }

    /**
     * Обновляет фильтр объектов
     */
    fun updatePropertyFilter(filter: PropertyFilter) {
        println("DEBUG: Обновляем фильтр объектов: $filter")
        _propertyFilter.value = filter
        // Применяем обновленные фильтры
        applyFilters()
    }
    
    /**
     * Очищает все фильтры объектов
     */
    fun clearPropertyFilters() {
        println("DEBUG: Очищаем все фильтры объектов")
        _propertyFilter.value = PropertyFilter()
        applyFilters()
    }
    
    /**
     * Обновляет поисковый запрос для объектов
     */
    fun updateSearchQuery(query: String) {
        println("DEBUG: Обновляем поисковый запрос: $query")
        _propertyFilter.value = _propertyFilter.value.copy(searchQuery = query)
        applyFilters()
    }

    fun addProperty(property: Property) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = propertyUseCases.addProperty(property)
            _isLoading.value = false
            
            result.onSuccess { addedProperty ->
                // При использовании Flow из Room, база данных сама уведомит нас об изменениях
                // Но для демонстрации успешного добавления, можно обновить список
                val currentList = _properties.value.toMutableList()
                if (!currentList.any { it.id == addedProperty.id }) {
                    currentList.add(addedProperty)
                    _properties.value = currentList
                    applyFilters()
                }
            }.onFailure { exception ->
                _error.value = exception.message ?: "Не удалось добавить объект недвижимости"
            }
        }
    }

    fun updateProperty(property: Property) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = propertyUseCases.updateProperty(property)
            _isLoading.value = false
            
            result.onSuccess {
                // При использовании Flow обновление произойдет автоматически через наблюдение
                // Так же обновляем выбранный объект, если он был обновлен
                if (_selectedProperty.value?.id == property.id) {
                    _selectedProperty.value = property
                }
            }.onFailure { exception ->
                _error.value = exception.message ?: "Не удалось обновить объект недвижимости"
            }
        }
    }

    fun deleteProperty(propertyId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = propertyUseCases.deleteProperty(propertyId)
            _isLoading.value = false
            
            result.onSuccess {
                // При использовании Flow обновление списка произойдет автоматически
                // Но если выбранный объект был удален, его нужно очистить
                if (_selectedProperty.value?.id == propertyId) {
                    _selectedProperty.value = null
                }
            }.onFailure { exception ->
                _error.value = exception.message ?: "Не удалось удалить объект недвижимости"
            }
        }
    }
    
    // Метод для загрузки детальной информации об объекте по ID
    fun loadPropertyDetails(propertyId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            // Сначала пробуем найти объект в локальном кэше
            val cachedProperty = _properties.value.find { it.id == propertyId }
            if (cachedProperty != null) {
                _selectedProperty.value = cachedProperty
                _isLoading.value = false
                return@launch
            }
            
            // Если нет в кэше, запрашиваем из репозитория
            val result = propertyUseCases.getProperty(propertyId)
            _isLoading.value = false
            
            result.onSuccess { property ->
                _selectedProperty.value = property
            }.onFailure { exception ->
                _error.value = exception.message ?: "Не удалось загрузить детали объекта"
            }
        }
    }
    
    // Очистка состояния выбранного объекта
    fun clearSelectedProperty() {
        _selectedProperty.value = null
    }
    
    /**
     * Обновляет конфигурацию характеристик недвижимости на основе выбранного типа
     */
    fun updateCharacteristicsConfig(propertyType: String) {
        _currentCharacteristicsConfig.value = PropertyTypeCharacteristics.getCharacteristicsForType(propertyType)
    }
    
    /**
     * Обновляет состояние формы
     */
    fun updateFormState(formState: PropertyFormState) {
        _propertyFormState.value = formState
        
        // Если изменяется тип недвижимости, обновляем конфигурацию характеристик
        if (formState.propertyType.isNotEmpty()) {
            updateCharacteristicsConfig(formState.propertyType)
        }
    }
    
    /**
     * Создает форму редактирования на основе выбранного объекта недвижимости
     */
    fun createFormFromProperty(property: Property) {
        val formState = PropertyFormState(
            isLongTerm = property.monthlyRent != null,
            contactName = property.contactName,
            contactPhone = property.contactPhone,
            additionalContactInfo = property.additionalContactInfo ?: "",
            propertyType = property.propertyType,
            address = property.address,
            district = property.district,
            nearbyObjects = property.nearbyObjects,
            views = property.views,
            area = property.area.toString(),
            roomsCount = property.roomsCount.toString(),
            isStudio = property.isStudio,
            layout = property.layout,
            floor = property.floor.toString(),
            totalFloors = property.totalFloors.toString(),
            description = property.description ?: "",
            management = property.management,
            // Новые поля для разных типов недвижимости
            levelsCount = property.levelsCount.toString(),
            landArea = property.landArea.toString(),
            hasGarage = property.hasGarage,
            garageSpaces = property.garageSpaces.toString(),
            hasBathhouse = property.hasBathhouse,
            hasPool = property.hasPool,
            poolType = property.poolType,
            // остальные поля
            // ... rest of the conversion ...
        )
        
        _propertyFormState.value = formState
        updateCharacteristicsConfig(property.propertyType)
    }
    
    // Сбрасывает состояние формы к значениям по умолчанию
    fun resetFormState() {
        _propertyFormState.value = PropertyFormState()
        _currentCharacteristicsConfig.value = PropertyCharacteristicsConfig.DEFAULT
    }
    
    override fun onCleared() {
        super.onCleared()
        observePropertiesJob?.cancel()
    }
} 