package com.realestateassistant.pro.data.repository

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import com.realestateassistant.pro.domain.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Реализация репозитория для работы с изображениями
 */
class ImageRepositoryImpl(
    private val context: Context
) : ImageRepository {
    
    private val imageDir by lazy {
        File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "RealEstateAssistant").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
    
    private val cacheDir by lazy {
        File(context.cacheDir, "images").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
    
    override suspend fun saveImage(uri: Uri, compress: Boolean, quality: Int): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // Проверяем валидность URI
                if (!isValidImageUri(uri)) {
                    return@withContext Result.failure(IllegalArgumentException("Недопустимый URI изображения: $uri"))
                }
                
                // Создаем уникальное имя файла
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val name = "${timestamp}_${UUID.randomUUID()}"
                val extension = getFileExtension(uri) ?: "jpg"
                val file = File(imageDir, "$name.$extension")
                
                // Получаем поток из URI
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: return@withContext Result.failure(IOException("Не удалось открыть URI: $uri"))
                
                // Если сжатие не требуется, просто копируем файл
                if (!compress) {
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    inputStream.close()
                    return@withContext Result.success(file.absolutePath)
                }
                
                // Иначе, загружаем как bitmap и сжимаем
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                
                if (bitmap == null) {
                    return@withContext Result.failure(IOException("Не удалось декодировать изображение из URI: $uri"))
                }
                
                // Определяем формат для сжатия
                val compressionFormat = when (extension.lowercase()) {
                    "png" -> Bitmap.CompressFormat.PNG
                    "webp" -> getBitmapCompressFormatWebp()
                    else -> Bitmap.CompressFormat.JPEG
                }
                
                // Сжимаем и сохраняем
                FileOutputStream(file).use { outputStream ->
                    bitmap.compress(compressionFormat, quality, outputStream)
                }
                
                // Освобождаем ресурсы
                bitmap.recycle()
                
                Result.success(file.absolutePath)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    override suspend fun loadImage(path: String): Result<Bitmap> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(path)
                if (!file.exists()) {
                    return@withContext Result.failure(IOException("Файл не найден: $path"))
                }
                
                val bitmap = BitmapFactory.decodeFile(path)
                    ?: return@withContext Result.failure(IOException("Не удалось декодировать изображение из файла: $path"))
                
                Result.success(bitmap)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    override suspend fun deleteImage(path: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(path)
                if (!file.exists()) {
                    return@withContext Result.success(Unit) // Файл не существует, считаем успешным удалением
                }
                
                if (file.delete()) {
                    Result.success(Unit)
                } else {
                    Result.failure(IOException("Не удалось удалить файл: $path"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    override suspend fun isValidImageUri(uri: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Проверяем схему URI
                if (uri.scheme != ContentResolver.SCHEME_CONTENT && uri.scheme != ContentResolver.SCHEME_FILE) {
                    return@withContext false
                }
                
                // Проверяем MIME тип, если это content URI
                if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
                    val mimeType = context.contentResolver.getType(uri) ?: return@withContext false
                    return@withContext mimeType.startsWith("image/")
                }
                
                // Для file URI проверяем расширение
                if (uri.scheme == ContentResolver.SCHEME_FILE) {
                    val path = uri.path ?: return@withContext false
                    val extension = path.substringAfterLast('.', "")
                    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: return@withContext false
                    return@withContext mimeType.startsWith("image/")
                }
                
                false
            } catch (e: Exception) {
                false
            }
        }
    }
    
    override suspend fun clearImageCache(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Удаляем все файлы из директории кэша
                cacheDir.listFiles()?.forEach { file ->
                    file.delete()
                }
                
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    private fun getFileExtension(uri: Uri): String? {
        return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val mimeType = context.contentResolver.getType(uri)
            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        } else {
            uri.path?.substringAfterLast('.')
        }
    }
    
    // Обработка изменений в Android API для WEBP формата
    @Suppress("DEPRECATION")
    private fun getBitmapCompressFormatWebp(): Bitmap.CompressFormat {
        return try {
            // Для новых версий Android используем WEBP_LOSSY
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                // Для более старых используем просто WEBP
                Bitmap.CompressFormat.WEBP
            }
        } catch (e: Exception) {
            // В случае любых ошибок, используем JPEG
            Bitmap.CompressFormat.JPEG
        }
    }
} 