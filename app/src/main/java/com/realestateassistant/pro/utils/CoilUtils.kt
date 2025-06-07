package com.realestateassistant.pro.utils

import android.content.Context
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.decode.DataSource
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import coil.size.Size
import coil.transform.Transformation
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import coil.ComponentRegistry
import android.util.Log
import androidx.core.graphics.drawable.toDrawable
import coil.decode.DecodeResult
import coil.decode.Decoder
import coil.fetch.SourceResult
import coil.request.Options
import coil.size.pxOrElse
import coil.transition.CrossfadeTransition
import okio.Buffer
import okio.BufferedSource
import okio.Source
import kotlin.math.max
import kotlin.math.min
import coil.request.ErrorResult
import coil.request.SuccessResult

private const val TAG = "CoilUtils"
private const val MAX_IMAGE_DIMENSION = 2048

/**
 * Утилиты для работы с Coil - библиотекой загрузки изображений
 */
object CoilUtils {
    
    /**
     * Создает оптимизированный ImageLoader для загрузки изображений
     * @param context Контекст приложения
     * @param diskCacheSize Размер дискового кэша в байтах (по умолчанию 200 МБ)
     * @param memoryCacheSize Размер кэша в памяти в байтах (по умолчанию 20% доступной памяти)
     * @return Настроенный ImageLoader
     */
    fun createImageLoader(
        context: Context,
        diskCacheSize: Long = 200 * 1024 * 1024, // 200 МБ
        memoryCacheSize: Int? = null // null - использовать значение по умолчанию
    ): ImageLoader {
        try {
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
                        .maxSizePercent(0.15) // Уменьшаем до 15% доступной памяти
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
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .writeTimeout(20, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                        .build()
                }
                // Настройка анимации и производительности
                .crossfade(true)
                .crossfade(200) // Уменьшаем до 200мс для более быстрого перехода
                .allowRgb565(true) // Экономия памяти за счет меньшего качества 
                .allowHardware(true) // Аппаратное ускорение
                .respectCacheHeaders(false) // Игнорируем заголовки для локальных файлов
                .dispatcher(Dispatchers.IO)
                // Добавляем собственные компоненты
                .components(createComponents(context))
                .build()
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка создания ImageLoader: ${e.message}", e)
            // Возвращаем базовый ImageLoader в случае ошибки
            return ImageLoader.Builder(context)
                .allowHardware(true)
                .build()
        }
    }
    
    /**
     * Создает запрос на загрузку изображения с оптимальными настройками
     * @param context Контекст приложения
     * @param data Данные для загрузки (URI, URL, путь к файлу и т.д.)
     * @param size Размер изображения (по умолчанию оригинальный)
     * @param precision Точность масштабирования
     * @return Настроенный ImageRequest.Builder
     */
    fun createImageRequest(
        context: Context,
        data: Any,
        size: Size = Size.ORIGINAL,
        diskCachePolicy: CachePolicy = CachePolicy.ENABLED,
        memoryCachePolicy: CachePolicy = CachePolicy.ENABLED,
        transformations: List<Transformation>? = null,
        precision: Precision = Precision.AUTOMATIC
    ): ImageRequest.Builder {
        try {
            val builder = ImageRequest.Builder(context)
                .data(data)
                .size(size)
                .diskCachePolicy(diskCachePolicy)
                .memoryCachePolicy(memoryCachePolicy)
                .crossfade(true)
                .precision(precision)
                .scale(Scale.FILL)
                .placeholderMemoryCacheKey(getCacheKey(data))
                .dispatcher(Dispatchers.IO)
                .listener(
                    onError = { _, result ->
                        Log.e(TAG, "Ошибка загрузки изображения: ${result.throwable.message}")
                    },
                    onSuccess = { _, _ ->
                        // Успешная загрузка
                    }
                )
                
            // Применяем трансформации, если они предоставлены
            transformations?.forEach { transformation ->
                builder.transformations(transformation)
            }
            
            return builder
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка создания ImageRequest: ${e.message}", e)
            // Возвращаем базовый ImageRequest в случае ошибки
            return ImageRequest.Builder(context)
                .data(data)
                .dispatcher(Dispatchers.IO)
        }
    }
    
    /**
     * Создает запрос на предзагрузку изображения только в дисковый кэш
     * @param context Контекст приложения
     * @param data Данные для загрузки (URI, URL, путь к файлу и т.д.)
     * @return Настроенный ImageRequest
     */
    fun createPreloadRequest(
        context: Context,
        data: Any
    ): ImageRequest {
        return createImageRequest(context, data)
            .memoryCachePolicy(CachePolicy.DISABLED) // Не использовать память
            .diskCachePolicy(CachePolicy.ENABLED) // Использовать дисковый кэш
            .size(MAX_IMAGE_DIMENSION / 2, MAX_IMAGE_DIMENSION / 2) // Загружаем в уменьшенном размере
            .build()
    }
    
    /**
     * Создает запрос на загрузку изображения для миниатюры с оптимизированными настройками
     * @param context Контекст приложения
     * @param data Данные для загрузки (URI, URL, путь к файлу и т.д.)
     * @param thumbnailSize Размер миниатюры
     * @return Настроенный ImageRequest.Builder
     */
    fun createThumbnailRequest(
        context: Context,
        data: Any,
        thumbnailSize: Int = 100
    ): ImageRequest.Builder {
        return ImageRequest.Builder(context)
            .data(data)
            .size(thumbnailSize, thumbnailSize)
            .precision(Precision.EXACT) // Точное соответствие размеру для кэширования
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .crossfade(true)
            .allowHardware(false) // Отключаем для экономии памяти на миниатюрах
            .allowRgb565(true) // Используем RGB565 для экономии памяти
            .dispatcher(Dispatchers.IO)
    }
    
    /**
     * Создает ключ кэша для изображения
     * @param data Данные изображения
     * @return Строковый ключ или null
     */
    private fun getCacheKey(data: Any): String? {
        return when (data) {
            is String -> data
            else -> data.toString()
        }
    }

    /**
     * Создает свой FileKeyer для оптимизации работы с файлами
     * Выполняет операции с файловой системой в фоновом потоке
     */
    class AsyncFileKeyer : coil.key.Keyer<File> {
        override fun key(data: File, options: coil.request.Options): String {
            // Используем только путь файла без lastModified для предотвращения 
            // блокирующих операций чтения с диска в главном потоке
            return "file://${data.path}"
        }
    }

    /**
     * Создает оптимизированные компоненты для работы с файловой системой
     * @param context Контекст приложения
     * @return Набор компонентов для Coil
     */
    fun createComponents(context: Context): ComponentRegistry {
        return ComponentRegistry.Builder()
            // Регистрируем собственный FileKeyer для асинхронной работы с файлами
            .add(AsyncFileKeyer(), File::class.java)
            .build()
    }
} 