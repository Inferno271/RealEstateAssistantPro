package com.realestateassistant.pro.domain.usecase

import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.Flow

class AddProperty(private val repository: PropertyRepository) {
    suspend operator fun invoke(property: Property): Result<Property> {
        return repository.addProperty(property)
    }
}

class UpdateProperty(private val repository: PropertyRepository) {
    suspend operator fun invoke(property: Property): Result<Unit> {
        return repository.updateProperty(property)
    }
}

class DeleteProperty(private val repository: PropertyRepository) {
    suspend operator fun invoke(propertyId: String): Result<Unit> {
        return repository.deleteProperty(propertyId)
    }
}

class GetProperty(private val repository: PropertyRepository) {
    suspend operator fun invoke(propertyId: String): Result<Property> {
        return repository.getProperty(propertyId)
    }
}

class GetAllProperties(private val repository: PropertyRepository) {
    suspend operator fun invoke(): Result<List<Property>> {
        return repository.getAllProperties()
    }
}

class ObserveAllProperties(private val repository: PropertyRepository) {
    operator fun invoke(): Flow<List<Property>> {
        return repository.observeAllProperties()
    }
}

class ObservePropertiesByType(private val repository: PropertyRepository) {
    operator fun invoke(type: String): Flow<List<Property>> {
        return repository.observePropertiesByType(type)
    }
}

// Собираем все use case в один контейнер для удобства работы
data class PropertyUseCases(
    val addProperty: AddProperty,
    val updateProperty: UpdateProperty,
    val deleteProperty: DeleteProperty,
    val getProperty: GetProperty,
    val getAllProperties: GetAllProperties,
    val observeAllProperties: ObserveAllProperties,
    val observePropertiesByType: ObservePropertiesByType
) 