package com.realestateassistant.pro.presentation.screens.client

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.presentation.components.CheckboxWithText
import com.realestateassistant.pro.presentation.components.DatePickerField
import com.realestateassistant.pro.presentation.components.ExpandableClientCard
import com.realestateassistant.pro.presentation.components.NumericTextField
import com.realestateassistant.pro.presentation.components.OutlinedTextFieldWithColors
import com.realestateassistant.pro.presentation.components.EditableDropdownFieldWithText
import com.realestateassistant.pro.presentation.model.ClientFormState
import com.realestateassistant.pro.presentation.model.ClientSection
import com.realestateassistant.pro.presentation.model.RentalType

/**
 * Секция с требованиями для краткосрочной (посуточной) аренды.
 * 
 * @param formState Состояние формы клиента
 * @param onFormStateChange Функция обновления состояния формы
 * @param expandedSections Карта с состояниями развернутости секций
 * @param isFieldInvalid Функция для проверки валидности поля
 * @param showOnlyRequiredFields Флаг отображения только обязательных полей
 */
@Composable
fun ShortTermRequirementsSection(
    formState: ClientFormState,
    onFormStateChange: (ClientFormState) -> Unit,
    expandedSections: MutableMap<ClientSection, Boolean>,
    isFieldInvalid: (String) -> Boolean = { false },
    showOnlyRequiredFields: Boolean = false
) {
    // Проверяем, если выбран не краткосрочный тип аренды, не отображаем секцию
    if (formState.rentalType != RentalType.SHORT_TERM) {
        return
    }
    
    // Кэшируем состояние для улучшения производительности
    val hasAdditionalGuests = formState.hasAdditionalGuests
    
    ExpandableClientCard(
        title = "Требования для посуточной аренды",
        sectionKey = ClientSection.SHORT_TERM_REQUIREMENTS,
        expandedSections = expandedSections,
        isError = isFieldInvalid("shortTermBudgetMax") || isFieldInvalid("shortTermCheckInDate") || isFieldInvalid("shortTermCheckOutDate")
    ) {
        // Бюджет (от и до) в одной строке
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Минимальный бюджет
            NumericTextField(
                value = formState.shortTermBudgetMin,
                onValueChange = { onFormStateChange(formState.copy(shortTermBudgetMin = it)) },
                label = "Бюджет от",
                allowDecimal = true,
                suffix = " ₽/сутки",
                modifier = Modifier.weight(1f)
            )
            
            // Максимальный бюджет (обязательное поле)
            NumericTextField(
                value = formState.shortTermBudgetMax,
                onValueChange = { onFormStateChange(formState.copy(shortTermBudgetMax = it)) },
                label = "Бюджет до",
                allowDecimal = true,
                suffix = " ₽/сутки",
                isError = isFieldInvalid("shortTermBudgetMax"),
                errorMessage = if (isFieldInvalid("shortTermBudgetMax")) "Обязательное поле" else null,
                isRequired = true,
                modifier = Modifier.weight(1f)
            )
        }
        
        // Даты заезда и выезда (обязательные поля)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DatePickerField(
                value = formState.shortTermCheckInDate,
                onValueChange = { onFormStateChange(formState.copy(shortTermCheckInDate = it)) },
                label = "Дата заезда",
                modifier = Modifier.weight(1f),
                isError = isFieldInvalid("shortTermCheckInDate"),
                errorMessage = if (isFieldInvalid("shortTermCheckInDate")) "Обязательное поле" else null,
                isRequired = true
            )
            
            DatePickerField(
                value = formState.shortTermCheckOutDate,
                onValueChange = { onFormStateChange(formState.copy(shortTermCheckOutDate = it)) },
                label = "Дата выезда",
                modifier = Modifier.weight(1f),
                isError = isFieldInvalid("shortTermCheckOutDate"),
                errorMessage = if (isFieldInvalid("shortTermCheckOutDate")) "Обязательное поле" else null,
                isRequired = true
            )
        }
        
        // Отображаем необязательные поля только если не включен режим только обязательных полей
        if (!showOnlyRequiredFields) {
            // Чекбокс для указания возможности дополнительных гостей
            CheckboxWithText(
                checked = hasAdditionalGuests,
                onCheckedChange = { onFormStateChange(formState.copy(hasAdditionalGuests = it)) },
                text = "Планируются дополнительные гости"
            )
            
            // Поле для количества гостей, отображается только если планируются дополнительные гости
            if (hasAdditionalGuests) {
                NumericTextField(
                    value = formState.shortTermGuests,
                    onValueChange = { onFormStateChange(formState.copy(shortTermGuests = it)) },
                    label = "Количество дополнительных гостей",
                    allowDecimal = false
                )
            }
            
            // Условия заезда/выезда
            OutlinedTextFieldWithColors(
                value = formState.checkInOutConditions,
                onValueChange = { onFormStateChange(formState.copy(checkInOutConditions = it)) },
                label = "Условия заезда/выезда",
                placeholder = { Text("Например: поздний заезд, ранний выезд, трансфер") }
            )
            
            // Дополнительные услуги
            OutlinedTextFieldWithColors(
                value = formState.additionalServices,
                onValueChange = { onFormStateChange(formState.copy(additionalServices = it)) },
                label = "Дополнительные услуги",
                placeholder = { Text("Например: Wi-Fi, уборка, питание") },
                minLines = 2,
                maxLines = 4
            )
            
            // Дополнительные требования
            OutlinedTextFieldWithColors(
                value = formState.additionalShortTermRequirements,
                onValueChange = { onFormStateChange(formState.copy(additionalShortTermRequirements = it)) },
                label = "Дополнительные требования",
                placeholder = { Text("Любые другие пожелания") },
                minLines = 2,
                maxLines = 4
            )
        }
    }
} 