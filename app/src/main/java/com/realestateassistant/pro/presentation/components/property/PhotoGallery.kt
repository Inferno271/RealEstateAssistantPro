package com.realestateassistant.pro.presentation.components.property

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Size
import coil.Coil
import coil.ImageLoader
import com.realestateassistant.pro.utils.CoilUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "PhotoGallery"

/**
 * Максимальное количество изображений для предварительной загрузки
 */
private const val MAX_PRELOAD_IMAGES = 3

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoGallery(photos: List<String>, modifier: Modifier = Modifier) {
    if (photos.isEmpty()) {
        EmptyGalleryPlaceholder(modifier)
        return
    }

    val pagerState = rememberPagerState(pageCount = { photos.size })
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Используем общий ImageLoader
    val imageLoader = remember { 
        Coil.imageLoader(context)
    }
    
    // Предварительно загружаем только первые несколько изображений с ограничением размера
    LaunchedEffect(photos) {
        withContext(Dispatchers.IO) {
            photos.take(MAX_PRELOAD_IMAGES).forEach { photoUri ->
                try {
                    val preloadRequest = CoilUtils.createPreloadRequest(context, photoUri)
                    imageLoader.enqueue(preloadRequest)
                    Log.d(TAG, "Предзагрузка изображения: $photoUri")
                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка при предзагрузке изображения: ${e.message}")
                }
            }
        }
    }
    
    // Состояние для отслеживания, показывать ли полноэкранный режим
    var showFullscreen by remember { mutableStateOf(false) }
    var selectedPhotoIndex by remember { mutableStateOf(0) }
    
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                key = { index -> "photo_${photos[index]}_$index" }, // Уникальный ключ с учетом содержимого и позиции
                pageSpacing = 2.dp
            ) { page ->
                // Загружаем текущую и следующую страницу для плавного просмотра
                LaunchedEffect(page) {
                    val nextPage = (page + 1) % photos.size
                    withContext(Dispatchers.IO) {
                        try {
                            val preloadRequest = CoilUtils.createPreloadRequest(context, photos[nextPage])
                            imageLoader.enqueue(preloadRequest)
                        } catch (e: Exception) {
                            Log.e(TAG, "Ошибка при подгрузке следующего изображения: ${e.message}")
                        }
                    }
                }
                
                // Создаем запрос на загрузку изображения с указанным размером и кэшированием
                val imageRequest = remember(photos[page]) {
                    CoilUtils.createImageRequest(
                        context = context,
                        data = photos[page],
                        precision = Precision.INEXACT, // Допускаем неточное масштабирование для производительности
                        size = Size(800, 600)  // Ограничиваем размер для экономии памяти
                    ).build()
                }
                
                SubcomposeAsyncImage(
                    model = imageRequest,
                    contentDescription = "Фото объекта",
                    contentScale = ContentScale.Crop,
                    imageLoader = imageLoader,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            selectedPhotoIndex = page
                            showFullscreen = true
                        }
                ) {
                    val state = painter.state
                    when {
                        // Отображение индикатора загрузки
                        state is AsyncImagePainter.State.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp),
                                    strokeWidth = 3.dp
                                )
                            }
                        }
                        // Обработка ошибки загрузки
                        state is AsyncImagePainter.State.Error -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.errorContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BrokenImage,
                                        contentDescription = "Ошибка загрузки",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Ошибка загрузки",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                        // Успешная загрузка - контент отображается автоматически
                        else -> SubcomposeAsyncImageContent()
                    }
                }
            }
            
            // Градиент в нижней части для лучшей видимости индикаторов
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.35f)
                            )
                        )
                    )
            )
            
            // Индикатор страниц
            if (photos.size > 1) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Ограничиваем количество видимых точек для экономии ресурсов
                    val visibleCount = minOf(7, photos.size)
                    
                    if (photos.size <= visibleCount) {
                        // Если изображений мало, показываем все точки
                        photos.forEachIndexed { index, _ ->
                            val isSelected = index == pagerState.currentPage
                            PageIndicator(
                                isSelected = isSelected,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            )
                            
                            if (index < photos.size - 1) {
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    } else {
                        // Если точек много, показываем только часть
                        val currentPage = pagerState.currentPage
                        val halfVisible = visibleCount / 2
                        
                        // Определяем диапазон видимых точек
                        val startIndex = if (currentPage <= halfVisible) {
                            0
                        } else if (currentPage >= photos.size - halfVisible) {
                            photos.size - visibleCount
                        } else {
                            currentPage - halfVisible
                        }
                        
                        val endIndex = (startIndex + visibleCount).coerceAtMost(photos.size)
                        
                        // Показываем троеточие в начале, если нужно
                        if (startIndex > 0) {
                            Text(
                                text = "...",
                                color = Color.White.copy(alpha = 0.5f),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        
                        // Показываем видимые индикаторы
                        for (i in startIndex until endIndex) {
                            val isSelected = i == pagerState.currentPage
                            PageIndicator(
                                isSelected = isSelected,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(i)
                                    }
                                }
                            )
                            
                            if (i < endIndex - 1) {
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                        
                        // Показываем троеточие в конце, если нужно
                        if (endIndex < photos.size) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "...",
                                color = Color.White.copy(alpha = 0.5f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
    
    if (showFullscreen) {
        FullscreenPhotoViewer(
            photos = photos,
            initialIndex = selectedPhotoIndex,
            onDismiss = { showFullscreen = false },
            imageLoader = imageLoader
        )
    }
}

@Composable
private fun EmptyGalleryPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Нет фотографий",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Компонент для отображения индикатора страницы
 */
@Composable
private fun PageIndicator(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(if (isSelected) 10.dp else 8.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) 
                    MaterialTheme.colorScheme.primary
                else 
                    Color.White.copy(alpha = 0.5f)
            )
            .clickable(onClick = onClick)
    )
}

/**
 * Компонент для полноэкранного просмотра изображений с возможностью листания
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FullscreenPhotoViewer(
    photos: List<String>,
    initialIndex: Int,
    onDismiss: () -> Unit,
    imageLoader: ImageLoader? = null
) {
    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { photos.size }
    )
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Используем глобальный ImageLoader, если он не был передан
    val finalImageLoader = imageLoader ?: remember { Coil.imageLoader(context) }
    
    // Предварительно загружаем только соседние изображения для текущей страницы
    LaunchedEffect(pagerState.currentPage) {
        withContext(Dispatchers.IO) {
            val pagesToPreload = listOf(
                (pagerState.currentPage - 1).coerceAtLeast(0),
                pagerState.currentPage,
                (pagerState.currentPage + 1).coerceAtMost(photos.size - 1)
            ).distinct().filter { it != pagerState.currentPage }
            
            pagesToPreload.forEach { index ->
                try {
                    // Используем меньший размер для предзагрузки
                    val preloadRequest = CoilUtils.createPreloadRequest(context, photos[index])
                    finalImageLoader.enqueue(preloadRequest)
                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка при предзагрузке изображения в полноэкранном режиме: ${e.message}")
                }
            }
        }
    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Пейджер для изображений
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                key = { index -> "fullscreen_${photos[index]}_$index" }
            ) { page ->
                // Оптимизированный запрос для полноэкранного просмотра
                val imageRequest = remember(photos[page]) {
                    CoilUtils.createImageRequest(
                        context = context,
                        data = photos[page],
                        size = Size.ORIGINAL,
                        precision = Precision.AUTOMATIC
                    ).build()
                }
                
                SubcomposeAsyncImage(
                    model = imageRequest,
                    contentDescription = "Фото объекта",
                    contentScale = ContentScale.Fit,
                    imageLoader = finalImageLoader,
                    modifier = Modifier.fillMaxSize()
                ) {
                    val state = painter.state
                    when {
                        // Отображение индикатора загрузки
                        state is AsyncImagePainter.State.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                        // Обработка ошибки загрузки
                        state is AsyncImagePainter.State.Error -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BrokenImage,
                                        contentDescription = "Ошибка загрузки",
                                        tint = Color.Red,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Ошибка загрузки изображения",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        // Успешная загрузка - контент отображается автоматически
                        else -> SubcomposeAsyncImageContent()
                    }
                }
            }
            
            // Кнопка закрытия
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(48.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Закрыть",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Индикатор текущей страницы
            if (photos.size > 1) {
                Text(
                    text = "${pagerState.currentPage + 1} / ${photos.size}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
} 