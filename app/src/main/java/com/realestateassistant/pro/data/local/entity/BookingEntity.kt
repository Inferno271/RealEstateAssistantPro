package com.realestateassistant.pro.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.realestateassistant.pro.domain.model.AdditionalService
import com.realestateassistant.pro.domain.model.BookingStatus
import com.realestateassistant.pro.domain.model.PaymentStatus

/**
 * Entity класс для хранения бронирований в локальной базе данных
 */
@Entity(
    tableName = "bookings",
    foreignKeys = [
        ForeignKey(
            entity = PropertyEntity::class,
            parentColumns = ["id"],
            childColumns = ["propertyId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("propertyId"),
        Index("clientId"),
        Index("startDate"),
        Index("endDate"),
        Index("status")
    ]
)
data class BookingEntity(
    @PrimaryKey
    val id: String,
    
    // Связи с другими сущностями
    val propertyId: String,
    val clientId: String?, // Может быть null для предварительных бронирований
    
    // Общие поля для обоих типов аренды
    val startDate: Long, // Дата заезда/начала аренды
    val endDate: Long, // Дата выезда/окончания аренды
    val status: String, // Статус бронирования (из enum BookingStatus)
    val paymentStatus: String, // Статус оплаты (из enum PaymentStatus)
    val totalAmount: Double, // Общая сумма бронирования
    val depositAmount: Double?, // Сумма депозита
    val notes: String?, // Дополнительные примечания
    
    // Поля для посуточной аренды
    val guestsCount: Int?,
    val checkInTime: String?,
    val checkOutTime: String?,
    val includedServices: List<String>,
    val additionalServices: List<AdditionalService>,
    
    // Поля для долгосрочной аренды
    val rentPeriodMonths: Int?,
    val monthlyPaymentAmount: Double?,
    val utilityPayments: Boolean?,
    val contractType: String?,
    
    // Служебные поля
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
) 