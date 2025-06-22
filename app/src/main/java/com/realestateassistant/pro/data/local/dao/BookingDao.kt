package com.realestateassistant.pro.data.local.dao

import androidx.room.*
import com.realestateassistant.pro.data.local.entity.BookingEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO для работы с бронированиями
 */
@Dao
interface BookingDao {
    /**
     * Вставляет новое бронирование
     * Если бронирование с таким id уже существует, оно будет заменено
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    /**
     * Вставляет список бронирований
     * Если бронирование с таким id уже существует, оно будет заменено
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookings(bookings: List<BookingEntity>)

    /**
     * Обновляет существующее бронирование
     */
    @Update
    suspend fun updateBooking(booking: BookingEntity)

    /**
     * Удаляет бронирование по его id
     */
    @Query("DELETE FROM bookings WHERE id = :bookingId")
    suspend fun deleteBooking(bookingId: String)

    /**
     * Получает бронирование по его id
     */
    @Query("SELECT * FROM bookings WHERE id = :bookingId")
    suspend fun getBooking(bookingId: String): BookingEntity?

    /**
     * Получает список всех бронирований
     */
    @Query("SELECT * FROM bookings")
    fun getAllBookings(): Flow<List<BookingEntity>>
    
    /**
     * Получает список всех бронирований для конкретного объекта недвижимости
     */
    @Query("SELECT * FROM bookings WHERE propertyId = :propertyId")
    fun getBookingsByProperty(propertyId: String): Flow<List<BookingEntity>>
    
    /**
     * Получает список всех бронирований для конкретного клиента
     */
    @Query("SELECT * FROM bookings WHERE clientId = :clientId")
    fun getBookingsByClient(clientId: String): Flow<List<BookingEntity>>
    
    /**
     * Получает список бронирований по статусу
     */
    @Query("SELECT * FROM bookings WHERE status = :status")
    fun getBookingsByStatus(status: String): Flow<List<BookingEntity>>
    
    /**
     * Получает список бронирований в указанном диапазоне дат
     */
    @Query("SELECT * FROM bookings WHERE (startDate BETWEEN :fromDate AND :toDate) OR (endDate BETWEEN :fromDate AND :toDate) OR (:fromDate BETWEEN startDate AND endDate)")
    fun getBookingsInDateRange(fromDate: Long, toDate: Long): Flow<List<BookingEntity>>
    
    /**
     * Получает список бронирований для объекта недвижимости в указанном диапазоне дат
     */
    @Query("SELECT * FROM bookings WHERE propertyId = :propertyId AND ((startDate BETWEEN :fromDate AND :toDate) OR (endDate BETWEEN :fromDate AND :toDate) OR (:fromDate BETWEEN startDate AND endDate))")
    fun getBookingsForPropertyInDateRange(propertyId: String, fromDate: Long, toDate: Long): Flow<List<BookingEntity>>
    
    /**
     * Проверяет, есть ли конфликты бронирований для указанного объекта в указанном диапазоне дат
     * Возвращает количество конфликтующих бронирований
     */
    @Query("SELECT COUNT(*) FROM bookings WHERE propertyId = :propertyId AND status != 'CANCELLED' AND status != 'EXPIRED' AND ((startDate BETWEEN :fromDate AND :toDate) OR (endDate BETWEEN :fromDate AND :toDate) OR (:fromDate BETWEEN startDate AND endDate))")
    suspend fun countBookingConflicts(propertyId: String, fromDate: Long, toDate: Long): Int
    
    /**
     * Проверяет, есть ли конфликты бронирований для указанного объекта в указанном диапазоне дат,
     * исключая указанное бронирование по id (для редактирования)
     * Возвращает количество конфликтующих бронирований
     */
    @Query("SELECT COUNT(*) FROM bookings WHERE propertyId = :propertyId AND id != :excludeBookingId AND status != 'CANCELLED' AND status != 'EXPIRED' AND ((startDate BETWEEN :fromDate AND :toDate) OR (endDate BETWEEN :fromDate AND :toDate) OR (:fromDate BETWEEN startDate AND endDate))")
    suspend fun countBookingConflictsExcludingBooking(propertyId: String, fromDate: Long, toDate: Long, excludeBookingId: String): Int
    
    /**
     * Обновляет статус бронирования
     */
    @Query("UPDATE bookings SET status = :status, updatedAt = :updatedAt WHERE id = :bookingId")
    suspend fun updateBookingStatus(bookingId: String, status: String, updatedAt: Long = System.currentTimeMillis())
    
    /**
     * Обновляет статус оплаты бронирования
     */
    @Query("UPDATE bookings SET paymentStatus = :paymentStatus, updatedAt = :updatedAt WHERE id = :bookingId")
    suspend fun updatePaymentStatus(bookingId: String, paymentStatus: String, updatedAt: Long = System.currentTimeMillis())
    
    /**
     * Получает количество бронирований в базе данных
     */
    @Query("SELECT COUNT(*) FROM bookings")
    suspend fun getBookingCount(): Int
    
    /**
     * Получает список бронирований, которые не синхронизированы
     */
    @Query("SELECT * FROM bookings WHERE isSynced = 0")
    suspend fun getUnsyncedBookings(): List<BookingEntity>

    /**
     * Обновляет статус синхронизации бронирования
     */
    @Query("UPDATE bookings SET isSynced = :isSynced WHERE id = :bookingId")
    suspend fun updateSyncStatus(bookingId: String, isSynced: Boolean)
    
    /**
     * Получает список активных бронирований (подтвержденные или активные)
     */
    @Query("SELECT * FROM bookings WHERE status IN ('CONFIRMED', 'ACTIVE')")
    fun getActiveBookings(): Flow<List<BookingEntity>>
    
    /**
     * Получает список предстоящих бронирований (дата начала в будущем)
     */
    @Query("SELECT * FROM bookings WHERE startDate > :currentTime AND status IN ('CONFIRMED', 'PENDING')")
    fun getUpcomingBookings(currentTime: Long = System.currentTimeMillis()): Flow<List<BookingEntity>>
    
    /**
     * Получает список прошедших бронирований (дата окончания в прошлом)
     */
    @Query("SELECT * FROM bookings WHERE endDate < :currentTime")
    fun getPastBookings(currentTime: Long = System.currentTimeMillis()): Flow<List<BookingEntity>>
    
    /**
     * Автоматически обновляет статусы бронирований на основе текущей даты
     * - Переводит CONFIRMED в ACTIVE, если текущая дата >= startDate
     * - Переводит ACTIVE в COMPLETED, если текущая дата > endDate
     * - Переводит PENDING в EXPIRED, если прошло более 48 часов с момента создания
     */
    @Query("UPDATE bookings SET status = CASE " +
           "WHEN status = 'CONFIRMED' AND :currentTime >= startDate THEN 'ACTIVE' " +
           "WHEN status = 'ACTIVE' AND :currentTime > endDate THEN 'COMPLETED' " +
           "WHEN status = 'PENDING' AND (:currentTime - createdAt) > 172800000 THEN 'EXPIRED' " +
           "ELSE status END, " +
           "updatedAt = CASE " +
           "WHEN status != CASE " +
           "WHEN status = 'CONFIRMED' AND :currentTime >= startDate THEN 'ACTIVE' " +
           "WHEN status = 'ACTIVE' AND :currentTime > endDate THEN 'COMPLETED' " +
           "WHEN status = 'PENDING' AND (:currentTime - createdAt) > 172800000 THEN 'EXPIRED' " +
           "ELSE status END " +
           "THEN :currentTime ELSE updatedAt END")
    suspend fun updateBookingStatusesAutomatically(currentTime: Long = System.currentTimeMillis())
    
    /**
     * Удаляет все бронирования из базы данных
     */
    @Query("DELETE FROM bookings")
    suspend fun deleteAllBookings()
} 