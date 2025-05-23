package com.realestateassistant.pro.presentation.screens.property

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.presentation.components.*
import com.realestateassistant.pro.presentation.model.PropertyFormState
import com.realestateassistant.pro.presentation.model.PropertySection

@Composable
fun LongTermRentalSection(
    formState: PropertyFormState,
    onFormStateChange: (PropertyFormState) -> Unit,
    expandedSections: MutableMap<PropertySection, Boolean>
) {
    // Кэшируем локальные переменные
    val hasCompensationContract = formState.hasCompensationContract
    val isSelfEmployed = formState.isSelfEmployed
    val isPersonalIncomeTax = formState.isPersonalIncomeTax
    val isCompanyIncomeTax = formState.isCompanyIncomeTax
    val utilitiesIncluded = formState.utilitiesIncluded

    ExpandablePropertyCard(
        title = "Условия долгосрочной аренды",
        sectionKey = PropertySection.LONG_TERM_RENTAL,
        expandedSections = expandedSections
    ) {
        NumericTextField(
            value = formState.monthlyRent,
            onValueChange = { onFormStateChange(formState.copy(monthlyRent = it)) },
            label = "Круглогодичная стоимость аренды",
            allowDecimal = true,
            suffix = " ₽"
        )
        
        NumericTextField(
            value = formState.winterMonthlyRent,
            onValueChange = { onFormStateChange(formState.copy(winterMonthlyRent = it)) },
            label = "Стоимость аренды зимой",
            allowDecimal = true,
            suffix = " ₽"
        )
        
        NumericTextField(
            value = formState.summerMonthlyRent,
            onValueChange = { onFormStateChange(formState.copy(summerMonthlyRent = it)) },
            label = "Стоимость аренды летом",
            allowDecimal = true,
            suffix = " ₽"
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Договор с компенсацией",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Используем оптимизированный компонент
            RadioGroupRow(
                options = listOf("Да", "Нет"),
                selectedOption = if (hasCompensationContract) "Да" else "Нет",
                onOptionSelected = { option ->
                    onFormStateChange(formState.copy(hasCompensationContract = option == "Да"))
                }
            )
        }

        if (hasCompensationContract) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Тип регистрации",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Используем checkboxes с оптимизированной перерисовкой
                CheckboxWithText(
                    checked = isSelfEmployed,
                    onCheckedChange = { onFormStateChange(formState.copy(isSelfEmployed = it)) },
                    text = "Самозанятый"
                )
                
                CheckboxWithText(
                    checked = isPersonalIncomeTax,
                    onCheckedChange = { onFormStateChange(formState.copy(isPersonalIncomeTax = it)) },
                    text = "НДФЛ"
                )
                
                CheckboxWithText(
                    checked = isCompanyIncomeTax,
                    onCheckedChange = { onFormStateChange(formState.copy(isCompanyIncomeTax = it)) },
                    text = "НДФЛ для юрлиц"
                )
            }
        }

        CheckboxWithText(
            checked = utilitiesIncluded,
            onCheckedChange = { onFormStateChange(formState.copy(utilitiesIncluded = it)) },
            text = "Коммунальные включены"
        )

        if (!utilitiesIncluded) {
            NumericTextField(
                value = formState.utilitiesCost,
                onValueChange = { onFormStateChange(formState.copy(utilitiesCost = it)) },
                label = "Стоимость коммунальных",
                allowDecimal = true,
                suffix = " ₽"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NumericTextField(
                value = formState.minRentPeriod,
                onValueChange = { onFormStateChange(formState.copy(minRentPeriod = it)) },
                label = "Минимальный срок",
                modifier = Modifier.weight(1f),
                allowDecimal = false
            )
            NumericTextField(
                value = formState.maxRentPeriod,
                onValueChange = { onFormStateChange(formState.copy(maxRentPeriod = it)) },
                label = "Максимальный срок",
                modifier = Modifier.weight(1f),
                allowDecimal = false
            )
        }

        NumericTextField(
            value = formState.depositCustomAmount,
            onValueChange = { onFormStateChange(formState.copy(depositCustomAmount = it)) },
            label = "Залог",
            allowDecimal = true,
            suffix = " ₽"
        )

        NumericTextField(
            value = formState.securityDeposit,
            onValueChange = { onFormStateChange(formState.copy(securityDeposit = it)) },
            label = "Страховой депозит",
            allowDecimal = true,
            suffix = " ₽"
        )

        OutlinedTextFieldWithColors(
            value = formState.additionalExpenses,
            onValueChange = { onFormStateChange(formState.copy(additionalExpenses = it)) },
            label = "Дополнительные расходы",
            minLines = 3,
            maxLines = 5
        )

        OutlinedTextFieldWithColors(
            value = formState.longTermRules,
            onValueChange = { onFormStateChange(formState.copy(longTermRules = it)) },
            label = "Правила проживания",
            minLines = 3,
            maxLines = 5
        )
    }
} 