package com.realestateassistant.pro.presentation.components.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.realestateassistant.pro.domain.model.Booking
import com.realestateassistant.pro.domain.model.BookingStatus
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.model.PaymentStatus
import com.realestateassistant.pro.domain.model.Property
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Диалог для создания и редактирования бронирования
 * 
 * @param propertyId ID объекта недвижимости
 * @param startDate Выбранная начальная дата
 * @param endDate Выбранная конечная дата
 * @param clients Список клиентов
 * @param selectedClient Выбранный клиент
 * @param property Объект недвижимости
 * @param onClientSelect Колбэк для выбора клиента
 * @param onDateChange Колбэк для изменения дат
 * @param onCancel Колбэк для отмены диалога
 * @param onSave Колбэк для сохранения бронирования
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDialog(
    propertyId: String,
    startDate: LocalDate?,
    endDate: LocalDate?,
    clients: List<Client>,
    selectedClient: Client?,
    property: Property?,
    onClientSelect: (Client) -> Unit,
    onDateChange: (LocalDate, LocalDate) -> Unit,
    onCancel: () -> Unit,
    onSave: (Double, Int, String?) -> Unit,
    initialAmount: Double? = null,
    initialGuestsCount: Int = 1,
    initialNotes: String? = null,
    isEditing: Boolean = false
) {
    // Состояния для формы
    var amount by remember { mutableStateOf(initialAmount?.toString() ?: "") }
    var guestsCount by remember { mutableStateOf(initialGuestsCount.toString()) }
    var notes by remember { mutableStateOf(initialNotes ?: "") }
    
    // Локальные состояния для дат, чтобы не вызывать колбэк при каждом изменении
    var localStartDate by remember { mutableStateOf(startDate ?: LocalDate.now()) }
    var localEndDate by remember { mutableStateOf(endDate ?: LocalDate.now().plusDays(1)) }
    
    // Состояние для отображения выбора дат
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showClientSelector by remember { mutableStateOf(false) }
    
    // Форматтер для дат
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    
    // Расчет количества ночей
    val nightsCount = ChronoUnit.DAYS.between(localStartDate, localEndDate)
    
    // Определяем, является ли бронирование долгосрочным (более 30 дней)
    val isLongTerm = nightsCount > 30
    
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Заголовок
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Бронирование",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Закрыть"
                        )
                    }
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // Основное содержимое диалога в прокручиваемой колонке
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Выбор дат
                    DateRangeSelector(
                        startDate = localStartDate,
                        endDate = localEndDate,
                        onStartDateClick = { showStartDatePicker = true },
                        onEndDateClick = { showEndDatePicker = true },
                        dateFormatter = dateFormatter,
                        nightsCount = nightsCount
                    )
                    
                    // Выбор клиента
                    ClientSelector(
                        selectedClient = selectedClient,
                        onClick = { showClientSelector = true }
                    )
                    
                    // Поле для ввода суммы с разными вариантами цен
                    AmountField(
                        value = amount,
                        onValueChange = { amount = it },
                        nightsCount = nightsCount,
                        isLongTerm = isLongTerm,
                        property = property
                    )
                    
                    // Поле для количества гостей
                    GuestsField(
                        value = guestsCount,
                        onValueChange = { guestsCount = it },
                        maxGuests = property?.maxGuests
                    )
                    
                    // Поле для примечаний
                    NotesField(
                        value = notes,
                        onValueChange = { notes = it }
                    )
                }
                
                // Кнопки действий
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text("Отмена")
                    }
                    
                    Button(
                        onClick = {
                            try {
                                val amountValue = if (amount.isBlank()) initialAmount ?: 0.0 else amount.toDouble()
                                val guestsCountValue = if (guestsCount.isBlank()) 1 else guestsCount.toInt()
                                onSave(amountValue, guestsCountValue, notes)
                            } catch (e: NumberFormatException) {
                                // Обработка ошибки преобразования
                                onSave(initialAmount ?: 0.0, 1, "")
                            }
                        },
                        enabled = selectedClient != null && (amount.isNotBlank() || initialAmount != null),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text("Сохранить")
                    }
                }
            }
        }
    }
    
    // Диалог выбора начальной даты
    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        localStartDate = localStartDate
                        showStartDatePicker = false
                        if (localEndDate.isBefore(localStartDate)) {
                            localEndDate = localStartDate.plusDays(1)
                        }
                        onDateChange(localStartDate, localEndDate)
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showStartDatePicker = false }
                ) {
                    Text("Отмена")
                }
            }
        ) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = localStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            )
            
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
            
            // Обновляем выбранную дату при изменении
            datePickerState.selectedDateMillis?.let { millis ->
                val selectedDate = Instant.ofEpochMilli(millis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                localStartDate = selectedDate
            }
        }
    }
    
    // Диалог выбора конечной даты
    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        localEndDate = localEndDate
                        showEndDatePicker = false
                        onDateChange(localStartDate, localEndDate)
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEndDatePicker = false }
                ) {
                    Text("Отмена")
                }
            }
        ) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = localEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            )
            
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
            
            // Обновляем выбранную дату при изменении
            datePickerState.selectedDateMillis?.let { millis ->
                val selectedDate = Instant.ofEpochMilli(millis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                
                if (selectedDate.isAfter(localStartDate)) {
                    localEndDate = selectedDate
                }
            }
        }
    }
    
    // Диалог выбора клиента
    if (showClientSelector) {
        ClientSelectorDialog(
            clients = clients,
            onClientSelected = { client ->
                onClientSelect(client)
                showClientSelector = false
            },
            onDismiss = { showClientSelector = false }
        )
    }
}

@Composable
private fun ClientSelectorDialog(
    clients: List<Client>,
    onClientSelected: (Client) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    // Фильтрация клиентов по поисковому запросу
    val filteredClients = remember(searchQuery, clients) {
        if (searchQuery.isBlank()) {
            clients
        } else {
            clients.filter { client ->
                client.fullName.contains(searchQuery, ignoreCase = true) ||
                client.phone.any { it.contains(searchQuery) } ||
                (client.occupation?.contains(searchQuery, ignoreCase = true) ?: false) ||
                (client.preferredDistrict?.contains(searchQuery, ignoreCase = true) ?: false) ||
                (client.desiredPropertyType?.contains(searchQuery, ignoreCase = true) ?: false)
            }
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Заголовок
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Выберите клиента",
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
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Поле поиска
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Поиск по имени, телефону, району") },
                    leadingIcon = { 
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Поиск"
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Очистить"
                                )
                            }
                        }
                    },
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Список клиентов
                if (filteredClients.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isBlank()) 
                                "Нет доступных клиентов" 
                            else 
                                "Клиенты не найдены",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        filteredClients.forEach { client ->
                            ClientItem(
                                client = client,
                                onClick = { onClientSelected(client) }
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientItem(
    client: Client,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Аватар клиента
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val initials = remember(client.fullName) {
                        client.fullName.split(" ")
                            .take(2)
                            .joinToString("") { it.take(1) }
                    }
                    
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Информация о клиенте
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = client.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                if (client.phone.isNotEmpty()) {
                    Text(
                        text = client.phone.first(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                if (client.desiredPropertyType != null || client.rentalType.isNotEmpty()) {
                    Row {
                        if (client.desiredPropertyType != null) {
                            Text(
                                text = client.desiredPropertyType,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        
                        if (client.desiredPropertyType != null && client.rentalType.isNotEmpty()) {
                            Text(
                                text = " • ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                        
                        if (client.rentalType.isNotEmpty()) {
                            Text(
                                text = client.rentalType,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateRangeSelector(
    startDate: LocalDate,
    endDate: LocalDate,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit,
    dateFormatter: DateTimeFormatter,
    nightsCount: Long
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Даты бронирования",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Даты заезда и выезда
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Дата заезда
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Заезд",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onStartDateClick)
                                .padding(top = 4.dp),
                            color = Color.Transparent
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = startDate.format(dateFormatter),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Дата выезда
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Выезд",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onEndDateClick)
                                .padding(top = 4.dp),
                            color = Color.Transparent
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = endDate.format(dateFormatter),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
                
                // Информация о количестве ночей
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.3f)
                    )
                    Text(
                        text = "$nightsCount ${
                            when {
                                nightsCount % 10 == 1L && nightsCount % 100 != 11L -> "ночь"
                                nightsCount % 10 in 2L..4L && (nightsCount % 100 < 10L || nightsCount % 100 >= 20L) -> "ночи"
                                else -> "ночей"
                            }
                        }",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AmountField(
    value: String,
    onValueChange: (String) -> Unit,
    nightsCount: Long,
    isLongTerm: Boolean,
    property: Property?
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Стоимость",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        // Находим текущую сезонную цену, если возможно
        val seasonalPrice = remember(property, nightsCount) {
            property?.seasonalPrices?.find { seasonalPrice ->
                val today = LocalDate.now()
                val startDate = Instant.ofEpochMilli(seasonalPrice.startDate)
                    .atZone(ZoneId.systemDefault()).toLocalDate()
                val endDate = Instant.ofEpochMilli(seasonalPrice.endDate)
                    .atZone(ZoneId.systemDefault()).toLocalDate()
                
                // Проверяем, находится ли текущая дата в диапазоне сезона
                (today.isEqual(startDate) || today.isAfter(startDate)) &&
                (today.isEqual(endDate) || today.isBefore(endDate))
            }
        }
        
        // Рассчитываем цены для различных вариантов
        val dailyPriceAmount = property?.dailyPrice ?: 0.0
        val seasonalPriceAmount = seasonalPrice?.price ?: 0.0
        val monthlyPriceAmount = property?.monthlyRent ?: 0.0
        val summerMonthlyPriceAmount = property?.summerMonthlyRent ?: monthlyPriceAmount
        val winterMonthlyPriceAmount = property?.winterMonthlyRent ?: monthlyPriceAmount
        
        // Определяем, какой сейчас месяц для определения сезонной цены месячной аренды
        val currentMonth = LocalDate.now().monthValue
        val isSummerSeason = currentMonth in 5..9  // Май-Сентябрь
        
        // Рассчитываем итоговые суммы
        val dailyTotal = dailyPriceAmount * nightsCount
        val seasonalTotal = seasonalPriceAmount * nightsCount
        
        // Показываем варианты цен в зависимости от типа аренды
        if (isLongTerm) {
            // Компонент для долгосрочной аренды
            PriceOptions(
                title = "Вариант цены для долгосрочной аренды:",
                options = listOf(
                    PriceOption(
                        label = "Ежемесячная (стандартная)",
                        price = monthlyPriceAmount,
                        description = "${formatCurrency(monthlyPriceAmount)} × ${nightsCount / 30} мес."
                    ),
                    PriceOption(
                        label = if (isSummerSeason) "Летний сезон" else "Зимний сезон",
                        price = if (isSummerSeason) summerMonthlyPriceAmount else winterMonthlyPriceAmount,
                        description = "${formatCurrency(if (isSummerSeason) summerMonthlyPriceAmount else winterMonthlyPriceAmount)} × ${nightsCount / 30} мес."
                    ),
                    PriceOption(
                        label = "Произвольная сумма",
                        isCustom = true
                    )
                ),
                currentValue = value,
                onValueSelected = onValueChange
            )
        } else {
            // Компонент для краткосрочной аренды
            PriceOptions(
                title = "Вариант цены для посуточной аренды:",
                options = buildList {
                    add(PriceOption(
                        label = "Стандартная",
                        price = dailyTotal,
                        description = "${formatCurrency(dailyPriceAmount)} × $nightsCount ночей"
                    ))
                    if (seasonalPrice != null) {
                        add(PriceOption(
                            label = "Сезонная",
                            price = seasonalTotal,
                            description = "${formatCurrency(seasonalPriceAmount)} × $nightsCount ночей"
                        ))
                    }
                    add(PriceOption(
                        label = "Произвольная сумма",
                        isCustom = true
                    ))
                },
                currentValue = value,
                onValueSelected = onValueChange
            )
        }
        
        // Поле для ручного ввода суммы
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Введите стоимость") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.AttachMoney,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )
    }
}

// Модель для варианта цены
data class PriceOption(
    val label: String,
    val price: Double = 0.0,
    val description: String = "",
    val isCustom: Boolean = false
)

@Composable
fun PriceOptions(
    title: String,
    options: List<PriceOption>,
    currentValue: String,
    onValueSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        if (!option.isCustom) {
                            onValueSelected(option.price.toString())
                        }
                    }
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = if (option.isCustom) {
                        currentValue.isNotEmpty() && options.none { !it.isCustom && it.price.toString() == currentValue }
                    } else {
                        currentValue == option.price.toString()
                    },
                    onClick = {
                        if (!option.isCustom) {
                            onValueSelected(option.price.toString())
                        }
                    }
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (option.description.isNotEmpty()) {
                        Text(
                            text = option.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                if (!option.isCustom) {
                    Text(
                        text = formatCurrency(option.price),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// Функция форматирования суммы в валюту
private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance().apply {
        maximumFractionDigits = 0
        currency = java.util.Currency.getInstance("RUB")
    }
    return formatter.format(amount)
}

@Composable
private fun GuestsField(
    value: String,
    onValueChange: (String) -> Unit,
    maxGuests: Int? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Количество гостей" + (maxGuests?.let { " (макс. $it)" } ?: ""),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                // Проверяем, что не превышен максимум гостей
                if (maxGuests == null || newValue.isEmpty() || (newValue.toIntOrNull() ?: 0) <= maxGuests) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Введите число") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Group,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
    }
}

@Composable
private fun NotesField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Примечания",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Дополнительная информация") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Notes,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            minLines = 3,
            maxLines = 5
        )
    }
}

@Composable
private fun ClientSelector(
    selectedClient: Client?,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Клиент",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        OutlinedTextField(
            value = selectedClient?.fullName ?: "",
            onValueChange = { /* Управляется через селектор */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            placeholder = { Text("Выберите клиента") },
            readOnly = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Выбрать клиента"
                    )
                }
            }
        )
    }
} 