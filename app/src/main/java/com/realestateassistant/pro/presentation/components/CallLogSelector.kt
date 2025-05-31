package com.realestateassistant.pro.presentation.components

// Добавляем импорт
import android.provider.CallLog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.util.CallLogEntry
import com.realestateassistant.pro.util.CallLogHelper
import com.realestateassistant.pro.util.PhoneUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallLogSelector(
    onPhoneSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    existingPhones: List<String> = emptyList()
) {
    val context = LocalContext.current
    var callLogEntries by remember { mutableStateOf<List<CallLogEntry>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentLimit by remember { mutableStateOf(15) } // Начальный лимит
    
    // Загружаем данные из журнала звонков при первом запуске или изменении лимита
    LaunchedEffect(currentLimit) {
        try {
            isLoading = true
            if (CallLogHelper.hasCallLogPermission(context)) {
                callLogEntries = CallLogHelper.getRecentCalls(context.contentResolver, currentLimit)
            } else {
                errorMessage = "Нет разрешения на доступ к журналу звонков"
            }
        } catch (e: Exception) {
            errorMessage = "Ошибка при загрузке журнала звонков: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { 
            Text(
                "Выбрать из журнала звонков",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            ) 
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    errorMessage != null -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = { isLoading = true },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Повторить попытку")
                            }
                        }
                    }
                    callLogEntries.isEmpty() -> {
                        Text(
                            text = "Журнал звонков пуст",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .wrapContentHeight()
                                    .heightIn(min = 0.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(callLogEntries) { entry ->
                                    CallLogEntryItem(
                                        entry = entry,
                                        onClick = {
                                            // Нормализуем номер в международный формат E.164 перед возвратом
                                            val normalizedNumber = PhoneUtils.normalizeInternationalPhoneNumber(entry.number)
                                            
                                            // Проверяем, не существует ли уже такой номер
                                            if (existingPhones.contains(normalizedNumber)) {
                                                // Показываем сообщение о дубликате
                                                Toast.makeText(
                                                    context,
                                                    "Этот номер уже добавлен",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                onPhoneSelected(normalizedNumber)
                                                onDismiss()
                                            }
                                        }
                                    )
                                }
                            }
                            
                            // Кнопка "Показать еще"
                            OutlinedButton(
                                onClick = { currentLimit += 15 }, // Увеличиваем лимит на 15
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Показать еще")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Отмена")
            }
        }
    )
}

@Composable
fun CallLogEntryItem(
    entry: CallLogEntry,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка типа звонка в круглом фоне
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = when (entry.type) {
                            CallLog.Calls.MISSED_TYPE -> MaterialTheme.colorScheme.errorContainer
                            CallLog.Calls.OUTGOING_TYPE -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.secondaryContainer
                        },
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (entry.type) {
                        CallLog.Calls.OUTGOING_TYPE -> Icons.Default.Call
                        else -> Icons.Default.Phone
                    },
                    contentDescription = null,
                    tint = when (entry.type) {
                        CallLog.Calls.MISSED_TYPE -> MaterialTheme.colorScheme.error
                        CallLog.Calls.OUTGOING_TYPE -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.secondary
                    },
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Информация о звонке
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = entry.name.ifEmpty { "Неизвестный" },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(Modifier.height(2.dp))
                
                // Форматируем номер в международном формате
                Text(
                    text = PhoneUtils.formatInternationalPhoneNumber(entry.number),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Дата звонка
            Text(
                text = formatCallDate(entry.date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Форматирует дату звонка
 */
private fun formatCallDate(date: Long): String {
    val dateFormat = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())
    return dateFormat.format(Date(date))
} 