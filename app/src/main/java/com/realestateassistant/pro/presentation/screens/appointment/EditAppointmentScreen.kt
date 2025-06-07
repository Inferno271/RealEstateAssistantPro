package com.realestateassistant.pro.presentation.screens.appointment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.presentation.screens.appointment.components.AppointmentDialog
import com.realestateassistant.pro.presentation.screens.appointment.models.AppointmentEvent
import com.realestateassistant.pro.ui.theme.PrimaryContainer
import com.realestateassistant.pro.ui.theme.OnPrimaryContainer

/**
 * Экран для редактирования встречи
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAppointmentScreen(
    appointmentId: String,
    onNavigateBack: () -> Unit,
    viewModel: AppointmentViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // Загружаем информацию о клиентах, объектах и конкретной встрече
    LaunchedEffect(key1 = appointmentId) {
        viewModel.handleEvent(AppointmentEvent.LoadClients)
        viewModel.handleEvent(AppointmentEvent.LoadProperties)
        viewModel.handleEvent(AppointmentEvent.LoadAppointment(appointmentId))
        // Инициализируем форму для редактирования встречи
        viewModel.handleEvent(AppointmentEvent.ShowEditDialog(appointmentId))
    }
    
    // Если встреча успешно обновлена или удалена, возвращаемся к предыдущему экрану
    LaunchedEffect(key1 = state.saveSuccess) {
        if (state.saveSuccess) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактирование встречи") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    // Кнопка удаления
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Удалить",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    
                    // Кнопка сохранения
                    IconButton(onClick = { viewModel.handleEvent(AppointmentEvent.SaveAppointment) }) {
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
                isEditMode = true,
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
    
    // Диалог подтверждения удаления
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Подтверждение удаления") },
            text = { Text("Вы уверены, что хотите удалить эту встречу?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.handleEvent(AppointmentEvent.DeleteAppointment(appointmentId))
                        showDeleteDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
} 