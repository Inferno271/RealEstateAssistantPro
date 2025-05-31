package com.realestateassistant.pro.presentation.model

import androidx.compose.runtime.mutableStateMapOf

/**
 * Перечисление секций формы клиента.
 * Используется для управления состоянием раскрытия/скрытия секций в форме.
 */
enum class ClientSection {
    CONTACT_INFO,         // Контактная информация
    CLIENT_INFO,          // Информация о клиенте
    RENTAL_PREFERENCES,   // Предпочтения по аренде
    SEARCH_FLEXIBILITY,   // Гибкость поиска и приоритеты
    HOUSING_PREFERENCES,  // Предпочтения по жилищным характеристикам
    AMENITIES_PREFERENCES, // Предпочтения по удобствам
    SPECIFIC_PROPERTY_PREFERENCES, // Специальные предпочтения для конкретных типов недвижимости
    LEGAL_PREFERENCES,    // Юридические предпочтения
    LONG_TERM_REQUIREMENTS, // Требования для долгосрочной аренды
    SHORT_TERM_REQUIREMENTS // Требования для краткосрочной аренды
}

/**
 * Создает начальное состояние для секций клиента.
 * По умолчанию первые три секции раскрыты, остальные скрыты.
 * Использует mutableStateMapOf для реактивного обновления UI при изменении состояния.
 */
fun createInitialClientSectionState() = mutableStateMapOf(
    ClientSection.CONTACT_INFO to true,
    ClientSection.CLIENT_INFO to true,
    ClientSection.RENTAL_PREFERENCES to true,
    ClientSection.SEARCH_FLEXIBILITY to true,
    ClientSection.HOUSING_PREFERENCES to false,
    ClientSection.AMENITIES_PREFERENCES to false,
    ClientSection.SPECIFIC_PROPERTY_PREFERENCES to false,
    ClientSection.LEGAL_PREFERENCES to false,
    ClientSection.LONG_TERM_REQUIREMENTS to false,
    ClientSection.SHORT_TERM_REQUIREMENTS to false
) 