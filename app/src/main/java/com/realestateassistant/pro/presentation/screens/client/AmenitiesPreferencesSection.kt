package com.realestateassistant.pro.presentation.screens.client

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.presentation.components.ExpandableClientCard
import com.realestateassistant.pro.presentation.components.MultiSelectField
import com.realestateassistant.pro.presentation.components.EditableDropdownFieldWithText
import com.realestateassistant.pro.presentation.model.ClientFormState
import com.realestateassistant.pro.presentation.model.ClientSection
import com.realestateassistant.pro.presentation.viewmodel.OptionsViewModel

/**
 * Секция с предпочтениями клиента по удобствам и объектам инфраструктуры.
 * 
 * @param formState Состояние формы клиента
 * @param onFormStateChange Функция обновления состояния формы
 * @param expandedSections Карта с состояниями развернутости секций
 * @param optionsViewModel ViewModel для работы со справочниками
 * @param isFieldInvalid Функция для проверки валидности поля
 */
@Composable
fun AmenitiesPreferencesSection(
    formState: ClientFormState,
    onFormStateChange: (ClientFormState) -> Unit,
    expandedSections: MutableMap<ClientSection, Boolean>,
    optionsViewModel: OptionsViewModel = hiltViewModel(),
    isFieldInvalid: (String) -> Boolean = { false }
) {
    // Получаем списки опций из ViewModel
    val amenities by optionsViewModel.amenities.collectAsState(initial = emptyList())
    val nearbyObjects by optionsViewModel.nearbyObjects.collectAsState(initial = emptyList())
    
    ExpandableClientCard(
        title = "Предпочтения по удобствам",
        sectionKey = ClientSection.AMENITIES_PREFERENCES,
        expandedSections = expandedSections
    ) {
        // Удобства в жилье
        MultiSelectField(
            label = "Желаемые удобства в жилье",
            options = amenities,
            selectedOptions = formState.preferredAmenities,
            onSelectionChange = { onFormStateChange(formState.copy(preferredAmenities = it)) },
            onOptionsChange = optionsViewModel::updateAmenities
        )
        
        // Объекты инфраструктуры рядом
        MultiSelectField(
            label = "Желаемые объекты инфраструктуры поблизости",
            options = nearbyObjects,
            selectedOptions = formState.preferredNearbyObjects,
            onSelectionChange = { onFormStateChange(formState.copy(preferredNearbyObjects = it)) },
            onOptionsChange = optionsViewModel::updateNearbyObjects
        )
    }
} 