package com.realestateassistant.pro.domain.usecase.property

import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.model.PropertyStatus
import com.realestateassistant.pro.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Usecase для получения объектов недвижимости по их статусу
 */
class GetPropertiesByStatusUseCase @Inject constructor(
    private val propertyRepository: PropertyRepository
) {
    /**
     * Возвращает Flow с объектами недвижимости, имеющими указанный статус
     */
    operator fun invoke(status: PropertyStatus): Flow<List<Property>> {
        return propertyRepository.observePropertiesByStatus(status)
    }
} 