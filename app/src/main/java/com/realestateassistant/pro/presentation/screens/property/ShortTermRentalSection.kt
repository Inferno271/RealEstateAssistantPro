package com.realestateassistant.pro.presentation.screens.property

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.presentation.components.*
import com.realestateassistant.pro.presentation.model.PropertyFormState
import com.realestateassistant.pro.presentation.model.PropertySection

@Composable
fun ShortTermRentalSection(
    formState: PropertyFormState,
    onFormStateChange: (PropertyFormState) -> Unit,
    expandedSections: MutableMap<PropertySection, Boolean>,
    isFieldInvalid: (String) -> Boolean,
    showOnlyRequiredFields: Boolean = false
) {
    // Кэшируем локальные состояния
    val cleaningService = formState.cleaningService
    val hasExtraBed = formState.hasExtraBed
    
    // Проверяем наличие ошибок в секции
    val hasDailyPriceError = isFieldInvalid("dailyPrice")
    val hasMaxGuestsError = isFieldInvalid("maxGuests")
    
    val hasSectionError = hasDailyPriceError || hasMaxGuestsError
    
    // Формируем сообщение об ошибке для секции
    val errorMessage = when {
        hasDailyPriceError -> "Необходимо указать стоимость за сутки"
        hasMaxGuestsError -> "Необходимо указать максимальное количество гостей"
        else -> null
    }
    
    ExpandablePropertyCard(
        title = "Условия посуточной аренды",
        sectionKey = PropertySection.SHORT_TERM_RENTAL,
        expandedSections = expandedSections,
        hasError = hasSectionError,
        errorMessage = errorMessage
    ) {
        // Обязательные поля всегда отображаются
        NumericTextField(
            value = formState.dailyPrice,
            onValueChange = { onFormStateChange(formState.copy(dailyPrice = it)) },
            label = "Стоимость за сутки",
            allowDecimal = true,
            suffix = " ₽",
            isError = hasDailyPriceError,
            errorMessage = if (hasDailyPriceError) "Обязательное поле" else null,
            isRequired = true
        )

        NumericTextField(
            value = formState.maxGuests,
            onValueChange = { onFormStateChange(formState.copy(maxGuests = it)) },
            label = "Максимум гостей",
            allowDecimal = false,
            isError = hasMaxGuestsError,
            errorMessage = if (hasMaxGuestsError) "Обязательное поле" else null,
            isRequired = true
        )
        
        // Отображаем необязательные поля только если не включен режим только обязательных полей
        if (!showOnlyRequiredFields) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumericTextField(
                    value = formState.minStayDays,
                    onValueChange = { onFormStateChange(formState.copy(minStayDays = it)) },
                    label = "Минимум дней",
                    modifier = Modifier.weight(1f),
                    allowDecimal = false
                )
                NumericTextField(
                    value = formState.maxStayDays,
                    onValueChange = { onFormStateChange(formState.copy(maxStayDays = it)) },
                    label = "Максимум дней",
                    modifier = Modifier.weight(1f),
                    allowDecimal = false
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TimeTextField(
                    value = formState.checkInTime,
                    onValueChange = { onFormStateChange(formState.copy(checkInTime = it)) },
                    label = "Заезд с",
                    modifier = Modifier.weight(1f)
                )
                TimeTextField(
                    value = formState.checkOutTime,
                    onValueChange = { onFormStateChange(formState.copy(checkOutTime = it)) },
                    label = "Выезд до",
                    modifier = Modifier.weight(1f)
                )
            }

            NumericTextField(
                value = formState.shortTermDepositCustomAmount,
                onValueChange = { onFormStateChange(formState.copy(shortTermDepositCustomAmount = it)) },
                label = "Залог",
                allowDecimal = true,
                suffix = " ₽"
            )
            
            NumericTextField(
                value = formState.shortTermDeposit,
                onValueChange = { onFormStateChange(formState.copy(shortTermDeposit = it)) },
                label = "Страховой депозит",
                allowDecimal = true,
                suffix = " ₽"
            )
            
            // Добавляем компонент для сезонных цен
            Spacer(modifier = Modifier.height(16.dp))
            
            SeasonalPriceEditor(
                seasonalPrices = formState.seasonalPrices,
                onSeasonalPricesChanged = { updatedPrices ->
                    onFormStateChange(formState.copy(seasonalPrices = updatedPrices))
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            NumericTextField(
                value = formState.weekdayPrice,
                onValueChange = { onFormStateChange(formState.copy(weekdayPrice = it)) },
                label = "Цена в будни",
                allowDecimal = true,
                suffix = " ₽"
            )

            NumericTextField(
                value = formState.weekendPrice,
                onValueChange = { onFormStateChange(formState.copy(weekendPrice = it)) },
                label = "Цена в выходные",
                allowDecimal = true,
                suffix = " ₽"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumericTextField(
                    value = formState.weeklyDiscount,
                    onValueChange = { onFormStateChange(formState.copy(weeklyDiscount = it)) },
                    label = "Скидка на неделю (%)",
                    modifier = Modifier.weight(1f),
                    allowDecimal = true
                )
                NumericTextField(
                    value = formState.monthlyDiscount,
                    onValueChange = { onFormStateChange(formState.copy(monthlyDiscount = it)) },
                    label = "Скидка на месяц (%)",
                    modifier = Modifier.weight(1f),
                    allowDecimal = true
                )
            }

            OutlinedTextFieldWithColors(
                value = formState.additionalServices,
                onValueChange = { onFormStateChange(formState.copy(additionalServices = it)) },
                label = "Доп. услуги",
                minLines = 3,
                maxLines = 5,
                isError = false
            )

            OutlinedTextFieldWithColors(
                value = formState.shortTermRules,
                onValueChange = { onFormStateChange(formState.copy(shortTermRules = it)) },
                label = "Правила проживания",
                minLines = 3,
                maxLines = 5,
                isError = false
            )

            CheckboxWithText(
                checked = cleaningService,
                onCheckedChange = { onFormStateChange(formState.copy(cleaningService = it)) },
                text = "Уборка"
            )

            if (cleaningService) {
                OutlinedTextFieldWithColors(
                    value = formState.cleaningDetails,
                    onValueChange = { onFormStateChange(formState.copy(cleaningDetails = it)) },
                    label = "Детали уборки",
                    minLines = 2,
                    maxLines = 3,
                    isError = false
                )
            }

            CheckboxWithText(
                checked = hasExtraBed,
                onCheckedChange = { onFormStateChange(formState.copy(hasExtraBed = it)) },
                text = "Доп. спальное место"
            )

            if (hasExtraBed) {
                NumericTextField(
                    value = formState.extraBedPrice,
                    onValueChange = { onFormStateChange(formState.copy(extraBedPrice = it)) },
                    label = "Цена доп. места",
                    allowDecimal = true,
                    suffix = " ₽"
                )
            }

            CheckboxWithText(
                checked = formState.partiesAllowed,
                onCheckedChange = { onFormStateChange(formState.copy(partiesAllowed = it)) },
                text = "Разрешены мероприятия"
            )

            OutlinedTextFieldWithColors(
                value = formState.specialOffers,
                onValueChange = { onFormStateChange(formState.copy(specialOffers = it)) },
                label = "Специальные предложения",
                minLines = 3,
                maxLines = 5,
                isError = false
            )

            OutlinedTextFieldWithColors(
                value = formState.additionalComments,
                onValueChange = { onFormStateChange(formState.copy(additionalComments = it)) },
                label = "Дополнительные комментарии",
                minLines = 2,
                maxLines = 3
            )
        }
    }
}