package com.realestateassistant.pro.presentation.screens.client

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.presentation.components.EditableDropdownField
import com.realestateassistant.pro.presentation.components.EditableDropdownFieldWithText
import com.realestateassistant.pro.presentation.components.ExpandableClientCard
import com.realestateassistant.pro.presentation.components.OutlinedTextFieldWithColors
import com.realestateassistant.pro.presentation.model.ClientFormState
import com.realestateassistant.pro.presentation.model.ClientSection
import com.realestateassistant.pro.presentation.viewmodel.OptionsViewModel

/**
 * Секция с общими предпочтениями по аренде для клиента.
 * 
 * @param formState Состояние формы клиента
 * @param onFormStateChange Функция обновления состояния формы
 * @param expandedSections Карта с состояниями развернутости секций
 * @param optionsViewModel ViewModel для работы со справочниками
 * @param isFieldInvalid Функция для проверки валидности поля
 * @param showOnlyRequiredFields Флаг отображения только обязательных полей
 */
@Composable
fun RentalPreferencesSection(
    formState: ClientFormState,
    onFormStateChange: (ClientFormState) -> Unit,
    expandedSections: MutableMap<ClientSection, Boolean>,
    optionsViewModel: OptionsViewModel = hiltViewModel(),
    isFieldInvalid: (String) -> Boolean = { false },
    showOnlyRequiredFields: Boolean = false
) {
    // Получаем список районов из ViewModel
    val districts by optionsViewModel.districts.collectAsState()
    // Получаем список типов недвижимости из ViewModel
    val propertyTypes by optionsViewModel.propertyTypes.collectAsState(initial = emptyList())
    
    ExpandableClientCard(
        title = "Предпочтения по аренде",
        sectionKey = ClientSection.RENTAL_PREFERENCES,
        expandedSections = expandedSections,
        isError = isFieldInvalid("desiredPropertyType") || isFieldInvalid("preferredDistrict")
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Группа полей для предпочтений по недвижимости
            Text(
                text = "Предпочтения по объекту",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Тип недвижимости (обязательное поле)
            EditableDropdownFieldWithText(
                value = formState.desiredPropertyType,
                onValueChange = { onFormStateChange(formState.copy(desiredPropertyType = it)) },
                label = "Тип недвижимости",
                options = propertyTypes,
                onOptionsChange = optionsViewModel::updatePropertyTypes,
                placeholderText = "Выберите или добавьте тип недвижимости",
                isError = isFieldInvalid("desiredPropertyType"),
                errorMessage = if (isFieldInvalid("desiredPropertyType")) "Необходимо указать тип недвижимости" else null,
                isRequired = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Группа полей для предпочтений по району
            Text(
                text = "Предпочтения по расположению",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Предпочитаемый район (обязательное поле)
            EditableDropdownFieldWithText(
                value = formState.preferredDistrict,
                onValueChange = { onFormStateChange(formState.copy(preferredDistrict = it)) },
                label = "Район",
                options = districts,
                onOptionsChange = optionsViewModel::updateDistricts,
                placeholderText = "Выберите или добавьте район",
                isError = isFieldInvalid("preferredDistrict"),
                errorMessage = if (isFieldInvalid("preferredDistrict")) "Необходимо указать район" else null,
                isRequired = true
            )
            
            // Отображаем необязательные поля только если не включен режим только обязательных полей
            if (!showOnlyRequiredFields) {
                // Предпочитаемый адрес
                OutlinedTextFieldWithColors(
                    value = formState.preferredAddress,
                    onValueChange = { onFormStateChange(formState.copy(preferredAddress = it)) },
                    label = "Адрес или ориентир",
                    placeholder = { Text("Улица, дом или ближайший ориентир") },
                    minLines = 1,
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Дополнительные комментарии
                Text(
                    text = "Дополнительная информация",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Комментарии
                OutlinedTextFieldWithColors(
                    value = formState.additionalComments,
                    onValueChange = { onFormStateChange(formState.copy(additionalComments = it)) },
                    label = "Дополнительные комментарии",
                    placeholder = { Text("Особые пожелания клиента по аренде") },
                    minLines = 3,
                    maxLines = 5
                )
            }
        }
    }
} 