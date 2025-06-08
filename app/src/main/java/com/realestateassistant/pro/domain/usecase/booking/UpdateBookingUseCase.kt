package com.realestateassistant.pro.domain.usecase.booking

import com.realestateassistant.pro.domain.model.Booking
import com.realestateassistant.pro.domain.repository.BookingRepository
import javax.inject.Inject

/**
 * Use case для обновления бронирования
 */
class UpdateBookingUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    /**
     * Обновляет существующее бронирование с проверкой на конфликты
     * 
     * @param booking Бронирование для обновления
     * @return Result с успехом или ошибкой
     */
    suspend operator fun invoke(booking: Booking): Result<Unit> {
        // Получаем текущее бронирование для проверки
        val currentBookingResult = bookingRepository.getBooking(booking.id)
        if (currentBookingResult.isFailure) {
            return Result.failure(currentBookingResult.exceptionOrNull() 
                ?: IllegalStateException("Не удалось найти бронирование для обновления"))
        }
        
        val currentBooking = currentBookingResult.getOrThrow()
        
        // Если даты изменились, проверяем наличие конфликтов
        if (currentBooking.startDate != booking.startDate || currentBooking.endDate != booking.endDate) {
            val hasConflicts = bookingRepository.hasBookingConflicts(
                propertyId = booking.propertyId,
                fromDate = booking.startDate,
                toDate = booking.endDate
            ).getOrNull() ?: false
            
            // Если есть конфликты, возвращаем ошибку
            if (hasConflicts) {
                return Result.failure(IllegalStateException("Выбранные даты уже заняты для этого объекта недвижимости"))
            }
        }
        
        // Если нет конфликтов, обновляем бронирование
        return bookingRepository.updateBooking(booking)
    }
} 