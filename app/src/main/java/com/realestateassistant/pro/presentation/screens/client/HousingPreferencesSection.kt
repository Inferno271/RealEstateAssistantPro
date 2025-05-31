package com.realestateassistant.pro.presentation.screens.client

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
 * Секция с предпочтениями клиента по жилищным характеристикам.
 * 
 * @param formState Состояние формы клиента
 * @param onFormStateChange Функция обновления состояния формы
 * @param expandedSections Карта с состояниями развернутости секций
 * @param optionsViewModel ViewModel для работы со справочниками
 * @param isFieldInvalid Функция для проверки валидности поля
 */
@Composable
fun HousingPreferencesSection(
    formState: ClientFormState,
    onFormStateChange: (ClientFormState) -> Unit,
    expandedSections: MutableMap<ClientSection, Boolean>,
    optionsViewModel: OptionsViewModel = hiltViewModel(),
    isFieldInvalid: (String) -> Boolean = { false }
) {
    // Получаем списки опций из ViewModel
    val repairStates by optionsViewModel.repairStates.collectAsState(initial = emptyList())
    val bathroomTypes by optionsViewModel.bathroomTypes.collectAsState(initial = emptyList())
    val heatingTypes by optionsViewModel.heatingTypes.collectAsState(initial = emptyList())
    val parkingTypes by optionsViewModel.parkingTypes.collectAsState(initial = emptyList())
    val views by optionsViewModel.views.collectAsState(initial = emptyList())
    
    // Создаем кэшированные копии состояний для уменьшения перерисовок
    val needsElevator = formState.needsElevator
    val needsFurniture = formState.needsFurniture
    val needsAppliances = formState.needsAppliances
    val needsParking = formState.needsParking
    val isSmokingClient = formState.isSmokingClient

    ExpandableClientCard(
        title = "Предпочтения по жилищным характеристикам",
        sectionKey = ClientSection.HOUSING_PREFERENCES,
        expandedSections = expandedSections
    ) {
        // Предпочтения по этажности
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NumericTextField(
                value = formState.preferredFloorMin,
                onValueChange = { onFormStateChange(formState.copy(preferredFloorMin = it)) },
                label = "Этаж от",
                modifier = Modifier.weight(1f),
                allowDecimal = false
            )
            
            NumericTextField(
                value = formState.preferredFloorMax,
                onValueChange = { onFormStateChange(formState.copy(preferredFloorMax = it)) },
                label = "Этаж до",
                modifier = Modifier.weight(1f),
                allowDecimal = false
            )
        }
        
        // Нужен ли лифт
        CheckboxWithText(
            checked = needsElevator,
            onCheckedChange = { onFormStateChange(formState.copy(needsElevator = it)) },
            text = "Обязательно наличие лифта"
        )
        
        // Состояние ремонта
        EditableDropdownField(
            value = formState.preferredRepairState,
            onValueChange = { onFormStateChange(formState.copy(preferredRepairState = it)) },
            label = "Предпочтительное состояние ремонта",
            options = repairStates,
            onOptionsChange = optionsViewModel::updateRepairStates
        )
        
        // Количество балконов
        NumericTextField(
            value = formState.preferredBalconiesCount,
            onValueChange = { onFormStateChange(formState.copy(preferredBalconiesCount = it)) },
            label = "Желаемое количество балконов",
            allowDecimal = false
        )
        
        // Количество санузлов
        NumericTextField(
            value = formState.preferredBathroomsCount,
            onValueChange = { onFormStateChange(formState.copy(preferredBathroomsCount = it)) },
            label = "Желаемое количество санузлов",
            allowDecimal = false
        )
        
        // Тип санузла
        EditableDropdownField(
            value = formState.preferredBathroomType,
            onValueChange = { onFormStateChange(formState.copy(preferredBathroomType = it)) },
            label = "Предпочтительный тип санузла",
            options = bathroomTypes,
            onOptionsChange = optionsViewModel::updateBathroomTypes
        )
        
        // Мебель и бытовая техника
        CheckboxWithText(
            checked = needsFurniture,
            onCheckedChange = { onFormStateChange(formState.copy(needsFurniture = it)) },
            text = "Нужна мебель"
        )
        
        CheckboxWithText(
            checked = needsAppliances,
            onCheckedChange = { onFormStateChange(formState.copy(needsAppliances = it)) },
            text = "Нужна бытовая техника"
        )
        
        // Тип отопления
        EditableDropdownField(
            value = formState.preferredHeatingType,
            onValueChange = { onFormStateChange(formState.copy(preferredHeatingType = it)) },
            label = "Предпочтительный тип отопления",
            options = heatingTypes,
            onOptionsChange = optionsViewModel::updateHeatingTypes
        )
        
        // Парковка
        CheckboxWithText(
            checked = needsParking,
            onCheckedChange = { onFormStateChange(formState.copy(needsParking = it)) },
            text = "Нужна парковка"
        )
        
        if (needsParking) {
            EditableDropdownField(
                value = formState.preferredParkingType,
                onValueChange = { onFormStateChange(formState.copy(preferredParkingType = it)) },
                label = "Предпочтительный тип парковки",
                options = parkingTypes,
                onOptionsChange = optionsViewModel::updateParkingTypes
            )
        }
        
        // Виды из окон
        MultiSelectField(
            label = "Предпочтительные виды из окон",
            options = views,
            selectedOptions = formState.preferredViews,
            onSelectionChange = { onFormStateChange(formState.copy(preferredViews = it)) },
            onOptionsChange = optionsViewModel::updateViews
        )
        
        // Статус курящего клиента
        CheckboxWithText(
            checked = isSmokingClient,
            onCheckedChange = { onFormStateChange(formState.copy(isSmokingClient = it)) },
            text = "Клиент является курящим"
        )
    }
} 