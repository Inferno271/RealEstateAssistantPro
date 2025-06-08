package com.realestateassistant.pro.domain.usecase.booking

import com.realestateassistant.pro.domain.model.Booking
import com.realestateassistant.pro.domain.repository.BookingRepository
import javax.inject.Inject

/**
 * Use case для получения бронирования по ID
 */
class GetBookingUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    /**
     * Получает бронирование по его ID
     * 
     * @param bookingId ID бронирования
     * @return Result с найденным бронированием или ошибкой
     */
    suspend operator fun invoke(bookingId: String): Result<Booking> {
        return bookingRepository.getBooking(bookingId)
    }
} 