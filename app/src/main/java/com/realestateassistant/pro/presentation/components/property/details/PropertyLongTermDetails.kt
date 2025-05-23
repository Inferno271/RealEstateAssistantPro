package com.realestateassistant.pro.presentation.components.property.details

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.presentation.components.property.FeatureRow
import com.realestateassistant.pro.presentation.components.property.PropertySectionCard
import com.realestateassistant.pro.presentation.utils.formatPrice

@Composable
fun PropertyLongTermDetails(property: Property) {
    // Оптимизируем проверку, добавляем новые сезонные поля для проверки
    val hasData = property.hasCompensationContract || 
                 property.isSelfEmployed || 
                 property.isPersonalIncomeTax || 
                 property.isCompanyIncomeTax ||
                 property.utilitiesIncluded ||
                 property.utilitiesCost != null ||
                 property.maxRentPeriod != null ||
                 property.securityDeposit != null ||
                 !property.additionalExpenses.isNullOrEmpty() ||
                 !property.longTermRules.isNullOrEmpty() ||
                 !property.minRentPeriod.isNullOrEmpty() ||
                 property.depositCustomAmount != null ||
                 property.winterMonthlyRent != null ||
                 property.summerMonthlyRent != null
    
    if (!hasData) return
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Строим колонку для отображения данных по аренде
            
            // Цены аренды
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.AttachMoney,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Стоимость аренды",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(
                modifier = Modifier.padding(start = 36.dp)
            ) {
                // Отображаем основную цену
                property.monthlyRent?.let { price ->
                    FeatureRow(
                        label = "Круглогодичная цена:",
                        value = "${formatPrice(price)} ₽/месяц"
                    )
                }
                
                // Отображаем зимнюю цену, если есть
                property.winterMonthlyRent?.let { winterPrice ->
                    Spacer(modifier = Modifier.height(8.dp))
                    FeatureRow(
                        label = "Цена зимой:",
                        value = "${formatPrice(winterPrice)} ₽/месяц"
                    )
                }
                
                // Отображаем летнюю цену, если есть
                property.summerMonthlyRent?.let { summerPrice ->
                    Spacer(modifier = Modifier.height(8.dp))
                    FeatureRow(
                        label = "Цена летом:",
                        value = "${formatPrice(summerPrice)} ₽/месяц"
                    )
                }
                
                // Коммунальные платежи
                Spacer(modifier = Modifier.height(8.dp))
                
                if (property.utilitiesIncluded) {
                    FeatureRow(
                        label = "Коммунальные платежи:",
                        value = "Включены"
                    )
                } else {
                    property.utilitiesCost?.let { utilitiesPrice ->
                        FeatureRow(
                            label = "Коммунальные платежи:",
                            value = "${formatPrice(utilitiesPrice)} ₽"
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Налогообложение и договор
            if (property.hasCompensationContract) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = "Договор и регистрация",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Column(
                    modifier = Modifier.padding(start = 36.dp)
                ) {
                    if (property.hasCompensationContract) {
                        FeatureRow(
                            label = "Договор компенсации",
                            value = "Да"
                        )
                    }
                    
                    if (property.isSelfEmployed) {
                        FeatureRow(
                            label = "Самозанятость",
                            value = "Да"
                        )
                    }
                    
                    if (property.isPersonalIncomeTax) {
                        FeatureRow(
                            label = "НДФЛ",
                            value = "Да"
                        )
                    }
                    
                    if (property.isCompanyIncomeTax) {
                        FeatureRow(
                            label = "Юридическое лицо",
                            value = "Да"
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Сроки аренды
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Сроки аренды",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(
                modifier = Modifier.padding(start = 36.dp)
            ) {
                // Минимальный срок аренды
                property.minRentPeriod?.let { period ->
                    if (period.isNotEmpty()) {
                        FeatureRow(
                            label = "Минимальный срок:",
                            value = period
                        )
                    }
                }
                
                // Максимальный срок аренды
                property.maxRentPeriod?.let { period ->
                    Spacer(modifier = Modifier.height(8.dp))
                    FeatureRow(
                        label = "Максимальный срок:",
                        value = "$period мес."
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Депозиты
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.AttachMoney,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Депозиты",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(
                modifier = Modifier.padding(start = 36.dp)
            ) {
                // Основной залог
                property.depositCustomAmount?.let { deposit ->
                    FeatureRow(
                        label = "Залог:",
                        value = "${formatPrice(deposit)} ₽"
                    )
                }
                
                // Страховой депозит
                property.securityDeposit?.let { deposit ->
                    Spacer(modifier = Modifier.height(8.dp))
                    FeatureRow(
                        label = "Страховой депозит:",
                        value = "${formatPrice(deposit)} ₽"
                    )
                }
            }
            
            // Дополнительные расходы
            property.additionalExpenses?.let { expenses ->
                if (expenses.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AttachMoney,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = "Дополнительные расходы",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = expenses,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 36.dp)
                    )
                }
            }
            
            // Правила проживания
            property.longTermRules?.let { rules ->
                if (rules.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = "Правила проживания",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = rules,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 36.dp)
                    )
                }
            }
        }
    }
} 