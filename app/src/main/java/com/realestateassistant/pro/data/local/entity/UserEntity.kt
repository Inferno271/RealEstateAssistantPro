package com.realestateassistant.pro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity класс для хранения пользователей в локальной базе данных
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val name: String,
    val phone: String,
    val photoUrl: String,
    
    // Локальные системные поля
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
) 