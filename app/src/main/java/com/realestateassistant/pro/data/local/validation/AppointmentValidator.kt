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
        val validations = mutableListOf<ValidationResult>()

        // Обязательные поля
        validations.add(Validators.validateRequired(appointment.id, "ID"))
        validations.add(Validators.validateRequired(appointment.propertyId, "ID объекта"))
        validations.add(Validators.validateRequired(appointment.clientId, "ID клиента"))
        validations.add(Validators.validateRequired(appointment.agentId, "ID агента"))
        validations.add(Validators.validateRequired(appointment.title, "Заголовок"))
        validations.add(Validators.validateRequired(appointment.location, "Место встречи"))

        // Валидация времени
        if (appointment.startTime <= System.currentTimeMillis()) {
            validations.add(ValidationResult.Error(
                "Время начала встречи не может быть в прошлом",
                "startTime"
            ))
        }

        // Валидация продолжительности
        if (appointment.endTime <= appointment.startTime) {
            validations.add(ValidationResult.Error(
                "Время окончания должно быть позже времени начала",
                "endTime"
            ))
        }

        // Валидация напоминания
        appointment.reminderTime?.let { reminderTime ->
            if (reminderTime >= appointment.startTime) {
                validations.add(ValidationResult.Error(
                    "Время напоминания должно быть раньше времени встречи",
                    "reminderTime"
                ))
            }
        }

        // Валидация заметок
        appointment.notes.let { notes ->
            validations.add(Validators.validateMaxLength(notes, 500, "Заметки"))
        }

        return validations.combine()
    }
} 