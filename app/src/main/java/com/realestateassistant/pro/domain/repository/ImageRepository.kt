package com.realestateassistant.pro.domain.repository

import android.graphics.Bitmap
import android.net.Uri

/**
 * Репозиторий для работы с изображениями.
 * Отвечает за сохранение, загрузку и удаление изображений.
 */
interface ImageRepository {
    /**
     * Сохраняет изображение из переданного URI.
     * @param uri URI изображения
     * @param compress Нужно ли сжимать изображение
     * @param quality Качество сжатия (0-100), применяется если compress=true
     * @return Result с путем к сохраненному изображению при успехе
     */
    suspend fun saveImage(uri: Uri, compress: Boolean = true, quality: Int = 80): Result<String>
    
    /**
     * Загружает изображение по указанному пути.
     * @param path Путь к изображению
     * @return Result с Bitmap изображения при успехе
     */
    suspend fun loadImage(path: String): Result<Bitmap>
    
    /**
     * Удаляет изображение по указанному пути.
     * @param path Путь к изображению
     * @return Result с Unit при успехе
     */
    suspend fun deleteImage(path: String): Result<Unit>
    
    /**
     * Проверяет валидность URI изображения.
     * @param uri URI для проверки
     * @return true, если URI валидный и указывает на изображение
     */
    suspend fun isValidImageUri(uri: Uri): Boolean
    
    /**
     * Очищает кэш изображений.
     * @return Result с Unit при успехе
     */
    suspend fun clearImageCache(): Result<Unit>
} 