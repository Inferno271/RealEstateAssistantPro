package com.realestateassistant.pro.presentation.screens.client

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.realestateassistant.pro.presentation.components.ExpandableClientCard
import com.realestateassistant.pro.presentation.components.OutlinedTextFieldWithColors
import com.realestateassistant.pro.presentation.components.PhoneListField
import com.realestateassistant.pro.presentation.model.ClientFormState
import com.realestateassistant.pro.presentation.model.ClientSection

/**
 * Секция с контактной информацией клиента.
 * 
 * @param formState Состояние формы клиента
 * @param onFormStateChange Функция обновления состояния формы
 * @param expandedSections Карта с состояниями развернутости секций
 * @param isFieldInvalid Функция для проверки валидности поля
 * @param showOnlyRequiredFields Флаг отображения только обязательных полей
 */
@Composable
fun ContactInfoSection(
    formState: ClientFormState,
    onFormStateChange: (ClientFormState) -> Unit,
    expandedSections: MutableMap<ClientSection, Boolean>,
    isFieldInvalid: (String) -> Boolean = { false },
    showOnlyRequiredFields: Boolean = false
) {
    ExpandableClientCard(
        title = "Контактная информация",
        sectionKey = ClientSection.CONTACT_INFO,
        expandedSections = expandedSections,
        isError = isFieldInvalid("fullName") || isFieldInvalid("phone")
    ) {
        OutlinedTextFieldWithColors(
            value = formState.fullName,
            onValueChange = { onFormStateChange(formState.copy(fullName = it)) },
            label = "ФИО клиента",
            placeholder = { Text("Введите полное имя клиента") },
            isError = isFieldInvalid("fullName"),
            errorMessage = if (isFieldInvalid("fullName")) "Необходимо указать ФИО клиента" else null,
            isRequired = true
        )

        PhoneListField(
            phones = formState.phone,
            onPhonesChange = { onFormStateChange(formState.copy(phone = it)) },
            isError = isFieldInvalid("phone")
        )

        // Отображаем необязательные поля только если не включен режим только обязательных полей
        if (!showOnlyRequiredFields) {
            OutlinedTextFieldWithColors(
                value = formState.comment,
                onValueChange = { onFormStateChange(formState.copy(comment = it)) },
                label = "Дополнительная информация",
                placeholder = { Text("Введите любую дополнительную информацию о клиенте") },
                minLines = 3,
                maxLines = 5
            )
        }
    }
} 