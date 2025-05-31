package com.realestateassistant.pro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.realestateassistant.pro.data.local.converter.StringListConverter

/**
 * Entity класс для хранения клиентов в локальной базе данных
 */
@Entity(tableName = "clients")
@TypeConverters(StringListConverter::class)
data class ClientEntity(
    @PrimaryKey
    val id: String,
    
    // Общие поля
    val fullName: String,
    val phone: List<String>,
    val rentalType: String, // например, "длительная" или "посуточная"
    val comment: String,

    // Информация о клиенте
    val familyComposition: String?,
    val peopleCount: Int?,
    val childrenCount: Int?,
    val childrenAges: List<Int>,
    val hasPets: Boolean,
    val petTypes: List<String>,
    val petCount: Int?,
    val occupation: String?,
    val isSmokingClient: Boolean = false, // Является ли клиент курящим

    // Общие поля предпочтений по аренде
    val preferredDistrict: String?,
    val preferredAddress: String?,
    val additionalComments: String?,
    val urgencyLevel: String? = null,
    val budgetFlexibility: Boolean = false,
    val maxBudgetIncrease: Double? = null,
    val requirementFlexibility: List<String> = emptyList(),
    val priorityCriteria: List<String> = emptyList(),
    
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
    
    // Юридические предпочтения
    val needsOfficialAgreement: Boolean = false,
    val preferredTaxOption: String? = null,
    
    // Поля для длительной аренды
    val longTermBudgetMin: Double?,
    val longTermBudgetMax: Double?,
    val desiredPropertyType: String?,
    val desiredRoomsCount: Int?,
    val desiredArea: Double?,
    val additionalRequirements: String?,
    val legalPreferences: String?,
    /** Срок актуальности заявки клиента */
    val moveInDeadline: Long? = null,

    // Поля для посуточной аренды
    val shortTermBudgetMin: Double?,
    val shortTermBudgetMax: Double?,
    val shortTermCheckInDate: Long?,
    val shortTermCheckOutDate: Long?,
    val hasAdditionalGuests: Boolean = false,
    val shortTermGuests: Int?,
    val dailyBudget: Double?,
    val preferredShortTermDistrict: String?,
    val checkInOutConditions: String?,
    val additionalServices: String?,
    val additionalShortTermRequirements: String?,
    
    // Локальные системные поля
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
) 