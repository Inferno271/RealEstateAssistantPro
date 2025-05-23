package com.realestateassistant.pro.data.local.validation

import com.realestateassistant.pro.data.local.entity.PropertyEntity
import javax.inject.Inject

/**
 * Валидатор для сущности PropertyEntity
 */
class PropertyValidator @Inject constructor() {

    /**
     * Проводит валидацию объекта недвижимости
     * @param property Объект недвижимости для валидации
     * @return Результат валидации
     */
    fun validate(property: PropertyEntity): ValidationResult {
        val validations = mutableListOf<ValidationResult>()

        // Обязательные поля
        validations.add(Validators.validateRequired(property.id, "ID"))
        validations.add(Validators.validateRequired(property.contactName, "Имя контакта"))
        validations.add(Validators.validateRequired(property.propertyType, "Тип недвижимости"))
        validations.add(Validators.validateRequired(property.address, "Адрес"))
        validations.add(Validators.validateRequired(property.district, "Район"))

        // Проверка контактных данных
        if (property.contactPhone.isNotEmpty()) {
            property.contactPhone.forEach { phone ->
                validations.add(Validators.validatePhone(phone, "Телефон контакта"))
            }
        } else {
            validations.add(ValidationResult.Error("Должен быть указан хотя бы один контактный телефон", "contactPhone"))
        }

        // Числовые значения
        validations.add(Validators.validatePositiveNumber(property.area, "Площадь"))
        validations.add(Validators.validatePositiveInt(property.roomsCount, "Количество комнат"))
        validations.add(Validators.validatePositiveInt(property.floor, "Этаж"))
        validations.add(Validators.validatePositiveInt(property.totalFloors, "Всего этажей"))
        
        // Проверка этажности
        if (property.floor > property.totalFloors) {
            validations.add(ValidationResult.Error(
                "Этаж не может быть больше общего количества этажей",
                "floor"
            ))
        }

        // Проверка цен
        if (property.monthlyRent != null) {
            validations.add(Validators.validatePositiveNumber(property.monthlyRent, "Ежемесячная аренда"))
            
            // Если указана сезонная аренда
            property.winterMonthlyRent?.let { winter ->
                validations.add(Validators.validatePositiveNumber(winter, "Зимняя аренда"))
            }
            property.summerMonthlyRent?.let { summer ->
                validations.add(Validators.validatePositiveNumber(summer, "Летняя аренда"))
            }
        }

        if (property.dailyPrice != null) {
            validations.add(Validators.validatePositiveNumber(property.dailyPrice, "Посуточная аренда"))
            
            // Проверка периода проживания
            property.minStayDays?.let { min ->
                property.maxStayDays?.let { max ->
                    if (min > max) {
                        validations.add(ValidationResult.Error(
                            "Минимальный срок проживания не может быть больше максимального",
                            "minStayDays"
                        ))
                    }
                }
            }
        }

        // Проверка размеров депозита
        property.depositCustomAmount?.let { deposit ->
            validations.add(Validators.validatePositiveNumber(deposit, "Размер депозита"))
        }

        // Проверка коммунальных платежей
        property.utilitiesCost?.let { utilities ->
            validations.add(Validators.validatePositiveNumber(utilities, "Коммунальные платежи"))
        }

        // Проверка описаний и комментариев
        property.description?.let { desc ->
            validations.add(Validators.validateMaxLength(desc, 1000, "Описание"))
        }

        // Проверка фотографий
        if (property.photos.isEmpty()) {
            validations.add(ValidationResult.Error("Необходимо добавить хотя бы одну фотографию", "photos"))
        }

        return validations.combine()
    }
} 