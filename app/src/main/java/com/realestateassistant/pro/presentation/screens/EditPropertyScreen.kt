package com.realestateassistant.pro.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.presentation.components.RentalTypeSelector
import com.realestateassistant.pro.presentation.model.PropertyFormState
import com.realestateassistant.pro.presentation.model.PropertySection
import com.realestateassistant.pro.presentation.model.createInitialSectionState
import com.realestateassistant.pro.presentation.screens.property.*
import com.realestateassistant.pro.presentation.viewmodel.OptionsViewModel
import com.realestateassistant.pro.presentation.viewmodel.PropertyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPropertyScreen(
    propertyId: String,
    onNavigateBack: () -> Unit,
    propertyViewModel: PropertyViewModel = hiltViewModel(),
    optionsViewModel: OptionsViewModel = hiltViewModel()
) {
    val selectedProperty by propertyViewModel.selectedProperty.collectAsState()
    val isLoading by propertyViewModel.isLoading.collectAsState()
    val error by propertyViewModel.error.collectAsState()
    val context = LocalContext.current
    
    // Загружаем детали объекта при входе на экран
    LaunchedEffect(propertyId) {
        propertyViewModel.loadPropertyDetails(propertyId)
    }
    
    // Состояние для формы - инициализируем из выбранного объекта
    var formState by remember { mutableStateOf(PropertyFormState()) }
    
    // Обновляем formState, когда объект загружен
    LaunchedEffect(selectedProperty) {
        selectedProperty?.let {
            formState = PropertyFormState.fromProperty(it)
        }
    }
    
    // Состояние для отслеживания, какие секции развернуты
    val expandedSections = remember { createInitialSectionState() }
    
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
    
    // Обновляем конфигурацию при изменении типа недвижимости
    LaunchedEffect(formState.propertyType) {
        if (formState.propertyType.isNotEmpty()) {
            propertyViewModel.updateCharacteristicsConfig(formState.propertyType)
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { 
                        Text(
                            "Редактировать объект",
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
                
                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    // Отображаем индикатор загрузки
                    CircularProgressIndicator(
                        modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                    )
                }
                error != null -> {
                    // Отображаем ошибку
                    Text(
                        text = "Ошибка: ${error ?: "Не удалось загрузить объект"}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(androidx.compose.ui.Alignment.Center)
                            .padding(16.dp)
                    )
                }
                selectedProperty == null -> {
                    // Если объект не найден
                    Text(
                        text = "Объект не найден",
                        modifier = Modifier
                            .align(androidx.compose.ui.Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    // Содержимое формы редактирования
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        RentalTypeSelector(
                            isLongTerm = formState.isLongTerm,
                            onIsLongTermChange = { formState = formState.copy(isLongTerm = it) }
                        )
                        
                        ContactInfoSection(
                            formState = formState,
                            onFormStateChange = { formState = it },
                            expandedSections = expandedSections,
                            isFieldInvalid = { false },
                            showOnlyRequiredFields = showOnlyRequiredFields
                        )
                        
                        PropertyInfoSection(
                            formState = formState,
                            onFormStateChange = { formState = it },
                            optionsViewModel = optionsViewModel,
                            propertyTypes = propertyTypes,
                            districts = districts,
                            layouts = layouts,
                            expandedSections = expandedSections,
                            characteristicsConfig = currentCharacteristicsConfig,
                            isFieldInvalid = { false },
                            showOnlyRequiredFields = showOnlyRequiredFields
                        )
                        
                        PropertyCharacteristicsSection(
                            formState = formState,
                            onFormStateChange = { formState = it },
                            optionsViewModel = optionsViewModel,
                            repairStates = repairStates,
                            bathroomTypes = bathroomTypes,
                            heatingTypes = heatingTypes,
                            parkingTypes = parkingTypes,
                            expandedSections = expandedSections,
                            isFieldInvalid = { false },
                            showOnlyRequiredFields = showOnlyRequiredFields
                        )
                        
                        LivingConditionsSection(
                            formState = formState,
                            onFormStateChange = { formState = it },
                            optionsViewModel = optionsViewModel,
                            expandedSections = expandedSections,
                            isFieldInvalid = { false },
                            showOnlyRequiredFields = showOnlyRequiredFields
                        )
                        
                        if (formState.isLongTerm) {
                            LongTermRentalSection(
                                formState = formState,
                                onFormStateChange = { formState = it },
                                expandedSections = expandedSections,
                                isFieldInvalid = { false },
                                showOnlyRequiredFields = showOnlyRequiredFields
                            )
                        } else {
                            ShortTermRentalSection(
                                formState = formState,
                                onFormStateChange = { formState = it },
                                expandedSections = expandedSections,
                                isFieldInvalid = { false },
                                showOnlyRequiredFields = showOnlyRequiredFields
                            )
                        }
                        
                        MediaSection(
                            formState = formState,
                            onFormStateChange = { formState = it },
                            expandedSections = expandedSections,
                            isFieldInvalid = { false },
                            showOnlyRequiredFields = showOnlyRequiredFields
                        )
                        
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
                                    // Создаем объект с существующим id
                                    val updatedProperty = formState.toProperty().copy(id = propertyId)
                                    
                                    // Сохраняем изменения
                                    propertyViewModel.updateProperty(updatedProperty)
                                    Toast.makeText(context, "Изменения сохранены", Toast.LENGTH_SHORT).show()
                                    onNavigateBack()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Сохранить")
                            }
                            
                            // Кнопка "Отмена"
                            OutlinedButton(
                                onClick = onNavigateBack,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Отмена")
                            }
                        }
                    }
                }
            }
        }
    }
} 