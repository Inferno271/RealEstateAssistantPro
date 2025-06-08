package com.realestateassistant.pro.domain.usecase.booking

import com.realestateassistant.pro.domain.model.Booking
import com.realestateassistant.pro.domain.repository.BookingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case для получения бронирований объекта недвижимости в диапазоне дат
 */
class GetPropertyBookingsInDateRangeUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    /**
     * Получает поток бронирований для указанного объекта недвижимости в указанном диапазоне дат
     * 
     * @param propertyId ID объекта недвижимости
     * @param fromDate Начальная дата диапазона (timestamp)
     * @param toDate Конечная дата диапазона (timestamp)
     * @return Flow со списком бронирований
     */
    operator fun invoke(propertyId: String, fromDate: Long, toDate: Long): Flow<List<Booking>> {
        return bookingRepository.observeBookingsForPropertyInDateRange(propertyId, fromDate, toDate)
    }
} 