package com.realestateassistant.pro.domain.usecase.booking

import com.realestateassistant.pro.domain.model.Booking
import com.realestateassistant.pro.domain.repository.BookingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase для наблюдения за бронированиями конкретного объекта недвижимости
 */
class ObserveBookingsByPropertyUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    operator fun invoke(propertyId: String): Flow<List<Booking>> {
        return repository.observeBookingsByProperty(propertyId)
    }
} 