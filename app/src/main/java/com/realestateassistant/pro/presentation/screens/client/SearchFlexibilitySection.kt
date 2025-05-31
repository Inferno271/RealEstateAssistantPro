package com.realestateassistant.pro.presentation.screens.client

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.presentation.components.CheckboxWithText
import com.realestateassistant.pro.presentation.components.EditableDropdownField
import com.realestateassistant.pro.presentation.components.EditableDropdownFieldWithText
import com.realestateassistant.pro.presentation.components.ExpandableClientCard
import com.realestateassistant.pro.presentation.components.MultiSelectField
import com.realestateassistant.pro.presentation.components.NumericTextField
import com.realestateassistant.pro.presentation.model.ClientFormState
import com.realestateassistant.pro.presentation.model.ClientSection
import com.realestateassistant.pro.presentation.viewmodel.OptionsViewModel

/**
 * Секция с гибкостью поиска и приоритетами клиента.
 * 
 * @param formState Состояние формы клиента
 * @param onFormStateChange Функция обновления состояния формы
 * @param expandedSections Карта с состояниями развернутости секций
 * @param optionsViewModel ViewModel для работы со справочниками
 * @param isFieldInvalid Функция для проверки валидности поля
 * @param showOnlyRequiredFields Флаг отображения только обязательных полей
 */
@Composable
fun SearchFlexibilitySection(
    formState: ClientFormState,
    onFormStateChange: (ClientFormState) -> Unit,
    expandedSections: MutableMap<ClientSection, Boolean>,
    optionsViewModel: OptionsViewModel = hiltViewModel(),
    isFieldInvalid: (String) -> Boolean = { false },
    showOnlyRequiredFields: Boolean = false
) {
    // Получаем опции из ViewModel
    val urgencyLevels by optionsViewModel.urgencyLevels.collectAsState(initial = listOf(
        "Срочно (до недели)", 
        "В ближайший месяц", 
        "В перспективе (более месяца)"
    ))
    val possibleRequirements by optionsViewModel.possibleRequirements.collectAsState(initial = listOf(
        "Район", 
        "Этаж", 
        "Площадь", 
        "Количество комнат",
        "Мебель/техника",
        "Парковка",
        "Наличие балкона/лоджии",
        "Ремонт"
    ))
    val priorityCriteria by optionsViewModel.priorityCriteria.collectAsState(initial = listOf(
        "Цена",
        "Район", 
        "Этаж", 
        "Площадь", 
        "Количество комнат",
        "Мебель/техника",
        "Парковка",
        "Транспортная доступность",
        "Инфраструктура",
        "Качество ремонта",
        "Тишина"
    ))
    
    // Кэшируем состояние
    val budgetFlexibility = formState.budgetFlexibility
    
    ExpandableClientCard(
        title = "Гибкость поиска и приоритеты",
        sectionKey = ClientSection.SEARCH_FLEXIBILITY,
        expandedSections = expandedSections,
        isError = isFieldInvalid("urgencyLevel")
    ) {
        // Срочность поиска (обязательное поле)
        EditableDropdownField(
            value = formState.urgencyLevel,
            onValueChange = { onFormStateChange(formState.copy(urgencyLevel = it)) },
            label = "Срочность поиска",
            options = urgencyLevels,
            onOptionsChange = optionsViewModel::updateUrgencyLevels,
            isError = isFieldInvalid("urgencyLevel"),
            errorMessage = if (isFieldInvalid("urgencyLevel")) "Необходимо указать срочность поиска" else null,
            isRequired = true
        )
        
        // Отображаем необязательные поля только если не включен режим только обязательных полей
        if (!showOnlyRequiredFields) {
            // Гибкость бюджета
            CheckboxWithText(
                checked = budgetFlexibility,
                onCheckedChange = { onFormStateChange(formState.copy(budgetFlexibility = it)) },
                text = "Возможно расширение бюджета при подходящем варианте"
            )
            
            // Показываем поле для ввода максимального увеличения бюджета только если выбрана гибкость
            if (budgetFlexibility) {
                NumericTextField(
                    value = formState.maxBudgetIncrease,
                    onValueChange = { onFormStateChange(formState.copy(maxBudgetIncrease = it)) },
                    label = "Максимальное увеличение бюджета",
                    allowDecimal = true,
                    suffix = " %"
                )
            }
            
            // Гибкость требований
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                MultiSelectField(
                    label = "Требования, по которым возможен компромисс",
                    options = possibleRequirements,
                    selectedOptions = formState.requirementFlexibility,
                    onSelectionChange = { onFormStateChange(formState.copy(requirementFlexibility = it)) },
                    onOptionsChange = optionsViewModel::updatePossibleRequirements
                )
            }
            
            // Приоритетные критерии
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                MultiSelectField(
                    label = "Приоритетные критерии (выберите до 5 важнейших)",
                    options = priorityCriteria,
                    selectedOptions = formState.priorityCriteria,
                    onSelectionChange = { 
                        // Ограничиваем выбор до 5 приоритетных критериев
                        val updatedCriteria = if (it.size <= 5) it else it.take(5)
                        onFormStateChange(formState.copy(priorityCriteria = updatedCriteria)) 
                    },
                    onOptionsChange = optionsViewModel::updatePriorityCriteria
                )
            }
        }
    }
} 