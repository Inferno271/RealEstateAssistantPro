package com.realestateassistant.pro.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun AddPropertyScreen(
    onNavigateBack: () -> Unit,
    propertyViewModel: PropertyViewModel = hiltViewModel(),
    optionsViewModel: OptionsViewModel = hiltViewModel()
) {
    var formState by remember { mutableStateOf(PropertyFormState()) }
    val context = LocalContext.current
    
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
        
        // Показываем уведомление
        Toast.makeText(context, "Объект сохранен, создание нового объекта", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
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
                            Icons.Default.ArrowBack, 
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                        propertyViewModel.addProperty(formState.toProperty())
                        Toast.makeText(context, "Объект сохранен", Toast.LENGTH_SHORT).show()
                        onNavigateBack()
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
                        propertyViewModel.addProperty(formState.toProperty())
                        resetFormWithContact()
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