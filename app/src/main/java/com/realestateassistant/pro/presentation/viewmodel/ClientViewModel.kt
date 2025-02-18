package com.realestateassistant.pro.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.usecase.ClientUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val clientUseCases: ClientUseCases
) : ViewModel() {

    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    val clients: StateFlow<List<Client>> get() = _clients

    init {
        loadClients()
    }

    fun loadClients() {
        viewModelScope.launch {
            clientUseCases.getAllClients().onSuccess { list ->
                _clients.value = list
            }.onFailure { exception ->
                // Здесь можно обработать ошибку, например, вывести сообщение пользователю
            }
        }
    }

    fun addClient(client: Client) {
        viewModelScope.launch {
            clientUseCases.addClient(client).onSuccess { addedClient ->
                _clients.value = _clients.value + addedClient
            }.onFailure { exception ->
                // Обработка ошибки
            }
        }
    }

    fun updateClient(client: Client) {
        viewModelScope.launch {
            clientUseCases.updateClient(client).onSuccess {
                loadClients()
            }.onFailure { exception ->
                // Обработка ошибки
            }
        }
    }

    fun deleteClient(clientId: String) {
        viewModelScope.launch {
            clientUseCases.deleteClient(clientId).onSuccess {
                _clients.value = _clients.value.filter { it.id != clientId }
            }.onFailure { exception ->
                // Обработка ошибки
            }
        }
    }
} 