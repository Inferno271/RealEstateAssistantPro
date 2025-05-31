package com.realestateassistant.pro.presentation.model

import com.realestateassistant.pro.domain.model.Client
import java.util.UUID

/**
 * Модель состояния формы клиента.
 * Используется для хранения данных при создании и редактировании клиента.
 */
data class ClientFormState(
    val id: String = UUID.randomUUID().toString(),
    // Общие поля
    val fullName: String = "",
    val phone: List<String> = emptyList(),
    val rentalType: RentalType = RentalType.LONG_TERM,
    val comment: String = "",

    // Информация о клиенте
    val familyComposition: String = "",
    val peopleCount: String = "",
    val childrenCount: String = "",
    val childrenAges: List<String> = emptyList(),
    val hasPets: Boolean = false,
    val petTypes: List<String> = emptyList(),
    val petCount: String = "",
    val occupation: String = "",
    val isSmokingClient: Boolean = false, // Является ли клиент курящим

    // Общие поля предпочтений по аренде
    val preferredDistrict: String = "",
    val preferredAddress: String = "",
    val additionalComments: String = "",
    val urgencyLevel: String = "", // Срочность поиска
    val budgetFlexibility: Boolean = false, // Гибкость бюджета
    val maxBudgetIncrease: String = "", // Максимальное увеличение бюджета
    val requirementFlexibility: List<String> = emptyList(), // Гибкие требования
    val priorityCriteria: List<String> = emptyList(), // Приоритетные критерии
    
    // Новые поля предпочтений по удобствам и характеристикам
    val preferredAmenities: List<String> = emptyList(), // Желаемые удобства
    val preferredViews: List<String> = emptyList(), // Предпочтительные виды из окон
    val preferredNearbyObjects: List<String> = emptyList(), // Предпочтительные объекты инфраструктуры
    val preferredRepairState: String = "", // Предпочтительное состояние ремонта
    val preferredFloorMin: String = "", // Минимальный этаж
    val preferredFloorMax: String = "", // Максимальный этаж
    val needsElevator: Boolean = false, // Нужен ли лифт
    val preferredBalconiesCount: String = "", // Предпочтительное количество балконов
    val preferredBathroomsCount: String = "", // Предпочтительное количество санузлов
    val preferredBathroomType: String = "", // Предпочтительный тип санузла
    val needsFurniture: Boolean = true, // Нужна ли мебель
    val needsAppliances: Boolean = true, // Нужна ли бытовая техника
    val preferredHeatingType: String = "", // Предпочтительный тип отопления
    val needsParking: Boolean = false, // Нужна ли парковка
    val preferredParkingType: String = "", // Предпочтительный тип парковки
    
    // Дополнительные предпочтения для разных типов недвижимости
    val needsYard: Boolean = false, // Нужен ли двор/участок
    val preferredYardArea: String = "", // Предпочтительная площадь участка
    val needsGarage: Boolean = false, // Нужен ли гараж
    val preferredGarageSpaces: String = "", // Предпочтительное количество мест в гараже
    val needsBathhouse: Boolean = false, // Нужна ли баня
    val needsPool: Boolean = false, // Нужен ли бассейн
    
    // Юридические предпочтения (расширенные)
    val needsOfficialAgreement: Boolean = false, // Нужен ли официальный договор
    val preferredTaxOption: String = "", // Предпочтительный вариант налогообложения
    
    // Поля для длительной аренды
    val longTermBudgetMin: String = "",
    val longTermBudgetMax: String = "",
    val desiredPropertyType: String = "",
    val desiredRoomsCount: String = "",
    val desiredArea: String = "",
    val additionalRequirements: String = "",
    val legalPreferences: String = "",
    val moveInDeadline: Long? = null,

    // Поля для посуточной аренды
    val shortTermBudgetMin: String = "",
    val shortTermBudgetMax: String = "",
    val shortTermCheckInDate: Long? = null,
    val shortTermCheckOutDate: Long? = null,
    val hasAdditionalGuests: Boolean = false, // Будут ли дополнительные гости
    val shortTermGuests: String = "",
    val dailyBudget: String = "",
    val preferredShortTermDistrict: String = "",
    val checkInOutConditions: String = "",
    val additionalServices: String = "",
    val additionalShortTermRequirements: String = ""
) {
    /**
     * Преобразует состояние формы в модель клиента.
     * 
     * @return Модель клиента, созданная на основе состояния формы
     */
    fun toClient(): Client {
        return Client(
            id = id,
            // Общие поля
            fullName = fullName,
            phone = phone,
            rentalType = rentalType.toStringValue(),
            comment = comment,

            // Информация о клиенте
            familyComposition = familyComposition.takeIf { it.isNotEmpty() },
            peopleCount = peopleCount.toIntOrNull(),
            childrenCount = childrenCount.toIntOrNull(),
            childrenAges = childrenAges.mapNotNull { it.toIntOrNull() },
            hasPets = hasPets,
            petTypes = petTypes,
            petCount = petCount.toIntOrNull(),
            occupation = occupation.takeIf { it.isNotEmpty() },
            isSmokingClient = isSmokingClient,

            // Общие поля предпочтений по аренде
            preferredDistrict = preferredDistrict.takeIf { it.isNotEmpty() },
            preferredAddress = preferredAddress.takeIf { it.isNotEmpty() },
            additionalComments = additionalComments.takeIf { it.isNotEmpty() },
            urgencyLevel = urgencyLevel.takeIf { it.isNotEmpty() },
            budgetFlexibility = budgetFlexibility,
            maxBudgetIncrease = maxBudgetIncrease.toDoubleOrNull(),
            requirementFlexibility = requirementFlexibility,
            priorityCriteria = priorityCriteria,
            
            // Новые поля предпочтений
            preferredAmenities = preferredAmenities,
            preferredViews = preferredViews,
            preferredNearbyObjects = preferredNearbyObjects,
            preferredRepairState = preferredRepairState.takeIf { it.isNotEmpty() },
            preferredFloorMin = preferredFloorMin.toIntOrNull(),
            preferredFloorMax = preferredFloorMax.toIntOrNull(),
            needsElevator = needsElevator,
            preferredBalconiesCount = preferredBalconiesCount.toIntOrNull(),
            preferredBathroomsCount = preferredBathroomsCount.toIntOrNull(),
            preferredBathroomType = preferredBathroomType.takeIf { it.isNotEmpty() },
            needsFurniture = needsFurniture,
            needsAppliances = needsAppliances,
            preferredHeatingType = preferredHeatingType.takeIf { it.isNotEmpty() },
            needsParking = needsParking,
            preferredParkingType = preferredParkingType.takeIf { it.isNotEmpty() },
            
            // Дополнительные предпочтения
            needsYard = needsYard,
            preferredYardArea = preferredYardArea.toDoubleOrNull(),
            needsGarage = needsGarage,
            preferredGarageSpaces = preferredGarageSpaces.toIntOrNull(),
            needsBathhouse = needsBathhouse,
            needsPool = needsPool,
            
            // Юридические предпочтения
            needsOfficialAgreement = needsOfficialAgreement,
            preferredTaxOption = preferredTaxOption.takeIf { it.isNotEmpty() },
            
            // Поля для длительной аренды
            longTermBudgetMin = longTermBudgetMin.toDoubleOrNull(),
            longTermBudgetMax = longTermBudgetMax.toDoubleOrNull(),
            desiredPropertyType = desiredPropertyType.takeIf { it.isNotEmpty() },
            desiredRoomsCount = desiredRoomsCount.toIntOrNull(),
            desiredArea = desiredArea.toDoubleOrNull(),
            additionalRequirements = additionalRequirements.takeIf { it.isNotEmpty() },
            legalPreferences = legalPreferences.takeIf { it.isNotEmpty() },
            moveInDeadline = moveInDeadline,

            // Поля для посуточной аренды
            shortTermBudgetMin = shortTermBudgetMin.toDoubleOrNull(),
            shortTermBudgetMax = shortTermBudgetMax.toDoubleOrNull(),
            shortTermCheckInDate = shortTermCheckInDate,
            shortTermCheckOutDate = shortTermCheckOutDate,
            hasAdditionalGuests = hasAdditionalGuests,
            shortTermGuests = shortTermGuests.toIntOrNull(),
            dailyBudget = dailyBudget.toDoubleOrNull(),
            preferredShortTermDistrict = preferredShortTermDistrict.takeIf { it.isNotEmpty() },
            checkInOutConditions = checkInOutConditions.takeIf { it.isNotEmpty() },
            additionalServices = additionalServices.takeIf { it.isNotEmpty() },
            additionalShortTermRequirements = additionalShortTermRequirements.takeIf { it.isNotEmpty() }
        )
    }

    companion object {
        /**
         * Создает состояние формы из модели клиента.
         * 
         * @param client Модель клиента
         * @return Состояние формы, созданное на основе модели клиента
         */
        fun fromClient(client: Client): ClientFormState {
            return ClientFormState(
                id = client.id,
                // Общие поля
                fullName = client.fullName,
                phone = client.phone,
                rentalType = RentalType.fromString(client.rentalType),
                comment = client.comment,

                // Информация о клиенте
                familyComposition = client.familyComposition ?: "",
                peopleCount = client.peopleCount?.toString() ?: "",
                childrenCount = client.childrenCount?.toString() ?: "",
                childrenAges = client.childrenAges.map { it.toString() },
                hasPets = client.hasPets,
                petTypes = client.petTypes,
                petCount = client.petCount?.toString() ?: "",
                occupation = client.occupation ?: "",
                isSmokingClient = client.isSmokingClient,

                // Общие поля предпочтений по аренде
                preferredDistrict = client.preferredDistrict ?: "",
                preferredAddress = client.preferredAddress ?: "",
                additionalComments = client.additionalComments ?: "",
                urgencyLevel = client.urgencyLevel ?: "",
                budgetFlexibility = client.budgetFlexibility,
                maxBudgetIncrease = client.maxBudgetIncrease?.toString() ?: "",
                requirementFlexibility = client.requirementFlexibility,
                priorityCriteria = client.priorityCriteria,
                
                // Новые поля предпочтений
                preferredAmenities = client.preferredAmenities,
                preferredViews = client.preferredViews,
                preferredNearbyObjects = client.preferredNearbyObjects,
                preferredRepairState = client.preferredRepairState ?: "",
                preferredFloorMin = client.preferredFloorMin?.toString() ?: "",
                preferredFloorMax = client.preferredFloorMax?.toString() ?: "",
                needsElevator = client.needsElevator,
                preferredBalconiesCount = client.preferredBalconiesCount?.toString() ?: "",
                preferredBathroomsCount = client.preferredBathroomsCount?.toString() ?: "",
                preferredBathroomType = client.preferredBathroomType ?: "",
                needsFurniture = client.needsFurniture,
                needsAppliances = client.needsAppliances,
                preferredHeatingType = client.preferredHeatingType ?: "",
                needsParking = client.needsParking,
                preferredParkingType = client.preferredParkingType ?: "",
                
                // Дополнительные предпочтения
                needsYard = client.needsYard,
                preferredYardArea = client.preferredYardArea?.toString() ?: "",
                needsGarage = client.needsGarage,
                preferredGarageSpaces = client.preferredGarageSpaces?.toString() ?: "",
                needsBathhouse = client.needsBathhouse,
                needsPool = client.needsPool,
                
                // Юридические предпочтения
                needsOfficialAgreement = client.needsOfficialAgreement,
                preferredTaxOption = client.preferredTaxOption ?: "",
                
                // Поля для длительной аренды
                longTermBudgetMin = client.longTermBudgetMin?.toString() ?: "",
                longTermBudgetMax = client.longTermBudgetMax?.toString() ?: "",
                desiredPropertyType = client.desiredPropertyType ?: "",
                desiredRoomsCount = client.desiredRoomsCount?.toString() ?: "",
                desiredArea = client.desiredArea?.toString() ?: "",
                additionalRequirements = client.additionalRequirements ?: "",
                legalPreferences = client.legalPreferences ?: "",
                moveInDeadline = client.moveInDeadline,

                // Поля для посуточной аренды
                shortTermBudgetMin = client.shortTermBudgetMin?.toString() ?: "",
                shortTermBudgetMax = client.shortTermBudgetMax?.toString() ?: "",
                shortTermCheckInDate = client.shortTermCheckInDate,
                shortTermCheckOutDate = client.shortTermCheckOutDate,
                hasAdditionalGuests = client.hasAdditionalGuests,
                shortTermGuests = client.shortTermGuests?.toString() ?: "",
                dailyBudget = client.dailyBudget?.toString() ?: "",
                preferredShortTermDistrict = client.preferredShortTermDistrict ?: "",
                checkInOutConditions = client.checkInOutConditions ?: "",
                additionalServices = client.additionalServices ?: "",
                additionalShortTermRequirements = client.additionalShortTermRequirements ?: ""
            )
        }
    }
} 