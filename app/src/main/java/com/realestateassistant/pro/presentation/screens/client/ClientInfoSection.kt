package com.realestateassistant.pro.presentation.screens.client

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.presentation.components.*
import com.realestateassistant.pro.presentation.model.ClientFormState
import com.realestateassistant.pro.presentation.model.ClientSection
import com.realestateassistant.pro.presentation.viewmodel.OptionsViewModel
import com.realestateassistant.pro.presentation.components.EditableDropdownFieldWithText

/**
 * Секция с общей информацией о клиенте, не зависящей от типа аренды.
 * 
 * @param formState Состояние формы клиента
 * @param onFormStateChange Функция обновления состояния формы
 * @param expandedSections Карта с состояниями развернутости секций
 * @param optionsViewModel ViewModel для работы со справочниками
 * @param isFieldInvalid Функция для проверки валидности поля
 * @param showOnlyRequiredFields Флаг отображения только обязательных полей
 */
@Composable
fun ClientInfoSection(
    formState: ClientFormState,
    onFormStateChange: (ClientFormState) -> Unit,
    expandedSections: MutableMap<ClientSection, Boolean>,
    optionsViewModel: OptionsViewModel = hiltViewModel(),
    isFieldInvalid: (String) -> Boolean = { false },
    showOnlyRequiredFields: Boolean = false
) {
    // Получаем опции из ViewModel
    val familyCompositions by optionsViewModel.familyCompositions.collectAsState(initial = emptyList())
    val petTypes by optionsViewModel.petTypes.collectAsState(initial = emptyList())
    val childAgeCategories by optionsViewModel.childAgeCategories.collectAsState(initial = emptyList())
    val occupations by optionsViewModel.occupations.collectAsState(initial = emptyList())
    
    ExpandableClientCard(
        title = "Информация о клиенте",
        sectionKey = ClientSection.CLIENT_INFO,
        expandedSections = expandedSections,
        isError = isFieldInvalid("familyComposition") || isFieldInvalid("peopleCount")
    ) {
        // Добавляем поле для состава семьи (выпадающий список с возможностью редактирования)
        EditableDropdownField(
            value = formState.familyComposition,
            onValueChange = { onFormStateChange(formState.copy(familyComposition = it)) },
            label = "Состав семьи",
            options = familyCompositions,
            onOptionsChange = optionsViewModel::updateFamilyCompositions,
            isError = isFieldInvalid("familyComposition"),
            errorMessage = if (isFieldInvalid("familyComposition")) "Необходимо указать состав семьи" else null,
            isRequired = true
        )
        
        // Поле для количества человек (обязательное)
        NumericTextField(
            value = formState.peopleCount,
            onValueChange = { onFormStateChange(formState.copy(peopleCount = it)) },
            label = "Количество проживающих",
            allowDecimal = false,
            isError = isFieldInvalid("peopleCount"),
            errorMessage = if (isFieldInvalid("peopleCount")) "Обязательное поле" else null,
            isRequired = true
        )
        
        // Отображаем необязательные поля только если не включен режим только обязательных полей
        if (!showOnlyRequiredFields) {
            // Добавляем поле для указания рода занятий
            EditableDropdownField(
                value = formState.occupation,
                onValueChange = { onFormStateChange(formState.copy(occupation = it)) },
                label = "Род занятий/работы",
                options = occupations,
                onOptionsChange = optionsViewModel::updateOccupations
            )
            
            // Поле для количества детей
            NumericTextField(
                value = formState.childrenCount,
                onValueChange = { onFormStateChange(formState.copy(childrenCount = it)) },
                label = "Количество детей",
                allowDecimal = false
            )
            
            // Поля для возраста детей (активны только если есть дети)
            val childrenCount = formState.childrenCount.toIntOrNull() ?: 0
            if (childrenCount > 0) {
                Text(
                    text = "Возраст детей",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                for (i in 0 until childrenCount) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ребенок ${i + 1}:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.width(100.dp)
                        )
                        
                        NumericTextField(
                            value = formState.childrenAges.getOrNull(i) ?: "",
                            onValueChange = { newValue ->
                                val newAges = formState.childrenAges.toMutableList().apply {
                                    if (i < size) {
                                        set(i, newValue)
                                    } else {
                                        add(newValue)
                                    }
                                }
                                onFormStateChange(formState.copy(childrenAges = newAges))
                            },
                            label = "Возраст",
                            allowDecimal = false,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                // Отображаем категории возрастов как подсказку
                Text(
                    text = "Категории: ${childAgeCategories.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp),
                    textAlign = TextAlign.Start
                )
            }
            
            // Информация о домашних животных
            Spacer(modifier = Modifier.height(8.dp))
            
            // Переключатель для наличия домашних животных
            CheckboxWithText(
                checked = formState.hasPets,
                onCheckedChange = { onFormStateChange(formState.copy(hasPets = it)) },
                text = "Есть домашние животные"
            )
            
            // Если есть домашние животные, показываем дополнительные поля
            if (formState.hasPets) {
                NumericTextField(
                    value = formState.petCount,
                    onValueChange = { onFormStateChange(formState.copy(petCount = it)) },
                    label = "Количество животных",
                    allowDecimal = false
                )
                
                // Мультиселект для типов животных
                MultiSelectField(
                    label = "Типы животных",
                    options = petTypes,
                    selectedOptions = formState.petTypes,
                    onSelectionChange = { onFormStateChange(formState.copy(petTypes = it)) },
                    onOptionsChange = optionsViewModel::updatePetTypes
                )
            }
        }
    }
} 