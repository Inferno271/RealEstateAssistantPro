package com.realestateassistant.pro.domain.usecase.booking

import com.realestateassistant.pro.domain.model.Booking
import com.realestateassistant.pro.domain.repository.BookingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case для получения бронирований объекта недвижимости
 */
class GetPropertyBookingsUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    /**
     * Получает поток бронирований для указанного объекта недвижимости
     * 
     * @param propertyId ID объекта недвижимости
     * @return Flow со списком бронирований
     */
    operator fun invoke(propertyId: String): Flow<List<Booking>> {
        return bookingRepository.observeBookingsByProperty(propertyId)
    }
} 