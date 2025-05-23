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
            email = entity.email,
            rentalType = entity.rentalType,
            comment = entity.comment,
            preferredAddress = entity.preferredAddress,
            longTermBudgetMin = entity.longTermBudgetMin,
            longTermBudgetMax = entity.longTermBudgetMax,
            desiredPropertyType = entity.desiredPropertyType,
            desiredRoomsCount = entity.desiredRoomsCount,
            peopleCount = entity.peopleCount,
            childrenCount = entity.childrenCount,
            petsInfo = entity.petsInfo,
            desiredArea = entity.desiredArea,
            additionalRequirements = entity.additionalRequirements,
            legalPreferences = entity.legalPreferences,
            shortTermCheckInDate = entity.shortTermCheckInDate,
            shortTermCheckOutDate = entity.shortTermCheckOutDate,
            shortTermGuests = entity.shortTermGuests,
            dailyBudget = entity.dailyBudget,
            preferredShortTermDistrict = entity.preferredShortTermDistrict,
            checkInOutConditions = entity.checkInOutConditions,
            additionalServices = entity.additionalServices,
            additionalShortTermRequirements = entity.additionalShortTermRequirements
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
            email = domain.email,
            rentalType = domain.rentalType,
            comment = domain.comment,
            preferredAddress = domain.preferredAddress,
            longTermBudgetMin = domain.longTermBudgetMin,
            longTermBudgetMax = domain.longTermBudgetMax,
            desiredPropertyType = domain.desiredPropertyType,
            desiredRoomsCount = domain.desiredRoomsCount,
            peopleCount = domain.peopleCount,
            childrenCount = domain.childrenCount,
            petsInfo = domain.petsInfo,
            desiredArea = domain.desiredArea,
            additionalRequirements = domain.additionalRequirements,
            legalPreferences = domain.legalPreferences,
            shortTermCheckInDate = domain.shortTermCheckInDate,
            shortTermCheckOutDate = domain.shortTermCheckOutDate,
            shortTermGuests = domain.shortTermGuests,
            dailyBudget = domain.dailyBudget,
            preferredShortTermDistrict = domain.preferredShortTermDistrict,
            checkInOutConditions = domain.checkInOutConditions,
            additionalServices = domain.additionalServices,
            additionalShortTermRequirements = domain.additionalShortTermRequirements,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            isSynced = false
        )
    }
} 