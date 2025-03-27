package com.realestateassistant.pro.presentation.model

import androidx.compose.runtime.mutableStateMapOf

// Перечисление всех секций для управления их видимостью
enum class PropertySection {
    CONTACT_INFO,
    PROPERTY_INFO,
    PROPERTY_CHARACTERISTICS,
    LIVING_CONDITIONS,
    LONG_TERM_RENTAL,
    SHORT_TERM_RENTAL,
    MEDIA
}

data class ExpandedStates(
    val propertyTypeExpanded: Boolean = false,
    val districtExpanded: Boolean = false,
    val layoutExpanded: Boolean = false,
    val repairStateExpanded: Boolean = false,
    val bathroomTypeExpanded: Boolean = false,
    val heatingTypeExpanded: Boolean = false,
    val parkingTypeExpanded: Boolean = false
)

// Вспомогательная функция для инициализации состояния секций
fun createInitialSectionState(): MutableMap<PropertySection, Boolean> {
    return mutableStateMapOf<PropertySection, Boolean>().apply {
        PropertySection.values().forEach { section ->
            // По умолчанию развернуты только контактная информация и общая информация
            put(section, section == PropertySection.CONTACT_INFO || section == PropertySection.PROPERTY_INFO)
        }
    }
} 