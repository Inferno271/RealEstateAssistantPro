package com.realestateassistant.pro.presentation.components.client

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Message
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.R
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.presentation.model.RentalType
import com.realestateassistant.pro.presentation.util.PhoneUtils
import com.realestateassistant.pro.presentation.utils.PropertyDisplayUtils.formatUpdatedDate

/**
 * Карточка клиента, отображающая основную информацию и контактные данные.
 * 
 * @param client Клиент для отображения
 * @param onClick Функция обратного вызова при нажатии на карточку
 * @param modifier Модификатор для кастомизации
 */
@Composable
fun ClientCard(
    client: Client,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isContactExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Основная часть карточки
            ClientCardMainContent(
                client = client,
                isContactExpanded = isContactExpanded,
                onExpandToggle = { isContactExpanded = !isContactExpanded }
            )
            
            // Расширяемая секция с контактной информацией
            if (isContactExpanded && client.phone.isNotEmpty()) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    thickness = 1.dp
                )
                
                ClientContactSection(client = client)
            }
        }
    }
}

@Composable
private fun ClientCardMainContent(
    client: Client,
    isContactExpanded: Boolean,
    onExpandToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Заголовок и тип аренды в одной строке
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Имя клиента
            Text(
                text = client.fullName,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            
            // Тип аренды (справа)
            val rentalTypeText = when (client.rentalType.lowercase()) {
                "длительная" -> RentalType.LONG_TERM.getDisplayName()
                "посуточная" -> RentalType.SHORT_TERM.getDisplayName()
                else -> client.rentalType
            }
            
            Text(
                text = rentalTypeText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        // Разделитель
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
            thickness = 0.5.dp,
            modifier = Modifier.padding(vertical = 2.dp)
        )
        
        // Основные характеристики в одной строке
        ClientFeaturesRow(client)
        
        // Район и бюджет в одной строке
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Район (слева)
            val district = client.preferredDistrict ?: client.preferredShortTermDistrict
            if (district?.isNotEmpty() == true) {
                Text(
                    text = "Район: $district",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            
            // Бюджет (справа)
            val budgetInfo = getBudgetInfo(client)
            if (budgetInfo.isNotEmpty()) {
                Text(
                    text = budgetInfo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )
            }
        }
        
        // Комментарий и кнопка разворачивания в одной строке
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Блок с комментариями (слева)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Комментарий (если есть)
                if (client.comment.isNotEmpty()) {
                    Text(
                        text = client.comment,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Срочность (если есть)
                client.urgencyLevel?.let { urgency ->
                    if (urgency.isNotEmpty()) {
                        Text(
                            text = "Срочность: $urgency",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
                
                // Дата последнего обновления
                Text(
                    text = "Обновлено: ${formatUpdatedDate(client.updatedAt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            
            // Кнопка разворачивания контактной информации (справа)
            if (client.phone.isNotEmpty()) {
                IconButton(onClick = onExpandToggle, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (isContactExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Контактная информация",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun ClientFeaturesRow(client: Client) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Строка с количеством человек и комнат
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Состав семьи (если есть)
            client.familyComposition?.let {
                if (it.isNotEmpty()) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Количество человек и комнат (справа)
            Row(
                horizontalArrangement = Arrangement.End
            ) {
                // Количество человек
                client.peopleCount?.let { count ->
                    Text(
                        text = "$count чел.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Количество комнат
                client.desiredRoomsCount?.let { rooms ->
                    Text(
                        text = if (client.peopleCount != null) " • $rooms комн." else "$rooms комн.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Раздел с контактной информацией клиента.
 */
@Composable
private fun ClientContactSection(client: Client) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Отображаем список телефонов
        if (client.phone.isNotEmpty()) {
            client.phone.forEachIndexed { index, phone ->
                PhoneContactRow(
                    phone = phone,
                    context = context
                )
                
                if (index < client.phone.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun PhoneContactRow(
    phone: String,
    context: android.content.Context
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        // Телефон
        Text(
            text = PhoneUtils.formatPhone(phone),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        
        // Кнопки действий
        // Звонок
        ContactActionButton(
            iconVector = Icons.Filled.Call,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "Позвонить",
            onClick = {
                val phoneNumber = if (phone.startsWith("+")) {
                    phone.replace(" ", "")
                } else {
                    "+7${phone.replace(" ", "")}"
                }
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                }
                context.startActivity(intent)
            }
        )
        
        // WhatsApp
        ContactActionButton(
            painter = painterResource(id = R.drawable.ic_whatsapp),
            tint = Color(0xFF25D366),
            contentDescription = "WhatsApp",
            onClick = {
                val phoneNumber = if (phone.startsWith("+")) {
                    phone.replace(" ", "")
                } else {
                    "+7${phone.replace(" ", "")}"
                }
                val whatsappNumber = phoneNumber.replace("+", "")
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/$whatsappNumber")
                }
                context.startActivity(intent)
            }
        )
        
        // Telegram
        ContactActionButton(
            painter = painterResource(id = R.drawable.ic_telegram),
            tint = Color(0xFF0088CC),
            contentDescription = "Telegram",
            onClick = {
                val phoneNumber = if (phone.startsWith("+")) {
                    phone.replace(" ", "")
                } else {
                    "+7${phone.replace(" ", "")}"
                }
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("tg://resolve?phone=${phoneNumber.replace("+", "")}")
                }
                context.startActivity(intent)
            }
        )
    }
}

@Composable
private fun ContactActionButton(
    iconVector: ImageVector? = null,
    painter: androidx.compose.ui.graphics.painter.Painter? = null,
    tint: Color,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(28.dp)
    ) {
        if (iconVector != null) {
            Icon(
                imageVector = iconVector,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
        } else if (painter != null) {
            Icon(
                painter = painter,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Получает форматированную строку с информацией о бюджете клиента.
 */
private fun getBudgetInfo(client: Client): String {
    // Вывод в консоль для отладки
    println("DEBUG: ClientCard.getBudgetInfo - тип аренды: ${client.rentalType}")
    
    return when (client.rentalType.lowercase()) {
        "длительная" -> {
            val min = client.longTermBudgetMin
            val max = client.longTermBudgetMax
            
            when {
                min != null && max != null -> "${min.toInt()} - ${max.toInt()} ₽/мес."
                min != null -> "от ${min.toInt()} ₽/мес."
                max != null -> "до ${max.toInt()} ₽/мес."
                else -> ""
            }
        }
        "посуточная" -> {
            val min = client.shortTermBudgetMin
            val max = client.shortTermBudgetMax
            
            when {
                min != null && max != null -> "${min.toInt()} - ${max.toInt()} ₽/сут."
                min != null -> "от ${min.toInt()} ₽/сут."
                max != null -> "до ${max.toInt()} ₽/сут."
                else -> ""
            }
        }
        else -> ""
    }
} 