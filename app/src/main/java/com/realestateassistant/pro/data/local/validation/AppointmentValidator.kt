package com.realestateassistant.pro.data.local.validation

import com.realestateassistant.pro.data.local.entity.AppointmentEntity
import javax.inject.Inject

/**
 * Валидатор для сущности AppointmentEntity
 */
class AppointmentValidator @Inject constructor() {

    /**
     * Проводит валидацию встречи
     * @param appointment Встреча для валидации
     * @return Результат валидации
     */
    fun validate(appointment: AppointmentEntity): ValidationResult {
        val validations = mutableListOf<ValidationResult>(
        // Обязательные поля
            Validators.validateRequired(appointment.id, "ID"),
            Validators.validateRequired(appointment.propertyId, "ID объекта"),
            Validators.validateRequired(appointment.clientId, "ID клиента"),
            Validators.validateRequired(appointment.title, "Название")
        )
        
        // Проверяем, что время начала раньше времени окончания
        if (appointment.startTime >= appointment.endTime) {
            validations.add(ValidationResult.Error(
                "Время начала должно быть раньше времени окончания",
                "startTime"
            ))
        }

        return validations.combine()
    }
} 