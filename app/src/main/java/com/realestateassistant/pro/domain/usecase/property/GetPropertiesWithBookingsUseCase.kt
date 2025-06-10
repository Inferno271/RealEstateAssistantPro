package com.realestateassistant.pro.domain.usecase.property

import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Usecase для получения объектов недвижимости с активными или предстоящими бронированиями
 */
class GetPropertiesWithBookingsUseCase @Inject constructor(
    private val propertyRepository: PropertyRepository
) {
    /**
     * Возвращает Flow с объектами недвижимости, имеющими активные бронирования (клиенты заселены)
     */
    fun getWithActiveBookings(): Flow<List<Property>> {
        return propertyRepository.observePropertiesWithActiveBookings()
    }
    
    /**
     * Возвращает Flow с объектами недвижимости, имеющими предстоящие бронирования (забронированы, но клиенты еще не заселены)
     */
    fun getWithUpcomingBookings(): Flow<List<Property>> {
        return propertyRepository.observePropertiesWithUpcomingBookings()
    }
    
    /**
     * Возвращает Flow с объектами недвижимости, доступными для бронирования в указанном диапазоне дат
     */
    fun getAvailableInDateRange(startDate: Long, endDate: Long): Flow<List<Property>> {
        return propertyRepository.getAvailableProperties(startDate, endDate)
    }
} 