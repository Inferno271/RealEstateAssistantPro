package com.realestateassistant.pro.presentation.screens.property

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
    expandedSections: MutableMap<PropertySection, Boolean>
) {
    ExpandablePropertyCard(
        title = "Контактная информация",
        sectionKey = PropertySection.CONTACT_INFO,
        expandedSections = expandedSections
    ) {
        OutlinedTextFieldWithColors(
            value = formState.contactName,
            onValueChange = { onFormStateChange(formState.copy(contactName = it)) },
            label = "Контактное лицо"
        )

        PhoneListField(
            phones = formState.contactPhone,
            onPhonesChange = { onFormStateChange(formState.copy(contactPhone = it)) }
        )

        OutlinedTextFieldWithColors(
            value = formState.additionalContactInfo,
            onValueChange = { onFormStateChange(formState.copy(additionalContactInfo = it)) },
            label = "Дополнительная информация",
            minLines = 3,
            maxLines = 5
        )
    }
} 