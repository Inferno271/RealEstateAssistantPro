package com.realestateassistant.pro.presentation.model

import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.model.SeasonalPrice

data class PropertyFormState(
    val isLongTerm: Boolean = true,
    // Контактная информация
    val contactName: String = "",
    val contactPhone: List<String> = emptyList(),
    val additionalContactInfo: String = "",
    
    // Общая информация
    val propertyType: String = "",
    val address: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val district: String = "",
    val nearbyObjects: List<String> = emptyList(),
    val views: List<String> = emptyList(),
    val area: String = "",
    val roomsCount: String = "",
    val isStudio: Boolean = false,
    val layout: String = "",
    val floor: String = "",
    val totalFloors: String = "",
    val description: String = "",
    val management: List<String> = emptyList(),

    // Новые поля для разных типов недвижимости
    val levelsCount: String = "",
    val landArea: String = "",
    val hasGarage: Boolean = false,
    val garageSpaces: String = "",
    val hasBathhouse: Boolean = false,
    val hasPool: Boolean = false,
    val poolType: String = "",

    // Характеристики объекта
    val repairState: String = "",
    val bedsCount: String = "",
    val bathroomsCount: String = "",
    val bathroomType: String = "",
    val noFurniture: Boolean = false,
    val hasAppliances: Boolean = false,
    val heatingType: String = "",
    val balconiesCount: String = "",
    val elevatorsCount: String = "",
    val hasParking: Boolean = false,
    val parkingType: String = "",
    val parkingSpaces: String = "",
    val amenities: List<String> = emptyList(),
    val smokingAllowed: Boolean = false,

    // Условия проживания
    val childrenAllowed: Boolean = false,
    val minChildAge: String = "",
    val maxChildrenCount: String = "",
    val petsAllowed: Boolean = false,
    val maxPetsCount: String = "",
    val allowedPetTypes: List<String> = emptyList(),

    // Для длительной аренды
    val monthlyRent: String = "",
    val winterMonthlyRent: String = "",
    val summerMonthlyRent: String = "",
    val hasCompensationContract: Boolean = false,
    val isSelfEmployed: Boolean = false,
    val isPersonalIncomeTax: Boolean = false,
    val isCompanyIncomeTax: Boolean = false,
    val utilitiesIncluded: Boolean = false,
    val utilitiesCost: String = "",
    val minRentPeriod: String = "",
    val maxRentPeriod: String = "",
    val depositCustomAmount: String = "",
    val securityDeposit: String = "",
    val additionalExpenses: String = "",
    val longTermRules: String = "",

    // Для посуточной аренды
    val dailyPrice: String = "",
    val minStayDays: String = "",
    val maxStayDays: String = "",
    val maxGuests: String = "",
    val checkInTime: String = "",
    val checkOutTime: String = "",
    val shortTermDeposit: String = "",
    val shortTermDepositCustomAmount: String = "",
    val seasonalPrices: List<SeasonalPrice> = emptyList(),
    val weekdayPrice: String = "",
    val weekendPrice: String = "",
    val weeklyDiscount: String = "",
    val monthlyDiscount: String = "",
    val additionalServices: String = "",
    val shortTermRules: String = "",
    val cleaningService: Boolean = false,
    val cleaningDetails: String = "",
    val hasExtraBed: Boolean = false,
    val extraBedPrice: String = "",
    val partiesAllowed: Boolean = false,
    val specialOffers: String = "",
    val additionalComments: String = "",

    // Фото и документы
    val photos: List<String> = emptyList(),
    val documents: List<String> = emptyList()
) {
    fun toProperty(): Property {
        return Property(
            contactName = contactName,
            contactPhone = contactPhone,
            additionalContactInfo = additionalContactInfo.takeIf { it.isNotEmpty() },
            propertyType = propertyType,
            address = address,
            latitude = latitude.toDoubleOrNull(),
            longitude = longitude.toDoubleOrNull(),
            district = district,
            nearbyObjects = nearbyObjects,
            views = views,
            area = area.toDoubleOrNull() ?: 0.0,
            roomsCount = roomsCount.toIntOrNull() ?: 0,
            isStudio = isStudio,
            layout = layout,
            floor = floor.toIntOrNull() ?: 0,
            totalFloors = totalFloors.toIntOrNull() ?: 0,
            description = description.takeIf { it.isNotEmpty() },
            management = management,
            // Новые поля для разных типов недвижимости
            levelsCount = levelsCount.toIntOrNull() ?: 0,
            landArea = landArea.toDoubleOrNull() ?: 0.0,
            hasGarage = hasGarage,
            garageSpaces = garageSpaces.toIntOrNull() ?: 0,
            hasBathhouse = hasBathhouse,
            hasPool = hasPool,
            poolType = poolType,
            // Остальные поля
            repairState = repairState,
            bedsCount = bedsCount.toIntOrNull(),
            bathroomsCount = bathroomsCount.toIntOrNull(),
            bathroomType = bathroomType,
            noFurniture = noFurniture,
            hasAppliances = hasAppliances,
            heatingType = heatingType,
            balconiesCount = balconiesCount.toIntOrNull() ?: 0,
            elevatorsCount = elevatorsCount.toIntOrNull(),
            hasParking = hasParking,
            parkingType = parkingType.takeIf { hasParking },
            parkingSpaces = parkingSpaces.toIntOrNull(),
            amenities = amenities,
            smokingAllowed = smokingAllowed,
            childrenAllowed = childrenAllowed,
            minChildAge = minChildAge.takeIf { childrenAllowed },
            maxChildrenCount = maxChildrenCount.takeIf { childrenAllowed },
            petsAllowed = petsAllowed,
            maxPetsCount = maxPetsCount.takeIf { petsAllowed },
            allowedPetTypes = allowedPetTypes,
            monthlyRent = monthlyRent.toDoubleOrNull(),
            winterMonthlyRent = winterMonthlyRent.toDoubleOrNull(),
            summerMonthlyRent = summerMonthlyRent.toDoubleOrNull(),
            hasCompensationContract = hasCompensationContract,
            isSelfEmployed = isSelfEmployed,
            isPersonalIncomeTax = isPersonalIncomeTax,
            isCompanyIncomeTax = isCompanyIncomeTax,
            utilitiesIncluded = utilitiesIncluded,
            utilitiesCost = utilitiesCost.toDoubleOrNull(),
            minRentPeriod = minRentPeriod.takeIf { it.isNotEmpty() },
            maxRentPeriod = maxRentPeriod.toIntOrNull(),
            depositCustomAmount = depositCustomAmount.toDoubleOrNull(),
            securityDeposit = securityDeposit.toDoubleOrNull(),
            additionalExpenses = additionalExpenses.takeIf { it.isNotEmpty() },
            longTermRules = longTermRules.takeIf { it.isNotEmpty() },
            dailyPrice = dailyPrice.toDoubleOrNull(),
            minStayDays = minStayDays.toIntOrNull(),
            maxStayDays = maxStayDays.toIntOrNull(),
            maxGuests = maxGuests.toIntOrNull(),
            checkInTime = checkInTime,
            checkOutTime = checkOutTime,
            shortTermDeposit = shortTermDeposit.toDoubleOrNull(),
            shortTermDepositCustomAmount = shortTermDepositCustomAmount.toDoubleOrNull(),
            seasonalPrices = seasonalPrices,
            weekdayPrice = weekdayPrice.toDoubleOrNull(),
            weekendPrice = weekendPrice.toDoubleOrNull(),
            weeklyDiscount = weeklyDiscount.toDoubleOrNull(),
            monthlyDiscount = monthlyDiscount.toDoubleOrNull(),
            additionalServices = additionalServices.takeIf { it.isNotEmpty() },
            shortTermRules = shortTermRules.takeIf { it.isNotEmpty() },
            cleaningService = cleaningService,
            cleaningDetails = cleaningDetails.takeIf { cleaningService },
            hasExtraBed = hasExtraBed,
            extraBedPrice = extraBedPrice.toDoubleOrNull(),
            partiesAllowed = partiesAllowed,
            specialOffers = specialOffers.takeIf { it.isNotEmpty() },
            additionalComments = additionalComments.takeIf { it.isNotEmpty() },
            photos = photos,
            documents = documents
        )
    }
    
    companion object {
        fun fromProperty(property: Property): PropertyFormState {
            // Определяем тип аренды на основе наличия параметров
            val isLongTerm = property.monthlyRent != null || 
                            property.winterMonthlyRent != null || 
                            property.summerMonthlyRent != null
            
            return PropertyFormState(
                isLongTerm = isLongTerm,
                
                // Контактная информация
                contactName = property.contactName,
                contactPhone = property.contactPhone,
                additionalContactInfo = property.additionalContactInfo ?: "",
                
                // Общая информация
                propertyType = property.propertyType,
                address = property.address,
                latitude = property.latitude?.toString() ?: "",
                longitude = property.longitude?.toString() ?: "",
                district = property.district,
                nearbyObjects = property.nearbyObjects,
                views = property.views,
                area = property.area.toString(),
                roomsCount = property.roomsCount.toString(),
                isStudio = property.isStudio,
                layout = property.layout,
                floor = property.floor.toString(),
                totalFloors = property.totalFloors.toString(),
                description = property.description ?: "",
                management = property.management,
                
                // Поля для разных типов недвижимости
                levelsCount = property.levelsCount.toString(),
                landArea = property.landArea.toString(),
                hasGarage = property.hasGarage,
                garageSpaces = property.garageSpaces.toString(),
                hasBathhouse = property.hasBathhouse,
                hasPool = property.hasPool,
                poolType = property.poolType,
                
                // Характеристики объекта
                repairState = property.repairState,
                bedsCount = property.bedsCount?.toString() ?: "",
                bathroomsCount = property.bathroomsCount?.toString() ?: "",
                bathroomType = property.bathroomType,
                noFurniture = property.noFurniture,
                hasAppliances = property.hasAppliances,
                heatingType = property.heatingType,
                balconiesCount = property.balconiesCount.toString(),
                elevatorsCount = property.elevatorsCount?.toString() ?: "",
                hasParking = property.hasParking,
                parkingType = property.parkingType ?: "",
                parkingSpaces = property.parkingSpaces?.toString() ?: "",
                amenities = property.amenities,
                smokingAllowed = property.smokingAllowed,
                
                // Условия проживания
                childrenAllowed = property.childrenAllowed,
                minChildAge = property.minChildAge ?: "",
                maxChildrenCount = property.maxChildrenCount ?: "",
                petsAllowed = property.petsAllowed,
                maxPetsCount = property.maxPetsCount ?: "",
                allowedPetTypes = property.allowedPetTypes,
                
                // Для длительной аренды
                monthlyRent = property.monthlyRent?.toString() ?: "",
                winterMonthlyRent = property.winterMonthlyRent?.toString() ?: "",
                summerMonthlyRent = property.summerMonthlyRent?.toString() ?: "",
                hasCompensationContract = property.hasCompensationContract,
                isSelfEmployed = property.isSelfEmployed,
                isPersonalIncomeTax = property.isPersonalIncomeTax,
                isCompanyIncomeTax = property.isCompanyIncomeTax,
                utilitiesIncluded = property.utilitiesIncluded,
                utilitiesCost = property.utilitiesCost?.toString() ?: "",
                minRentPeriod = property.minRentPeriod ?: "",
                maxRentPeriod = property.maxRentPeriod?.toString() ?: "",
                depositCustomAmount = property.depositCustomAmount?.toString() ?: "",
                securityDeposit = property.securityDeposit?.toString() ?: "",
                additionalExpenses = property.additionalExpenses ?: "",
                longTermRules = property.longTermRules ?: "",
                
                // Для посуточной аренды
                dailyPrice = property.dailyPrice?.toString() ?: "",
                minStayDays = property.minStayDays?.toString() ?: "",
                maxStayDays = property.maxStayDays?.toString() ?: "",
                maxGuests = property.maxGuests?.toString() ?: "",
                checkInTime = property.checkInTime ?: "",
                checkOutTime = property.checkOutTime ?: "",
                shortTermDeposit = property.shortTermDeposit?.toString() ?: "",
                shortTermDepositCustomAmount = property.shortTermDepositCustomAmount?.toString() ?: "",
                seasonalPrices = property.seasonalPrices,
                weekdayPrice = property.weekdayPrice?.toString() ?: "",
                weekendPrice = property.weekendPrice?.toString() ?: "",
                weeklyDiscount = property.weeklyDiscount?.toString() ?: "",
                monthlyDiscount = property.monthlyDiscount?.toString() ?: "",
                additionalServices = property.additionalServices ?: "",
                shortTermRules = property.shortTermRules ?: "",
                cleaningService = property.cleaningService,
                cleaningDetails = property.cleaningDetails ?: "",
                hasExtraBed = property.hasExtraBed,
                extraBedPrice = property.extraBedPrice?.toString() ?: "",
                partiesAllowed = property.partiesAllowed,
                specialOffers = property.specialOffers ?: "",
                additionalComments = property.additionalComments ?: "",
                
                // Фото и документы
                photos = property.photos,
                documents = property.documents
            )
        }
    }
} 