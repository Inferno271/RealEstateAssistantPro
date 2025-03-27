package com.realestateassistant.pro.domain.model

/**
 * Модель недвижимости.
 *
 * Общие поля (для обоих вариантов аренды):
 * - id: идентификатор объекта.
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
 *
 * Дополнительные поля для посуточной аренды:
 * - instantBooking: возможность мгновенного бронирования.
 * - smokingAllowed: разрешение на курение.
 * - petsAllowed: разрешение на животных.
 * - partiesAllowed: разрешение на мероприятия.
 * - childrenAllowed: разрешение на детей.
 * - minimumAge: минимальный возраст гостей.
 * - documentsRequired: требование предоставления документов.
 * - depositRequired: требование предоставления депозита.
 * - cleaningIncluded: включение уборки в стоимость.
 * - cleaningFee: стоимость уборки.
 * - bedrooms: количество спален.
 * - beds: количество кроватей.
 * - bathrooms: количество ванных комнат.
 * - amenities: удобства и услуги.
 * - houseRules: правила проживания.
 * - cancellationPolicy: политика отмены бронирования.
 * - checkInInstructions: инструкции для заезда.
 * - nearbyAttractions: достопримечательности вблизи.
 * - transportationAccess: транспортное сообщение.
 * - weekendPrice: стоимость на выходные.
 * - holidayPrice: стоимость в праздничные дни.
 * - weeklyDiscount: скидка на неделю.
 * - monthlyDiscount: скидка на месяц.
 * - seasonalPricing: сезонное ценообразование.
 */

data class Property(
    val id: String = "",
    
    // Контактная информация
    val contactName: String = "",
    val contactPhone: List<String> = emptyList(),
    val additionalContactInfo: String? = null,

    // Общая информация
    val propertyType: String = "",
    val address: String = "",
    val district: String = "",
    val nearbyObjects: List<String> = emptyList(),
    val views: List<String> = emptyList(),
    val area: Double = 0.0,
    val roomsCount: Int = 0,
    val isStudio: Boolean = false,
    val layout: String = "",
    val floor: Int = 0,
    val totalFloors: Int = 0,
    val description: String? = null,
    val management: List<String> = emptyList(),

    // Характеристики объекта
    val repairState: String = "",
    val bedsCount: Int? = null,
    val bathroomsCount: Int? = null,
    val bathroomType: String = "",
    val noFurniture: Boolean = false,
    val hasAppliances: Boolean = false,
    val heatingType: String = "",
    val balconiesCount: Int = 0,
    val elevatorsCount: Int? = null,
    val hasParking: Boolean = false,
    val parkingType: String? = null,
    val parkingSpaces: Int? = null,
    val amenities: List<String> = emptyList(),
    val smokingAllowed: Boolean = false,

    // Условия проживания
    val childrenAllowed: Boolean = false,
    val minChildAge: String? = null,
    val maxChildrenCount: String? = null,
    val petsAllowed: Boolean = false,
    val maxPetsCount: String? = null,
    val allowedPetTypes: List<String> = emptyList(),

    // Для длительной аренды
    val monthlyRent: Double? = null,
    val hasCompensationContract: Boolean = false,
    val isSelfEmployed: Boolean = false,
    val isPersonalIncomeTax: Boolean = false,
    val isCompanyIncomeTax: Boolean = false,
    val utilitiesIncluded: Boolean = false,
    val utilitiesCost: Double? = null,
    val minRentPeriod: String? = null,
    val maxRentPeriod: Int? = null,
    val depositMonths: String? = null,
    val depositCustomAmount: Double? = null,
    val securityDeposit: Double? = null,
    val additionalExpenses: String? = null,
    val longTermRules: String? = null,

    // Для посуточной аренды
    val dailyPrice: Double? = null,
    val minStayDays: Int? = null,
    val maxStayDays: Int? = null,
    val maxGuests: Int? = null,
    val checkInTime: String? = null,
    val checkOutTime: String? = null,
    val shortTermDeposit: Double? = null,
    val shortTermDepositCustomAmount: Double? = null,
    val seasonalPrices: List<SeasonalPrice> = emptyList(),
    val weekdayPrice: Double? = null,
    val weekendPrice: Double? = null,
    val weeklyDiscount: Double? = null,
    val monthlyDiscount: Double? = null,
    val additionalServices: String? = null,
    val shortTermRules: String? = null,
    val cleaningService: Boolean = false,
    val cleaningDetails: String? = null,
    val hasExtraBed: Boolean = false,
    val extraBedPrice: Double? = null,
    val partiesAllowed: Boolean = false,
    val specialOffers: String? = null,
    val additionalComments: String? = null,

    // Фото и документы
    val photos: List<String> = emptyList(),
    val documents: List<String> = emptyList()
)

data class SeasonalPrice(
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val price: Double = 0.0
) 