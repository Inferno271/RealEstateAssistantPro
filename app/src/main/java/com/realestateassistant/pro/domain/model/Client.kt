package com.realestateassistant.pro.domain.model

/**
 * Модель клиента.
 *
 * Общие поля:
 * - fullName: ФИО клиента (полное имя).
 * - phone: контактные телефоны (список телефонов для связи, мессенджеров).
 * - rentalType: тип аренды (например, "длительная", "посуточная").
 * - comment: дополнительные пожелания или комментарии.
 *
 * Информация о клиенте:
 * - familyComposition: состав семьи (например, "Семейная пара с ребенком", "Студенты").
 * - peopleCount: количество человек для проживания.
 * - childrenCount: количество детей.
 * - childrenAges: список возрастов детей (например, [3, 5, 7]).
 * - hasPets: есть ли домашние животные.
 * - petTypes: типы животных (например, ["Кошка", "Собака мелкой породы"]).
 * - petCount: общее количество животных.
 * - occupation: род занятий/работы клиента.
 * - isSmokingClient: является ли клиент курящим.
 *
 * Общие поля предпочтений по аренде:
 * - preferredDistrict: предпочитаемый район.
 * - preferredAddress: предпочитаемый адрес недвижимости.
 * - additionalComments: дополнительные комментарии по предпочтениям.
 * - urgencyLevel: уровень срочности поиска жилья (например, "Срочно", "В течение месяца", "Не срочно").
 * - budgetFlexibility: готовность увеличить бюджет при подходящем варианте.
 * - maxBudgetIncrease: максимальное возможное увеличение бюджета (в процентах или абсолютном значении).
 * - requirementFlexibility: список требований, по которым клиент готов идти на компромисс.
 * - priorityCriteria: список приоритетных критериев для клиента (наиболее важные параметры жилья).
 *
 * Дополнительные предпочтения:
 * - preferredAmenities: желаемые удобства.
 * - preferredViews: предпочтительные виды из окон.
 * - preferredNearbyObjects: предпочтительные объекты инфраструктуры поблизости.
 * - preferredRepairState: предпочтительное состояние ремонта.
 * - preferredFloorMin/Max: предпочитаемый диапазон этажей.
 * - needsElevator: нужен ли лифт.
 * - preferredBalconiesCount: предпочтительное количество балконов.
 * - preferredBathroomsCount: предпочтительное количество санузлов.
 * - preferredBathroomType: предпочтительный тип санузла.
 * - needsFurniture: нужна ли мебель.
 * - needsAppliances: нужна ли бытовая техника.
 * - preferredHeatingType: предпочтительный тип отопления.
 * - needsParking: нужна ли парковка.
 * - preferredParkingType: предпочтительный тип парковки.
 *
 * Дополнительные предпочтения для домов:
 * - needsYard: нужен ли двор/участок.
 * - preferredYardArea: предпочтительная площадь участка.
 * - needsGarage: нужен ли гараж.
 * - preferredGarageSpaces: предпочтительное количество мест в гараже.
 * - needsBathhouse: нужна ли баня.
 * - needsPool: нужен ли бассейн.
 *
 * Юридические предпочтения:
 * - needsOfficialAgreement: нужен ли официальный договор.
 * - preferredTaxOption: предпочтительный вариант налогообложения.
 *
 * Поля для клиентов, заинтересованных в длительной аренде:
 * - longTermBudgetMin: минимальный бюджет аренды за месяц.
 * - longTermBudgetMax: максимальный бюджет аренды за месяц.
 * - desiredPropertyType: интересующий тип недвижимости (квартира, дом, комната, апартаменты, коттедж и т.д.).
 * - desiredRoomsCount: желаемое количество комнат.
 * - desiredArea: желаемая площадь недвижимости в кв.м.
 * - additionalRequirements: дополнительные требования (наличие мебели, бытовой техники и т.п.).
 * - legalPreferences: юридические предпочтения (например, возможность заключения официального договора).
 * - moveInDeadline: срок актуальности заявки (до какой даты необходимо заселиться).
 *
 * Поля для клиентов, заинтересованных в посуточной аренде:
 * - shortTermBudgetMin: минимальный бюджет аренды за сутки.
 * - shortTermBudgetMax: максимальный бюджет аренды за сутки.
 * - shortTermCheckInDate: желаемая дата заезда (timestamp).
 * - shortTermCheckOutDate: желаемая дата выезда (timestamp).
 * - hasAdditionalGuests: планируются ли дополнительные гости (не проживающие в семье постоянно).
 * - shortTermGuests: количество дополнительных гостей.
 * - dailyBudget: бюджет аренды за сутки.
 * - preferredShortTermDistrict: предпочтительный район или расположение.
 * - checkInOutConditions: условия заезда и выезда.
 * - additionalServices: дополнительные услуги (например, Wi-Fi, уборка, трансфер и т.п.).
 * - additionalShortTermRequirements: дополнительные требования (условия оплаты, оперативная бронь и т.п.).
 */

data class Client(
    val id: String = "",
    // Общие поля
    val fullName: String = "",
    val phone: List<String> = emptyList(),
    val rentalType: String = "", // например, "длительная" или "посуточная"
    val comment: String = "",

    // Информация о клиенте
    val familyComposition: String? = null,
    val peopleCount: Int? = null,
    val childrenCount: Int? = null,
    val childrenAges: List<Int> = emptyList(),
    val hasPets: Boolean = false,
    val petTypes: List<String> = emptyList(),
    val petCount: Int? = null,
    val occupation: String? = null,
    val isSmokingClient: Boolean = false,

    // Общие поля предпочтений по аренде
    val preferredDistrict: String? = null,
    val preferredAddress: String? = null,
    val additionalComments: String? = null,
    val urgencyLevel: String? = null, // "Срочно", "В течение месяца", "Не срочно"
    val budgetFlexibility: Boolean = false, // готовность увеличить бюджет
    val maxBudgetIncrease: Double? = null, // максимальное увеличение бюджета
    val requirementFlexibility: List<String> = emptyList(), // требования с возможностью компромисса
    val priorityCriteria: List<String> = emptyList(), // приоритетные критерии для подбора
    
    // Новые поля предпочтений по удобствам и характеристикам
    val preferredAmenities: List<String> = emptyList(),
    val preferredViews: List<String> = emptyList(),
    val preferredNearbyObjects: List<String> = emptyList(),
    val preferredRepairState: String? = null,
    val preferredFloorMin: Int? = null,
    val preferredFloorMax: Int? = null,
    val needsElevator: Boolean = false,
    val preferredBalconiesCount: Int? = null,
    val preferredBathroomsCount: Int? = null,
    val preferredBathroomType: String? = null,
    val needsFurniture: Boolean = true,
    val needsAppliances: Boolean = true,
    val preferredHeatingType: String? = null,
    val needsParking: Boolean = false,
    val preferredParkingType: String? = null,
    
    // Дополнительные предпочтения для разных типов недвижимости
    val needsYard: Boolean = false,
    val preferredYardArea: Double? = null,
    val needsGarage: Boolean = false,
    val preferredGarageSpaces: Int? = null,
    val needsBathhouse: Boolean = false,
    val needsPool: Boolean = false,
    
    // Юридические предпочтения (расширенные)
    val needsOfficialAgreement: Boolean = false,
    val preferredTaxOption: String? = null,
    
    // Поля для длительной аренды
    val longTermBudgetMin: Double? = null,
    val longTermBudgetMax: Double? = null,
    val desiredPropertyType: String? = null,
    val desiredRoomsCount: Int? = null,
    val desiredArea: Double? = null,
    val additionalRequirements: String? = null,
    val legalPreferences: String? = null,
    val moveInDeadline: Long? = null,

    // Поля для посуточной аренды
    val shortTermBudgetMin: Double? = null,
    val shortTermBudgetMax: Double? = null,
    val shortTermCheckInDate: Long? = null,
    val shortTermCheckOutDate: Long? = null,
    val hasAdditionalGuests: Boolean = false,
    val shortTermGuests: Int? = null,
    val dailyBudget: Double? = null,
    val preferredShortTermDistrict: String? = null,
    val checkInOutConditions: String? = null,
    val additionalServices: String? = null,
    val additionalShortTermRequirements: String? = null,
    
    // Служебные поля
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) 