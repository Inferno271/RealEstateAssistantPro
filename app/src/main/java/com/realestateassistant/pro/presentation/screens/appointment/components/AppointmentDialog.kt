package com.realestateassistant.pro.presentation.screens.appointment.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.realestateassistant.pro.domain.model.AppointmentStatus
import com.realestateassistant.pro.domain.model.AppointmentType
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.presentation.components.DatePickerDialog
import com.realestateassistant.pro.presentation.components.TimePickerDialog
import com.realestateassistant.pro.presentation.screens.appointment.models.AppointmentFormState
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Диалоговое окно для создания и редактирования встреч
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDialog(
    isEditMode: Boolean,
    formState: AppointmentFormState,
    clients: List<Client> = emptyList(),
    properties: List<Property> = emptyList(),
    onEvent: (AppointmentDialogEvent) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                // Заголовок диалога
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditMode) "Редактировать встречу" else "Создать встречу",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Закрыть"
                        )
                    }
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // Форма
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Заголовок встречи
                    OutlinedTextField(
                        value = formState.title,
                        onValueChange = { onEvent(AppointmentDialogEvent.SetTitle(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Заголовок") },
                        supportingText = {
                            if (formState.titleError != null) {
                                Text(formState.titleError)
                            }
                        },
                        isError = formState.titleError != null,
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Клиент
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = formState.clientName,
                            onValueChange = { /* Controlled by selector */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            label = { Text("Клиент") },
                            isError = formState.clientError != null,
                            supportingText = { formState.clientError?.let { Text(it) } },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { onEvent(AppointmentDialogEvent.ShowClientSelector(true)) }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Выбрать клиента"
                                    )
                                }
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Объект недвижимости
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = formState.propertyAddress,
                            onValueChange = { /* Controlled by selector */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            label = { Text("Объект недвижимости") },
                            isError = formState.propertyError != null,
                            supportingText = { formState.propertyError?.let { Text(it) } },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { onEvent(AppointmentDialogEvent.ShowPropertySelector(true)) }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Выбрать объект"
                                    )
                                }
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Флаг "Весь день"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Весь день",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Checkbox(
                            checked = formState.isAllDay,
                            onCheckedChange = { onEvent(AppointmentDialogEvent.SetIsAllDay(it)) }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Дата и время начала
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = formatDate(formState.startDate),
                            onValueChange = { /* Controlled by date picker */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            label = { Text("Дата начала") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { onEvent(AppointmentDialogEvent.ShowDatePicker(true, true)) }) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Выбрать дату начала"
                                    )
                                }
                            }
                        )
                    }
                    
                    // Время начала (только если не весь день)
                    if (!formState.isAllDay) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = formatTime(formState.startTime),
                                onValueChange = { /* Controlled by time picker */ },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                label = { Text("Время начала") },
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { onEvent(AppointmentDialogEvent.ShowTimePicker(true, true)) }) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = "Выбрать время начала"
                                        )
                                    }
                                },
                                isError = formState.timeError != null
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Предупреждение для пользователя
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Время окончания должно быть позже времени начала",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Дата и время окончания
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = formatDate(formState.endDate),
                            onValueChange = { /* Controlled by date picker */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            label = { Text("Дата окончания") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { onEvent(AppointmentDialogEvent.ShowDatePicker(true, false)) }) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Выбрать дату окончания"
                                    )
                                }
                            },
                            isError = formState.endDateError != null
                        )
                    }
                    
                    // Отображение ошибки даты окончания
                    formState.endDateError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                    
                    // Время окончания (только если не весь день)
                    if (!formState.isAllDay) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = formatTime(formState.endTime),
                                onValueChange = { /* Controlled by time picker */ },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                label = { Text("Время окончания") },
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { onEvent(AppointmentDialogEvent.ShowTimePicker(true, false)) }) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = "Выбрать время окончания"
                                        )
                                    }
                                },
                                isError = formState.timeError != null
                            )
                        }
                        
                        // Показываем ошибку времени, если она есть
                        formState.timeError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Тип встречи
                    Box(modifier = Modifier.fillMaxWidth()) {
                        var expanded by remember { mutableStateOf(false) }
                        
                        OutlinedTextField(
                            value = getAppointmentTypeText(formState.type),
                            onValueChange = { /* Controlled by dropdown */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            label = { Text("Тип встречи") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Выбрать тип встречи"
                                    )
                                }
                            }
                        )
                        
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            AppointmentTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(getAppointmentTypeText(type)) },
                                    onClick = {
                                        onEvent(AppointmentDialogEvent.SetType(type.name))
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Статус встречи
                    Box(modifier = Modifier.fillMaxWidth()) {
                        var expanded by remember { mutableStateOf(false) }
                        
                        OutlinedTextField(
                            value = getAppointmentStatusText(formState.status),
                            onValueChange = { /* Controlled by dropdown */ },
                            label = { Text("Статус встречи") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Выбрать статус встречи"
                                    )
                                }
                            }
                        )
                        
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            AppointmentStatuses.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(getAppointmentStatusText(status)) },
                                    onClick = {
                                        onEvent(AppointmentDialogEvent.SetStatus(status.name))
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Местоположение
                    OutlinedTextField(
                        value = formState.location,
                        onValueChange = { onEvent(AppointmentDialogEvent.SetLocation(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Местоположение") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null
                            )
                        },
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Описание
                    OutlinedTextField(
                        value = formState.description,
                        onValueChange = { onEvent(AppointmentDialogEvent.SetDescription(it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        label = { Text("Описание") },
                        minLines = 3
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Примечания
                    OutlinedTextField(
                        value = formState.notes,
                        onValueChange = { onEvent(AppointmentDialogEvent.SetNotes(it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        label = { Text("Примечания") },
                        minLines = 3
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // Кнопки
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Отмена")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = { onEvent(AppointmentDialogEvent.SaveAppointment) },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Сохранить")
                    }
                }
            }
        }
        
        // Селектор клиентов
        if (formState.showClientSelector) {
            ClientSelector(
                clients = clients,
                onClientSelected = { clientId ->
                    val selectedClient = clients.find { it.id == clientId }
                    selectedClient?.let {
                        onEvent(AppointmentDialogEvent.SetClient(it.id, it.fullName))
                    }
                },
                onDismiss = { onEvent(AppointmentDialogEvent.ShowClientSelector(false)) }
            )
        }
        
        // Селектор объектов недвижимости
        if (formState.showPropertySelector) {
            PropertySelector(
                properties = properties,
                onPropertySelected = { propertyId ->
                    val selectedProperty = properties.find { it.id == propertyId }
                    selectedProperty?.let {
                        onEvent(AppointmentDialogEvent.SetProperty(it.id, it.address))
                    }
                },
                onDismiss = { onEvent(AppointmentDialogEvent.ShowPropertySelector(false)) }
            )
        }
        
        // Селектор даты
        if (formState.showDatePicker) {
            val dateToShow = if (formState.isStartDateSelection) {
                formState.startDate
            } else {
                formState.endDate
            }
            
            DatePickerDialog(
                initialDate = dateToShow,
                onDateSelected = { selectedDate ->
                    if (formState.isStartDateSelection) {
                        onEvent(AppointmentDialogEvent.SetStartDate(selectedDate))
                    } else {
                        onEvent(AppointmentDialogEvent.SetEndDate(selectedDate))
                    }
                },
                onDismiss = { onEvent(AppointmentDialogEvent.ShowDatePicker(false, formState.isStartDateSelection)) }
            )
        }
        
        // Диалог выбора времени
        if (formState.showTimePicker) {
            val isStartTime = formState.isStartTimeSelection
            val initialTime = if (isStartTime) {
                formState.startTime
            } else {
                formState.endTime
            }
            
            TimePickerDialog(
                initialTime = initialTime,
                onTimeSelected = { time ->
                    if (isStartTime) {
                        onEvent(AppointmentDialogEvent.SetStartTime(time))
                    } else {
                        onEvent(AppointmentDialogEvent.SetEndTime(time))
                    }
                },
                onDismiss = {
                    onEvent(AppointmentDialogEvent.ShowTimePicker(false, isStartTime))
                }
            )
        }
    }
}

// Список типов встреч
private val AppointmentTypes = AppointmentType.values()

// Список статусов встреч
private val AppointmentStatuses = AppointmentStatus.values()

// Функции форматирования и получения текстовых представлений

/**
 * Форматирует дату
 */
private fun formatDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    return date.format(formatter)
}

/**
 * Форматирует время
 */
private fun formatTime(time: LocalTime): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return time.format(formatter)
}

/**
 * Возвращает текстовое представление типа встречи
 */
private fun getAppointmentTypeText(type: AppointmentType): String {
    return when (type) {
        AppointmentType.SHOWING -> "Показ"
        AppointmentType.CLIENT_MEETING -> "Встреча с клиентом"
        AppointmentType.PROPERTY_INSPECTION -> "Осмотр"
        AppointmentType.CONTRACT_SIGNING -> "Подписание договора"
        AppointmentType.KEY_HANDOVER -> "Передача ключей"
        AppointmentType.OWNER_MEETING -> "Встреча с собственником"
        AppointmentType.SIGNING -> "Подписание документов"
        AppointmentType.INSPECTION -> "Инспекция объекта"
        AppointmentType.PHOTO_SESSION -> "Фотосессия объекта"
        AppointmentType.MAINTENANCE -> "Техническое обслуживание"
        AppointmentType.OTHER -> "Другое"
    }
}

/**
 * Возвращает текстовое представление статуса встречи
 */
private fun getAppointmentStatusText(status: AppointmentStatus): String {
    return when (status) {
        AppointmentStatus.SCHEDULED -> "Запланирована"
        AppointmentStatus.CONFIRMED -> "Подтверждена"
        AppointmentStatus.CANCELLED -> "Отменена"
        AppointmentStatus.COMPLETED -> "Завершена"
        AppointmentStatus.RESCHEDULED -> "Перенесена"
        AppointmentStatus.NO_SHOW -> "Не явились"
        else -> "Неизвестно"
    }
} 