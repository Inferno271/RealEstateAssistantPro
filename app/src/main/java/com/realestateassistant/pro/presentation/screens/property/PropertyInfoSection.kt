package com.realestateassistant.pro.presentation.screens.property

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.presentation.components.*
import com.realestateassistant.pro.presentation.model.PropertyFormState
import com.realestateassistant.pro.presentation.model.PropertySection
import com.realestateassistant.pro.presentation.viewmodel.OptionsViewModel

@Composable
fun PropertyInfoSection(
    formState: PropertyFormState,
    onFormStateChange: (PropertyFormState) -> Unit,
    optionsViewModel: OptionsViewModel,
    propertyTypes: List<String>,
    districts: List<String>,
    layouts: List<String>,
    expandedSections: MutableMap<PropertySection, Boolean>
) {
    val nearbyObjects by optionsViewModel.nearbyObjects.collectAsState()
    val views by optionsViewModel.views.collectAsState()

    ExpandablePropertyCard(
        title = "Общая информация",
        sectionKey = PropertySection.PROPERTY_INFO,
        expandedSections = expandedSections
    ) {
        EditableDropdownField(
            value = formState.propertyType,
            onValueChange = { onFormStateChange(formState.copy(propertyType = it)) },
            label = "Тип недвижимости",
            options = propertyTypes,
            onOptionsChange = optionsViewModel::updatePropertyTypes
        )

        AddressTextField(
            value = formState.address,
            onValueChange = { onFormStateChange(formState.copy(address = it)) }
        )

        EditableDropdownField(
            value = formState.district,
            onValueChange = { onFormStateChange(formState.copy(district = it)) },
            label = "Район/Метро",
            options = districts,
            onOptionsChange = optionsViewModel::updateDistricts
        )

        MultiSelectField(
            label = "Близость к объектам",
            options = nearbyObjects,
            selectedOptions = formState.nearbyObjects,
            onSelectionChange = { onFormStateChange(formState.copy(nearbyObjects = it)) },
            onOptionsChange = optionsViewModel::updateNearbyObjects
        )

        MultiSelectField(
            label = "Вид из окон",
            options = views,
            selectedOptions = formState.views,
            onSelectionChange = { onFormStateChange(formState.copy(views = it)) },
            onOptionsChange = optionsViewModel::updateViews
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NumericTextField(
                value = formState.area,
                onValueChange = { onFormStateChange(formState.copy(area = it)) },
                label = "Площадь (м²)",
                modifier = Modifier.weight(1f),
                allowDecimal = true,
                suffix = " м²"
            )
            NumericTextField(
                value = formState.roomsCount,
                onValueChange = { onFormStateChange(formState.copy(roomsCount = it)) },
                label = "Комнат",
                modifier = Modifier.weight(1f),
                allowDecimal = false
            )
        }

        CheckboxWithText(
            checked = formState.isStudio,
            onCheckedChange = { onFormStateChange(formState.copy(isStudio = it)) },
            text = "Студия"
        )

        EditableDropdownField(
            value = formState.layout,
            onValueChange = { onFormStateChange(formState.copy(layout = it)) },
            label = "Планировка",
            options = layouts,
            onOptionsChange = optionsViewModel::updateLayouts
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NumericTextField(
                value = formState.floor,
                onValueChange = { onFormStateChange(formState.copy(floor = it)) },
                label = "Этаж",
                modifier = Modifier.weight(1f),
                allowDecimal = false
            )
            NumericTextField(
                value = formState.totalFloors,
                onValueChange = { onFormStateChange(formState.copy(totalFloors = it)) },
                label = "Этажность",
                modifier = Modifier.weight(1f),
                allowDecimal = false
            )
        }

        OutlinedTextFieldWithColors(
            value = formState.description,
            onValueChange = { onFormStateChange(formState.copy(description = it)) },
            label = "Описание объекта",
            minLines = 3,
            maxLines = 5
        )
    }
} 