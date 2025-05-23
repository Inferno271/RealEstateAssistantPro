package com.realestateassistant.pro.presentation.screens.property

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.presentation.components.ExpandablePropertyCard
import com.realestateassistant.pro.presentation.components.DocumentItem
import com.realestateassistant.pro.presentation.components.PhotoGalleryViewer
import com.realestateassistant.pro.presentation.components.PhotoThumbnail
import com.realestateassistant.pro.presentation.components.common.ErrorDialog
import com.realestateassistant.pro.presentation.model.PropertyFormState
import com.realestateassistant.pro.presentation.model.PropertySection
import com.realestateassistant.pro.presentation.viewmodel.ImageViewModel
import com.realestateassistant.pro.presentation.viewmodels.DocumentViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaSection(
    formState: PropertyFormState,
    onFormStateChange: (PropertyFormState) -> Unit,
    expandedSections: MutableMap<PropertySection, Boolean>,
    imageViewModel: ImageViewModel = hiltViewModel(),
    documentViewModel: DocumentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Состояние загрузки и ошибок из ImageViewModel
    val isLoading by imageViewModel.isLoading.collectAsState()
    val error by imageViewModel.error.collectAsState()
    
    // Состояние для отображения галереи
    var showGallery by remember { mutableStateOf(false) }
    var initialPhotoUri by remember { mutableStateOf("") }
    
    // Локальное состояние для сообщения об ошибке
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Состояние для отображения прогресса загрузки документов
    var isDocumentLoading by remember { mutableStateOf(false) }
    
    // Эффект для отображения ошибок из ViewModel
    LaunchedEffect(error) {
        if (error != null) {
            errorMessage = error
            // Очищаем ошибку в ViewModel
            imageViewModel.clearError()
        }
    }
    
    // Запуск галереи для выбора изображений
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            // Показываем прогресс и сохраняем изображения через ViewModel
            coroutineScope.launch {
                imageViewModel.saveImages(uris).onSuccess { paths ->
                    onFormStateChange(formState.copy(photos = formState.photos + paths))
                }
            }
        }
    }
    
    // Запуск файлового менеджера для выбора документов
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            isDocumentLoading = true
            coroutineScope.launch {
                val savedDocuments = mutableListOf<String>()
                
                uris.forEach { uri ->
                    documentViewModel.saveDocument(uri).onSuccess { path ->
                        savedDocuments.add(path)
                    }.onFailure { exception ->
                        errorMessage = "Ошибка при сохранении документа: ${exception.message}"
                    }
                }
                
                if (savedDocuments.isNotEmpty()) {
                    onFormStateChange(formState.copy(documents = formState.documents + savedDocuments))
                }
                
                isDocumentLoading = false
            }
        }
    }
    
    // Для Android 14+ (API 34+): Launcher для выбора доступа к конкретным фотографиям
    val selectImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            coroutineScope.launch {
                imageViewModel.saveImages(uris).onSuccess { paths ->
                    onFormStateChange(formState.copy(photos = formState.photos + paths))
                }
            }
        }
    }
    
    // Запрос разрешения на доступ к файлам для документов
    val requestDocumentPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Если разрешение получено, открываем файловый менеджер
            documentPickerLauncher.launch("*/*") // Любой тип файла
        } else {
            Toast.makeText(
                context,
                "Необходимо разрешение для доступа к файлам",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    // Запрос разрешения на доступ к изображениям
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Если разрешение получено, открываем галерею с учетом версии Android
            if (android.os.Build.VERSION.SDK_INT >= 34) { // Android 14+
                // Для Android 14+ используем новый подход без запроса разрешений
                selectImagesLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            } else {
                // Для других версий запускаем выбор контента
                imagePickerLauncher.launch("image/*")
            }
        } else {
            Toast.makeText(
                context,
                "Необходимо разрешение для доступа к фотографиям",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    ExpandablePropertyCard(
        title = "Медиафайлы",
        sectionKey = PropertySection.MEDIA,
        expandedSections = expandedSections
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .heightIn(min = 0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Фотографии",
                style = MaterialTheme.typography.bodyLarge
            )
            
            // Отображение выбранных фотографий, если они есть
            if (formState.photos.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .heightIn(min = 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(formState.photos) { photoUri ->
                        PhotoThumbnail(
                            photoUri = photoUri,
                            onDeleteClick = {
                                onFormStateChange(formState.copy(photos = formState.photos - photoUri))
                            },
                            allPhotos = formState.photos,
                            onShowFullscreen = { uri, photos ->
                                initialPhotoUri = uri
                                showGallery = true
                            }
                        )
                    }
                }
            }
            
            Button(
                onClick = { 
                    // Проверяем разрешение на доступ к изображениям в зависимости от версии Android
                    if (android.os.Build.VERSION.SDK_INT >= 34) { // Android 14+
                        // Для Android 14+ используем новый подход без запроса разрешений
                        selectImagesLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        // Для Android 13 (API 33) используем READ_MEDIA_IMAGES
                        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    } else {
                        // Для старых версий используем READ_EXTERNAL_STORAGE
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Добавить фото")
            }
            
            Text(
                text = "Документы",
                style = MaterialTheme.typography.bodyLarge
            )
            
            // Отображение выбранных документов, если они есть
            if (formState.documents.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .heightIn(min = 0.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    formState.documents.forEachIndexed { index, documentPath ->
                        DocumentItem(
                            documentUri = documentPath,
                            onDeleteClick = {
                                // Удаляем документ из хранилища и из списка
                                coroutineScope.launch {
                                    documentViewModel.deleteDocument(documentPath)
                                    onFormStateChange(formState.copy(documents = formState.documents - documentPath))
                                }
                            },
                            index = index
                        )
                    }
                }
            }
            
            Button(
                onClick = { 
                    // Запускаем выбор документов
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        // Для Android 13+ проверяем разрешение на чтение медиа
                        requestDocumentPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    } else {
                        // Для старых версий используем READ_EXTERNAL_STORAGE
                        requestDocumentPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Добавить документ")
            }
            
            // Добавляем отображение индикатора прогресса
            if (isLoading || isDocumentLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
    
    // Отображение полноэкранной галереи
    if (showGallery) {
        PhotoGalleryViewer(
            photos = formState.photos,
            initialPhoto = initialPhotoUri,
            onDismiss = { showGallery = false }
        )
    }
    
    // Отображение диалога с ошибкой
    if (errorMessage != null) {
        ErrorDialog(
            message = errorMessage ?: "Неизвестная ошибка",
            onDismiss = { errorMessage = null }
        )
    }
} 