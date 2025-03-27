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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.util.CallLogHelper

@Composable
fun PhoneListField(
    phones: List<String>,
    onPhonesChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier
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
        Text(
            text = "Контактные телефоны",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        
        if (phones.isEmpty()) {
            Text(
                text = "Не добавлено ни одного номера",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 160.dp, min = 0.dp)
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = phones,
                    key = { it } // Используем сам номер телефона как ключ
                ) { phone ->
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Кэшируем форматированный номер
                            val formattedNumber = remember(phone) { formatPhoneNumber(phone) }
                            
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
                contentPadding = PaddingValues(horizontal = 16.dp)
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
                    PhoneTextField(
                        value = newPhone,
                        onValueChange = { newPhone = it },
                        label = "Номер телефона",
                        isError = phoneError,
                        errorMessage = if (phoneError) "Введите полный номер телефона" else null
                    )
                    
                    if (phoneError) {
                        Text(
                            text = "Введите 10 цифр номера телефона без кода страны",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text(
                            text = "Пример: 9876543210 (без +7)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPhone.length == 10) {
                            onPhonesChange(phones + newPhone)
                            showAddDialog = false
                            newPhone = ""
                            phoneError = false
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
                if (phoneNumber.length == 10 && phoneNumber !in phones) {
                    onPhonesChange(phones + phoneNumber)
                }
            },
            onDismiss = { showCallLogDialog = false }
        )
    }
}

// Вспомогательная функция для форматирования номера телефона
fun formatPhoneNumber(phone: String): String {
    return buildFormattedPhoneNumber(phone)
} 