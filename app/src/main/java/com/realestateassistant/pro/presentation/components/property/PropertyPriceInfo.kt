package com.realestateassistant.pro.presentation.components.property

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.presentation.utils.formatPrice
import com.realestateassistant.pro.presentation.utils.getDaysText
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PropertyPriceInfo(property: Property) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Условия аренды",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Определяем, есть ли у объекта оба типа аренды
            val hasLongTerm = property.monthlyRent != null
            val hasShortTerm = property.dailyPrice != null
            val hasBothTypes = hasLongTerm && hasShortTerm
            
            // Для долгосрочной аренды
            if (hasLongTerm) {
                if (hasBothTypes) {
                    SectionHeader(
                        text = "Долгосрочная аренда",
                        icon = Icons.Outlined.Home
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                PriceRowHighlighted(
                    icon = Icons.Outlined.AttachMoney,
                    label = "Стоимость аренды:",
                    value = "${formatPrice(property.monthlyRent!!)} ₽/месяц"
                )
                
                // Отображаем зимнюю и летнюю цену если они заданы
                property.winterMonthlyRent?.let { winterPrice ->
                    Spacer(modifier = Modifier.height(8.dp))
                    PriceRow(
                        icon = Icons.Outlined.AttachMoney,
                        label = "Стоимость зимой:",
                        value = "${formatPrice(winterPrice)} ₽/месяц"
                    )
                }
                
                property.summerMonthlyRent?.let { summerPrice ->
                    Spacer(modifier = Modifier.height(8.dp))
                    PriceRow(
                        icon = Icons.Outlined.AttachMoney,
                        label = "Стоимость летом:",
                        value = "${formatPrice(summerPrice)} ₽/месяц"
                    )
                }
                
                property.minRentPeriod?.let { period ->
                    Spacer(modifier = Modifier.height(8.dp))
                    PriceRow(
                        icon = Icons.Outlined.CalendarMonth,
                        label = "Минимальный срок:",
                        value = period
                    )
                }
                
                property.depositCustomAmount?.let { customDeposit ->
                    Spacer(modifier = Modifier.height(8.dp))
                    PriceRow(
                        icon = Icons.Outlined.AttachMoney,
                        label = "Залог:",
                        value = "${formatPrice(customDeposit)} ₽"
                    )
                }
                
                property.securityDeposit?.let { deposit ->
                    Spacer(modifier = Modifier.height(8.dp))
                    PriceRow(
                        icon = Icons.Outlined.AttachMoney,
                        label = "Страховой депозит:",
                        value = "${formatPrice(deposit)} ₽"
                    )
                }
            }
            
            // Разделитель между типами аренды, если есть оба типа
            if (hasBothTypes) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                SectionHeader(
                    text = "Посуточная аренда",
                    icon = Icons.Outlined.CalendarMonth
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Для краткосрочной аренды
            if (hasShortTerm) {
                PriceRowHighlighted(
                    icon = Icons.Outlined.AttachMoney,
                    label = "Стоимость аренды:",
                    value = "${formatPrice(property.dailyPrice!!)} ₽/сутки"
                )
                
                // Добавляем отображение сезонных цен
                if (property.seasonalPrices.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Сезонные цены:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Column {
                        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                        property.seasonalPrices.forEach { seasonalPrice ->
                            val startDateStr = dateFormat.format(Date(seasonalPrice.startDate))
                            val endDateStr = dateFormat.format(Date(seasonalPrice.endDate))
                            
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.DateRange,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    Text(
                                        text = "$startDateStr - $endDateStr",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    
                                    Spacer(modifier = Modifier.weight(1f))
                                    
                                    Text(
                                        text = "${formatPrice(seasonalPrice.price)} ₽/сутки",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
                
                property.minStayDays?.let { minDays ->
                    Spacer(modifier = Modifier.height(8.dp))
                    PriceRow(
                        icon = Icons.Outlined.CalendarMonth,
                        label = "Минимальный срок:",
                        value = "$minDays ${getDaysText(minDays)}"
                    )
                }
                
                property.shortTermDepositCustomAmount?.let { customDeposit ->
                    Spacer(modifier = Modifier.height(8.dp))
                    PriceRow(
                        icon = Icons.Outlined.AttachMoney,
                        label = "Залог:",
                        value = "${formatPrice(customDeposit)} ₽"
                    )
                }
                
                property.shortTermDeposit?.let { deposit ->
                    Spacer(modifier = Modifier.height(8.dp))
                    PriceRow(
                        icon = Icons.Outlined.AttachMoney,
                        label = "Страховой депозит:",
                        value = "${formatPrice(deposit)} ₽"
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
} 