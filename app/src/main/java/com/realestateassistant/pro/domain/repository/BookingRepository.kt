package com.realestateassistant.pro.domain.repository

import com.realestateassistant.pro.domain.model.Booking
import com.realestateassistant.pro.domain.model.BookingStatus
import com.realestateassistant.pro.domain.model.PaymentStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Репозиторий для работы с бронированиями
 */
interface BookingRepository {
    /**
     * Добавляет новое бронирование
     * Возвращает добавленное бронирование с обновленным id, если операция успешна
     */
    suspend fun addBooking(booking: Booking): Result<Booking>

    /**
     * Обновляет существующее бронирование
     */
    suspend fun updateBooking(booking: Booking): Result<Unit>

    /**
     * Удаляет бронирование по его id
     */
    suspend fun deleteBooking(bookingId: String): Result<Unit>

    /**
     * Получает бронирование по его id
     */
    suspend fun getBooking(bookingId: String): Result<Booking>

    /**
     * Получает список всех бронирований
     */
    suspend fun getAllBookings(): Result<List<Booking>>
    
    /**
     * Наблюдает за всеми бронированиями
     */
    fun observeAllBookings(): Flow<List<Booking>>
    
    /**
     * Наблюдает за бронированиями для конкретного объекта недвижимости
     */
    fun observeBookingsByProperty(propertyId: String): Flow<List<Booking>>
    
    /**
     * Наблюдает за бронированиями для конкретного клиента
     */
    fun observeBookingsByClient(clientId: String): Flow<List<Booking>>
    
    /**
     * Наблюдает за бронированиями в указанном диапазоне дат
     */
    fun observeBookingsInDateRange(fromDate: Long, toDate: Long): Flow<List<Booking>>
    
    /**
     * Наблюдает за бронированиями для объекта недвижимости в указанном диапазоне дат
     */
    fun observeBookingsForPropertyInDateRange(propertyId: String, fromDate: Long, toDate: Long): Flow<List<Booking>>
    
    /**
     * Проверяет, есть ли конфликты бронирований для указанного объекта в указанном диапазоне дат
     * Возвращает true, если есть конфликты
     */
    suspend fun hasBookingConflicts(propertyId: String, fromDate: Long, toDate: Long): Result<Boolean>
    
    /**
     * Проверяет, есть ли конфликты бронирований для указанного объекта в указанном диапазоне дат,
     * исключая указанное бронирование (для редактирования)
     * Возвращает true, если есть конфликты
     */
    suspend fun hasBookingConflictsExcludingBooking(propertyId: String, fromDate: Long, toDate: Long, excludeBookingId: String): Result<Boolean>
    
    /**
     * Обновляет статус бронирования
     */
    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit>
    
    /**
     * Обновляет статус оплаты бронирования
     */
    suspend fun updatePaymentStatus(bookingId: String, paymentStatus: PaymentStatus): Result<Unit>
    
    /**
     * Наблюдает за активными бронированиями (подтвержденные или активные)
     */
    fun observeActiveBookings(): Flow<List<Booking>>
    
    /**
     * Наблюдает за предстоящими бронированиями (дата начала в будущем)
     */
    fun observeUpcomingBookings(): Flow<List<Booking>>
    
    /**
     * Наблюдает за прошедшими бронированиями (дата окончания в прошлом)
     */
    fun observePastBookings(): Flow<List<Booking>>
    
    /**
     * Автоматически обновляет статусы бронирований на основе текущей даты
     */
    suspend fun updateBookingStatusesAutomatically(): Result<Unit>
    
    /**
     * Синхронизирует данные между локальной базой данных и удаленным сервером
     */
    suspend fun syncBookings(): Result<Unit>
} 