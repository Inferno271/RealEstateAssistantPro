package com.realestateassistant.pro.domain.usecase.booking

import com.realestateassistant.pro.domain.model.BookingStatus
import com.realestateassistant.pro.domain.repository.BookingRepository
import javax.inject.Inject

/**
 * Use case для обновления статуса бронирования
 */
class UpdateBookingStatusUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    /**
     * Обновляет статус бронирования
     * 
     * @param bookingId ID бронирования
     * @param status Новый статус бронирования
     * @return Result с успехом или ошибкой
     */
    suspend operator fun invoke(bookingId: String, status: BookingStatus): Result<Unit> {
        return bookingRepository.updateBookingStatus(bookingId, status)
    }
} 