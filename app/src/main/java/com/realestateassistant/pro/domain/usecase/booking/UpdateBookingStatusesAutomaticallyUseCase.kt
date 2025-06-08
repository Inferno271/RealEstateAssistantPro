package com.realestateassistant.pro.domain.usecase.booking

import com.realestateassistant.pro.domain.repository.BookingRepository
import com.realestateassistant.pro.domain.model.BookingStatus
import javax.inject.Inject
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Use case для автоматического обновления статусов бронирований
 */
class UpdateBookingStatusesAutomaticallyUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    /**
     * Автоматически обновляет статусы бронирований на основе текущей даты:
     * - Переводит CONFIRMED в ACTIVE, если текущая дата >= startDate
     * - Переводит ACTIVE в COMPLETED, если текущая дата > endDate
     * - Переводит PENDING в EXPIRED, если прошло более 48 часов с момента создания
     * - Добавляет дополнительные проверки для других статусов
     * 
     * @return Result с успехом или ошибкой
     */
    suspend operator fun invoke(): Result<Unit> {
        val currentTime = System.currentTimeMillis()
        
        // Проверяем и получаем все бронирования для обработки
        val allBookingsResult = bookingRepository.getAllBookings()
        if (allBookingsResult.isFailure) {
            return Result.failure(allBookingsResult.exceptionOrNull() ?: Exception("Не удалось получить бронирования"))
        }
        
        val bookings = allBookingsResult.getOrThrow()
        var hasErrors = false
        
        // Обрабатываем каждое бронирование
        bookings.forEach { booking ->
            try {
                val shouldUpdate = when (booking.status) {
                    BookingStatus.PENDING -> {
                        // Если прошло более 48 часов с момента создания без подтверждения
                        val hoursFromCreation = ChronoUnit.HOURS.between(
                            Instant.ofEpochMilli(booking.createdAt),
                            Instant.ofEpochMilli(currentTime)
                        )
                        hoursFromCreation > 48
                    }
                    BookingStatus.CONFIRMED -> {
                        // Если наступила дата заезда
                        currentTime >= booking.startDate
                    }
                    BookingStatus.ACTIVE -> {
                        // Если дата выезда уже прошла
                        currentTime > booking.endDate
                    }
                    else -> false
                }
                
                if (shouldUpdate) {
                    val newStatus = when (booking.status) {
                        BookingStatus.PENDING -> BookingStatus.EXPIRED
                        BookingStatus.CONFIRMED -> BookingStatus.ACTIVE
                        BookingStatus.ACTIVE -> BookingStatus.COMPLETED
                        else -> booking.status
                    }
                    
                    val updateResult = bookingRepository.updateBookingStatus(booking.id, newStatus)
                    if (updateResult.isFailure) {
                        hasErrors = true
                    }
                }
            } catch (e: Exception) {
                hasErrors = true
            }
        }
        
        return if (hasErrors) {
            Result.failure(Exception("Произошли ошибки при обновлении некоторых бронирований"))
        } else {
            Result.success(Unit)
        }
    }
} 