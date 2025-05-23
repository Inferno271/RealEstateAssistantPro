package com.realestateassistant.pro.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.realestateassistant.pro.domain.repository.ImageRepository

/**
 * Сохраняет изображение из URI
 */
class SaveImage(private val repository: ImageRepository) {
    suspend operator fun invoke(uri: Uri, compress: Boolean = true, quality: Int = 80): Result<String> {
        return repository.saveImage(uri, compress, quality)
    }
}

/**
 * Загружает изображение по пути
 */
class LoadImage(private val repository: ImageRepository) {
    suspend operator fun invoke(path: String): Result<Bitmap> {
        return repository.loadImage(path)
    }
}

/**
 * Удаляет изображение по пути
 */
class DeleteImage(private val repository: ImageRepository) {
    suspend operator fun invoke(path: String): Result<Unit> {
        return repository.deleteImage(path)
    }
}

/**
 * Проверяет валидность URI изображения
 */
class ValidateImageUri(private val repository: ImageRepository) {
    suspend operator fun invoke(uri: Uri): Boolean {
        return repository.isValidImageUri(uri)
    }
}

/**
 * Очищает кэш изображений
 */
class ClearImageCache(private val repository: ImageRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.clearImageCache()
    }
}

/**
 * Сохраняет список изображений из URI
 */
class SaveImages(private val repository: ImageRepository) {
    suspend operator fun invoke(uris: List<Uri>, compress: Boolean = true, quality: Int = 80): Result<List<String>> {
        val results = mutableListOf<String>()
        var error: Throwable? = null
        
        for (uri in uris) {
            repository.saveImage(uri, compress, quality)
                .onSuccess { path ->
                    results.add(path)
                }
                .onFailure { exception ->
                    error = exception
                    return@onFailure
                }
            
            if (error != null) {
                // Если произошла ошибка, удаляем все сохраненные до этого изображения
                for (path in results) {
                    repository.deleteImage(path)
                }
                return Result.failure(error!!)
            }
        }
        
        return Result.success(results)
    }
}

// Собираем все use case в контейнер
data class ImageUseCases(
    val saveImage: SaveImage,
    val saveImages: SaveImages,
    val loadImage: LoadImage,
    val deleteImage: DeleteImage,
    val validateImageUri: ValidateImageUri,
    val clearImageCache: ClearImageCache
) 