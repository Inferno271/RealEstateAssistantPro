package com.realestateassistant.pro.presentation.components.property.details

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.presentation.components.property.FeatureRow
import com.realestateassistant.pro.presentation.components.property.PropertySectionCard
import com.realestateassistant.pro.presentation.components.property.TimeDisplayColumn
import com.realestateassistant.pro.presentation.utils.formatPrice
import com.realestateassistant.pro.presentation.utils.getDaysText

@Composable
fun PropertyShortTermDetails(property: Property) {
    // Оптимизируем проверку
    val hasData = property.weekdayPrice != null || 
                 property.weekendPrice != null || 
                 property.weeklyDiscount != null || 
                 property.monthlyDiscount != null ||
                 !property.additionalServices.isNullOrEmpty() ||
                 !property.shortTermRules.isNullOrEmpty() ||
                 property.cleaningService ||
                 !property.cleaningDetails.isNullOrEmpty() ||
                 property.hasExtraBed ||
                 property.extraBedPrice != null ||
                 !property.specialOffers.isNullOrEmpty() ||
                 !property.additionalComments.isNullOrEmpty() ||
                 property.maxGuests != null ||
                 property.maxStayDays != null ||
                 !property.checkInTime.isNullOrEmpty() ||
                 !property.checkOutTime.isNullOrEmpty() ||
                 property.shortTermDepositCustomAmount != null
    
    if (!hasData) return
    
    PropertySectionCard(title = "Детали краткосрочной аренды") {
        // Информация о гостях и сроках
        property.maxGuests?.let { guests ->
            FeatureRow(
                label = "Максимум гостей:",
                value = "$guests"
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        property.maxStayDays?.let { days ->
            FeatureRow(
                label = "Максимальный срок:",
                value = "$days ${getDaysText(days)}"
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Время заезда и выезда
        if (!property.checkInTime.isNullOrEmpty() || !property.checkOutTime.isNullOrEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                property.checkInTime?.let { checkIn ->
                    TimeDisplayColumn(
                        title = "Заезд с",
                        time = checkIn
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                property.checkOutTime?.let { checkOut ->
                    TimeDisplayColumn(
                        title = "Выезд до",
                        time = checkOut
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Информация о разных ценах
        property.weekdayPrice?.let { price ->
            FeatureRow(
                label = "Цена в будни:",
                value = "${formatPrice(price)} ₽/сутки"
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        property.weekendPrice?.let { price ->
            FeatureRow(
                label = "Цена в выходные:",
                value = "${formatPrice(price)} ₽/сутки"
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Скидки
        property.weeklyDiscount?.let { discount ->
            FeatureRow(
                label = "Скидка на неделю:",
                value = "${discount}%"
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        property.monthlyDiscount?.let { discount ->
            FeatureRow(
                label = "Скидка на месяц:",
                value = "${discount}%"
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Уборка
        if (property.cleaningService) {
            FeatureRow(
                label = "Уборка:",
                value = "Включена"
            )
            
            property.cleaningDetails?.let { details ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = details,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Дополнительное спальное место
        if (property.hasExtraBed) {
            FeatureRow(
                label = "Дополнительное спальное место:",
                value = "Есть"
            )
            
            property.extraBedPrice?.let { price ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Стоимость: ${formatPrice(price)} ₽",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Дополнительные услуги
        property.additionalServices?.let { services ->
            if (services.isNotEmpty()) {
                Text(
                    text = "Дополнительные услуги:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = services,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        
        // Правила проживания
        property.shortTermRules?.let { rules ->
            if (rules.isNotEmpty()) {
                Text(
                    text = "Правила проживания:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = rules,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        
        // Специальные предложения
        property.specialOffers?.let { offers ->
            if (offers.isNotEmpty()) {
                Text(
                    text = "Специальные предложения:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = offers,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        
        // Дополнительные комментарии
        property.additionalComments?.let { comments ->
            if (comments.isNotEmpty()) {
                Text(
                    text = "Дополнительные комментарии:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = comments,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
} 