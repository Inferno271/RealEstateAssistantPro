package com.realestateassistant.pro.domain.usecase.booking

import com.realestateassistant.pro.domain.repository.BookingRepository
import javax.inject.Inject

/**
 * Use case для проверки конфликтов бронирования
 */
class CheckBookingConflictsUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    /**
     * Проверяет наличие конфликтов бронирования для указанного объекта недвижимости в указанном диапазоне дат
     * 
     * @param propertyId ID объекта недвижимости
     * @param fromDate Начальная дата диапазона (timestamp)
     * @param toDate Конечная дата диапазона (timestamp)
     * @return Result с булевым значением (true, если есть конфликты) или ошибкой
     */
    suspend operator fun invoke(propertyId: String, fromDate: Long, toDate: Long): Result<Boolean> {
        // Проверяем валидность дат
        if (fromDate >= toDate) {
            return Result.failure(IllegalArgumentException("Дата начала должна быть раньше даты окончания"))
        }
        
        return bookingRepository.hasBookingConflicts(propertyId, fromDate, toDate)
    }
} 