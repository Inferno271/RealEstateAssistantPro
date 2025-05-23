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
        validations.add(Validators.validateRequired(appointment.location, "Место встречи"))

        // Валидация времени
        if (appointment.appointmentTime <= System.currentTimeMillis()) {
            validations.add(ValidationResult.Error(
                "Время встречи не может быть в прошлом",
                "appointmentTime"
            ))
        }

        // Валидация продолжительности
        validations.add(Validators.validateRange(
            appointment.duration,
            15, // минимум 15 минут
            240, // максимум 4 часа
            "Продолжительность"
        ))

        // Валидация напоминания
        appointment.reminderTime?.let { reminderTime ->
            if (reminderTime >= appointment.appointmentTime) {
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