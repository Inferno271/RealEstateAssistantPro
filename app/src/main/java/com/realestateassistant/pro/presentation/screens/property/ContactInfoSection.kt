package com.realestateassistant.pro.presentation.screens.property

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.realestateassistant.pro.presentation.components.ExpandablePropertyCard
import com.realestateassistant.pro.presentation.components.OutlinedTextFieldWithColors
import com.realestateassistant.pro.presentation.components.PhoneListField
import com.realestateassistant.pro.presentation.model.PropertyFormState
import com.realestateassistant.pro.presentation.model.PropertySection

@Composable
fun ContactInfoSection(
    formState: PropertyFormState,
    onFormStateChange: (PropertyFormState) -> Unit,
    expandedSections: MutableMap<PropertySection, Boolean>,
    isFieldInvalid: (String) -> Boolean,
    showOnlyRequiredFields: Boolean = false
) {
    // Проверяем наличие ошибок в секции
    val hasContactNameError = isFieldInvalid("contactName")
    val hasContactPhoneError = isFieldInvalid("contactPhone")
    val hasSectionError = hasContactNameError || hasContactPhoneError
    
    // Формируем сообщение об ошибке для секции
    val errorMessage = when {
        hasContactNameError -> "Необходимо указать имя контактного лица"
        hasContactPhoneError -> "Укажите хотя бы один номер телефона"
        else -> null
    }
    
    ExpandablePropertyCard(
        title = "Контактная информация",
        sectionKey = PropertySection.CONTACT_INFO,
        expandedSections = expandedSections,
        hasError = hasSectionError,
        errorMessage = errorMessage
    ) {
        OutlinedTextFieldWithColors(
            value = formState.contactName,
            onValueChange = { onFormStateChange(formState.copy(contactName = it)) },
            label = "Контактное лицо",
            placeholder = { Text("Введите имя контактного лица") },
            isRequired = true,
            isError = hasContactNameError,
            errorMessage = if (hasContactNameError) "Обязательное поле" else null
        )

        PhoneListField(
            phones = formState.contactPhone,
            onPhonesChange = { onFormStateChange(formState.copy(contactPhone = it)) },
            isRequired = true,
            isError = hasContactPhoneError,
            errorMessage = if (hasContactPhoneError) "Укажите хотя бы один номер телефона" else null
        )

        // Отображаем необязательные поля только если не включен режим только обязательных полей
        if (!showOnlyRequiredFields) {
            OutlinedTextFieldWithColors(
                value = formState.additionalContactInfo,
                onValueChange = { onFormStateChange(formState.copy(additionalContactInfo = it)) },
                label = "Дополнительная информация",
                placeholder = { Text("Введите дополнительную контактную информацию") },
                minLines = 3,
                maxLines = 5
            )
        }
    }
} 