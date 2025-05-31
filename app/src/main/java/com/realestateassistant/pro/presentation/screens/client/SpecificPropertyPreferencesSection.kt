package com.realestateassistant.pro.presentation.screens.client

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.presentation.components.CheckboxWithText
import com.realestateassistant.pro.presentation.components.EditableDropdownField
import com.realestateassistant.pro.presentation.components.EditableDropdownFieldWithText
import com.realestateassistant.pro.presentation.components.ExpandableClientCard
import com.realestateassistant.pro.presentation.components.NumericTextField
import com.realestateassistant.pro.presentation.model.ClientFormState
import com.realestateassistant.pro.presentation.model.ClientSection
import com.realestateassistant.pro.presentation.viewmodel.OptionsViewModel

/**
 * Секция с предпочтениями клиента по специальным типам недвижимости (дома, участки и т.д.).
 * 
 * @param formState Состояние формы клиента
 * @param onFormStateChange Функция обновления состояния формы
 * @param expandedSections Карта с состояниями развернутости секций
 * @param optionsViewModel ViewModel для работы со справочниками
 * @param isFieldInvalid Функция для проверки валидности поля
 */
@Composable
fun SpecificPropertyPreferencesSection(
    formState: ClientFormState,
    onFormStateChange: (ClientFormState) -> Unit,
    expandedSections: MutableMap<ClientSection, Boolean>,
    optionsViewModel: OptionsViewModel = hiltViewModel(),
    isFieldInvalid: (String) -> Boolean = { false }
) {
    // Получаем списки опций из ViewModel
    val poolTypes by optionsViewModel.poolTypes.collectAsState(initial = emptyList())
    
    // Создаем кэшированные копии состояний для уменьшения перерисовок
    val needsYard = formState.needsYard
    val needsGarage = formState.needsGarage
    val needsBathhouse = formState.needsBathhouse
    val needsPool = formState.needsPool
    
    // Проверяем, нужна ли эта секция в зависимости от типа недвижимости
    // Если клиент ищет квартиру, секция не нужна
    val isApplicable = formState.desiredPropertyType.lowercase() !in listOf("квартира", "апартаменты", "комната")
    
    if (!isApplicable) {
        return
    }
    
    ExpandableClientCard(
        title = "Особые требования для частного дома/коттеджа",
        sectionKey = ClientSection.SPECIFIC_PROPERTY_PREFERENCES,
        expandedSections = expandedSections
    ) {
        // Нужен ли участок
        CheckboxWithText(
            checked = needsYard,
            onCheckedChange = { onFormStateChange(formState.copy(needsYard = it)) },
            text = "Нужен земельный участок"
        )
        
        if (needsYard) {
            NumericTextField(
                value = formState.preferredYardArea,
                onValueChange = { onFormStateChange(formState.copy(preferredYardArea = it)) },
                label = "Желаемая площадь участка (сотки)",
                allowDecimal = true,
                suffix = " сот."
            )
        }
        
        // Нужен ли гараж
        CheckboxWithText(
            checked = needsGarage,
            onCheckedChange = { onFormStateChange(formState.copy(needsGarage = it)) },
            text = "Нужен гараж"
        )
        
        if (needsGarage) {
            NumericTextField(
                value = formState.preferredGarageSpaces,
                onValueChange = { onFormStateChange(formState.copy(preferredGarageSpaces = it)) },
                label = "Количество машиномест",
                allowDecimal = false
            )
        }
        
        // Нужна ли баня
        CheckboxWithText(
            checked = needsBathhouse,
            onCheckedChange = { onFormStateChange(formState.copy(needsBathhouse = it)) },
            text = "Нужна баня/сауна"
        )
        
        // Нужен ли бассейн
        CheckboxWithText(
            checked = needsPool,
            onCheckedChange = { onFormStateChange(formState.copy(needsPool = it)) },
            text = "Нужен бассейн"
        )
    }
} 