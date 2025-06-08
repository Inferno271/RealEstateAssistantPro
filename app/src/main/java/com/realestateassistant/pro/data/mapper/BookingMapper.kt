package com.realestateassistant.pro.data.mapper

import com.realestateassistant.pro.data.local.entity.BookingEntity
import com.realestateassistant.pro.domain.model.AdditionalService
import com.realestateassistant.pro.domain.model.Booking
import com.realestateassistant.pro.domain.model.BookingStatus
import com.realestateassistant.pro.domain.model.PaymentStatus
import java.util.UUID

/**
 * Маппер для конвертации между Entity и доменной моделью бронирования
 */
object BookingMapper {
    /**
     * Конвертирует доменную модель в Entity
     */
    fun toEntity(booking: Booking): BookingEntity {
        return BookingEntity(
            id = booking.id.ifEmpty { UUID.randomUUID().toString() },
            propertyId = booking.propertyId,
            clientId = booking.clientId,
            startDate = booking.startDate,
            endDate = booking.endDate,
            status = booking.status.name,
            paymentStatus = booking.paymentStatus.name,
            totalAmount = booking.totalAmount,
            depositAmount = booking.depositAmount,
            notes = booking.notes,
            guestsCount = booking.guestsCount,
            checkInTime = booking.checkInTime,
            checkOutTime = booking.checkOutTime,
            includedServices = booking.includedServices,
            additionalServices = booking.additionalServices,
            rentPeriodMonths = booking.rentPeriodMonths,
            monthlyPaymentAmount = booking.monthlyPaymentAmount,
            utilityPayments = booking.utilityPayments,
            contractType = booking.contractType,
            createdAt = booking.createdAt,
            updatedAt = booking.updatedAt,
            isSynced = booking.isSynced
        )
    }

    /**
     * Конвертирует Entity в доменную модель
     */
    fun toDomain(entity: BookingEntity): Booking {
        return Booking(
            id = entity.id,
            propertyId = entity.propertyId,
            clientId = entity.clientId,
            startDate = entity.startDate,
            endDate = entity.endDate,
            status = try {
                BookingStatus.valueOf(entity.status)
            } catch (e: Exception) {
                BookingStatus.PENDING
            },
            paymentStatus = try {
                PaymentStatus.valueOf(entity.paymentStatus)
            } catch (e: Exception) {
                PaymentStatus.UNPAID
            },
            totalAmount = entity.totalAmount,
            depositAmount = entity.depositAmount,
            notes = entity.notes,
            guestsCount = entity.guestsCount,
            checkInTime = entity.checkInTime,
            checkOutTime = entity.checkOutTime,
            includedServices = entity.includedServices,
            additionalServices = entity.additionalServices,
            rentPeriodMonths = entity.rentPeriodMonths,
            monthlyPaymentAmount = entity.monthlyPaymentAmount,
            utilityPayments = entity.utilityPayments,
            contractType = entity.contractType,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            isSynced = entity.isSynced
        )
    }

    /**
     * Конвертирует список Entity в список доменных моделей
     */
    fun toDomainList(entities: List<BookingEntity>): List<Booking> {
        return entities.map { toDomain(it) }
    }

    /**
     * Конвертирует список доменных моделей в список Entity
     */
    fun toEntityList(bookings: List<Booking>): List<BookingEntity> {
        return bookings.map { toEntity(it) }
    }
} 