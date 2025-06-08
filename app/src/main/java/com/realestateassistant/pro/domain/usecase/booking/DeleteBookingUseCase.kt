package com.realestateassistant.pro.domain.usecase.booking

import com.realestateassistant.pro.domain.repository.BookingRepository
import javax.inject.Inject

/**
 * Use case для удаления бронирования
 */
class DeleteBookingUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    /**
     * Удаляет бронирование по его ID
     * 
     * @param bookingId ID бронирования для удаления
     * @return Result с успехом или ошибкой
     */
    suspend operator fun invoke(bookingId: String): Result<Unit> {
        return bookingRepository.deleteBooking(bookingId)
    }
} 