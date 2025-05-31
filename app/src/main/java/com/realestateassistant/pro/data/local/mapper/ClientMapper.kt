package com.realestateassistant.pro.data.local.mapper

import com.realestateassistant.pro.data.local.entity.ClientEntity
import com.realestateassistant.pro.domain.model.Client

/**
 * Маппер для преобразования Client между Entity и Domain моделями
 */
object ClientMapper {
    /**
     * Преобразует Entity модель в Domain модель
     */
    fun mapToDomain(entity: ClientEntity): Client {
        return Client(
            id = entity.id,
            fullName = entity.fullName,
            phone = entity.phone,
            rentalType = entity.rentalType,
            comment = entity.comment,
            
            // Информация о клиенте
            familyComposition = entity.familyComposition,
            peopleCount = entity.peopleCount,
            childrenCount = entity.childrenCount,
            childrenAges = entity.childrenAges,
            hasPets = entity.hasPets,
            petTypes = entity.petTypes,
            petCount = entity.petCount,
            occupation = entity.occupation,
            isSmokingClient = entity.isSmokingClient,
            
            // Общие поля предпочтений по аренде
            preferredDistrict = entity.preferredDistrict,
            preferredAddress = entity.preferredAddress,
            additionalComments = entity.additionalComments,
            urgencyLevel = entity.urgencyLevel,
            budgetFlexibility = entity.budgetFlexibility,
            maxBudgetIncrease = entity.maxBudgetIncrease,
            requirementFlexibility = entity.requirementFlexibility,
            priorityCriteria = entity.priorityCriteria,
            
            // Новые поля предпочтений по удобствам и характеристикам
            preferredAmenities = entity.preferredAmenities,
            preferredViews = entity.preferredViews,
            preferredNearbyObjects = entity.preferredNearbyObjects,
            preferredRepairState = entity.preferredRepairState,
            preferredFloorMin = entity.preferredFloorMin,
            preferredFloorMax = entity.preferredFloorMax,
            needsElevator = entity.needsElevator,
            preferredBalconiesCount = entity.preferredBalconiesCount,
            preferredBathroomsCount = entity.preferredBathroomsCount,
            preferredBathroomType = entity.preferredBathroomType,
            needsFurniture = entity.needsFurniture,
            needsAppliances = entity.needsAppliances,
            preferredHeatingType = entity.preferredHeatingType,
            needsParking = entity.needsParking,
            preferredParkingType = entity.preferredParkingType,
            
            // Дополнительные предпочтения для разных типов недвижимости
            needsYard = entity.needsYard,
            preferredYardArea = entity.preferredYardArea,
            needsGarage = entity.needsGarage,
            preferredGarageSpaces = entity.preferredGarageSpaces,
            needsBathhouse = entity.needsBathhouse,
            needsPool = entity.needsPool,
            
            // Юридические предпочтения
            needsOfficialAgreement = entity.needsOfficialAgreement,
            preferredTaxOption = entity.preferredTaxOption,
            
            // Поля для длительной аренды
            longTermBudgetMin = entity.longTermBudgetMin,
            longTermBudgetMax = entity.longTermBudgetMax,
            desiredPropertyType = entity.desiredPropertyType,
            desiredRoomsCount = entity.desiredRoomsCount,
            desiredArea = entity.desiredArea,
            additionalRequirements = entity.additionalRequirements,
            legalPreferences = entity.legalPreferences,
            moveInDeadline = entity.moveInDeadline,
            
            // Поля для посуточной аренды
            shortTermBudgetMin = entity.shortTermBudgetMin,
            shortTermBudgetMax = entity.shortTermBudgetMax,
            shortTermCheckInDate = entity.shortTermCheckInDate,
            shortTermCheckOutDate = entity.shortTermCheckOutDate,
            hasAdditionalGuests = entity.hasAdditionalGuests,
            shortTermGuests = entity.shortTermGuests,
            dailyBudget = entity.dailyBudget,
            preferredShortTermDistrict = entity.preferredShortTermDistrict,
            checkInOutConditions = entity.checkInOutConditions,
            additionalServices = entity.additionalServices,
            additionalShortTermRequirements = entity.additionalShortTermRequirements,
            
            // Служебные поля
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    /**
     * Преобразует Domain модель в Entity модель
     */
    fun mapToEntity(domain: Client): ClientEntity {
        return ClientEntity(
            id = domain.id,
            fullName = domain.fullName,
            phone = domain.phone,
            rentalType = domain.rentalType,
            comment = domain.comment,
            
            // Информация о клиенте
            familyComposition = domain.familyComposition,
            peopleCount = domain.peopleCount,
            childrenCount = domain.childrenCount,
            childrenAges = domain.childrenAges,
            hasPets = domain.hasPets,
            petTypes = domain.petTypes,
            petCount = domain.petCount,
            occupation = domain.occupation,
            isSmokingClient = domain.isSmokingClient,
            
            // Общие поля предпочтений по аренде
            preferredDistrict = domain.preferredDistrict,
            preferredAddress = domain.preferredAddress,
            additionalComments = domain.additionalComments,
            urgencyLevel = domain.urgencyLevel,
            budgetFlexibility = domain.budgetFlexibility,
            maxBudgetIncrease = domain.maxBudgetIncrease,
            requirementFlexibility = domain.requirementFlexibility,
            priorityCriteria = domain.priorityCriteria,
            
            // Новые поля предпочтений по удобствам и характеристикам
            preferredAmenities = domain.preferredAmenities,
            preferredViews = domain.preferredViews,
            preferredNearbyObjects = domain.preferredNearbyObjects,
            preferredRepairState = domain.preferredRepairState,
            preferredFloorMin = domain.preferredFloorMin,
            preferredFloorMax = domain.preferredFloorMax,
            needsElevator = domain.needsElevator,
            preferredBalconiesCount = domain.preferredBalconiesCount,
            preferredBathroomsCount = domain.preferredBathroomsCount,
            preferredBathroomType = domain.preferredBathroomType,
            needsFurniture = domain.needsFurniture,
            needsAppliances = domain.needsAppliances,
            preferredHeatingType = domain.preferredHeatingType,
            needsParking = domain.needsParking,
            preferredParkingType = domain.preferredParkingType,
            
            // Дополнительные предпочтения для разных типов недвижимости
            needsYard = domain.needsYard,
            preferredYardArea = domain.preferredYardArea,
            needsGarage = domain.needsGarage,
            preferredGarageSpaces = domain.preferredGarageSpaces,
            needsBathhouse = domain.needsBathhouse,
            needsPool = domain.needsPool,
            
            // Юридические предпочтения
            needsOfficialAgreement = domain.needsOfficialAgreement,
            preferredTaxOption = domain.preferredTaxOption,
            
            // Поля для длительной аренды
            longTermBudgetMin = domain.longTermBudgetMin,
            longTermBudgetMax = domain.longTermBudgetMax,
            desiredPropertyType = domain.desiredPropertyType,
            desiredRoomsCount = domain.desiredRoomsCount,
            desiredArea = domain.desiredArea,
            additionalRequirements = domain.additionalRequirements,
            legalPreferences = domain.legalPreferences,
            moveInDeadline = domain.moveInDeadline,
            
            // Поля для посуточной аренды
            shortTermBudgetMin = domain.shortTermBudgetMin,
            shortTermBudgetMax = domain.shortTermBudgetMax,
            shortTermCheckInDate = domain.shortTermCheckInDate,
            shortTermCheckOutDate = domain.shortTermCheckOutDate,
            hasAdditionalGuests = domain.hasAdditionalGuests,
            shortTermGuests = domain.shortTermGuests,
            dailyBudget = domain.dailyBudget,
            preferredShortTermDistrict = domain.preferredShortTermDistrict,
            checkInOutConditions = domain.checkInOutConditions,
            additionalServices = domain.additionalServices,
            additionalShortTermRequirements = domain.additionalShortTermRequirements,
            
            // Системные поля
            createdAt = domain.createdAt,
            updatedAt = System.currentTimeMillis(),
            isSynced = false
        )
    }
} 