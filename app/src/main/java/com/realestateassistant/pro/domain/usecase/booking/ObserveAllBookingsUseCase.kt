package com.realestateassistant.pro.domain.usecase.booking

import com.realestateassistant.pro.domain.model.Booking
import com.realestateassistant.pro.domain.repository.BookingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase для наблюдения за всеми бронированиями
 */
class ObserveAllBookingsUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    operator fun invoke(): Flow<List<Booking>> {
        return repository.observeAllBookings()
    }
} 