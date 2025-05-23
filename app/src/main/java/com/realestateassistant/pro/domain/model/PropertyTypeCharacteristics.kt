package com.realestateassistant.pro.domain.model

/**
 * Модель для определения зависимостей между типами недвижимости и их характеристиками.
 * Определяет какие поля должны отображаться для каждого типа недвижимости.
 */
object PropertyTypeCharacteristics {
    // Типы недвижимости
    val APARTMENT = "Квартира"
    val HOUSE = "Дом"
    val TOWNHOUSE = "Таунхаус"
    val ROOM = "Комната"
    val STUDIO = "Студия"
    val APARTMENTS = "Апартаменты"
    val PENTHOUSE = "Пентхаус"
    val DUPLEX = "Дуплекс"
    val COMMERCIAL = "Коммерческая"
    val GARAGE = "Гараж"
    val LAND = "Земельный участок"

    // Матрица соответствия типов недвижимости и их характеристик
    val typeCharacteristicsMap: Map<String, PropertyCharacteristicsConfig> = mapOf(
        APARTMENT to PropertyCharacteristicsConfig(
            hasFloor = true,
            hasTotalFloors = true,
            hasElevator = true,
            hasBalconies = true,
            hasRoomsCount = true,
            hasBathrooms = true,
            hasLevels = false,
            hasSquare = true,
            hasLandSquare = false,
            hasGarage = false,
            hasBathhouse = false,
            hasPool = false
        ),
        STUDIO to PropertyCharacteristicsConfig(
            hasFloor = true,
            hasTotalFloors = true,
            hasElevator = true,
            hasBalconies = true,
            hasRoomsCount = false,
            hasBathrooms = true,
            hasLevels = false,
            hasSquare = true,
            hasLandSquare = false,
            hasGarage = false,
            hasBathhouse = false,
            hasPool = false
        ),
        HOUSE to PropertyCharacteristicsConfig(
            hasFloor = false,
            hasTotalFloors = false,
            hasElevator = false,
            hasBalconies = true,
            hasRoomsCount = true,
            hasBathrooms = true,
            hasLevels = true,
            hasSquare = true,
            hasLandSquare = true,
            hasGarage = true,
            hasBathhouse = true,
            hasPool = true
        ),
        TOWNHOUSE to PropertyCharacteristicsConfig(
            hasFloor = false,
            hasTotalFloors = false,
            hasElevator = false,
            hasBalconies = true,
            hasRoomsCount = true,
            hasBathrooms = true,
            hasLevels = true,
            hasSquare = true,
            hasLandSquare = true,
            hasGarage = true,
            hasBathhouse = false,
            hasPool = false
        ),
        ROOM to PropertyCharacteristicsConfig(
            hasFloor = true,
            hasTotalFloors = true,
            hasElevator = true,
            hasBalconies = false,
            hasRoomsCount = false,
            hasBathrooms = true,
            hasLevels = false,
            hasSquare = true,
            hasLandSquare = false,
            hasGarage = false,
            hasBathhouse = false,
            hasPool = false
        ),
        APARTMENTS to PropertyCharacteristicsConfig(
            hasFloor = true,
            hasTotalFloors = true,
            hasElevator = true,
            hasBalconies = true,
            hasRoomsCount = true,
            hasBathrooms = true,
            hasLevels = false,
            hasSquare = true,
            hasLandSquare = false,
            hasGarage = false,
            hasBathhouse = false,
            hasPool = false
        ),
        PENTHOUSE to PropertyCharacteristicsConfig(
            hasFloor = true,
            hasTotalFloors = true,
            hasElevator = true,
            hasBalconies = true,
            hasRoomsCount = true,
            hasBathrooms = true,
            hasLevels = true,
            hasSquare = true,
            hasLandSquare = false,
            hasGarage = false,
            hasBathhouse = false,
            hasPool = true
        ),
        DUPLEX to PropertyCharacteristicsConfig(
            hasFloor = true,
            hasTotalFloors = true,
            hasElevator = true,
            hasBalconies = true,
            hasRoomsCount = true,
            hasBathrooms = true,
            hasLevels = true,
            hasSquare = true,
            hasLandSquare = false,
            hasGarage = false,
            hasBathhouse = false,
            hasPool = false
        ),
        COMMERCIAL to PropertyCharacteristicsConfig(
            hasFloor = true,
            hasTotalFloors = true,
            hasElevator = true,
            hasBalconies = false,
            hasRoomsCount = false,
            hasBathrooms = true,
            hasLevels = false,
            hasSquare = true,
            hasLandSquare = false,
            hasGarage = false,
            hasBathhouse = false,
            hasPool = false
        ),
        GARAGE to PropertyCharacteristicsConfig(
            hasFloor = false,
            hasTotalFloors = false,
            hasElevator = false,
            hasBalconies = false,
            hasRoomsCount = false,
            hasBathrooms = false,
            hasLevels = true,
            hasSquare = true,
            hasLandSquare = false,
            hasGarage = false,
            hasBathhouse = false,
            hasPool = false
        ),
        LAND to PropertyCharacteristicsConfig(
            hasFloor = false,
            hasTotalFloors = false,
            hasElevator = false,
            hasBalconies = false,
            hasRoomsCount = false,
            hasBathrooms = false,
            hasLevels = false,
            hasSquare = false,
            hasLandSquare = true,
            hasGarage = false,
            hasBathhouse = false,
            hasPool = false
        )
    )

    // Возвращает конфигурацию характеристик для указанного типа недвижимости
    fun getCharacteristicsForType(propertyType: String): PropertyCharacteristicsConfig {
        return typeCharacteristicsMap[propertyType] ?: PropertyCharacteristicsConfig.DEFAULT
    }
}

/**
 * Конфигурация характеристик для определенного типа недвижимости.
 * Определяет какие характеристики применимы к данному типу.
 */
data class PropertyCharacteristicsConfig(
    val hasFloor: Boolean,
    val hasTotalFloors: Boolean,
    val hasElevator: Boolean,
    val hasBalconies: Boolean,
    val hasRoomsCount: Boolean,
    val hasBathrooms: Boolean,
    val hasLevels: Boolean,
    val hasSquare: Boolean,
    val hasLandSquare: Boolean,
    val hasGarage: Boolean,
    val hasBathhouse: Boolean,
    val hasPool: Boolean
) {
    companion object {
        // Конфигурация по умолчанию, если тип недвижимости не найден
        val DEFAULT = PropertyCharacteristicsConfig(
            hasFloor = true,
            hasTotalFloors = true,
            hasElevator = true,
            hasBalconies = true,
            hasRoomsCount = true,
            hasBathrooms = true,
            hasLevels = false,
            hasSquare = true,
            hasLandSquare = false,
            hasGarage = false,
            hasBathhouse = false,
            hasPool = false
        )
    }
} 