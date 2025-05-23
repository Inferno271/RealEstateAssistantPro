package com.realestateassistant.pro.data.local.mapper

import com.realestateassistant.pro.data.local.entity.UserEntity
import com.realestateassistant.pro.domain.model.User

/**
 * Маппер для преобразования User между Entity и Domain моделями
 */
object UserMapper {
    /**
     * Преобразует Entity модель в Domain модель
     */
    fun mapToDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            email = entity.email,
            name = entity.name,
            phone = entity.phone,
            photoUrl = entity.photoUrl
        )
    }

    /**
     * Преобразует Domain модель в Entity модель
     */
    fun mapToEntity(domain: User): UserEntity {
        return UserEntity(
            id = domain.id,
            email = domain.email,
            name = domain.name,
            phone = domain.phone,
            photoUrl = domain.photoUrl,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            isSynced = false
        )
    }
} 