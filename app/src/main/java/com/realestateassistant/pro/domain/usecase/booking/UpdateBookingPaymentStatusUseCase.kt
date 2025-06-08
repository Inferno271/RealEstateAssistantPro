package com.realestateassistant.pro.domain.usecase.booking

import com.realestateassistant.pro.domain.model.PaymentStatus
import com.realestateassistant.pro.domain.repository.BookingRepository
import javax.inject.Inject

/**
 * Use case для обновления статуса оплаты бронирования
 */
class UpdateBookingPaymentStatusUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    /**
     * Обновляет статус оплаты бронирования
     * 
     * @param bookingId ID бронирования
     * @param paymentStatus Новый статус оплаты
     * @return Result с успехом или ошибкой
     */
    suspend operator fun invoke(bookingId: String, paymentStatus: PaymentStatus): Result<Unit> {
        return bookingRepository.updatePaymentStatus(bookingId, paymentStatus)
    }
} 