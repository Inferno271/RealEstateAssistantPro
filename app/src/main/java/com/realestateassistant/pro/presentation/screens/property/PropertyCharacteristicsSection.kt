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

@Composable
fun PropertyCharacteristicsSection(
    formState: PropertyFormState,
    onFormStateChange: (PropertyFormState) -> Unit,
    optionsViewModel: OptionsViewModel,
    repairStates: List<String>,
    bathroomTypes: List<String>,
    heatingTypes: List<String>,
    parkingTypes: List<String>,
    expandedSections: MutableMap<PropertySection, Boolean>
) {
    val amenities by optionsViewModel.amenities.collectAsState()
    
    // Кэшируем локальные состояния
    val noFurniture = formState.noFurniture
    val hasAppliances = formState.hasAppliances
    val hasParking = formState.hasParking
    val smokingAllowed = formState.smokingAllowed

    ExpandablePropertyCard(
        title = "Характеристики объекта",
        sectionKey = PropertySection.PROPERTY_CHARACTERISTICS,
        expandedSections = expandedSections
    ) {
        EditableDropdownField(
            value = formState.repairState,
            onValueChange = { onFormStateChange(formState.copy(repairState = it)) },
            label = "Состояние ремонта",
            options = repairStates,
            onOptionsChange = optionsViewModel::updateRepairStates
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NumericTextField(
                value = formState.bedsCount,
                onValueChange = { onFormStateChange(formState.copy(bedsCount = it)) },
                label = "Спальных мест",
                modifier = Modifier.weight(1f),
                allowDecimal = false
            )
            NumericTextField(
                value = formState.bathroomsCount,
                onValueChange = { onFormStateChange(formState.copy(bathroomsCount = it)) },
                label = "Санузлов",
                modifier = Modifier.weight(1f),
                allowDecimal = false
            )
        }

        EditableDropdownField(
            value = formState.bathroomType,
            onValueChange = { onFormStateChange(formState.copy(bathroomType = it)) },
            label = "Тип санузла",
            options = bathroomTypes,
            onOptionsChange = optionsViewModel::updateBathroomTypes
        )

        CheckboxWithText(
            checked = noFurniture,
            onCheckedChange = { onFormStateChange(formState.copy(noFurniture = it)) },
            text = "Без мебели"
        )

        CheckboxWithText(
            checked = hasAppliances,
            onCheckedChange = { onFormStateChange(formState.copy(hasAppliances = it)) },
            text = "Бытовая техника"
        )

        EditableDropdownField(
            value = formState.heatingType,
            onValueChange = { onFormStateChange(formState.copy(heatingType = it)) },
            label = "Отопление",
            options = heatingTypes,
            onOptionsChange = optionsViewModel::updateHeatingTypes
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NumericTextField(
                value = formState.balconiesCount,
                onValueChange = { onFormStateChange(formState.copy(balconiesCount = it)) },
                label = "Балконов",
                modifier = Modifier.weight(1f),
                allowDecimal = false
            )
            NumericTextField(
                value = formState.elevatorsCount,
                onValueChange = { onFormStateChange(formState.copy(elevatorsCount = it)) },
                label = "Лифтов",
                modifier = Modifier.weight(1f),
                allowDecimal = false
            )
        }

        CheckboxWithText(
            checked = hasParking,
            onCheckedChange = { onFormStateChange(formState.copy(hasParking = it)) },
            text = "Парковка"
        )

        if (hasParking) {
            EditableDropdownField(
                value = formState.parkingType,
                onValueChange = { onFormStateChange(formState.copy(parkingType = it)) },
                label = "Тип парковки",
                options = parkingTypes,
                onOptionsChange = optionsViewModel::updateParkingTypes
            )

            NumericTextField(
                value = formState.parkingSpaces,
                onValueChange = { onFormStateChange(formState.copy(parkingSpaces = it)) },
                label = "Количество мест",
                allowDecimal = false
            )
        }

        MultiSelectField(
            label = "Удобства",
            options = amenities,
            selectedOptions = formState.amenities,
            onSelectionChange = { onFormStateChange(formState.copy(amenities = it)) },
            onOptionsChange = optionsViewModel::updateAmenities
        )

        CheckboxWithText(
            checked = smokingAllowed,
            onCheckedChange = { onFormStateChange(formState.copy(smokingAllowed = it)) },
            text = "Разрешено курение"
        )
    }
} 