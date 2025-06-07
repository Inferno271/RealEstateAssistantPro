package com.realestateassistant.pro.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.model.RentalFilter
import com.realestateassistant.pro.domain.model.ClientFilter
import com.realestateassistant.pro.domain.usecase.ClientUseCases
import com.realestateassistant.pro.data.repository.ClientRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val clientUseCases: ClientUseCases,
    private val clientRepository: ClientRepositoryImpl
) : ViewModel() {

    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    val clients: StateFlow<List<Client>> get() = _clients
    
    // Новое состояние для отфильтрованных клиентов
    private val _filteredClients = MutableStateFlow<List<Client>>(emptyList())
    val filteredClients: StateFlow<List<Client>> get() = _filteredClients
    
    // Состояние для текущего фильтра
    private val _currentFilter = MutableStateFlow(RentalFilter.LONG_TERM)
    val currentFilter: StateFlow<RentalFilter> get() = _currentFilter

    // Состояние для фильтра клиентов
    private val _clientFilter = MutableStateFlow(ClientFilter())
    val clientFilter: StateFlow<ClientFilter> get() = _clientFilter

    init {
        loadClients()
        // Дополнительная проверка для отладки
        viewModelScope.launch {
            clientRepository.checkClientsInDatabase()
        }
    }

    fun loadClients() {
        viewModelScope.launch {
            try {
                clientUseCases.getAllClients().onSuccess { clientList ->
                    _clients.value = clientList
                    applyFilter(_currentFilter.value)
                }.onFailure { error ->
                    // Обработка ошибки загрузки клиентов
                    println("ERROR: Ошибка при загрузке клиентов: ${error.message}")
                }
            } catch (e: Exception) {
                // Обработка исключения
                println("ERROR: Исключение при загрузке клиентов: ${e.message}")
            }
        }
    }

    // Новый метод для установки фильтра
    fun setFilter(filter: RentalFilter) {
        if (_currentFilter.value != filter) {
            _currentFilter.value = filter
            applyFilter(filter)
        }
    }

    // Новый метод для применения фильтра
    private fun applyFilter(filter: RentalFilter) {
        val allClients = _clients.value
        
        // Сначала фильтруем по типу аренды
        val filteredByRentalType = when (filter) {
            RentalFilter.LONG_TERM -> allClients.filter { it.rentalType.lowercase() == "длительная" }
            RentalFilter.SHORT_TERM -> allClients.filter { it.rentalType.lowercase() == "посуточная" }
        }
        
        // Затем применяем все остальные фильтры из ClientFilter к этому списку
        val currentClientFilter = _clientFilter.value
        val finalFiltered = filteredByRentalType.filter { client ->
            // Применяем фильтр типа аренды, если выбраны типы
            val matchesRentalTypes = currentClientFilter.rentalTypes.isEmpty() || 
                (client.rentalType in currentClientFilter.rentalTypes)
            
            // Применяем фильтр состава семьи
            val matchesFamilyComposition = currentClientFilter.familyCompositions.isEmpty() ||
                (client.familyComposition != null && client.familyComposition in currentClientFilter.familyCompositions)
            
            // Применяем фильтр уровня срочности
            val matchesUrgencyLevel = currentClientFilter.urgencyLevels.isEmpty() ||
                (client.urgencyLevel != null && client.urgencyLevel in currentClientFilter.urgencyLevels)
            
            // Применяем фильтр типа недвижимости
            val matchesPropertyType = currentClientFilter.propertyTypes.isEmpty() ||
                (client.desiredPropertyType != null && client.desiredPropertyType in currentClientFilter.propertyTypes)
            
            // Применяем фильтр района
            val matchesDistrict = currentClientFilter.districts.isEmpty() ||
                (client.preferredDistrict != null && client.preferredDistrict in currentClientFilter.districts)
            
            // Применяем фильтр количества проживающих
            val matchesPeopleCount = if (currentClientFilter.peopleCountRanges.isEmpty()) {
                true
            } else {
                val peopleCount = client.peopleCount ?: 0
                currentClientFilter.peopleCountRanges.any { range ->
                    when (range) {
                        "1 человек" -> peopleCount == 1
                        "2 человека" -> peopleCount == 2
                        "3-4 человека" -> peopleCount in 3..4
                        "5+ человек" -> peopleCount >= 5
                        else -> false
                    }
                }
            }
            
            // Применяем фильтр бюджета для длительной аренды
            val matchesLongTermBudget = if (client.rentalType.lowercase() == "длительная") {
                // Используем longTermBudgetMin для минимального и longTermBudgetMax для максимального бюджета
                val clientMinBudget = client.longTermBudgetMin ?: 0.0
                val clientMaxBudget = client.longTermBudgetMax ?: Double.MAX_VALUE
                
                // Проверяем, попадает ли бюджет клиента в фильтр
                val minMatch = currentClientFilter.minLongTermBudget == null || 
                              (clientMinBudget != 0.0 && clientMinBudget >= currentClientFilter.minLongTermBudget)
                val maxMatch = currentClientFilter.maxLongTermBudget == null || 
                              (clientMaxBudget != Double.MAX_VALUE && clientMaxBudget <= currentClientFilter.maxLongTermBudget)
                
                minMatch && maxMatch
            } else true
            
            // Применяем фильтр бюджета для посуточной аренды
            val matchesShortTermBudget = if (client.rentalType.lowercase() == "посуточная") {
                // Используем shortTermBudgetMin для минимального и shortTermBudgetMax для максимального бюджета
                val clientMinBudget = client.shortTermBudgetMin ?: 0.0
                val clientMaxBudget = client.shortTermBudgetMax ?: Double.MAX_VALUE
                
                // Проверяем, попадает ли бюджет клиента в фильтр
                val minMatch = currentClientFilter.minShortTermBudget == null || 
                              (clientMinBudget != 0.0 && clientMinBudget >= currentClientFilter.minShortTermBudget)
                val maxMatch = currentClientFilter.maxShortTermBudget == null || 
                              (clientMaxBudget != Double.MAX_VALUE && clientMaxBudget <= currentClientFilter.maxShortTermBudget)
                
                minMatch && maxMatch
            } else true
            
            // Применяем текстовый поиск
            val matchesSearch = currentClientFilter.searchQuery.isEmpty() || 
                client.fullName.contains(currentClientFilter.searchQuery, ignoreCase = true) ||
                client.phone.any { it.contains(currentClientFilter.searchQuery) } ||
                client.comment.contains(currentClientFilter.searchQuery, ignoreCase = true) ||
                (client.additionalComments?.contains(currentClientFilter.searchQuery, ignoreCase = true) ?: false) ||
                // Расширенные поля поиска
                (client.preferredDistrict?.contains(currentClientFilter.searchQuery, ignoreCase = true) ?: false) ||
                (client.desiredPropertyType?.contains(currentClientFilter.searchQuery, ignoreCase = true) ?: false) ||
                (client.occupation?.contains(currentClientFilter.searchQuery, ignoreCase = true) ?: false) ||
                (client.preferredAddress?.contains(currentClientFilter.searchQuery, ignoreCase = true) ?: false) ||
                (client.urgencyLevel?.contains(currentClientFilter.searchQuery, ignoreCase = true) ?: false) ||
                (client.preferredRepairState?.contains(currentClientFilter.searchQuery, ignoreCase = true) ?: false) ||
                (client.preferredBathroomType?.contains(currentClientFilter.searchQuery, ignoreCase = true) ?: false) ||
                (client.preferredHeatingType?.contains(currentClientFilter.searchQuery, ignoreCase = true) ?: false) ||
                (client.preferredParkingType?.contains(currentClientFilter.searchQuery, ignoreCase = true) ?: false) ||
                (client.additionalRequirements?.contains(currentClientFilter.searchQuery, ignoreCase = true) ?: false) ||
                client.preferredAmenities.any { it.contains(currentClientFilter.searchQuery, ignoreCase = true) } ||
                client.preferredViews.any { it.contains(currentClientFilter.searchQuery, ignoreCase = true) } ||
                client.preferredNearbyObjects.any { it.contains(currentClientFilter.searchQuery, ignoreCase = true) } ||
                client.petTypes.any { it.contains(currentClientFilter.searchQuery, ignoreCase = true) } ||
                client.priorityCriteria.any { it.contains(currentClientFilter.searchQuery, ignoreCase = true) }
            
            // Объединяем условия фильтрации
            matchesRentalTypes && 
            matchesFamilyComposition && 
            matchesUrgencyLevel && 
            matchesPropertyType && 
            matchesDistrict &&
            matchesPeopleCount && 
            matchesLongTermBudget &&
            matchesShortTermBudget &&
            matchesSearch
        }
        
        // Обновляем список отфильтрованных клиентов
        _filteredClients.value = finalFiltered
        
        // Выводим в консоль информацию о фильтрации для отладки
        println("DEBUG: Применен фильтр $filter и клиентский фильтр: ${_clientFilter.value}")
        println("DEBUG: Всего клиентов: ${allClients.size}")
        println("DEBUG: Отфильтрованных по типу аренды: ${filteredByRentalType.size}")
        println("DEBUG: Финально отфильтрованных: ${finalFiltered.size}")
        println("DEBUG: Применены фильтры бюджета: длит. от ${currentClientFilter.minLongTermBudget} до ${currentClientFilter.maxLongTermBudget}, " +
                "посут. от ${currentClientFilter.minShortTermBudget} до ${currentClientFilter.maxShortTermBudget}")
    }

    /**
     * Обновляет поисковый запрос для клиентов
     */
    fun updateSearchQuery(query: String) {
        val updatedFilter = _clientFilter.value.copy(searchQuery = query)
        _clientFilter.value = updatedFilter
        applyFilter(_currentFilter.value)
        
        // Отладочная информация
        println("DEBUG: ClientViewModel - обновлен поисковый запрос: '$query'")
        println("DEBUG: ClientViewModel - текущий фильтр: ${_clientFilter.value}")
    }
    
    /**
     * Очищает все фильтры клиентов
     */
    fun clearClientFilters() {
        _clientFilter.value = ClientFilter()
        applyFilter(_currentFilter.value)
    }

    /**
     * Обновляет фильтры клиентов
     */
    fun updateClientFilter(filter: ClientFilter) {
        _clientFilter.value = filter
        applyFilter(_currentFilter.value)
    }

    fun addClient(client: Client) {
        viewModelScope.launch {
            clientUseCases.addClient(client).onSuccess { addedClient ->
                _clients.value = _clients.value + addedClient
                applyFilter(_currentFilter.value) // Применяем фильтр после добавления
            }.onFailure { exception ->
                // Обработка ошибки
            }
        }
    }

    fun updateClient(client: Client) {
        viewModelScope.launch {
            clientUseCases.updateClient(client).onSuccess {
                loadClients() // Загружаем клиентов заново и применяем фильтр в методе loadClients
            }.onFailure { exception ->
                // Обработка ошибки
            }
        }
    }

    fun deleteClient(clientId: String) {
        viewModelScope.launch {
            clientUseCases.deleteClient(clientId).onSuccess {
                _clients.value = _clients.value.filter { it.id != clientId }
                applyFilter(_currentFilter.value) // Применяем фильтр после удаления
            }.onFailure { exception ->
                // Обработка ошибки
            }
        }
    }
} 