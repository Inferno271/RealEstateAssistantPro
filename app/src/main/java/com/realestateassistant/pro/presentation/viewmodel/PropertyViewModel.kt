package com.realestateassistant.pro.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.usecase.PropertyUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PropertyViewModel @Inject constructor(
    private val propertyUseCases: PropertyUseCases
) : ViewModel() {

    private val _properties = MutableStateFlow<List<Property>>(emptyList())
    val properties: StateFlow<List<Property>> get() = _properties

    init {
        loadProperties()
    }

    fun loadProperties() {
        viewModelScope.launch {
            val result = propertyUseCases.getAllProperties()
            result.onSuccess { list ->
                _properties.value = list
            }.onFailure { exception ->
                // Здесь можно обработать ошибку, например, вывести сообщение пользователю
            }
        }
    }

    fun addProperty(property: Property) {
        viewModelScope.launch {
            val result = propertyUseCases.addProperty(property)
            result.onSuccess { addedProperty ->
                // Обновляем список объектов, добавляя новый
                _properties.value = _properties.value + addedProperty
            }.onFailure { exception ->
                // Обработка ошибки
            }
        }
    }

    fun updateProperty(property: Property) {
        viewModelScope.launch {
            val result = propertyUseCases.updateProperty(property)
            result.onSuccess {
                // После успешного обновления можно перезагрузить список
                loadProperties()
            }.onFailure { exception ->
                // Обработка ошибки
            }
        }
    }

    fun deleteProperty(propertyId: String) {
        viewModelScope.launch {
            val result = propertyUseCases.deleteProperty(propertyId)
            result.onSuccess {
                // Обновляем список, удаляя объект с указанным ID
                _properties.value = _properties.value.filter { it.id != propertyId }
            }.onFailure { exception ->
                // Обработка ошибки
            }
        }
    }
} 