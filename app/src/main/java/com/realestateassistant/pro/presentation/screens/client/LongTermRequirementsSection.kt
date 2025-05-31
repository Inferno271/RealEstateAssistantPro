package com.realestateassistant.pro.presentation.screens.client

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.presentation.components.DatePickerField
import com.realestateassistant.pro.presentation.components.EditableDropdownField
import com.realestateassistant.pro.presentation.components.EditableDropdownFieldWithText
import com.realestateassistant.pro.presentation.components.ExpandableClientCard
import com.realestateassistant.pro.presentation.components.NumericTextField
import com.realestateassistant.pro.presentation.components.OutlinedTextFieldWithColors
import com.realestateassistant.pro.presentation.model.ClientFormState
import com.realestateassistant.pro.presentation.model.ClientSection
import com.realestateassistant.pro.presentation.model.RentalType
import com.realestateassistant.pro.presentation.viewmodel.OptionsViewModel

/**
 * Секция с требованиями для долгосрочной аренды.
 * 
 * @param formState Состояние формы клиента
 * @param onFormStateChange Функция обновления состояния формы
 * @param expandedSections Карта с состояниями развернутости секций
 * @param optionsViewModel ViewModel для работы со справочниками
 * @param isFieldInvalid Функция для проверки валидности поля
 * @param showOnlyRequiredFields Флаг отображения только обязательных полей
 */
@Composable
fun LongTermRequirementsSection(
    formState: ClientFormState,
    onFormStateChange: (ClientFormState) -> Unit,
    expandedSections: MutableMap<ClientSection, Boolean>,
    optionsViewModel: OptionsViewModel = hiltViewModel(),
    isFieldInvalid: (String) -> Boolean = { false },
    showOnlyRequiredFields: Boolean = false
) {
    // Проверяем, если выбран не долгосрочный тип аренды, не отображаем секцию
    if (formState.rentalType != RentalType.LONG_TERM) {
        return
    }
    
    ExpandableClientCard(
        title = "Требования для долгосрочной аренды",
        sectionKey = ClientSection.LONG_TERM_REQUIREMENTS,
        expandedSections = expandedSections,
        isError = isFieldInvalid("longTermBudgetMax") || isFieldInvalid("desiredRoomsCount") || isFieldInvalid("moveInDeadline")
    ) {
        // Бюджет (от и до) в одной строке
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Минимальный бюджет
            NumericTextField(
                value = formState.longTermBudgetMin,
                onValueChange = { onFormStateChange(formState.copy(longTermBudgetMin = it)) },
                label = "Бюджет от",
                allowDecimal = true,
                suffix = " ₽",
                modifier = Modifier.weight(1f)
            )
            
            // Максимальный бюджет (обязательное поле)
            NumericTextField(
                value = formState.longTermBudgetMax,
                onValueChange = { onFormStateChange(formState.copy(longTermBudgetMax = it)) },
                label = "Бюджет до",
                allowDecimal = true,
                suffix = " ₽",
                isError = isFieldInvalid("longTermBudgetMax"),
                errorMessage = if (isFieldInvalid("longTermBudgetMax")) "Обязательное поле" else null,
                isRequired = true,
                modifier = Modifier.weight(1f)
            )
        }
        
        // Количество комнат и площадь в одной строке
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Количество комнат (обязательное поле)
            NumericTextField(
                value = formState.desiredRoomsCount,
                onValueChange = { onFormStateChange(formState.copy(desiredRoomsCount = it)) },
                label = "Количество комнат",
                allowDecimal = false,
                isError = isFieldInvalid("desiredRoomsCount"),
                errorMessage = if (isFieldInvalid("desiredRoomsCount")) "Обязательное поле" else null,
                isRequired = true,
                modifier = Modifier.weight(1f)
            )
            
            // Желаемая площадь (отображается всегда, даже в режиме обязательных полей)
            NumericTextField(
                value = formState.desiredArea,
                onValueChange = { onFormStateChange(formState.copy(desiredArea = it)) },
                label = "Площадь",
                allowDecimal = true,
                suffix = " м²",
                modifier = Modifier.weight(1f)
            )
        }
        
        // Срок заселения (обязательное поле)
        DatePickerField(
            value = formState.moveInDeadline,
            onValueChange = { onFormStateChange(formState.copy(moveInDeadline = it)) },
            label = "Срок заселения до",
            isError = isFieldInvalid("moveInDeadline"),
            errorMessage = if (isFieldInvalid("moveInDeadline")) "Необходимо указать срок заселения" else null,
            isRequired = true
        )
        
        // Отображаем необязательные поля только если не включен режим только обязательных полей
        if (!showOnlyRequiredFields) {
            // Юридические предпочтения
            OutlinedTextFieldWithColors(
                value = formState.legalPreferences,
                onValueChange = { onFormStateChange(formState.copy(legalPreferences = it)) },
                label = "Юридические предпочтения",
                placeholder = { Text("Например: официальный договор, нужны чеки") }
            )
            
            // Дополнительные требования
            OutlinedTextFieldWithColors(
                value = formState.additionalRequirements,
                onValueChange = { onFormStateChange(formState.copy(additionalRequirements = it)) },
                label = "Дополнительные требования",
                placeholder = { Text("Например: наличие мебели, бытовой техники") },
                minLines = 2,
                maxLines = 4
            )
        }
    }
} 