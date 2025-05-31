package com.realestateassistant.pro.presentation.components

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.util.CallLogHelper
import com.realestateassistant.pro.util.PhoneUtils

@Composable
fun PhoneListField(
    phones: List<String>,
    onPhonesChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    isRequired: Boolean = false,
    errorMessage: String? = null
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showCallLogDialog by remember { mutableStateOf(false) }
    var newPhone by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf(false) }
    
    // Для запроса разрешения на доступ к журналу звонков
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                showCallLogDialog = true
            } else {
                Toast.makeText(
                    context,
                    "Для доступа к журналу звонков необходимо разрешение",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )
    
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Создаем аннотированную строку с звездочкой для обязательного поля
            val labelText = buildAnnotatedString {
                append("Контактные телефоны")
                if (isRequired) {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                        append(" *")
                    }
                }
            }
            
            Text(
                text = labelText,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (isError) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = "Ошибка",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        if (phones.isEmpty()) {
            Text(
                text = "Не добавлено ни одного номера",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isError) 
                    MaterialTheme.colorScheme.error.copy(alpha = 0.8f) 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        } else {
            // Создаем список с явными уникальными ключами, чтобы избежать дублирования
            val uniquePhones = phones.distinctBy { it }
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 160.dp, min = 0.dp)
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = uniquePhones,
                    // Создаем гарантированно уникальный ключ, добавляя индекс к номеру
                    key = { phone -> "${uniquePhones.indexOf(phone)}_$phone" }
                ) { phone ->
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Форматируем номер телефона с учетом международного формата
                            val formattedNumber = remember(phone) { 
                                PhoneUtils.formatInternationalPhoneNumber(phone)
                            }
                            
                            Text(
                                text = formattedNumber,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { onPhonesChange(phones - phone) }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Удалить номер"
                                )
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { 
                    showAddDialog = true
                    newPhone = ""
                    phoneError = false
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить")
                    Spacer(Modifier.width(8.dp))
                    Text("Добавить номер")
                }
            }
            
            Button(
                onClick = { 
                    // Проверяем разрешение перед открытием диалога
                    if (CallLogHelper.hasCallLogPermission(context)) {
                        showCallLogDialog = true
                    } else {
                        Toast.makeText(
                            context,
                            "Для доступа к журналу необходимо разрешение",
                            Toast.LENGTH_LONG
                        ).show()
                        permissionLauncher.launch(Manifest.permission.READ_CALL_LOG)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "Из звонков")
                    Spacer(Modifier.width(8.dp))
                    Text("Из звонков")
                }
            }
        }
    }
    
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { 
                showAddDialog = false
                newPhone = ""
                phoneError = false
            },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { 
                Text(
                    "Добавить телефон",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                ) 
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Используем новый компонент PhoneInputField
                    PhoneInputField(
                        value = newPhone,
                        onValueChange = { newPhone = it },
                        label = "Номер телефона",
                        isError = phoneError,
                        errorMessage = if (phoneError) "Введите корректный номер телефона" else null
                    )
                    
                    if (phoneError) {
                        Text(
                            text = "Введите корректный номер телефона с кодом страны",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text(
                            text = "Введите номер с выбором кода страны, например: +7 (XXX) XXX-XX-XX",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Используем функцию валидации из PhoneUtils
                        if (PhoneUtils.isValidPhoneNumber(newPhone)) {
                            // Форматируем номер перед добавлением в E.164
                            val normalizedPhone = PhoneUtils.normalizeInternationalPhoneNumber(newPhone)
                            
                            // Проверяем, не дублируется ли номер
                            if (!phones.contains(normalizedPhone)) {
                                onPhonesChange(phones + normalizedPhone)
                                showAddDialog = false
                                newPhone = ""
                                phoneError = false
                            } else {
                                // Сообщаем о дубликате
                                phoneError = true
                                Toast.makeText(
                                    context,
                                    "Этот номер уже добавлен",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            phoneError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showAddDialog = false
                        newPhone = ""
                        phoneError = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Отмена")
                }
            }
        )
    }
    
    // Диалог выбора номера из журнала звонков
    if (showCallLogDialog) {
        CallLogSelector(
            onPhoneSelected = { phoneNumber ->
                // Проверка дубликатов перенесена в CallLogSelector
                onPhonesChange(phones + phoneNumber)
            },
            onDismiss = {
                showCallLogDialog = false
            },
            existingPhones = phones // Передаем существующие номера для проверки в CallLogSelector
        )
    }
}

