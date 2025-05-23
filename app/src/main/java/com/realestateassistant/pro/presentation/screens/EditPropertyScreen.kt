package com.realestateassistant.pro.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
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
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Основное содержимое формы
                        // Новый селектор типа аренды вместо сворачиваемого раздела
                        RentalTypeSelector(
                            isLongTerm = formState.isLongTerm,
                            onIsLongTermChange = { formState = formState.copy(isLongTerm = it) }
                        )
                        
                        ContactInfoSection(
                            formState = formState,
                            onFormStateChange = { formState = it },
                            expandedSections = expandedSections
                        )
                        
                        PropertyInfoSection(
                            formState = formState,
                            onFormStateChange = { formState = it },
                            optionsViewModel = optionsViewModel,
                            propertyTypes = propertyTypes,
                            districts = districts,
                            layouts = layouts,
                            expandedSections = expandedSections,
                            characteristicsConfig = currentCharacteristicsConfig
                        )
                        
                        PropertyCharacteristicsSection(
                            formState = formState,
                            onFormStateChange = { formState = it },
                            optionsViewModel = optionsViewModel,
                            repairStates = repairStates,
                            bathroomTypes = bathroomTypes,
                            heatingTypes = heatingTypes,
                            parkingTypes = parkingTypes,
                            expandedSections = expandedSections
                        )
                        
                        LivingConditionsSection(
                            formState = formState,
                            onFormStateChange = { formState = it },
                            optionsViewModel = optionsViewModel,
                            expandedSections = expandedSections
                        )
                        
                        if (formState.isLongTerm) {
                            LongTermRentalSection(
                                formState = formState,
                                onFormStateChange = { formState = it },
                                expandedSections = expandedSections
                            )
                        } else {
                            ShortTermRentalSection(
                                formState = formState,
                                onFormStateChange = { formState = it },
                                expandedSections = expandedSections
                            )
                        }
                        
                        MediaSection(
                            formState = formState,
                            onFormStateChange = { formState = it },
                            expandedSections = expandedSections
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