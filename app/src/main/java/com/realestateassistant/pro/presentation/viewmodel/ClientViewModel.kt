package com.realestateassistant.pro.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.model.RentalFilter
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

    init {
        loadClients()
        // Дополнительная проверка для отладки
        viewModelScope.launch {
            clientRepository.checkClientsInDatabase()
        }
    }

    fun loadClients() {
        viewModelScope.launch {
            clientUseCases.getAllClients().onSuccess { list ->
                println("DEBUG: ClientViewModel.loadClients - загружено клиентов: ${list.size}")
                println("DEBUG: ClientViewModel.loadClients - типы аренды: ${list.map { it.rentalType }}")
                _clients.value = list
                applyFilter(_currentFilter.value) // Применяем текущий фильтр к загруженным клиентам
            }.onFailure { exception ->
                println("DEBUG: ClientViewModel.loadClients - ошибка: ${exception.message}")
                // Здесь можно обработать ошибку, например, вывести сообщение пользователю
            }
        }
    }

    // Новый метод для установки фильтра
    fun setFilter(filter: RentalFilter) {
        println("DEBUG: ClientViewModel.setFilter - установка фильтра: $filter")
        if (_currentFilter.value != filter) {
            _currentFilter.value = filter
            applyFilter(filter)
        }
    }

    // Новый метод для применения фильтра
    private fun applyFilter(filter: RentalFilter) {
        println("DEBUG: ClientViewModel.applyFilter - применение фильтра: $filter")
        println("DEBUG: ClientViewModel.applyFilter - всего клиентов до фильтрации: ${_clients.value.size}")
        
        // Отладочная информация о типах аренды
        val rentalTypes = _clients.value.groupBy { it.rentalType }.mapValues { it.value.size }
        println("DEBUG: ClientViewModel.applyFilter - распределение типов аренды: $rentalTypes")
        
        _filteredClients.value = _clients.value.filter { client ->
            val result = when (filter) {
                RentalFilter.LONG_TERM -> 
                    client.rentalType.lowercase() == "длительная"
                RentalFilter.SHORT_TERM -> 
                    client.rentalType.lowercase() == "посуточная"
            }
            println("DEBUG: ClientViewModel.applyFilter - клиент ${client.id}: тип=${client.rentalType}, подходит для $filter: $result")
            result
        }
        
        // Выводим в консоль информацию о фильтрации для отладки
        println("DEBUG: Применен фильтр $filter")
        println("DEBUG: Всего клиентов: ${_clients.value.size}")
        println("DEBUG: Отфильтровано клиентов: ${_filteredClients.value.size}")
        println("DEBUG: Типы аренды клиентов: ${_clients.value.map { it.rentalType }}")
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