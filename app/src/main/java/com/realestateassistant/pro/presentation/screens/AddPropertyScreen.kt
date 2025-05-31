package com.realestateassistant.pro.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import com.realestateassistant.pro.presentation.components.RentalTypeSelector
import com.realestateassistant.pro.presentation.model.PropertyFormState
import com.realestateassistant.pro.presentation.model.PropertySection
import com.realestateassistant.pro.presentation.model.createInitialSectionState
import com.realestateassistant.pro.presentation.screens.property.*
import com.realestateassistant.pro.presentation.viewmodel.OptionsViewModel
import com.realestateassistant.pro.presentation.viewmodel.PropertyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPropertyScreen(
    onNavigateBack: () -> Unit,
    propertyViewModel: PropertyViewModel = hiltViewModel(),
    optionsViewModel: OptionsViewModel = hiltViewModel()
) {
    var formState by remember { mutableStateOf(PropertyFormState()) }
    val context = LocalContext.current
    
    // Состояние для отслеживания, какие секции развернуты
    val expandedSections = remember { createInitialSectionState() }
    
    // Состояние для отображения ошибок валидации
    var validationError by remember { mutableStateOf<String?>(null) }
    
    // Состояние для отслеживания невалидных полей
    var invalidFields by remember { mutableStateOf(setOf<String>()) }
    
    // Состояние для отображения только обязательных полей
    var showOnlyRequiredFields by remember { mutableStateOf(false) }

    // Собираем состояния списков опций
    val propertyTypes by optionsViewModel.propertyTypes.collectAsState()
    val districts by optionsViewModel.districts.collectAsState()
    val layouts by optionsViewModel.layouts.collectAsState()
    val repairStates by optionsViewModel.repairStates.collectAsState()
    val bathroomTypes by optionsViewModel.bathroomTypes.collectAsState()
    val heatingTypes by optionsViewModel.heatingTypes.collectAsState()
    val parkingTypes by optionsViewModel.parkingTypes.collectAsState()
    val poolTypes by optionsViewModel.poolTypes.collectAsState()
    
    // Получаем конфигурацию для текущего типа недвижимости
    val currentCharacteristicsConfig by propertyViewModel.currentCharacteristicsConfig.collectAsState()
    
    // Скролл состояние для прокрутки к секции
    val scrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    
    // Якоря для позиций секций
    val sectionPositions = remember { mutableStateMapOf<PropertySection, Float>() }
    
    // Функция для валидации обязательных полей формы
    fun validateForm(): Boolean {
        val newInvalidFields = mutableSetOf<String>()
        
        // Проверяем обязательные поля в секции контактной информации
        if (formState.contactName.isBlank()) {
            validationError = "Необходимо указать имя контактного лица"
            newInvalidFields.add("contactName")
            invalidFields = newInvalidFields
            
            // Разворачиваем секцию с ошибкой
            expandedSections[PropertySection.CONTACT_INFO] = true
            return false
        }
        
        if (formState.contactPhone.isEmpty() || formState.contactPhone.all { it.isBlank() }) {
            validationError = "Необходимо указать хотя бы один номер телефона"
            newInvalidFields.add("contactPhone")
            invalidFields = newInvalidFields
            
            // Разворачиваем секцию с ошибкой
            expandedSections[PropertySection.CONTACT_INFO] = true
            return false
        }
        
        // Проверяем обязательные поля в секции общей информации
        if (formState.propertyType.isBlank()) {
            validationError = "Необходимо указать тип недвижимости"
            newInvalidFields.add("propertyType")
            invalidFields = newInvalidFields
            
            // Разворачиваем секцию с ошибкой
            expandedSections[PropertySection.PROPERTY_INFO] = true
            return false
        }
        
        if (formState.address.isBlank()) {
            validationError = "Необходимо указать адрес объекта"
            newInvalidFields.add("address")
            invalidFields = newInvalidFields
            
            // Разворачиваем секцию с ошибкой
            expandedSections[PropertySection.PROPERTY_INFO] = true
            return false
        }
        
        if (formState.district.isBlank()) {
            validationError = "Необходимо указать район/метро"
            newInvalidFields.add("district")
            invalidFields = newInvalidFields
            
            // Разворачиваем секцию с ошибкой
            expandedSections[PropertySection.PROPERTY_INFO] = true
            return false
        }
        
        if (formState.area.isBlank()) {
            validationError = "Необходимо указать площадь объекта"
            newInvalidFields.add("area")
            invalidFields = newInvalidFields
            
            // Разворачиваем секцию с ошибкой
            expandedSections[PropertySection.PROPERTY_INFO] = true
            return false
        }
        
        // Проверяем количество комнат только для жилых помещений
        if (formState.propertyType.contains("квартира", ignoreCase = true) || 
            formState.propertyType.contains("дом", ignoreCase = true) || 
            formState.propertyType.contains("комната", ignoreCase = true)) {
            if (formState.roomsCount.isBlank()) {
                validationError = "Необходимо указать количество комнат"
                newInvalidFields.add("roomsCount")
                invalidFields = newInvalidFields
                
                // Разворачиваем секцию с ошибкой
                expandedSections[PropertySection.PROPERTY_INFO] = true
                return false
            }
        }
        
        // Проверяем поля для долгосрочной аренды, если выбран этот тип
        if (formState.isLongTerm) {
            // Проверяем, заполнено ли хотя бы одно поле стоимости аренды
            if (formState.monthlyRent.isBlank() && formState.winterMonthlyRent.isBlank() && formState.summerMonthlyRent.isBlank()) {
                validationError = "Необходимо указать стоимость аренды (круглогодичную, зимнюю или летнюю)"
                newInvalidFields.add("monthlyRent")
                newInvalidFields.add("winterMonthlyRent")
                newInvalidFields.add("summerMonthlyRent")
                invalidFields = newInvalidFields
                
                // Разворачиваем секцию с ошибкой
                expandedSections[PropertySection.LONG_TERM_RENTAL] = true
                return false
            }
            
            // Проверяем, заполнено ли поле залога
            if (formState.depositCustomAmount.isBlank()) {
                validationError = "Необходимо указать залог"
                newInvalidFields.add("depositCustomAmount")
                invalidFields = newInvalidFields
                
                // Разворачиваем секцию с ошибкой
                expandedSections[PropertySection.LONG_TERM_RENTAL] = true
                return false
            }
        } else {
            // Проверяем поля для посуточной аренды
            if (formState.dailyPrice.isBlank()) {
                validationError = "Необходимо указать стоимость за сутки"
                newInvalidFields.add("dailyPrice")
                invalidFields = newInvalidFields
                
                // Разворачиваем секцию с ошибкой
                expandedSections[PropertySection.SHORT_TERM_RENTAL] = true
                return false
            }
            
            if (formState.maxGuests.isBlank()) {
                validationError = "Необходимо указать максимальное количество гостей"
                newInvalidFields.add("maxGuests")
                invalidFields = newInvalidFields
                
                // Разворачиваем секцию с ошибкой
                expandedSections[PropertySection.SHORT_TERM_RENTAL] = true
                return false
            }
        }
        
        // Проверяем наличие фотографий
        if (formState.photos.isEmpty()) {
            validationError = "Необходимо добавить хотя бы одну фотографию"
            newInvalidFields.add("photos")
            invalidFields = newInvalidFields
            
            // Разворачиваем секцию с ошибкой
            expandedSections[PropertySection.MEDIA] = true
            return false
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
    
    // Функция для прокрутки к выбранной секции
    fun scrollToSection(section: PropertySection) {
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
    
    // Функция для прокрутки к секции с ошибкой
    fun scrollToErrorSection() {
        coroutineScope.launch {
            // Определяем секцию для прокрутки на основе первого невалидного поля
            val targetSection = when {
                invalidFields.any { it in listOf("contactName", "contactPhone") } -> PropertySection.CONTACT_INFO
                invalidFields.any { it in listOf("propertyType", "address", "district", "area", "roomsCount") } -> PropertySection.PROPERTY_INFO
                invalidFields.any { it in listOf("monthlyRent", "winterMonthlyRent", "summerMonthlyRent", "depositCustomAmount") } -> PropertySection.LONG_TERM_RENTAL
                invalidFields.any { it in listOf("dailyPrice", "maxGuests") } -> PropertySection.SHORT_TERM_RENTAL
                invalidFields.any { it == "photos" } -> PropertySection.MEDIA
                else -> PropertySection.CONTACT_INFO
            }
            
            // Прокручиваем к нужной секции, используя сохраненную позицию
            val targetPosition = sectionPositions[targetSection] ?: 0f
            scrollState.animateScrollTo(targetPosition.toInt())
        }
    }
    
    // Обновляем конфигурацию при изменении типа недвижимости
    LaunchedEffect(formState.propertyType) {
        if (formState.propertyType.isNotEmpty()) {
            propertyViewModel.updateCharacteristicsConfig(formState.propertyType)
        }
    }

    // Функция для очистки формы, но с сохранением контактной информации
    fun resetFormWithContact() {
        // Создаем новую форму, но сохраняем контактную информацию
        formState = PropertyFormState(
            contactName = formState.contactName,
            contactPhone = formState.contactPhone,
            additionalContactInfo = formState.additionalContactInfo
        )
        
        // Возвращаем настройки развернутых секций к начальному состоянию
        expandedSections.clear()
        PropertySection.values().forEach { section ->
            expandedSections[section] = section == PropertySection.CONTACT_INFO || section == PropertySection.PROPERTY_INFO
        }
        
        // Сбрасываем состояние валидации
        validationError = null
        invalidFields = emptySet()
        
        // Показываем уведомление
        Toast.makeText(context, "Объект сохранен, создание нового объекта", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { 
                        Text(
                            "Добавить объект",
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
                    // Базовые секции, которые всегда отображаются
                    val basePropertySections = listOf(
                        Pair(PropertySection.CONTACT_INFO, "Контакты"),
                        Pair(PropertySection.PROPERTY_INFO, "Общая информация"),
                        Pair(PropertySection.PROPERTY_CHARACTERISTICS, "Характеристики"),
                        Pair(PropertySection.LIVING_CONDITIONS, "Условия проживания")
                    )
                    
                    // Секции, зависящие от типа аренды
                    val rentalSections = if (formState.isLongTerm) {
                        listOf(Pair(PropertySection.LONG_TERM_RENTAL, "Долгосрочная"))
                    } else {
                        listOf(Pair(PropertySection.SHORT_TERM_RENTAL, "Посуточная"))
                    }
                    
                    // Секция медиа (всегда последняя)
                    val mediaSections = listOf(
                        Pair(PropertySection.MEDIA, "Медиа")
                    )
                    
                    // Объединяем секции и отображаем чипы
                    val allSections = basePropertySections + rentalSections + mediaSections
                    
                    allSections.forEach { (section, title) ->
                        val isExpanded = expandedSections[section] ?: false
                        val hasError = when (section) {
                            PropertySection.CONTACT_INFO -> isFieldInvalid("contactName") || isFieldInvalid("contactPhone")
                            PropertySection.PROPERTY_INFO -> isFieldInvalid("propertyType") || isFieldInvalid("address") || isFieldInvalid("district") || isFieldInvalid("area") || isFieldInvalid("roomsCount")
                            PropertySection.LONG_TERM_RENTAL -> isFieldInvalid("monthlyRent") || isFieldInvalid("winterMonthlyRent") || isFieldInvalid("summerMonthlyRent") || isFieldInvalid("depositCustomAmount")
                            PropertySection.SHORT_TERM_RENTAL -> isFieldInvalid("dailyPrice") || isFieldInvalid("maxGuests")
                            PropertySection.MEDIA -> isFieldInvalid("photos")
                            else -> false
                        }
                        
                        // Определяем цвета в зависимости от наличия ошибки и развернутости секции
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
            // Новый селектор типа аренды вместо сворачиваемого раздела
            RentalTypeSelector(
                isLongTerm = formState.isLongTerm,
                onIsLongTermChange = { formState = formState.copy(isLongTerm = it) }
            )
            
            // Секция контактной информации с отслеживанием позиции
            Box(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        sectionPositions[PropertySection.CONTACT_INFO] = coordinates.positionInParent().y
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
            
            // Секция общей информации с отслеживанием позиции
            Box(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        sectionPositions[PropertySection.PROPERTY_INFO] = coordinates.positionInParent().y
                    }
            ) {
                PropertyInfoSection(
                    formState = formState,
                    onFormStateChange = { formState = it },
                    optionsViewModel = optionsViewModel,
                    propertyTypes = propertyTypes,
                    districts = districts,
                    layouts = layouts,
                    expandedSections = expandedSections,
                    characteristicsConfig = currentCharacteristicsConfig,
                    isFieldInvalid = ::isFieldInvalid,
                    showOnlyRequiredFields = showOnlyRequiredFields
                )
            }
            
            // Секция характеристик с отслеживанием позиции
            Box(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        sectionPositions[PropertySection.PROPERTY_CHARACTERISTICS] = coordinates.positionInParent().y
                    }
            ) {
                PropertyCharacteristicsSection(
                    formState = formState,
                    onFormStateChange = { formState = it },
                    optionsViewModel = optionsViewModel,
                    repairStates = repairStates,
                    bathroomTypes = bathroomTypes,
                    heatingTypes = heatingTypes,
                    parkingTypes = parkingTypes,
                    expandedSections = expandedSections,
                    isFieldInvalid = ::isFieldInvalid,
                    showOnlyRequiredFields = showOnlyRequiredFields
                )
            }
            
            // Секция условий проживания с отслеживанием позиции
            Box(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        sectionPositions[PropertySection.LIVING_CONDITIONS] = coordinates.positionInParent().y
                    }
            ) {
                LivingConditionsSection(
                    formState = formState,
                    onFormStateChange = { formState = it },
                    optionsViewModel = optionsViewModel,
                    expandedSections = expandedSections,
                    isFieldInvalid = ::isFieldInvalid,
                    showOnlyRequiredFields = showOnlyRequiredFields
                )
            }
            
            // Секции типа аренды с отслеживанием позиции
            if (formState.isLongTerm) {
                Box(
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            sectionPositions[PropertySection.LONG_TERM_RENTAL] = coordinates.positionInParent().y
                        }
                ) {
                    LongTermRentalSection(
                        formState = formState,
                        onFormStateChange = { formState = it },
                        expandedSections = expandedSections,
                        isFieldInvalid = ::isFieldInvalid,
                        showOnlyRequiredFields = showOnlyRequiredFields
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            sectionPositions[PropertySection.SHORT_TERM_RENTAL] = coordinates.positionInParent().y
                        }
                ) {
                    ShortTermRentalSection(
                        formState = formState,
                        onFormStateChange = { formState = it },
                        expandedSections = expandedSections,
                        isFieldInvalid = ::isFieldInvalid,
                        showOnlyRequiredFields = showOnlyRequiredFields
                    )
                }
            }
            
            // Секция медиа с отслеживанием позиции
            Box(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        sectionPositions[PropertySection.MEDIA] = coordinates.positionInParent().y
                    }
            ) {
                MediaSection(
                    formState = formState,
                    onFormStateChange = { formState = it },
                    expandedSections = expandedSections,
                    isFieldInvalid = ::isFieldInvalid,
                    showOnlyRequiredFields = showOnlyRequiredFields
                )
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
                            propertyViewModel.addProperty(formState.toProperty())
                            Toast.makeText(context, "Объект сохранен", Toast.LENGTH_SHORT).show()
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
                
                // Кнопка "Сохранить+"
                Button(
                    onClick = {
                        if (validateForm()) {
                            propertyViewModel.addProperty(formState.toProperty())
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
                    Text("Сохранить+")
                }
            }
        }
    }
} 