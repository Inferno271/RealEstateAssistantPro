package com.realestateassistant.pro.utils

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Утилиты для работы с Coil - библиотекой загрузки изображений
 */
object CoilUtils {
    
    /**
     * Создает оптимизированный ImageLoader для загрузки изображений
     * @param context Контекст приложения
     * @param diskCacheSize Размер дискового кэша в байтах (по умолчанию 100 МБ)
     * @param memoryCacheSize Размер кэша в памяти в байтах (по умолчанию 25% доступной памяти)
     * @return Настроенный ImageLoader
     */
    fun createImageLoader(
        context: Context,
        diskCacheSize: Long = 100 * 1024 * 1024, // 100 МБ
        memoryCacheSize: Int? = null // null - использовать значение по умолчанию
    ): ImageLoader {
        return ImageLoader.Builder(context)
            // Настройка дискового кэша
            .diskCache {
                DiskCache.Builder()
                    .directory(File(context.cacheDir, "image_cache"))
                    .maxSizeBytes(diskCacheSize)
                    .build()
            }
            // Настройка кэша в памяти
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25) // 25% доступной памяти
                    .apply {
                        if (memoryCacheSize != null) {
                            maxSizeBytes(memoryCacheSize)
                        }
                    }
                    .build()
            }
            // Настройка HTTP клиента
            .okHttpClient {
                OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()
            }
            // Включаем кроссфейд для плавной анимации
            .crossfade(true)
            .build()
    }
    
    /**
     * Создает запрос на загрузку изображения с оптимальными настройками
     * @param context Контекст приложения
     * @param data Данные для загрузки (URI, URL, путь к файлу и т.д.)
     * @param size Размер изображения (по умолчанию оригинальный)
     * @param diskCachePolicy Политика кэширования на диске
     * @param memoryCachePolicy Политика кэширования в памяти
     * @return Настроенный ImageRequest.Builder
     */
    fun createImageRequest(
        context: Context,
        data: Any,
        size: Size = Size.ORIGINAL,
        diskCachePolicy: CachePolicy = CachePolicy.ENABLED,
        memoryCachePolicy: CachePolicy = CachePolicy.ENABLED
    ): ImageRequest.Builder {
        return ImageRequest.Builder(context)
            .data(data)
            .size(size)
            .diskCachePolicy(diskCachePolicy)
            .memoryCachePolicy(memoryCachePolicy)
            .crossfade(true)
    }
} 