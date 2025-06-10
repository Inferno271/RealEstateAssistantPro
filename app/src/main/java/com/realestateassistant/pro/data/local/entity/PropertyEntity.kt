package com.realestateassistant.pro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.realestateassistant.pro.data.local.converter.ListStringConverter
import com.realestateassistant.pro.data.local.converter.SeasonalPriceListConverter

/**
 * Entity класс для хранения объектов недвижимости в локальной базе данных
 */
@Entity(tableName = "properties")
@TypeConverters(ListStringConverter::class, SeasonalPriceListConverter::class)
data class PropertyEntity(
    @PrimaryKey
    val id: String,
    
    // Контактная информация
    val contactName: String,
    val contactPhone: List<String>,
    val additionalContactInfo: String?,

    // Общая информация
    val propertyType: String,
    val address: String,
    val latitude: Double?,
    val longitude: Double?,
    val district: String,
    val nearbyObjects: List<String>,
    val views: List<String>,
    val area: Double,
    val roomsCount: Int,
    val isStudio: Boolean,
    val layout: String,
    val floor: Int,
    val totalFloors: Int,
    val description: String?,
    val management: List<String>,

    // Новые поля для разных типов недвижимости
    val levelsCount: Int,
    val landArea: Double,
    val hasGarage: Boolean,
    val garageSpaces: Int,
    val hasBathhouse: Boolean,
    val hasPool: Boolean,
    val poolType: String,

    // Характеристики объекта
    val repairState: String,
    val bedsCount: Int?,
    val bathroomsCount: Int?,
    val bathroomType: String,
    val noFurniture: Boolean,
    val hasAppliances: Boolean,
    val heatingType: String,
    val balconiesCount: Int,
    val elevatorsCount: Int?,
    val hasParking: Boolean,
    val parkingType: String?,
    val parkingSpaces: Int?,
    val amenities: List<String>,
    val smokingAllowed: Boolean,

    // Условия проживания
    val childrenAllowed: Boolean,
    val minChildAge: String?,
    val maxChildrenCount: String?,
    val petsAllowed: Boolean,
    val maxPetsCount: String?,
    val allowedPetTypes: List<String>,

    // Для длительной аренды
    val monthlyRent: Double?,
    val winterMonthlyRent: Double?,
    val summerMonthlyRent: Double?,
    val hasCompensationContract: Boolean,
    val isSelfEmployed: Boolean,
    val isPersonalIncomeTax: Boolean,
    val isCompanyIncomeTax: Boolean,
    val utilitiesIncluded: Boolean,
    val utilitiesCost: Double?,
    val minRentPeriod: String?,
    val maxRentPeriod: Int?,
    val depositCustomAmount: Double?,
    val securityDeposit: Double?,
    val additionalExpenses: String?,
    val longTermRules: String?,

    // Для посуточной аренды
    val dailyPrice: Double?,
    val minStayDays: Int?,
    val maxStayDays: Int?,
    val maxGuests: Int?,
    val checkInTime: String?,
    val checkOutTime: String?,
    val shortTermDeposit: Double?,
    val shortTermDepositCustomAmount: Double?,
    val seasonalPrices: List<SeasonalPriceEntity>,
    val weekdayPrice: Double?,
    val weekendPrice: Double?,
    val weeklyDiscount: Double?,
    val monthlyDiscount: Double?,
    val additionalServices: String?,
    val shortTermRules: String?,
    val cleaningService: Boolean,
    val cleaningDetails: String?,
    val hasExtraBed: Boolean,
    val extraBedPrice: Double?,
    val partiesAllowed: Boolean,
    val specialOffers: String?,
    val additionalComments: String?,

    // Фото и документы
    val photos: List<String>,
    val documents: List<String>,
    
    // Статус объекта
    val status: String = "AVAILABLE",
    
    // Локальные системные поля
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

data class SeasonalPriceEntity(
    val startDate: Long,
    val endDate: Long,
    val price: Double
) 