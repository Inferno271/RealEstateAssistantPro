package com.realestateassistant.pro.data.repository

import com.realestateassistant.pro.data.local.dao.BookingDao
import com.realestateassistant.pro.data.mapper.BookingMapper
import com.realestateassistant.pro.domain.model.Booking
import com.realestateassistant.pro.domain.model.BookingStatus
import com.realestateassistant.pro.domain.model.PaymentStatus
import com.realestateassistant.pro.domain.repository.BookingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.util.*
import com.realestateassistant.pro.domain.usecase.property.UpdatePropertyStatusesUseCase

/**
 * Реализация репозитория для работы с бронированиями
 */
class BookingRepositoryImpl @Inject constructor(
    private val bookingDao: BookingDao,
    private val updatePropertyStatusesUseCase: UpdatePropertyStatusesUseCase
) : BookingRepository {
    
    override suspend fun addBooking(booking: Booking): Result<Booking> = withContext(Dispatchers.IO) {
        try {
            Timber.d("Добавление нового бронирования")
            
            // Генерируем ID, если не задан
            val bookingWithId = if (booking.id.isEmpty()) {
                booking.copy(id = UUID.randomUUID().toString())
            } else {
                booking
            }
            
            // Сохраняем в БД
            val entity = BookingMapper.toEntity(bookingWithId)
            bookingDao.insertBooking(entity)
            
            // Обновляем статусы объектов недвижимости
            updatePropertyStatusesUseCase()
            
            Timber.d("Бронирование успешно добавлено с ID: ${bookingWithId.id}")
            Result.success(bookingWithId)
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при добавлении бронирования")
            Result.failure(e)
        }
    }

    override suspend fun updateBooking(booking: Booking): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (booking.id.isEmpty()) {
                Timber.e("Невозможно обновить бронирование - пустой ID")
                return@withContext Result.failure(Exception("Booking ID пустой"))
            }
            
            Timber.d("Обновление бронирования с ID: ${booking.id}")
            
            // Обновляем в БД
            val entity = BookingMapper.toEntity(booking)
            bookingDao.updateBooking(entity)
            
            // Обновляем статусы объектов недвижимости
            updatePropertyStatusesUseCase()
            
            Timber.d("Бронирование успешно обновлено")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при обновлении бронирования")
            Result.failure(e)
        }
    }

    override suspend fun deleteBooking(bookingId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Timber.d("Удаление бронирования с ID: $bookingId")
            
            bookingDao.deleteBooking(bookingId)
            
            // Обновляем статусы объектов недвижимости
            updatePropertyStatusesUseCase()
            
            Timber.d("Бронирование успешно удалено")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при удалении бронирования")
            Result.failure(e)
        }
    }

    override suspend fun getBooking(bookingId: String): Result<Booking> {
        return try {
            val bookingEntity = bookingDao.getBooking(bookingId)
                ?: return Result.failure(NoSuchElementException("Бронирование с id $bookingId не найдено"))
            Result.success(BookingMapper.toDomain(bookingEntity))
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при получении бронирования")
            Result.failure(e)
        }
    }

    override suspend fun getAllBookings(): Result<List<Booking>> {
        return try {
            val bookingEntities = bookingDao.getAllBookings().first()
            Result.success(BookingMapper.toDomainList(bookingEntities))
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при получении всех бронирований")
            Result.failure(e)
        }
    }

    override fun observeAllBookings(): Flow<List<Booking>> {
        return bookingDao.getAllBookings().map { BookingMapper.toDomainList(it) }
    }

    override fun observeBookingsByProperty(propertyId: String): Flow<List<Booking>> {
        return bookingDao.getBookingsByProperty(propertyId).map { BookingMapper.toDomainList(it) }
    }

    override fun observeBookingsByClient(clientId: String): Flow<List<Booking>> {
        return bookingDao.getBookingsByClient(clientId).map { BookingMapper.toDomainList(it) }
    }

    override fun observeBookingsInDateRange(fromDate: Long, toDate: Long): Flow<List<Booking>> {
        return bookingDao.getBookingsInDateRange(fromDate, toDate).map { BookingMapper.toDomainList(it) }
    }

    override fun observeBookingsForPropertyInDateRange(propertyId: String, fromDate: Long, toDate: Long): Flow<List<Booking>> {
        return bookingDao.getBookingsForPropertyInDateRange(propertyId, fromDate, toDate).map { BookingMapper.toDomainList(it) }
    }

    override suspend fun hasBookingConflicts(propertyId: String, fromDate: Long, toDate: Long): Result<Boolean> {
        return try {
            val conflictsCount = bookingDao.countBookingConflicts(propertyId, fromDate, toDate)
            Result.success(conflictsCount > 0)
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при проверке конфликтов бронирования")
            Result.failure(e)
        }
    }

    override suspend fun hasBookingConflictsExcludingBooking(propertyId: String, fromDate: Long, toDate: Long, excludeBookingId: String): Result<Boolean> {
        return try {
            val conflictsCount = bookingDao.countBookingConflictsExcludingBooking(propertyId, fromDate, toDate, excludeBookingId)
            Result.success(conflictsCount > 0)
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при проверке конфликтов бронирования (с исключением)")
            Result.failure(e)
        }
    }

    override suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            bookingDao.updateBookingStatus(bookingId, status.name)
            
            // Обновляем статусы объектов недвижимости
            updatePropertyStatusesUseCase()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при обновлении статуса бронирования")
            Result.failure(e)
        }
    }

    override suspend fun updatePaymentStatus(bookingId: String, paymentStatus: PaymentStatus): Result<Unit> {
        return try {
            bookingDao.updatePaymentStatus(bookingId, paymentStatus.name)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при обновлении статуса оплаты бронирования")
            Result.failure(e)
        }
    }

    override fun observeActiveBookings(): Flow<List<Booking>> {
        return bookingDao.getActiveBookings().map { BookingMapper.toDomainList(it) }
    }

    override fun observeUpcomingBookings(): Flow<List<Booking>> {
        return bookingDao.getUpcomingBookings().map { BookingMapper.toDomainList(it) }
    }

    override fun observePastBookings(): Flow<List<Booking>> {
        return bookingDao.getPastBookings().map { BookingMapper.toDomainList(it) }
    }

    override suspend fun updateBookingStatusesAutomatically(): Result<Unit> {
        return try {
            bookingDao.updateBookingStatusesAutomatically()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при автоматическом обновлении статусов бронирований")
            Result.failure(e)
        }
    }

    override suspend fun syncBookings(): Result<Unit> {
        // Здесь будет реализация синхронизации с удаленным сервером
        // Пока просто возвращаем успешный результат
        return Result.success(Unit)
    }
} 