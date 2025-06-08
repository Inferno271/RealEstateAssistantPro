package com.realestateassistant.pro.domain.usecase.booking

import com.realestateassistant.pro.core.result.Result
import com.realestateassistant.pro.domain.repository.BookingRepository
import javax.inject.Inject

/**
 * Use case для проверки конфликтов бронирования
 */
class HasBookingConflictsUseCase @Inject constructor(
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
            return Result.error("Дата начала должна быть раньше даты окончания")
        }
        
        // Преобразуем kotlin.Result в наш Result
        val kotlinResult = bookingRepository.hasBookingConflicts(propertyId, fromDate, toDate)
        return kotlinResult.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.error(it.message ?: "Неизвестная ошибка") }
        )
    }
} 