package com.realestateassistant.pro.presentation.components.property

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.content.Intent
import android.net.Uri
import com.realestateassistant.pro.R
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.presentation.utils.formatPhoneForDisplay

@Composable
fun PropertyContactInfo(property: Property) {
    if (property.contactName.isEmpty() && property.contactPhone.isEmpty()) return
    
    val context = LocalContext.current
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Контактная информация",
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
            
            // Имя контакта
            if (property.contactName.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(8.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = property.contactName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Телефоны с кнопками действий
            if (property.contactPhone.isNotEmpty()) {
                property.contactPhone.forEachIndexed { index, phone ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Кнопка со звонком
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { 
                                    // Добавляем +7 к номеру, если его нет
                                    val phoneNumber = if (phone.startsWith("+")) {
                                        phone.replace(" ", "")
                                    } else {
                                        "+7${phone.replace(" ", "")}"
                                    }
                                    
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:$phoneNumber")
                                    }
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Call,
                                    contentDescription = "Позвонить",
                                    modifier = Modifier.size(18.dp)
                                )
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Text(
                                    text = formatPhoneForDisplay(phone),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            
                            // WhatsApp кнопка
                            FilledIconButton(
                                onClick = {
                                    // Добавляем +7 к номеру, если его нет
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
                                },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = Color(0xFF25D366), // Зеленый цвет WhatsApp
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_whatsapp),
                                    contentDescription = "WhatsApp",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            // Telegram кнопка
                            FilledIconButton(
                                onClick = {
                                    // Добавляем +7 к номеру, если его нет
                                    val phoneNumber = if (phone.startsWith("+")) {
                                        phone.replace(" ", "")
                                    } else {
                                        "+7${phone.replace(" ", "")}"
                                    }
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse("tg://resolve?phone=${phoneNumber.replace("+", "")}")
                                    }
                                    context.startActivity(intent)
                                },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = Color(0xFF0088CC), // Синий цвет Telegram
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_telegram),
                                    contentDescription = "Telegram",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                    
                    if (index < property.contactPhone.size - 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            
            // Дополнительная контактная информация
            property.additionalContactInfo?.let { info ->
                if (info.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                        thickness = 0.5.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Дополнительно",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = info,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
} 