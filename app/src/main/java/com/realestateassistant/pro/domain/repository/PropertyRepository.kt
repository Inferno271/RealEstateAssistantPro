package com.realestateassistant.pro.domain.repository

import com.realestateassistant.pro.domain.model.Property

interface PropertyRepository {
    /**
     * Добавляет новый объект недвижимости.
     * Возвращает добавленный объект с обновленным id, если операция успешна.
     */
    suspend fun addProperty(property: Property): Result<Property>

    /**
     * Обновляет существующий объект недвижимости.
     */
    suspend fun updateProperty(property: Property): Result<Unit>

    /**
     * Удаляет объект недвижимости по его id.
     */
    suspend fun deleteProperty(propertyId: String): Result<Unit>

    /**
     * Получает объект недвижимости по его id.
     */
    suspend fun getProperty(propertyId: String): Result<Property>

    /**
     * Получает список всех объектов недвижимости.
     */
    suspend fun getAllProperties(): Result<List<Property>>
} 