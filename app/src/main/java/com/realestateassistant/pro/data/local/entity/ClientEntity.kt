package com.realestateassistant.pro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity класс для хранения клиентов в локальной базе данных
 */
@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey
    val id: String,
    
    // Общие поля
    val fullName: String,
    val phone: String,
    val email: String,
    val rentalType: String, // например, "длительная" или "посуточная"
    val comment: String,

    // Поля для длительной аренды
    val preferredAddress: String?,
    val longTermBudgetMin: Double?,
    val longTermBudgetMax: Double?,
    val desiredPropertyType: String?,
    val desiredRoomsCount: Int?,
    val peopleCount: Int?,
    val childrenCount: Int?,
    /** Возраст детей, указанный в форме */
    val childrenAge: String? = null,
    val petsInfo: String?,
    val desiredArea: Double?,
    val additionalRequirements: String?,
    val legalPreferences: String?,
    /** Срок актуальности заявки клиента */
    val moveInDeadline: Long? = null,
    /** Состав семьи, указанный в форме */
    val familyComposition: String? = null,

    // Поля для посуточной аренды
    val shortTermCheckInDate: Long?,
    val shortTermCheckOutDate: Long?,
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