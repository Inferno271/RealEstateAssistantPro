package com.realestateassistant.pro.data.local.validation

import com.realestateassistant.pro.data.local.entity.ClientEntity
import javax.inject.Inject

/**
 * Валидатор для сущности ClientEntity
 */
class ClientValidator @Inject constructor() {

    /**
     * Проводит валидацию клиента
     * @param client Клиент для валидации
     * @return Результат валидации
     */
    fun validate(client: ClientEntity): ValidationResult {
        val validations = mutableListOf<ValidationResult>()

        // Обязательные поля
        validations.add(Validators.validateRequired(client.id, "ID"))
        validations.add(Validators.validateRequired(client.fullName, "ФИО"))
        validations.add(Validators.validateRequired(client.phone, "Телефон"))
        validations.add(Validators.validateRequired(client.rentalType, "Тип аренды"))

        // Валидация контактных данных
        validations.add(Validators.validatePhone(client.phone, "Телефон"))
        validations.add(Validators.validateEmail(client.email, "Email"))

        // Валидация бюджета для длительной аренды
        client.longTermBudgetMin?.let { min ->
            validations.add(Validators.validatePositiveNumber(min, "Минимальный бюджет"))
            
            client.longTermBudgetMax?.let { max ->
                validations.add(Validators.validatePositiveNumber(max, "Максимальный бюджет"))
                
                if (min > max) {
                    validations.add(ValidationResult.Error(
                        "Минимальный бюджет не может быть больше максимального",
                        "longTermBudgetMin"
                    ))
                }
            }
        }

        // Валидация количества людей
        client.peopleCount?.let { count ->
            validations.add(Validators.validateRange(count, 1, 20, "Количество проживающих"))
        }

        client.childrenCount?.let { count ->
            validations.add(Validators.validateRange(count, 0, 10, "Количество детей"))
        }

        // Валидация площади
        client.desiredArea?.let { area ->
            validations.add(Validators.validatePositiveNumber(area, "Желаемая площадь"))
        }

        // Валидация комнат
        client.desiredRoomsCount?.let { rooms ->
            validations.add(Validators.validateRange(rooms, 1, 10, "Количество комнат"))
        }

        // Валидация для посуточной аренды
        if (client.rentalType == "посуточная" || client.rentalType == "both") {
            client.shortTermGuests?.let { guests ->
                validations.add(Validators.validateRange(guests, 1, 20, "Количество гостей"))
            }

            client.dailyBudget?.let { budget ->
                validations.add(Validators.validatePositiveNumber(budget, "Дневной бюджет"))
            }

            // Проверка дат заезда/выезда
            if (client.shortTermCheckInDate != null && client.shortTermCheckOutDate != null) {
                if (client.shortTermCheckInDate >= client.shortTermCheckOutDate) {
                    validations.add(ValidationResult.Error(
                        "Дата заезда должна быть раньше даты выезда",
                        "shortTermCheckInDate"
                    ))
                }
            }
        }

        // Валидация длины комментариев
        client.comment.let { comment ->
            validations.add(Validators.validateMaxLength(comment, 1000, "Комментарий"))
        }

        client.additionalRequirements?.let { requirements ->
            validations.add(Validators.validateMaxLength(requirements, 500, "Дополнительные требования"))
        }

        return validations.combine()
    }
} 