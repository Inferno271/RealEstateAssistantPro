package com.realestateassistant.pro.domain.usecase.booking

import com.realestateassistant.pro.domain.model.Booking
import com.realestateassistant.pro.domain.repository.BookingRepository
import javax.inject.Inject

/**
 * Use case для добавления нового бронирования
 */
class AddBookingUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    /**
     * Добавляет новое бронирование с проверкой на конфликты
     * 
     * @param booking Бронирование для добавления
     * @return Result с добавленным бронированием или ошибкой
     */
    suspend operator fun invoke(booking: Booking): Result<Booking> {
        // Проверяем наличие конфликтов с другими бронированиями
        val hasConflicts = bookingRepository.hasBookingConflicts(
            propertyId = booking.propertyId,
            fromDate = booking.startDate,
            toDate = booking.endDate
        ).getOrNull() ?: false
        
        // Если есть конфликты, возвращаем ошибку
        if (hasConflicts) {
            return Result.failure(IllegalStateException("Выбранные даты уже заняты для этого объекта недвижимости"))
        }
        
        // Если нет конфликтов, добавляем бронирование
        return bookingRepository.addBooking(booking)
    }
} 