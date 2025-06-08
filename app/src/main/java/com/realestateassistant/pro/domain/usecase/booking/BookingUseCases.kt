package com.realestateassistant.pro.domain.usecase.booking

import javax.inject.Inject

/**
 * Класс, объединяющий все use cases для бронирований
 */
data class BookingUseCases @Inject constructor(
    val addBookingUseCase: AddBookingUseCase,
    val updateBookingUseCase: UpdateBookingUseCase,
    val deleteBookingUseCase: DeleteBookingUseCase,
    val getBookingUseCase: GetBookingUseCase,
    val getPropertyBookingsUseCase: GetPropertyBookingsUseCase,
    val observeBookingsByPropertyUseCase: ObserveBookingsByPropertyUseCase,
    val observeAllBookingsUseCase: ObserveAllBookingsUseCase,
    val getPropertyBookingsInDateRangeUseCase: GetPropertyBookingsInDateRangeUseCase,
    val hasBookingConflictsUseCase: HasBookingConflictsUseCase,
    val updateBookingStatusUseCase: UpdateBookingStatusUseCase,
    val updateBookingPaymentStatusUseCase: UpdateBookingPaymentStatusUseCase,
    val updateBookingStatusesAutomaticallyUseCase: UpdateBookingStatusesAutomaticallyUseCase
) 