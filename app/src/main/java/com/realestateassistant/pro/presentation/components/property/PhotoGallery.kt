package com.realestateassistant.pro.presentation.components.property

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
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.realestateassistant.pro.utils.CoilUtils

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoGallery(photos: List<String>, modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = { photos.size })
    val context = LocalContext.current
    
    // Создаем оптимизированный ImageLoader для загрузки изображений
    val imageLoader = remember {
        CoilUtils.createImageLoader(context)
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
                modifier = Modifier.fillMaxSize()
            ) { page ->
                SubcomposeAsyncImage(
                    model = CoilUtils.createImageRequest(
                            context,
                            photos[page]
                        ).build(),
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
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BrokenImage,
                                        contentDescription = "Ошибка загрузки",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Не удалось загрузить изображение",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                        // Успешно загруженное изображение
                        else -> {
                            SubcomposeAsyncImageContent()
                        }
                    }
                }
            }
            
            // Градиентная подложка внизу
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
                    repeat(photos.size) { index ->
                        val size = if (pagerState.currentPage == index) 8.dp else 6.dp
                        val color = if (pagerState.currentPage == index) {
                            Color.White
                        } else {
                            Color.White.copy(alpha = 0.4f)
                        }
                        
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(size)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }
            }
            
            // Индикатор количества фото
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${pagerState.currentPage + 1}/${photos.size}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
    
    // Полноэкранный просмотр фото
    if (showFullscreen) {
        FullscreenPhotoViewer(
            photos = photos,
            initialIndex = selectedPhotoIndex,
            onDismiss = { showFullscreen = false },
            imageLoader = imageLoader
        )
    }
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
    imageLoader: coil.ImageLoader? = null
) {
    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { photos.size }
    )
    val context = LocalContext.current
    
    // Создаем оптимизированный ImageLoader, если он не был передан
    val finalImageLoader = imageLoader ?: remember {
        CoilUtils.createImageLoader(context)
    }
    
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Пейджер для изображений
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                SubcomposeAsyncImage(
                    model = CoilUtils.createImageRequest(
                            context,
                            photos[page]
                        ).build(),
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
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BrokenImage,
                                        contentDescription = "Ошибка загрузки",
                                        tint = Color.White,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Не удалось загрузить изображение",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        // Успешно загруженное изображение
                        else -> {
                            SubcomposeAsyncImageContent()
                        }
                    }
                }
            }
            
            // Кнопка закрытия
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .size(36.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Закрыть",
                    tint = Color.White
                )
            }
            
            // Индикатор страниц
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .wrapContentSize()
            ) {
                Surface(
                    modifier = Modifier
                        .padding(8.dp)
                        .wrapContentSize(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(8.dp)
                            .wrapContentSize()
                    ) {
                        Text(
                            text = "${pagerState.currentPage + 1}/${photos.size}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Создаем свой индикатор
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .padding(8.dp)
                                .wrapContentSize()
                        ) {
                            val maxVisibleDots = 9
                            val visibleCount = minOf(photos.size, maxVisibleDots)
                            
                            if (photos.size <= maxVisibleDots) {
                                // Показываем все индикаторы
                                repeat(photos.size) { index ->
                                    PageIndicator(
                                        isSelected = index == pagerState.currentPage,
                                        activeColor = MaterialTheme.colorScheme.primary,
                                        inactiveColor = Color.White.copy(alpha = 0.3f)
                                    )
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
                                }
                                
                                // Показываем видимые индикаторы
                                for (i in startIndex until endIndex) {
                                    PageIndicator(
                                        isSelected = i == pagerState.currentPage,
                                        activeColor = MaterialTheme.colorScheme.primary,
                                        inactiveColor = Color.White.copy(alpha = 0.3f)
                                    )
                                }
                                
                                // Показываем троеточие в конце, если нужно
                                if (endIndex < photos.size) {
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
        }
    }
}

@Composable
fun Dialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismissRequest,
        properties = androidx.compose.ui.window.DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        content()
    }
}

@Composable
fun PageIndicator(
    isSelected: Boolean,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = Color.White.copy(alpha = 0.3f)
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .size(if (isSelected) 8.dp else 6.dp)
            .clip(CircleShape)
            .background(if (isSelected) activeColor else inactiveColor)
    )
} 