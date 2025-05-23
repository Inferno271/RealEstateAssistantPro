package com.realestateassistant.pro.presentation.components.property

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.R
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.presentation.utils.PropertyDisplayUtils.formatPhoneForDisplay
import com.realestateassistant.pro.presentation.utils.PropertyDisplayUtils.formatPrice
import com.realestateassistant.pro.presentation.utils.PropertyDisplayUtils.formatUpdatedDate
import com.realestateassistant.pro.presentation.utils.PropertyDisplayUtils.getLevelsText
import com.realestateassistant.pro.presentation.utils.PropertyDisplayUtils.getRoomsText
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.realestateassistant.pro.utils.CoilUtils

@Composable
fun PropertyCard(
    property: Property,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var isContactExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
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
            PropertyCardMainContent(
                property = property,
                isContactExpanded = isContactExpanded,
                onExpandToggle = { isContactExpanded = !isContactExpanded }
            )
            
            // Расширяемая секция с контактной информацией
            if (isContactExpanded && (property.contactName.isNotEmpty() || property.contactPhone.isNotEmpty())) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    thickness = 1.dp
                )
                
                PropertyContactSection(property = property)
            }
        }
    }
}

@Composable
private fun PropertyCardMainContent(
    property: Property,
    isContactExpanded: Boolean,
    onExpandToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Фото объекта (миниатюра)
        PropertyThumbnail(property)
        
        // Основной контент
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Заголовок: тип недвижимости и район
            PropertyTitleRow(property)
            
            // Разделитель
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                thickness = 0.5.dp
            )
            
            // Основные характеристики
            PropertyFeaturesRow(property)
            
            // Адрес без даты обновления
            Text(
                text = property.address,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            // Цена с крупным шрифтом и кнопкой развертывания
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    property.monthlyRent?.let { price ->
                        Text(
                            text = "${formatPrice(price)} ₽/мес",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    property.dailyPrice?.let { price ->
                        Text(
                            text = "${formatPrice(price)} ₽/сутки",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Кнопка разворачивания контактной информации
                if (property.contactName.isNotEmpty() || property.contactPhone.isNotEmpty()) {
                    IconButton(onClick = onExpandToggle, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = if (isContactExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Контактная информация",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Дата обновления (перенесена вниз)
            Text(
                text = "Обновлено: ${formatUpdatedDate(property.updatedAt)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun PropertyTitleRow(property: Property) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = property.propertyType,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Text(
            text = " • ${property.district}",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PropertyFeaturesRow(property: Property) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Первый ряд: основные характеристики
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Площадь всегда отображается
            Text(
                text = "${property.area} м²",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            
            // Комнаты отображаются, если не студия и подходящий тип недвижимости
            if (!property.isStudio && 
                property.propertyType !in listOf("Земельный участок", "Гараж") && 
                property.roomsCount > 0) {
                Text(
                    text = " • ${property.roomsCount} ${getRoomsText(property.roomsCount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Отображение этажа или уровней в зависимости от типа недвижимости
            if (property.propertyType in listOf("Квартира", "Студия", "Апартаменты")) {
                Text(
                    text = " • ${property.floor}/${property.totalFloors} эт.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        // Второй ряд: дополнительные характеристики для домов и участков
        if ((property.propertyType in listOf("Дом", "Таунхаус", "Дуплекс") && property.levelsCount > 0) ||
            (property.propertyType in listOf("Дом", "Таунхаус", "Земельный участок") && property.landArea > 0)) {
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Уровни для домов
                if (property.propertyType in listOf("Дом", "Таунхаус", "Дуплекс") && 
                      property.levelsCount > 0) {
                    Text(
                        text = "${property.levelsCount} ${getLevelsText(property.levelsCount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    
                    // Если есть и уровни и площадь участка, добавляем разделитель
                    if (property.landArea > 0) {
                        Text(
                            text = " • ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                // Площадь участка для соответствующих типов недвижимости
                if (property.propertyType in listOf("Дом", "Таунхаус", "Земельный участок") && 
                    property.landArea > 0) {
                    Text(
                        text = "${property.landArea} сот.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun PropertyThumbnail(property: Property) {
    // Контейнер для фото с рамкой
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Если есть фото, загружаем первое
        if (property.photos.isNotEmpty()) {
            val context = LocalContext.current
            // Создаем оптимизированный ImageLoader
            val imageLoader = remember { CoilUtils.createImageLoader(context) }
            
            SubcomposeAsyncImage(
                model = CoilUtils.createImageRequest(
                    context,
                    property.photos.first()
                ).build(),
                contentDescription = "Фото объекта",
                contentScale = ContentScale.Crop,
                imageLoader = imageLoader,
                modifier = Modifier.fillMaxSize()
            ) {
                val state = painter.state
                when {
                    // Отображение индикатора загрузки
                    state is AsyncImagePainter.State.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                    // Обработка ошибки загрузки
                    state is AsyncImagePainter.State.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BrokenImage,
                                contentDescription = "Ошибка загрузки",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    // Успешно загруженное изображение
                    else -> {
                        SubcomposeAsyncImageContent()
                    }
                }
            }
            
            // Индикатор количества фото, если их больше одного
            if (property.photos.size > 1) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(2.dp)
                        .size(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${property.photos.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        } else {
            // Если фото нет, показываем заглушку
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun PropertyContactSection(property: Property) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Имя контакта
        if (property.contactName.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = property.contactName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        
        // Телефоны с кнопками мессенджеров
        if (property.contactPhone.isNotEmpty()) {
            property.contactPhone.forEach { phone ->
                PhoneContactRow(
                    phone = phone,
                    context = context
                )
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
            text = formatPhoneForDisplay(phone),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        
        // Кнопки действий
        ContactActionButton(
            iconVector = Icons.Default.Call,
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

@Composable
fun PropertyFeatureChip(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(36.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
} 