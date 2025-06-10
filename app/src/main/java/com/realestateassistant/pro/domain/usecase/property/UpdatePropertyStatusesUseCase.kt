package com.realestateassistant.pro.domain.usecase.property

import com.realestateassistant.pro.domain.repository.PropertyRepository
import javax.inject.Inject

/**
 * Usecase для обновления статусов объектов недвижимости на основе бронирований
 */
class UpdatePropertyStatusesUseCase @Inject constructor(
    private val propertyRepository: PropertyRepository
) {
    /**
     * Обновляет статусы всех объектов недвижимости на основе текущих бронирований
     */
    suspend operator fun invoke(): Result<Unit> {
        return propertyRepository.updateAllPropertyStatuses()
    }
} 