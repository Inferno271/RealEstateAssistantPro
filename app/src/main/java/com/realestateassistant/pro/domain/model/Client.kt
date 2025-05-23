package com.realestateassistant.pro.domain.model

/**
 * Модель клиента.
 *
 * Общие поля:
 * - fullName: ФИО клиента (полное имя).
 * - phone: контактный телефон (для связи, мессенджеров).
 * - email: email клиента.
 * - rentalType: тип аренды (например, "длительная", "посуточная").
 * - comment: дополнительные пожелания или комментарии.
 *
 * Поля для клиентов, заинтересованных в длительной аренде:
 * - preferredAddress: предпочитаемый район или адрес недвижимости.
 * - longTermBudgetMin: минимальный бюджет аренды за месяц.
 * - longTermBudgetMax: максимальный бюджет аренды за месяц.
 * - desiredPropertyType: интересующий тип недвижимости (квартира, дом, комната, апартаменты, коттедж и т.д.).
 * - desiredRoomsCount: желаемое количество комнат.
 * - peopleCount: количество человек для проживания.
 * - childrenCount: количество детей.
 * - childrenAge: возраст детей (например, "3, 5, 7 лет").
 * - petsInfo: информация о животных (например, "2 собаки", "1 кошка").
 * - desiredArea: желаемая площадь недвижимости в кв.м.
 * - additionalRequirements: дополнительные требования (наличие мебели, бытовой техники и т.п.).
 * - legalPreferences: юридические предпочтения (например, возможность заключения официального договора).
 * - moveInDeadline: срок актуальности заявки (до какой даты необходимо заселиться).
 * - familyComposition: состав семьи (например, "Семейная пара с ребенком", "Студенты").
 *
 * Поля для клиентов, заинтересованных в посуточной аренде:
 * - shortTermCheckInDate: желаемая дата заезда (timestamp).
 * - shortTermCheckOutDate: желаемая дата выезда (timestamp).
 * - shortTermGuests: количество гостей.
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
    val phone: String = "",
    val email: String = "",
    val rentalType: String = "", // например, "длительная" или "посуточная"
    val comment: String = "",

    // Поля для длительной аренды
    val preferredAddress: String? = null,
    val longTermBudgetMin: Double? = null,
    val longTermBudgetMax: Double? = null,
    val desiredPropertyType: String? = null,
    val desiredRoomsCount: Int? = null,
    val peopleCount: Int? = null,
    val childrenCount: Int? = null,
    val childrenAge: String? = null,
    val petsInfo: String? = null,
    val desiredArea: Double? = null,
    val additionalRequirements: String? = null,
    val legalPreferences: String? = null,
    val moveInDeadline: Long? = null,
    val familyComposition: String? = null,

    // Поля для посуточной аренды
    val shortTermCheckInDate: Long? = null,
    val shortTermCheckOutDate: Long? = null,
    val shortTermGuests: Int? = null,
    val dailyBudget: Double? = null,
    val preferredShortTermDistrict: String? = null,
    val checkInOutConditions: String? = null,
    val additionalServices: String? = null,
    val additionalShortTermRequirements: String? = null
) 