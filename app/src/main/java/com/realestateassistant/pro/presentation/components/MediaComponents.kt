package com.realestateassistant.pro.presentation.components

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import java.io.File
import com.realestateassistant.pro.utils.CoilUtils

@Composable
fun PhotoThumbnail(
    photoUri: String,
    onDeleteClick: () -> Unit,
    allPhotos: List<String>,
    onShowFullscreen: (String, List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageLoader = remember { CoilUtils.createImageLoader(context) }
    
    Box(
        modifier = modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onShowFullscreen(photoUri, allPhotos) }
    ) {
        SubcomposeAsyncImage(
            model = CoilUtils.createImageRequest(
                    context,
                    photoUri
                ).build(),
            contentDescription = "Фото объекта",
            contentScale = ContentScale.Crop,
            imageLoader = imageLoader,
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
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
                // Обработка ошибки загрузки
                state is AsyncImagePainter.State.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BrokenImage,
                            contentDescription = "Ошибка загрузки",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                // Успешно загруженное изображение
                else -> {
                    SubcomposeAsyncImageContent()
                }
            }
        }
        
        // Кнопка удаления
        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(32.dp)
                .padding(4.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(50)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Удалить",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun DocumentItem(
    documentUri: String,
    onDeleteClick: () -> Unit,
    index: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val fileName = remember(documentUri) {
        try {
            val uri = Uri.parse(documentUri)
            val contentResolver = context.contentResolver
            
            when {
                uri.scheme == "content" -> {
                    // Пытаемся получить имя файла из метаданных контента
                    contentResolver.query(
                        uri,
                        null,
                        null,
                        null,
                        null
                    )?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val displayNameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                            if (displayNameIndex != -1) {
                                cursor.getString(displayNameIndex)
                            } else {
                                "Документ ${index + 1}"
                            }
                        } else {
                            "Документ ${index + 1}"
                        }
                    } ?: "Документ ${index + 1}"
                }
                uri.scheme == "file" -> {
                    // Для URI с схемой file берем последний сегмент пути
                    uri.lastPathSegment?.let { path ->
                        path.substringAfterLast('/')
                    } ?: "Документ ${index + 1}"
                }
                else -> "Документ ${index + 1}"
            }
        } catch (e: Exception) {
            "Документ ${index + 1}"
        }
    }
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = fileName,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(
                onClick = onDeleteClick
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoGalleryViewer(
    photos: List<String>,
    initialPhoto: String,
    onDismiss: () -> Unit
) {
    val initialPage = photos.indexOf(initialPhoto).coerceAtLeast(0)
    val pagerState = rememberPagerState(initialPage = initialPage) { photos.size }
    val context = LocalContext.current
    val imageLoader = remember { CoilUtils.createImageLoader(context) }
    
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
            // Используем HorizontalPager для свайпа изображений
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight()
            ) { page ->
                SubcomposeAsyncImage(
                    model = CoilUtils.createImageRequest(
                            context,
                            photos[page]
                        ).build(),
                    contentDescription = "Фото ${page + 1}/${photos.size}",
                    contentScale = ContentScale.Fit,
                    imageLoader = imageLoader,
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
private fun PageIndicator(
    isSelected: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(if (isSelected) 10.dp else 8.dp)
            .clip(CircleShape)
            .background(if (isSelected) activeColor else inactiveColor)
    )
} 