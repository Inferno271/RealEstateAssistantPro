package com.realestateassistant.pro.presentation.components.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.presentation.utils.formatPrice
import java.text.SimpleDateFormat
import java.util.*

/**
 * Компонент для отображения предпочтений клиента по аренде.
 */
@Composable
fun ClientRentalPreferences(client: Client) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Заголовок раздела
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Apartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Предпочтения по аренде",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    thickness = 0.5.dp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Группа: Основные характеристики
            RentalPreferenceGroup(title = "Основные характеристики") {
                // Тип аренды
                if (client.rentalType.isNotEmpty()) {
                    PreferenceRow(
                        icon = Icons.Outlined.Home,
                        label = "Тип аренды:",
                        value = client.rentalType,
                        highlighted = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Тип недвижимости
                client.desiredPropertyType?.let { 
                    if (it.isNotEmpty()) {
                        PreferenceRow(
                            icon = Icons.Outlined.Apartment,
                            label = "Тип недвижимости:",
                            value = it,
                            highlighted = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Группа: Расположение
            RentalPreferenceGroup(title = "Расположение") {
                // Район
                client.preferredDistrict?.let {
                    if (it.isNotEmpty()) {
                        PreferenceRow(
                            icon = Icons.Outlined.LocationOn,
                            label = "Предпочитаемый район:",
                            value = it,
                            highlighted = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                // Адрес
                client.preferredAddress?.let {
                    if (it.isNotEmpty()) {
                        PreferenceRow(
                            icon = Icons.Outlined.Place,
                            label = "Предпочитаемый адрес:",
                            value = it
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Группа: Условия поиска
            RentalPreferenceGroup(title = "Условия поиска") {
                // Срочность поиска
                client.urgencyLevel?.let {
                    if (it.isNotEmpty()) {
                        PreferenceRow(
                            icon = Icons.Outlined.Schedule,
                            label = "Срочность поиска:",
                            value = it,
                            highlighted = it.contains("Срочно")
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                // Гибкость бюджета
                if (client.budgetFlexibility) {
                    val flexibilityText = client.maxBudgetIncrease?.let { 
                        "Да (до $it%)" 
                    } ?: "Да"
                    
                    PreferenceRow(
                        icon = Icons.Outlined.Money,
                        label = "Гибкость бюджета:",
                        value = flexibilityText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Группа: Приоритеты и гибкие требования
            if (client.requirementFlexibility.isNotEmpty() || client.priorityCriteria.isNotEmpty()) {
                RentalPreferenceGroup(title = "Приоритеты и гибкость") {
                    // Гибкие требования
                    if (client.requirementFlexibility.isNotEmpty()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.SettingsApplications,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Column {
                                    Text(
                                        text = "Гибкие требования:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    client.requirementFlexibility.forEach { requirement ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(start = 8.dp)
                                        ) {
                                            Text(
                                                text = "•",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = requirement,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                    
                    // Приоритетные критерии
                    if (client.priorityCriteria.isNotEmpty()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Star,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Column {
                                    Text(
                                        text = "Приоритетные критерии:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    client.priorityCriteria.forEach { criteria ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(start = 8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Check,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = criteria,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Группа предпочтений по аренде с заголовком и контентом.
 */
@Composable
private fun RentalPreferenceGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            content()
        }
    }
}

/**
 * Компонент для отображения предпочтений по долгосрочной аренде.
 */
@Composable
fun ClientLongTermPreferences(client: Client) {
    // Проверяем, есть ли данные для отображения
    val hasData = client.longTermBudgetMin != null || 
                  client.longTermBudgetMax != null ||
                  client.desiredRoomsCount != null ||
                  client.desiredArea != null ||
                  client.moveInDeadline != null ||
                  !client.additionalRequirements.isNullOrEmpty() ||
                  !client.legalPreferences.isNullOrEmpty()
    
    if (!hasData) return
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Заголовок раздела
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.House,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Долгосрочная аренда",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    thickness = 0.5.dp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Группа: Финансовые условия
            RentalPreferenceGroup(title = "Финансовые условия") {
                // Бюджет
                if (client.longTermBudgetMin != null || client.longTermBudgetMax != null) {
                    val budgetText = when {
                        client.longTermBudgetMin != null && client.longTermBudgetMax != null -> 
                            "${formatPrice(client.longTermBudgetMin)} - ${formatPrice(client.longTermBudgetMax)} ₽/мес."
                        client.longTermBudgetMin != null -> 
                            "от ${formatPrice(client.longTermBudgetMin)} ₽/мес."
                        else -> 
                            "до ${formatPrice(client.longTermBudgetMax!!)} ₽/мес."
                    }
                    
                    PreferenceRow(
                        icon = Icons.Outlined.AttachMoney,
                        label = "Бюджет:",
                        value = budgetText,
                        highlighted = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Группа: Характеристики недвижимости
            RentalPreferenceGroup(title = "Характеристики недвижимости") {
                // Количество комнат
                client.desiredRoomsCount?.let {
                    PreferenceRow(
                        icon = Icons.Outlined.MeetingRoom,
                        label = "Желаемое количество комнат:",
                        value = it.toString(),
                        highlighted = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Желаемая площадь
                client.desiredArea?.let {
                    PreferenceRow(
                        icon = Icons.Outlined.SquareFoot,
                        label = "Желаемая площадь:",
                        value = "$it м²"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Группа: Сроки и условия
            RentalPreferenceGroup(title = "Сроки и условия") {
                // Срок заселения
                client.moveInDeadline?.let {
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(Date(it))
                    
                    PreferenceRow(
                        icon = Icons.Outlined.CalendarMonth,
                        label = "Срок заселения до:",
                        value = formattedDate,
                        highlighted = System.currentTimeMillis() > it - 30L * 24 * 60 * 60 * 1000 // Выделяем, если до срока меньше месяца
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Юридические предпочтения
                client.legalPreferences?.let {
                    if (it.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column {
                                Text(
                                    text = "Юридические предпочтения:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
            
            // Дополнительные требования
            client.additionalRequirements?.let {
                if (it.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    RentalPreferenceGroup(title = "Дополнительные требования") {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Компонент для отображения предпочтений по краткосрочной аренде.
 */
@Composable
fun ClientShortTermPreferences(client: Client) {
    // Проверяем, есть ли данные для отображения
    val hasData = client.shortTermBudgetMin != null || 
                  client.shortTermBudgetMax != null ||
                  client.shortTermCheckInDate != null ||
                  client.shortTermCheckOutDate != null ||
                  client.hasAdditionalGuests ||
                  client.shortTermGuests != null ||
                  client.dailyBudget != null ||
                  !client.preferredShortTermDistrict.isNullOrEmpty() ||
                  !client.checkInOutConditions.isNullOrEmpty() ||
                  !client.additionalServices.isNullOrEmpty() ||
                  !client.additionalShortTermRequirements.isNullOrEmpty()
    
    if (!hasData) return
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Заголовок раздела
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Weekend,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Краткосрочная аренда",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    thickness = 0.5.dp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Группа: Финансовые условия
            RentalPreferenceGroup(title = "Финансовые условия") {
                // Бюджет
                if (client.shortTermBudgetMin != null || client.shortTermBudgetMax != null) {
                    val budgetText = when {
                        client.shortTermBudgetMin != null && client.shortTermBudgetMax != null -> 
                            "${formatPrice(client.shortTermBudgetMin)} - ${formatPrice(client.shortTermBudgetMax)} ₽/сутки"
                        client.shortTermBudgetMin != null -> 
                            "от ${formatPrice(client.shortTermBudgetMin)} ₽/сутки"
                        else -> 
                            "до ${formatPrice(client.shortTermBudgetMax!!)} ₽/сутки"
                    }
                    
                    PreferenceRow(
                        icon = Icons.Outlined.AttachMoney,
                        label = "Бюджет:",
                        value = budgetText,
                        highlighted = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Группа: Даты и гости
            RentalPreferenceGroup(title = "Даты и гости") {
                // Даты заезда и выезда
                if (client.shortTermCheckInDate != null && client.shortTermCheckOutDate != null) {
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    val checkInDate = dateFormat.format(Date(client.shortTermCheckInDate))
                    val checkOutDate = dateFormat.format(Date(client.shortTermCheckOutDate))
                    
                    // Определяем, как скоро заезд (для выделения)
                    val isUpcoming = System.currentTimeMillis() > client.shortTermCheckInDate - 7L * 24 * 60 * 60 * 1000 // Меньше недели
                    
                    PreferenceRow(
                        icon = Icons.Outlined.DateRange,
                        label = "Период проживания:",
                        value = "с $checkInDate по $checkOutDate",
                        highlighted = isUpcoming
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Дополнительные гости
                if (client.hasAdditionalGuests) {
                    val guestsText = client.shortTermGuests?.toString() ?: "Есть"
                    
                    PreferenceRow(
                        icon = Icons.Outlined.Group,
                        label = "Дополнительные гости:",
                        value = guestsText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Группа: Местоположение
            client.preferredShortTermDistrict?.let {
                if (it.isNotEmpty()) {
                    RentalPreferenceGroup(title = "Местоположение") {
                        PreferenceRow(
                            icon = Icons.Outlined.LocationOn,
                            label = "Предпочтительный район:",
                            value = it,
                            highlighted = true
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            // Группа: Условия заезда и выезда
            client.checkInOutConditions?.let {
                if (it.isNotEmpty()) {
                    RentalPreferenceGroup(title = "Условия заезда и выезда") {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            // Группа: Дополнительные услуги
            client.additionalServices?.let {
                if (it.isNotEmpty()) {
                    RentalPreferenceGroup(title = "Дополнительные услуги") {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Checklist,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            // Группа: Дополнительные требования
            client.additionalShortTermRequirements?.let {
                if (it.isNotEmpty()) {
                    RentalPreferenceGroup(title = "Дополнительные требования") {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Строка с информацией о предпочтениях клиента.
 */
@Composable
fun PreferenceRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    highlighted: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Normal,
            color = if (highlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
} 