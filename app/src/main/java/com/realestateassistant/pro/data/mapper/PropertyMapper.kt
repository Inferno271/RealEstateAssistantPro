package com.realestateassistant.pro.data.mapper

import com.realestateassistant.pro.data.local.entity.PropertyEntity
import com.realestateassistant.pro.data.local.entity.SeasonalPriceEntity
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.model.PropertyStatus
import com.realestateassistant.pro.domain.model.SeasonalPrice
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Маппер для преобразования Property между Entity и Domain моделями
 */
@Singleton
class PropertyMapper @Inject constructor() {
    /**
     * Преобразует Entity модель в Domain модель
     */
    fun mapToDomain(entity: PropertyEntity): Property {
        return Property(
            id = entity.id,
            contactName = entity.contactName,
            contactPhone = entity.contactPhone,
            additionalContactInfo = entity.additionalContactInfo,
            propertyType = entity.propertyType,
            address = entity.address,
            latitude = entity.latitude,
            longitude = entity.longitude,
            district = entity.district,
            nearbyObjects = entity.nearbyObjects,
            views = entity.views,
            area = entity.area,
            roomsCount = entity.roomsCount,
            isStudio = entity.isStudio,
            layout = entity.layout,
            floor = entity.floor,
            totalFloors = entity.totalFloors,
            description = entity.description,
            management = entity.management,
            levelsCount = entity.levelsCount,
            landArea = entity.landArea,
            hasGarage = entity.hasGarage,
            garageSpaces = entity.garageSpaces,
            hasBathhouse = entity.hasBathhouse,
            hasPool = entity.hasPool,
            poolType = entity.poolType,
            repairState = entity.repairState,
            bedsCount = entity.bedsCount,
            bathroomsCount = entity.bathroomsCount,
            bathroomType = entity.bathroomType,
            noFurniture = entity.noFurniture,
            hasAppliances = entity.hasAppliances,
            heatingType = entity.heatingType,
            balconiesCount = entity.balconiesCount,
            elevatorsCount = entity.elevatorsCount,
            hasParking = entity.hasParking,
            parkingType = entity.parkingType,
            parkingSpaces = entity.parkingSpaces,
            amenities = entity.amenities,
            smokingAllowed = entity.smokingAllowed,
            childrenAllowed = entity.childrenAllowed,
            minChildAge = entity.minChildAge,
            maxChildrenCount = entity.maxChildrenCount,
            petsAllowed = entity.petsAllowed,
            maxPetsCount = entity.maxPetsCount,
            allowedPetTypes = entity.allowedPetTypes,
            monthlyRent = entity.monthlyRent,
            winterMonthlyRent = entity.winterMonthlyRent,
            summerMonthlyRent = entity.summerMonthlyRent,
            hasCompensationContract = entity.hasCompensationContract,
            isSelfEmployed = entity.isSelfEmployed,
            isPersonalIncomeTax = entity.isPersonalIncomeTax,
            isCompanyIncomeTax = entity.isCompanyIncomeTax,
            utilitiesIncluded = entity.utilitiesIncluded,
            utilitiesCost = entity.utilitiesCost,
            minRentPeriod = entity.minRentPeriod,
            maxRentPeriod = entity.maxRentPeriod,
            depositCustomAmount = entity.depositCustomAmount,
            securityDeposit = entity.securityDeposit,
            additionalExpenses = entity.additionalExpenses,
            longTermRules = entity.longTermRules,
            dailyPrice = entity.dailyPrice,
            minStayDays = entity.minStayDays,
            maxStayDays = entity.maxStayDays,
            maxGuests = entity.maxGuests,
            checkInTime = entity.checkInTime,
            checkOutTime = entity.checkOutTime,
            shortTermDeposit = entity.shortTermDeposit,
            shortTermDepositCustomAmount = entity.shortTermDepositCustomAmount,
            seasonalPrices = entity.seasonalPrices.map { mapSeasonalPriceToDomain(it) },
            weekdayPrice = entity.weekdayPrice,
            weekendPrice = entity.weekendPrice,
            weeklyDiscount = entity.weeklyDiscount,
            monthlyDiscount = entity.monthlyDiscount,
            additionalServices = entity.additionalServices,
            shortTermRules = entity.shortTermRules,
            cleaningService = entity.cleaningService,
            cleaningDetails = entity.cleaningDetails,
            hasExtraBed = entity.hasExtraBed,
            extraBedPrice = entity.extraBedPrice,
            partiesAllowed = entity.partiesAllowed,
            specialOffers = entity.specialOffers,
            additionalComments = entity.additionalComments,
            photos = entity.photos,
            documents = entity.documents,
            status = try {
                PropertyStatus.valueOf(entity.status)
            } catch (e: Exception) {
                PropertyStatus.AVAILABLE
            },
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    /**
     * Преобразует Domain модель в Entity модель
     */
    fun toEntity(domain: Property): PropertyEntity {
        return PropertyEntity(
            id = domain.id,
            contactName = domain.contactName,
            contactPhone = domain.contactPhone,
            additionalContactInfo = domain.additionalContactInfo,
            propertyType = domain.propertyType,
            address = domain.address,
            latitude = domain.latitude,
            longitude = domain.longitude,
            district = domain.district,
            nearbyObjects = domain.nearbyObjects,
            views = domain.views,
            area = domain.area,
            roomsCount = domain.roomsCount,
            isStudio = domain.isStudio,
            layout = domain.layout,
            floor = domain.floor,
            totalFloors = domain.totalFloors,
            description = domain.description,
            management = domain.management,
            levelsCount = domain.levelsCount,
            landArea = domain.landArea,
            hasGarage = domain.hasGarage,
            garageSpaces = domain.garageSpaces,
            hasBathhouse = domain.hasBathhouse,
            hasPool = domain.hasPool,
            poolType = domain.poolType,
            repairState = domain.repairState,
            bedsCount = domain.bedsCount,
            bathroomsCount = domain.bathroomsCount,
            bathroomType = domain.bathroomType,
            noFurniture = domain.noFurniture,
            hasAppliances = domain.hasAppliances,
            heatingType = domain.heatingType,
            balconiesCount = domain.balconiesCount,
            elevatorsCount = domain.elevatorsCount,
            hasParking = domain.hasParking,
            parkingType = domain.parkingType,
            parkingSpaces = domain.parkingSpaces,
            amenities = domain.amenities,
            smokingAllowed = domain.smokingAllowed,
            childrenAllowed = domain.childrenAllowed,
            minChildAge = domain.minChildAge,
            maxChildrenCount = domain.maxChildrenCount,
            petsAllowed = domain.petsAllowed,
            maxPetsCount = domain.maxPetsCount,
            allowedPetTypes = domain.allowedPetTypes,
            monthlyRent = domain.monthlyRent,
            winterMonthlyRent = domain.winterMonthlyRent,
            summerMonthlyRent = domain.summerMonthlyRent,
            hasCompensationContract = domain.hasCompensationContract,
            isSelfEmployed = domain.isSelfEmployed,
            isPersonalIncomeTax = domain.isPersonalIncomeTax,
            isCompanyIncomeTax = domain.isCompanyIncomeTax,
            utilitiesIncluded = domain.utilitiesIncluded,
            utilitiesCost = domain.utilitiesCost,
            minRentPeriod = domain.minRentPeriod,
            maxRentPeriod = domain.maxRentPeriod,
            depositCustomAmount = domain.depositCustomAmount,
            securityDeposit = domain.securityDeposit,
            additionalExpenses = domain.additionalExpenses,
            longTermRules = domain.longTermRules,
            dailyPrice = domain.dailyPrice,
            minStayDays = domain.minStayDays,
            maxStayDays = domain.maxStayDays,
            maxGuests = domain.maxGuests,
            checkInTime = domain.checkInTime,
            checkOutTime = domain.checkOutTime,
            shortTermDeposit = domain.shortTermDeposit,
            shortTermDepositCustomAmount = domain.shortTermDepositCustomAmount,
            seasonalPrices = domain.seasonalPrices.map { mapSeasonalPriceToEntity(it) },
            weekdayPrice = domain.weekdayPrice,
            weekendPrice = domain.weekendPrice,
            weeklyDiscount = domain.weeklyDiscount,
            monthlyDiscount = domain.monthlyDiscount,
            additionalServices = domain.additionalServices,
            shortTermRules = domain.shortTermRules,
            cleaningService = domain.cleaningService,
            cleaningDetails = domain.cleaningDetails,
            hasExtraBed = domain.hasExtraBed,
            extraBedPrice = domain.extraBedPrice,
            partiesAllowed = domain.partiesAllowed,
            specialOffers = domain.specialOffers,
            additionalComments = domain.additionalComments,
            photos = domain.photos,
            documents = domain.documents,
            status = domain.status.name,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            isSynced = false
        )
    }

    /**
     * Преобразует Entity модель сезонной цены в Domain модель
     */
    private fun mapSeasonalPriceToDomain(entity: SeasonalPriceEntity): SeasonalPrice {
        return SeasonalPrice(
            startDate = entity.startDate,
            endDate = entity.endDate,
            price = entity.price
        )
    }

    /**
     * Преобразует Domain модель сезонной цены в Entity модель
     */
    private fun mapSeasonalPriceToEntity(domain: SeasonalPrice): SeasonalPriceEntity {
        return SeasonalPriceEntity(
            startDate = domain.startDate,
            endDate = domain.endDate,
            price = domain.price
        )
    }
} 