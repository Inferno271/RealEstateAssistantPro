package com.realestateassistant.pro.domain.model

/**
 * Модель недвижимости.
 *
 * Общие поля (для обоих вариантов аренды):
 * - id: идентификатор объекта.
 * - title: название объекта.
 * - propertyType: тип недвижимости.
 * - separateEntrance: наличие отдельного входа.
 * - view: вид объекта.
 * - address: адрес объекта.
 *      (Опционально: housingComplex - ЖК, апартамент, комплекс;
 *       complexClass - класс ЖК: эконом, стандарт, бизнес, премиум, VIP.)
 * - district: район или ориентир, уточняющий расположение объекта.
 * - area: площадь недвижимости в кв.м.
 * - roomsCount: количество комнат.
 * - floor: этаж объекта.
 * - totalFloors: этажность здания.
 * - levelsCount: количество уровней для многоэтажных объектов (таунхаусы, дома, гаражи и т.д.).
 * - layout: планировка объекта.
 *      (Дополнительные параметры: balconyCount, loggiaCount, terraceCount, solarium, gazebo, barbecue, poolType, sauna, bath, hammam)
 * - balconyCount: количество балконов.
 * - loggiaCount: количество лоджий.
 * - terraceCount: количество террас.
 * - solarium: наличие солярия.
 * - gazebo: наличие беседки.
 * - barbecue: наличие мангала.
 * - poolType: тип бассейна (общий, личный, инфинити, уличный, внутренний).
 * - sauna: наличие сауны.
 * - bath: наличие бани.
 * - hammam: наличие хамама.
 * - parkingInfo: информация о парковке (например, "да, подземная", "нет", "уличная").
 * - mediaUrls: фото и медиа материалы (список URL).
 * - contactName: имя представителя.
 * - contactPhone: контактный телефон представителя (с возможностью перехода в мессенджеры).
 *
 * Поля для длительной аренды:
 * - monthlyPrice: стоимость аренды за месяц.
 * - minLeaseTerm: минимальный срок аренды (например, от 6 месяцев, 1 год и т.п.).
 * - deposit: размер депозита/залог.
 * - paymentTerms: условия оплаты и договора, режим оплаты.
 * - utilities: коммунальные платежи и дополнительные услуги.
 * - specialConditions: особые условия аренды.
 *
 * Поля для посуточной аренды:
 * - dailyPrice: стоимость аренды за сутки.
 * - minNights: минимальное количество ночей для бронирования.
 * - availabilityCalendar: список доступных дат (timestamp) для бронирования.
 * - checkInTime: время заезда (например, "14:00").
 * - checkOutTime: время выезда (например, "11:00").
 * - maxGuests: максимальное количество гостей.
 * - shortTermConditions: условия краткосрочной аренды.
 * - discounts: информация о скидках и акциях.
 */

data class Property(
    val id: String = "",
    val title: String = "",
    val propertyType: String = "",
    val separateEntrance: Boolean = false,
    val view: String = "",
    val address: String = "",
    val housingComplex: String? = null,
    val complexClass: String? = null,
    val district: String = "",
    val area: Double = 0.0,
    val roomsCount: Int = 0,
    val floor: Int = 0,
    val totalFloors: Int = 0,
    val levelsCount: Int? = null,
    val layout: String = "",
    val balconyCount: Int = 0,
    val loggiaCount: Int = 0,
    val terraceCount: Int = 0,
    val solarium: Boolean = false,
    val gazebo: Boolean = false,
    val barbecue: Boolean = false,
    val poolType: String? = null,
    val sauna: Boolean = false,
    val bath: Boolean = false,
    val hammam: Boolean = false,
    val parkingInfo: String = "",
    val mediaUrls: List<String> = emptyList(),
    val contactName: String = "",
    val contactPhone: String = "",
    
    // Поля для длительной аренды
    val monthlyPrice: Double? = null,
    val minLeaseTerm: String? = null,
    val deposit: Double? = null,
    val paymentTerms: String? = null,
    val utilities: String? = null,
    val specialConditions: String? = null,
    
    // Поля для посуточной аренды
    val dailyPrice: Double? = null,
    val minNights: Int? = null,
    val availabilityCalendar: List<Long>? = null,
    val checkInTime: String? = null,
    val checkOutTime: String? = null,
    val maxGuests: Int? = null,
    val shortTermConditions: String? = null,
    val discounts: String? = null
) 