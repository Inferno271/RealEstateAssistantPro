package com.realestateassistant.pro.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import com.realestateassistant.pro.presentation.model.ClientFormState
import com.realestateassistant.pro.presentation.model.ClientSection
import com.realestateassistant.pro.presentation.model.RentalType
import com.realestateassistant.pro.presentation.model.createInitialClientSectionState
import com.realestateassistant.pro.presentation.screens.client.ClientInfoSection
import com.realestateassistant.pro.presentation.screens.client.ContactInfoSection
import com.realestateassistant.pro.presentation.screens.client.LongTermRequirementsSection
import com.realestateassistant.pro.presentation.screens.client.RentalPreferencesSection
import com.realestateassistant.pro.presentation.screens.client.SearchFlexibilitySection
import com.realestateassistant.pro.presentation.screens.client.ShortTermRequirementsSection
import com.realestateassistant.pro.presentation.screens.client.LegalPreferencesSection
import com.realestateassistant.pro.presentation.screens.client.HousingPreferencesSection
import com.realestateassistant.pro.presentation.screens.client.AmenitiesPreferencesSection
import com.realestateassistant.pro.presentation.screens.client.SpecificPropertyPreferencesSection
import com.realestateassistant.pro.presentation.viewmodel.ClientViewModel
import com.realestateassistant.pro.presentation.viewmodel.OptionsViewModel
import com.realestateassistant.pro.presentation.components.RentalTypeSelector

/**
 * Экран добавления нового клиента.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClientScreen(
    onNavigateBack: () -> Unit,
    clientViewModel: ClientViewModel = hiltViewModel(),
    optionsViewModel: OptionsViewModel = hiltViewModel()
) {
    var formState by remember { mutableStateOf(ClientFormState()) }
    val context = LocalContext.current
    
    // Состояние для отслеживания, какие секции развернуты
    val expandedSections = remember { createInitialClientSectionState() }
    
    // Состояние для отображения ошибок валидации
    var validationError by remember { mutableStateOf<String?>(null) }
    
    // Состояние для отслеживания невалидных полей
    var invalidFields by remember { mutableStateOf(setOf<String>()) }
    
    // Скролл состояние для прокрутки к полю с ошибкой
    val scrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    
    // Якоря для позиций секций
    val sectionPositions = remember { mutableStateMapOf<ClientSection, Float>() }

    // Добавляем состояние для отслеживания режима отображения только обязательных полей
    var showOnlyRequiredFields by remember { mutableStateOf(false) }

    // Функция для валидации обязательных полей формы
    fun validateForm(): Boolean {
        val newInvalidFields = mutableSetOf<String>()
        
        // Проверяем общие обязательные поля
        if (formState.fullName.isBlank()) {
            validationError = "Необходимо указать ФИО клиента"
            newInvalidFields.add("fullName")
            invalidFields = newInvalidFields
            
            // Разворачиваем секцию с ошибкой
            expandedSections[ClientSection.CONTACT_INFO] = true
            return false
        }
        
        if (formState.phone.isEmpty() || formState.phone.all { it.isBlank() }) {
            validationError = "Необходимо указать хотя бы один номер телефона"
            newInvalidFields.add("phone")
            invalidFields = newInvalidFields
            
            // Разворачиваем секцию с ошибкой
            expandedSections[ClientSection.CONTACT_INFO] = true
            return false
        }
        
        if (formState.familyComposition.isBlank()) {
            validationError = "Необходимо указать состав семьи"
            newInvalidFields.add("familyComposition")
            invalidFields = newInvalidFields
            
            // Разворачиваем секцию с ошибкой
            expandedSections[ClientSection.CLIENT_INFO] = true
            return false
        }
        
        if (formState.peopleCount.isBlank()) {
            validationError = "Необходимо указать количество проживающих"
            newInvalidFields.add("peopleCount")
            invalidFields = newInvalidFields
            
            // Разворачиваем секцию с ошибкой
            expandedSections[ClientSection.CLIENT_INFO] = true
            return false
        }
        
        if (formState.desiredPropertyType.isBlank()) {
            validationError = "Необходимо указать желаемый тип недвижимости"
            newInvalidFields.add("desiredPropertyType")
            invalidFields = newInvalidFields
            
            // Разворачиваем секцию с ошибкой
            expandedSections[ClientSection.RENTAL_PREFERENCES] = true
            return false
        }
        
        if (formState.preferredDistrict.isBlank()) {
            validationError = "Необходимо указать предпочитаемый район"
            newInvalidFields.add("preferredDistrict")
            invalidFields = newInvalidFields
            
            // Разворачиваем секцию с ошибкой
            expandedSections[ClientSection.RENTAL_PREFERENCES] = true
            return false
        }
        
        if (formState.urgencyLevel.isBlank()) {
            validationError = "Необходимо указать срочность поиска"
            newInvalidFields.add("urgencyLevel")
            invalidFields = newInvalidFields
            
            // Разворачиваем секцию с ошибкой
            expandedSections[ClientSection.SEARCH_FLEXIBILITY] = true
            return false
        }
        
        // Проверяем поля в зависимости от типа аренды
        when (formState.rentalType) {
            RentalType.LONG_TERM -> {
                if (formState.longTermBudgetMax.isBlank()) {
                    validationError = "Необходимо указать максимальный бюджет"
                    newInvalidFields.add("longTermBudgetMax")
                    invalidFields = newInvalidFields
                    
                    // Разворачиваем секцию с ошибкой
                    expandedSections[ClientSection.LONG_TERM_REQUIREMENTS] = true
                    return false
                }
                
                if (formState.desiredRoomsCount.isBlank()) {
                    validationError = "Необходимо указать желаемое количество комнат"
                    newInvalidFields.add("desiredRoomsCount")
                    invalidFields = newInvalidFields
                    
                    // Разворачиваем секцию с ошибкой
                    expandedSections[ClientSection.LONG_TERM_REQUIREMENTS] = true
                    return false
                }
                
                if (formState.moveInDeadline == null) {
                    validationError = "Необходимо указать срок заселения"
                    newInvalidFields.add("moveInDeadline")
                    invalidFields = newInvalidFields
                    
                    // Разворачиваем секцию с ошибкой
                    expandedSections[ClientSection.LONG_TERM_REQUIREMENTS] = true
                    return false
                }
            }
            
            RentalType.SHORT_TERM -> {
                if (formState.shortTermBudgetMax.isBlank()) {
                    validationError = "Необходимо указать максимальный бюджет"
                    newInvalidFields.add("shortTermBudgetMax")
                    invalidFields = newInvalidFields
                    
                    // Разворачиваем секцию с ошибкой
                    expandedSections[ClientSection.SHORT_TERM_REQUIREMENTS] = true
                    return false
                }
                
                if (formState.shortTermCheckInDate == null) {
                    validationError = "Необходимо указать дату заезда"
                    newInvalidFields.add("shortTermCheckInDate")
                    invalidFields = newInvalidFields
                    
                    // Разворачиваем секцию с ошибкой
                    expandedSections[ClientSection.SHORT_TERM_REQUIREMENTS] = true
                    return false
                }
                
                if (formState.shortTermCheckOutDate == null) {
                    validationError = "Необходимо указать дату выезда"
                    newInvalidFields.add("shortTermCheckOutDate")
                    invalidFields = newInvalidFields
                    
                    // Разворачиваем секцию с ошибкой
                    expandedSections[ClientSection.SHORT_TERM_REQUIREMENTS] = true
                    return false
                }
            }
        }
        
        // Если все проверки пройдены, сбрасываем ошибку и возвращаем true
        validationError = null
        invalidFields = emptySet()
        return true
    }
    
    // Функция для проверки, является ли поле невалидным
    fun isFieldInvalid(fieldName: String): Boolean {
        return fieldName in invalidFields
    }

    // Функция для очистки формы, но с сохранением контактной информации
    fun resetFormWithContact() {
        // Создаем новую форму, но сохраняем контактную информацию
        formState = ClientFormState(
            fullName = formState.fullName,
            phone = formState.phone
        )
        
        // Возвращаем настройки развернутых секций к начальному состоянию
        expandedSections.clear()
        expandedSections.putAll(
            mapOf(
                ClientSection.CONTACT_INFO to true,
                ClientSection.CLIENT_INFO to true,
                ClientSection.RENTAL_PREFERENCES to true,
                ClientSection.SEARCH_FLEXIBILITY to false,
                ClientSection.HOUSING_PREFERENCES to false,
                ClientSection.AMENITIES_PREFERENCES to false,
                ClientSection.SPECIFIC_PROPERTY_PREFERENCES to false,
                ClientSection.LEGAL_PREFERENCES to false,
                ClientSection.LONG_TERM_REQUIREMENTS to false,
                ClientSection.SHORT_TERM_REQUIREMENTS to false
            )
        )
        
        // Сбрасываем ошибки валидации
        validationError = null
        invalidFields = emptySet()
        
        // Показываем уведомление
        Toast.makeText(context, "Клиент сохранен, создание нового клиента", Toast.LENGTH_SHORT).show()
    }

    // Функция для прокрутки к секции с ошибкой
    fun scrollToErrorSection() {
        coroutineScope.launch {
            // Определяем секцию для прокрутки на основе первого невалидного поля
            val targetSection = when {
                invalidFields.any { it in listOf("fullName", "phone") } -> ClientSection.CONTACT_INFO
                invalidFields.any { it in listOf("familyComposition", "peopleCount") } -> ClientSection.CLIENT_INFO
                invalidFields.any { it in listOf("desiredPropertyType", "preferredDistrict") } -> ClientSection.RENTAL_PREFERENCES
                invalidFields.any { it == "urgencyLevel" } -> ClientSection.SEARCH_FLEXIBILITY
                invalidFields.any { it in listOf("longTermBudgetMax", "desiredRoomsCount", "moveInDeadline") } -> ClientSection.LONG_TERM_REQUIREMENTS
                invalidFields.any { it in listOf("shortTermBudgetMax", "shortTermCheckInDate", "shortTermCheckOutDate") } -> ClientSection.SHORT_TERM_REQUIREMENTS
                else -> ClientSection.CONTACT_INFO
            }
            
            // Прокручиваем к нужной секции, используя сохраненную позицию
            val targetPosition = sectionPositions[targetSection] ?: 0f
            scrollState.animateScrollTo(targetPosition.toInt())
        }
    }
    
    // Функция для прокрутки к выбранной секции
    fun scrollToSection(section: ClientSection) {
        coroutineScope.launch {
            // Получаем текущее состояние секции
            val isCurrentlyExpanded = expandedSections[section] ?: false
            
            // Проверяем, видна ли секция в видимой области скролла
            val targetPosition = sectionPositions[section] ?: 0f
            val isVisible = targetPosition >= scrollState.value && 
                            targetPosition <= (scrollState.value + scrollState.maxValue * 0.2f)
            
            // Если секция видима и уже развернута, то сворачиваем её
            if (isVisible && isCurrentlyExpanded) {
                expandedSections[section] = false
                return@launch
            }
            
            // В противном случае, разворачиваем секцию и прокручиваем к ней
            expandedSections[section] = true
            scrollState.animateScrollTo(targetPosition.toInt())
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { 
                        Text(
                            "Добавить клиента",
                            style = MaterialTheme.typography.titleLarge
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack, 
                                contentDescription = "Назад",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    actions = {
                        // Кнопка для переключения режима отображения полей
                        TextButton(
                            onClick = { showOnlyRequiredFields = !showOnlyRequiredFields },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = if (showOnlyRequiredFields) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        ) {
                            Text(
                                text = if (showOnlyRequiredFields) "Все поля" else "Обязательные поля",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
                
                // Навигация по разделам
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(horizontalScrollState)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val sections = listOf(
                        Pair(ClientSection.CONTACT_INFO, "Контакты"),
                        Pair(ClientSection.CLIENT_INFO, "О клиенте"),
                        Pair(ClientSection.RENTAL_PREFERENCES, "Предпочтения"),
                        Pair(ClientSection.SEARCH_FLEXIBILITY, "Гибкость"),
                        Pair(ClientSection.HOUSING_PREFERENCES, "Жилье"),
                        Pair(ClientSection.AMENITIES_PREFERENCES, "Удобства"),
                        Pair(ClientSection.SPECIFIC_PROPERTY_PREFERENCES, "Спец.требования"),
                        Pair(ClientSection.LEGAL_PREFERENCES, "Юридические")
                    )
                    
                    // Добавляем секции в зависимости от типа аренды
                    val allSections = if (formState.rentalType == RentalType.LONG_TERM) {
                        sections + Pair(ClientSection.LONG_TERM_REQUIREMENTS, "Долгосрочная")
                    } else {
                        sections + Pair(ClientSection.SHORT_TERM_REQUIREMENTS, "Краткосрочная")
                    }
                    
                    // Отображаем все секции в виде чипов
                    allSections.forEach { (section, title) ->
                        val hasError = when (section) {
                            ClientSection.CONTACT_INFO -> isFieldInvalid("fullName") || isFieldInvalid("phone")
                            ClientSection.CLIENT_INFO -> isFieldInvalid("familyComposition") || isFieldInvalid("peopleCount")
                            ClientSection.RENTAL_PREFERENCES -> isFieldInvalid("desiredPropertyType") || isFieldInvalid("preferredDistrict")
                            ClientSection.SEARCH_FLEXIBILITY -> isFieldInvalid("urgencyLevel")
                            ClientSection.LONG_TERM_REQUIREMENTS -> isFieldInvalid("longTermBudgetMax") || isFieldInvalid("desiredRoomsCount") || isFieldInvalid("moveInDeadline")
                            ClientSection.SHORT_TERM_REQUIREMENTS -> isFieldInvalid("shortTermBudgetMax") || isFieldInvalid("shortTermCheckInDate") || isFieldInvalid("shortTermCheckOutDate")
                            else -> false
                        }
                        
                        // Определяем цвета в зависимости от наличия ошибки и развернутости секции
                        val isExpanded = expandedSections[section] ?: false
                        val containerColor = when {
                            hasError -> MaterialTheme.colorScheme.errorContainer
                            isExpanded -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        
                        val contentColor = when {
                            hasError -> MaterialTheme.colorScheme.onErrorContainer
                            isExpanded -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        
                        ElevatedFilterChip(
                            selected = isExpanded,
                            onClick = { scrollToSection(section) },
                            label = { 
                                Text(
                                    text = title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyMedium
                                ) 
                            },
                            leadingIcon = if (hasError) {
                                {
                                    Icon(
                                        Icons.Default.Error,
                                        contentDescription = "Ошибка",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.elevatedFilterChipColors(
                                containerColor = containerColor,
                                labelColor = contentColor,
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            border = if (hasError) {
                                FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isExpanded,
                                    borderColor = MaterialTheme.colorScheme.error
                                )
                            } else null,
                            elevation = FilterChipDefaults.elevatedFilterChipElevation(
                                elevation = if (isExpanded) 4.dp else 1.dp
                            )
                        )
                    }
                }
                
                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Основное содержимое формы
            // Секция контактной информации
            Box(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        sectionPositions[ClientSection.CONTACT_INFO] = coordinates.positionInParent().y
                    }
            ) {
                ContactInfoSection(
                    formState = formState,
                    onFormStateChange = { formState = it },
                    expandedSections = expandedSections,
                    isFieldInvalid = ::isFieldInvalid,
                    showOnlyRequiredFields = showOnlyRequiredFields
                )
            }
            
            // Секция информации о клиенте
            Box(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        sectionPositions[ClientSection.CLIENT_INFO] = coordinates.positionInParent().y
                    }
            ) {
                ClientInfoSection(
                    formState = formState,
                    onFormStateChange = { formState = it },
                    expandedSections = expandedSections,
                    optionsViewModel = optionsViewModel,
                    isFieldInvalid = ::isFieldInvalid,
                    showOnlyRequiredFields = showOnlyRequiredFields
                )
            }
            
            // Секция предпочтений по аренде
            Box(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        sectionPositions[ClientSection.RENTAL_PREFERENCES] = coordinates.positionInParent().y
                    }
            ) {
                RentalPreferencesSection(
                    formState = formState,
                    onFormStateChange = { formState = it },
                    expandedSections = expandedSections,
                    isFieldInvalid = ::isFieldInvalid,
                    showOnlyRequiredFields = showOnlyRequiredFields
                )
            }
            
            // Секция гибкости поиска
            Box(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        sectionPositions[ClientSection.SEARCH_FLEXIBILITY] = coordinates.positionInParent().y
                    }
            ) {
                SearchFlexibilitySection(
                    formState = formState,
                    onFormStateChange = { formState = it },
                    expandedSections = expandedSections,
                    optionsViewModel = optionsViewModel,
                    isFieldInvalid = ::isFieldInvalid,
                    showOnlyRequiredFields = showOnlyRequiredFields
                )
            }
            
            // Отображаем необязательные секции только если не включен режим только обязательных полей
            if (!showOnlyRequiredFields) {
                // Секция предпочтений по жилищным характеристикам
                Box(
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            sectionPositions[ClientSection.HOUSING_PREFERENCES] = coordinates.positionInParent().y
                        }
                ) {
                    HousingPreferencesSection(
                        formState = formState,
                        onFormStateChange = { formState = it },
                        expandedSections = expandedSections,
                        optionsViewModel = optionsViewModel,
                        isFieldInvalid = ::isFieldInvalid
                    )
                }
                
                // Секция предпочтений по удобствам
                Box(
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            sectionPositions[ClientSection.AMENITIES_PREFERENCES] = coordinates.positionInParent().y
                        }
                ) {
                    AmenitiesPreferencesSection(
                        formState = formState,
                        onFormStateChange = { formState = it },
                        expandedSections = expandedSections,
                        optionsViewModel = optionsViewModel,
                        isFieldInvalid = ::isFieldInvalid
                    )
                }
                
                // Секция специальных предпочтений для домов и участков
                Box(
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            sectionPositions[ClientSection.SPECIFIC_PROPERTY_PREFERENCES] = coordinates.positionInParent().y
                        }
                ) {
                    SpecificPropertyPreferencesSection(
                        formState = formState,
                        onFormStateChange = { formState = it },
                        expandedSections = expandedSections,
                        optionsViewModel = optionsViewModel,
                        isFieldInvalid = ::isFieldInvalid
                    )
                }
                
                // Добавляем секцию юридических предпочтений
                Box(
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            sectionPositions[ClientSection.LEGAL_PREFERENCES] = coordinates.positionInParent().y
                        }
                ) {
                    LegalPreferencesSection(
                        formState = formState,
                        onFormStateChange = { formState = it },
                        expandedSections = expandedSections,
                        isFieldInvalid = ::isFieldInvalid
                    )
                }
            }
            
            // Селектор типа аренды (вынесен из секции "Предпочтения по аренде")
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        "Тип аренды",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    RentalTypeSelector(
                        rentalType = formState.rentalType,
                        onRentalTypeChange = { formState = formState.copy(rentalType = it) }
                    )
                }
            }
            
            // Секция требований для долгосрочной аренды
            if (formState.rentalType == RentalType.LONG_TERM) {
                Box(
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            sectionPositions[ClientSection.LONG_TERM_REQUIREMENTS] = coordinates.positionInParent().y
                        }
                ) {
                    LongTermRequirementsSection(
                        formState = formState,
                        onFormStateChange = { formState = it },
                        expandedSections = expandedSections,
                        optionsViewModel = optionsViewModel,
                        isFieldInvalid = ::isFieldInvalid,
                        showOnlyRequiredFields = showOnlyRequiredFields
                    )
                }
            }
            
            // Секция требований для краткосрочной аренды
            if (formState.rentalType == RentalType.SHORT_TERM) {
                Box(
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            sectionPositions[ClientSection.SHORT_TERM_REQUIREMENTS] = coordinates.positionInParent().y
                        }
                ) {
                    ShortTermRequirementsSection(
                        formState = formState,
                        onFormStateChange = { formState = it },
                        expandedSections = expandedSections,
                        isFieldInvalid = ::isFieldInvalid,
                        showOnlyRequiredFields = showOnlyRequiredFields
                    )
                }
            }
            
            // Добавляем кнопки сохранения внизу формы
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Кнопка "Сохранить"
                Button(
                    onClick = {
                        if (validateForm()) {
                            clientViewModel.addClient(formState.toClient())
                            Toast.makeText(context, "Клиент сохранен", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        } else {
                            // Прокручиваем к полю с ошибкой
                            scrollToErrorSection()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Сохранить")
                }
                
                // Кнопка "Сохранить и добавить еще"
                Button(
                    onClick = {
                        if (validateForm()) {
                            clientViewModel.addClient(formState.toClient())
                            resetFormWithContact()
                        } else {
                            // Прокручиваем к полю с ошибкой
                            scrollToErrorSection()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Сохранить и добавить еще")
                }
            }
        }
    }
} 