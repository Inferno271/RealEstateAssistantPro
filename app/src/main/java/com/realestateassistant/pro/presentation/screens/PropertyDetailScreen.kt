package com.realestateassistant.pro.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.presentation.components.property.PropertyDetailContent
import com.realestateassistant.pro.presentation.viewmodel.PropertyViewModel
import com.realestateassistant.pro.presentation.viewmodel.PdfExportViewModel
import com.realestateassistant.pro.domain.model.Booking
import com.realestateassistant.pro.presentation.components.property.PropertyAvailabilityInfo
import com.realestateassistant.pro.presentation.viewmodels.BookingCalendarViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(
    propertyId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToBookingCalendar: (String) -> Unit,
    viewModel: PropertyViewModel = hiltViewModel(),
    bookingViewModel: BookingCalendarViewModel = hiltViewModel(),
    pdfExportViewModel: PdfExportViewModel = hiltViewModel()
) {
    val selectedProperty by viewModel.selectedProperty.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current
    
    // Состояние бронирований объекта
    var bookings by remember { mutableStateOf<List<Booking>>(emptyList()) }
    
    // Состояние диалога подтверждения удаления
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    
    // Состояние экспорта PDF
    val pdfExportState by pdfExportViewModel.exportState.collectAsState()
    
    // Загружаем детали объекта при входе на экран
    LaunchedEffect(propertyId) {
        viewModel.loadPropertyDetails(propertyId)
        
        // Инициализируем загрузку бронирований для этого объекта
        bookingViewModel.onEvent(com.realestateassistant.pro.presentation.viewmodels.BookingCalendarEvent.SelectProperty(propertyId))
    }
    
    // Наблюдаем за бронированиями объекта
    LaunchedEffect(propertyId) {
        bookingViewModel.state.collectLatest { state ->
            bookings = state.bookings
        }
    }
    
    // Очищаем выбранный объект при выходе с экрана
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSelectedProperty()
            pdfExportViewModel.resetState() // Сбрасываем состояние экспорта PDF
        }
    }
    
    // Обработка состояния экспорта PDF
    LaunchedEffect(pdfExportState) {
        when (pdfExportState) {
            is PdfExportViewModel.ExportState.Success -> {
                val uri = (pdfExportState as PdfExportViewModel.ExportState.Success).uri
                
                // Отображаем снэкбар с возможностью открыть или поделиться файлом
                // (Этот блок будет реализован через SnackbarHost)
            }
            is PdfExportViewModel.ExportState.Error -> {
                val errorMessage = (pdfExportState as PdfExportViewModel.ExportState.Error).message
                Toast.makeText(context, "Ошибка при создании PDF: $errorMessage", Toast.LENGTH_LONG).show()
            }
            else -> {
                // Не требуется действий
            }
        }
    }
    
    // Диалог подтверждения удаления
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Подтверждение удаления") },
            text = { Text("Вы действительно хотите удалить этот объект недвижимости? Это действие невозможно отменить.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProperty(propertyId)
                        showDeleteConfirmDialog = false
                        Toast.makeText(context, "Объект удален", Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
    
    // Создаем SnackbarHostState для отображения сообщений
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Обработка состояния экспорта PDF для отображения снэкбара
    LaunchedEffect(pdfExportState) {
        if (pdfExportState is PdfExportViewModel.ExportState.Success) {
            val uri = (pdfExportState as PdfExportViewModel.ExportState.Success).uri
            val result = snackbarHostState.showSnackbar(
                message = "PDF сохранен",
                actionLabel = "Открыть",
                duration = SnackbarDuration.Long
            )
            
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    selectedProperty?.let { property ->
                        pdfExportViewModel.openPdf(context, uri)
                    }
                }
                else -> { /* Игнорируем другие результаты */ }
            }
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = selectedProperty?.propertyType ?: "Детали объекта", 
                        style = MaterialTheme.typography.titleLarge
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    // Кнопка экспорта PDF
                    IconButton(
                        onClick = { 
                            selectedProperty?.let { property ->
                                pdfExportViewModel.exportPropertyToPdf(property)
                            }
                        },
                        enabled = selectedProperty != null && pdfExportState !is PdfExportViewModel.ExportState.Loading
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "Экспорт в PDF",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // Индикатор загрузки PDF
                    if (pdfExportState is PdfExportViewModel.ExportState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    
                    // Кнопка календаря бронирований
                    IconButton(
                        onClick = { onNavigateToBookingCalendar(propertyId) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Календарь бронирований",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                
                    // Кнопка редактирования
                    IconButton(
                        onClick = { onNavigateToEdit(propertyId) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Редактировать",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // Кнопка удаления
                    IconButton(
                        onClick = { showDeleteConfirmDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Удалить",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
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
                    // Индикатор загрузки
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    // Отображаем ошибку
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Ошибка загрузки: ${error}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { viewModel.loadPropertyDetails(propertyId) }
                        ) {
                            Text("Повторить")
                        }
                    }
                }
                selectedProperty != null -> {
                    // Отображаем детали объекта с информацией о доступности
                    Column {
                        PropertyDetailContent(property = selectedProperty!!)
                        
                        // Информация о доступности объекта
                        Spacer(modifier = Modifier.height(16.dp))
                        PropertyAvailabilityInfo(
                            property = selectedProperty!!,
                            currentBookings = bookings,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        
                        // Кнопки действий с объектом
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Кнопка для календаря бронирований
                            Button(
                                onClick = { onNavigateToBookingCalendar(propertyId) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("Календарь бронирований")
                            }
                            
                            // Кнопка для экспорта PDF
                            Button(
                                onClick = { 
                                    selectedProperty?.let { property ->
                                        pdfExportViewModel.exportPropertyToPdf(property)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = selectedProperty != null && pdfExportState !is PdfExportViewModel.ExportState.Loading
                            ) {
                                if (pdfExportState is PdfExportViewModel.ExportState.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.PictureAsPdf,
                                        contentDescription = null,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text("Экспорт в PDF")
                                }
                            }
                        }
                        
                        // Кнопка для поделиться PDF (появляется только когда PDF уже создан)
                        if (pdfExportState is PdfExportViewModel.ExportState.Success) {
                            val uri = (pdfExportState as PdfExportViewModel.ExportState.Success).uri
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { 
                                    selectedProperty?.let { property ->
                                        pdfExportViewModel.sharePdf(context, uri, property)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("Поделиться PDF")
                            }
                        }
                    }
                }
                else -> {
                    // Отображаем сообщение об отсутствии данных
                    Text(
                        text = "Объект не найден",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
} 