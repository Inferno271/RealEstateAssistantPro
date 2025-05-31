package com.realestateassistant.pro.presentation.screens.property

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.presentation.components.*
import com.realestateassistant.pro.presentation.model.PropertyFormState
import com.realestateassistant.pro.presentation.model.PropertySection
import com.realestateassistant.pro.presentation.viewmodel.OptionsViewModel
import com.realestateassistant.pro.presentation.components.EditableDropdownFieldWithText

@Composable
fun LivingConditionsSection(
    formState: PropertyFormState,
    onFormStateChange: (PropertyFormState) -> Unit,
    optionsViewModel: OptionsViewModel,
    expandedSections: MutableMap<PropertySection, Boolean>,
    isFieldInvalid: (String) -> Boolean,
    showOnlyRequiredFields: Boolean = false
) {
    val petTypes by optionsViewModel.petTypes.collectAsState()
    
    // Кэшируем локальные переменные для уменьшения перерисовок
    val childrenAllowed = formState.childrenAllowed
    val petsAllowed = formState.petsAllowed

    ExpandablePropertyCard(
        title = "Условия проживания",
        sectionKey = PropertySection.LIVING_CONDITIONS,
        expandedSections = expandedSections,
        hasError = false
    ) {
        // В этой секции все поля не обязательные, отображаем их только если не включен режим только обязательных полей
        if (!showOnlyRequiredFields) {
            CheckboxWithText(
                checked = childrenAllowed,
                onCheckedChange = { onFormStateChange(formState.copy(childrenAllowed = it)) },
                text = "Можно с детьми"
            )

            if (childrenAllowed) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NumericTextField(
                        value = formState.minChildAge,
                        onValueChange = { onFormStateChange(formState.copy(minChildAge = it)) },
                        label = "Минимальный возраст",
                        modifier = Modifier.weight(1f),
                        allowDecimal = false
                    )
                    NumericTextField(
                        value = formState.maxChildrenCount,
                        onValueChange = { onFormStateChange(formState.copy(maxChildrenCount = it)) },
                        label = "Максимум детей",
                        modifier = Modifier.weight(1f),
                        allowDecimal = false
                    )
                }
            }

            CheckboxWithText(
                checked = petsAllowed,
                onCheckedChange = { onFormStateChange(formState.copy(petsAllowed = it)) },
                text = "Можно с животными"
            )

            if (petsAllowed) {
                NumericTextField(
                    value = formState.maxPetsCount,
                    onValueChange = { onFormStateChange(formState.copy(maxPetsCount = it)) },
                    label = "Максимальное количество животных",
                    allowDecimal = false
                )

                MultiSelectField(
                    label = "Разрешенные типы животных",
                    options = petTypes,
                    selectedOptions = formState.allowedPetTypes,
                    onSelectionChange = { onFormStateChange(formState.copy(allowedPetTypes = it)) },
                    onOptionsChange = optionsViewModel::updatePetTypes
                )
            }
        }
    }
} 