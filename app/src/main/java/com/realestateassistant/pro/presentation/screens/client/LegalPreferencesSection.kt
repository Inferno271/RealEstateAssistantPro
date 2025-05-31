package com.realestateassistant.pro.presentation.screens.client

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.presentation.components.CheckboxWithText
import com.realestateassistant.pro.presentation.components.EditableDropdownField
import com.realestateassistant.pro.presentation.components.EditableDropdownFieldWithText
import com.realestateassistant.pro.presentation.components.ExpandableClientCard
import com.realestateassistant.pro.presentation.model.ClientFormState
import com.realestateassistant.pro.presentation.model.ClientSection
import com.realestateassistant.pro.presentation.viewmodel.OptionsViewModel

/**
 * Секция с юридическими предпочтениями клиента.
 * 
 * @param formState Состояние формы клиента
 * @param onFormStateChange Функция обновления состояния формы
 * @param expandedSections Карта с состояниями развернутости секций
 * @param optionsViewModel ViewModel для работы со справочниками
 * @param isFieldInvalid Функция для проверки валидности поля
 */
@Composable
fun LegalPreferencesSection(
    formState: ClientFormState,
    onFormStateChange: (ClientFormState) -> Unit,
    expandedSections: MutableMap<ClientSection, Boolean>,
    optionsViewModel: OptionsViewModel = hiltViewModel(),
    isFieldInvalid: (String) -> Boolean = { false }
) {
    // Получаем списки опций для налоговых опций
    val taxOptions by optionsViewModel.taxOptions.collectAsState(initial = listOf(
        "Самозанятый", 
        "НДФЛ (физлицо)", 
        "ООО/ИП (безналичный расчет)", 
        "Любой вариант"
    ))
    
    // Создаем кэшированные копии состояний для уменьшения перерисовок
    val needsOfficialAgreement = formState.needsOfficialAgreement
    
    ExpandableClientCard(
        title = "Юридические предпочтения",
        sectionKey = ClientSection.LEGAL_PREFERENCES,
        expandedSections = expandedSections
    ) {
        // Нужен ли официальный договор
        CheckboxWithText(
            checked = needsOfficialAgreement,
            onCheckedChange = { onFormStateChange(formState.copy(needsOfficialAgreement = it)) },
            text = "Требуется официальный договор аренды"
        )
        
        // Предпочтительный вариант налогообложения
        EditableDropdownField(
            value = formState.preferredTaxOption,
            onValueChange = { onFormStateChange(formState.copy(preferredTaxOption = it)) },
            label = "Предпочтительный вариант налогообложения",
            options = taxOptions,
            onOptionsChange = { newOptions ->
                optionsViewModel.updateCustomOptions("taxOptions", newOptions)
            }
        )
    }
} 