package com.realestateassistant.pro.presentation.screens.appointment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.presentation.screens.appointment.components.AppointmentDialog
import com.realestateassistant.pro.presentation.screens.appointment.models.AppointmentEvent
import com.realestateassistant.pro.ui.theme.PrimaryContainer
import com.realestateassistant.pro.ui.theme.OnPrimaryContainer

/**
 * Экран для добавления новой встречи
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppointmentScreen(
    onNavigateBack: () -> Unit,
    viewModel: AppointmentViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    // Загружаем информацию о клиентах и объектах недвижимости
    LaunchedEffect(key1 = Unit) {
        viewModel.handleEvent(AppointmentEvent.LoadClients)
        viewModel.handleEvent(AppointmentEvent.LoadProperties)
        // Инициализируем форму для создания новой встречи
        viewModel.handleEvent(AppointmentEvent.ShowCreateDialog)
    }
    
    // Если встреча успешно создана, возвращаемся к предыдущему экрану
    LaunchedEffect(key1 = state.saveSuccess) {
        if (state.saveSuccess) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новая встреча") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    // Кнопка сохранения
                    IconButton(
                        onClick = { viewModel.handleEvent(AppointmentEvent.SaveAppointment) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Сохранить",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryContainer,
                    titleContentColor = OnPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            AppointmentDialog(
                isEditMode = false,
                formState = state.formState,
                clients = state.availableClients,
                properties = state.availableProperties,
                onEvent = { dialogEvent -> 
                    viewModel.handleEvent(AppointmentEvent.OnDialogEvent(dialogEvent))
                },
                onDismiss = onNavigateBack
            )
        }
    }
} 