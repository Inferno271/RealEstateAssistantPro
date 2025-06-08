package com.realestateassistant.pro.domain.model

/**
 * Доменная модель бронирования объекта недвижимости
 *
 * Поддерживает как посуточную, так и долгосрочную аренду
 */
data class Booking(
    val id: String = "",
    
    // Связи с другими сущностями
    val propertyId: String,
    val clientId: String? = null, // Может быть null для предварительных бронирований
    
    // Общие поля для обоих типов аренды
    val startDate: Long, // Дата заезда/начала аренды
    val endDate: Long, // Дата выезда/окончания аренды
    val status: BookingStatus = BookingStatus.PENDING,
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
    val totalAmount: Double, // Общая сумма бронирования
    val depositAmount: Double? = null, // Сумма депозита
    val notes: String? = null, // Дополнительные примечания
    
    // Поля для посуточной аренды
    val guestsCount: Int? = null, // Количество гостей
    val checkInTime: String? = null, // Время заезда
    val checkOutTime: String? = null, // Время выезда
    val includedServices: List<String> = emptyList(), // Включенные услуги
    val additionalServices: List<AdditionalService> = emptyList(), // Дополнительные услуги
    
    // Поля для долгосрочной аренды
    val rentPeriodMonths: Int? = null, // Срок аренды в месяцах
    val monthlyPaymentAmount: Double? = null, // Ежемесячный платеж
    val utilityPayments: Boolean? = null, // Включены ли коммунальные платежи
    val contractType: String? = null, // Тип договора
    
    // Служебные поля
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

/**
 * Статусы бронирования
 */
enum class BookingStatus {
    PENDING, // Ожидает подтверждения
    CONFIRMED, // Подтверждено
    ACTIVE, // Активно (клиент заселился)
    COMPLETED, // Завершено
    CANCELLED, // Отменено
    EXPIRED // Истекло (не было подтверждено вовремя)
}

/**
 * Статусы оплаты
 */
enum class PaymentStatus {
    UNPAID, // Не оплачено
    PARTIALLY_PAID, // Частично оплачено (например, внесен только депозит)
    FULLY_PAID, // Полностью оплачено
    REFUNDED // Возвращено
}

/**
 * Дополнительные услуги для бронирования
 */
data class AdditionalService(
    val name: String,
    val price: Double,
    val quantity: Int = 1
) 