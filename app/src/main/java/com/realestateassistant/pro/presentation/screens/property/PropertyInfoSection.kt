package com.realestateassistant.pro.presentation.screens.property

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.domain.model.PropertyCharacteristicsConfig
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
    expandedSections: MutableMap<PropertySection, Boolean>,
    characteristicsConfig: PropertyCharacteristicsConfig = PropertyCharacteristicsConfig.DEFAULT,
    isFieldInvalid: (String) -> Boolean,
    showOnlyRequiredFields: Boolean = false
) {
    val nearbyObjects by optionsViewModel.nearbyObjects.collectAsState()
    val views by optionsViewModel.views.collectAsState()
    val poolTypes by optionsViewModel.poolTypes.collectAsState()

    // Проверяем наличие ошибок в секции
    val hasPropertyTypeError = isFieldInvalid("propertyType")
    val hasAddressError = isFieldInvalid("address")
    val hasDistrictError = isFieldInvalid("district")
    val hasAreaError = isFieldInvalid("area")
    val hasRoomsCountError = isFieldInvalid("roomsCount")
    
    val hasSectionError = hasPropertyTypeError || hasAddressError || hasDistrictError || hasAreaError || hasRoomsCountError
    
    // Формируем сообщение об ошибке для секции
    val errorMessage = when {
        hasPropertyTypeError -> "Необходимо указать тип недвижимости"
        hasAddressError -> "Необходимо указать адрес объекта"
        hasDistrictError -> "Необходимо указать район/метро"
        hasAreaError -> "Необходимо указать площадь объекта"
        hasRoomsCountError -> "Необходимо указать количество комнат"
        else -> null
    }

    ExpandablePropertyCard(
        title = "Общая информация",
        sectionKey = PropertySection.PROPERTY_INFO,
        expandedSections = expandedSections,
        hasError = hasSectionError,
        errorMessage = errorMessage
    ) {
        EditableDropdownField(
            value = formState.propertyType,
            onValueChange = { onFormStateChange(formState.copy(propertyType = it)) },
            label = "Тип недвижимости",
            options = propertyTypes,
            onOptionsChange = optionsViewModel::updatePropertyTypes,
            isError = hasPropertyTypeError,
            errorMessage = if (hasPropertyTypeError) "Обязательное поле" else null,
            isRequired = true
        )

        AddressTextField(
            value = GeocodedAddress(
                address = formState.address,
                latitude = formState.latitude?.toDoubleOrNull(),
                longitude = formState.longitude?.toDoubleOrNull()
            ),
            onValueChange = { geocodedAddress ->
                onFormStateChange(
                    formState.copy(
                        address = geocodedAddress.address,
                        latitude = geocodedAddress.latitude?.toString() ?: "",
                        longitude = geocodedAddress.longitude?.toString() ?: ""
                    )
                )
            },
            isError = hasAddressError,
            errorMessage = if (hasAddressError) "Обязательное поле" else null,
            isRequired = true
        )

        EditableDropdownField(
            value = formState.district,
            onValueChange = { onFormStateChange(formState.copy(district = it)) },
            label = "Район/Метро",
            options = districts,
            onOptionsChange = optionsViewModel::updateDistricts,
            isError = hasDistrictError,
            errorMessage = if (hasDistrictError) "Обязательное поле" else null,
            isRequired = true
        )

        // Отображаем необязательные поля только если не включен режим только обязательных полей
        if (!showOnlyRequiredFields) {
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
        }

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
                suffix = " м²",
                isError = hasAreaError,
                errorMessage = if (hasAreaError) "Обязательное поле" else null,
                isRequired = true
            )
            NumericTextField(
                value = formState.roomsCount,
                onValueChange = { onFormStateChange(formState.copy(roomsCount = it)) },
                label = "Комнат",
                modifier = Modifier.weight(1f),
                allowDecimal = false,
                isError = hasRoomsCountError,
                errorMessage = if (hasRoomsCountError) "Обязательное поле" else null,
                isRequired = true
            )
        }

        // Отображаем необязательные поля только если не включен режим только обязательных полей
        if (!showOnlyRequiredFields) {
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

            // Отображаем поля этаж и этажность только для объектов с hasFloor = true
            if (characteristicsConfig.hasFloor) {
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
            }

            // Добавляем специфичные поля для типа недвижимости
            if (characteristicsConfig.hasLevels) {
                NumericTextField(
                    value = formState.levelsCount,
                    onValueChange = { onFormStateChange(formState.copy(levelsCount = it)) },
                    label = "Количество уровней",
                    allowDecimal = false
                )
            }
            
            if (characteristicsConfig.hasLandSquare) {
                NumericTextField(
                    value = formState.landArea,
                    onValueChange = { onFormStateChange(formState.copy(landArea = it)) },
                    label = "Площадь участка (соток)",
                    allowDecimal = true,
                    suffix = " сот."
                )
            }
            
            if (characteristicsConfig.hasGarage) {
                CheckboxWithText(
                    checked = formState.hasGarage,
                    onCheckedChange = { onFormStateChange(formState.copy(hasGarage = it)) },
                    text = "Есть гараж"
                )
                
                if (formState.hasGarage) {
                    NumericTextField(
                        value = formState.garageSpaces,
                        onValueChange = { onFormStateChange(formState.copy(garageSpaces = it)) },
                        label = "Количество машиномест",
                        allowDecimal = false
                    )
                }
            }
            
            if (characteristicsConfig.hasBathhouse) {
                CheckboxWithText(
                    checked = formState.hasBathhouse,
                    onCheckedChange = { onFormStateChange(formState.copy(hasBathhouse = it)) },
                    text = "Есть баня/сауна"
                )
            }
            
            if (characteristicsConfig.hasPool) {
                CheckboxWithText(
                    checked = formState.hasPool,
                    onCheckedChange = { onFormStateChange(formState.copy(hasPool = it)) },
                    text = "Есть бассейн"
                )
                
                if (formState.hasPool) {
                    EditableDropdownField(
                        value = formState.poolType,
                        onValueChange = { onFormStateChange(formState.copy(poolType = it)) },
                        label = "Тип бассейна",
                        options = poolTypes,
                        onOptionsChange = optionsViewModel::updatePoolTypes
                    )
                }
            }

            OutlinedTextFieldWithColors(
                value = formState.description,
                onValueChange = { onFormStateChange(formState.copy(description = it)) },
                label = "Описание объекта",
                minLines = 3,
                maxLines = 5,
                isError = false
            )
        }
    }
} 