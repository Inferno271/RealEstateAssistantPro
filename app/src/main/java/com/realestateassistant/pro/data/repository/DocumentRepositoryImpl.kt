package com.realestateassistant.pro.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.realestateassistant.pro.domain.repository.DocumentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Реализация репозитория для работы с документами
 */
class DocumentRepositoryImpl(
    private val context: Context
) : DocumentRepository {
    
    private val documentDir by lazy {
        File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "RealEstateAssistant").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
    
    private val tempDir by lazy {
        File(context.cacheDir, "temp").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
    
    override suspend fun saveDocument(uri: Uri): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // Проверяем валидность URI
                if (!isValidDocumentUri(uri)) {
                    return@withContext Result.failure(IllegalArgumentException("Недопустимый URI документа: $uri"))
                }
                
                // Получаем оригинальное имя файла
                var originalName = getOriginalFileName(uri)
                
                // Если не удалось получить имя, генерируем новое
                if (originalName.isNullOrBlank()) {
                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    val extension = getFileExtension(uri) ?: "pdf"
                    originalName = "document_${timestamp}.$extension"
                }
                
                // Проверяем, есть ли уже файл с таким именем и добавляем суффикс если нужно
                val finalName = createUniqueFileName(originalName)
                val file = File(documentDir, finalName)
                
                // Получаем поток из URI
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: return@withContext Result.failure(IOException("Не удалось открыть URI: $uri"))
                
                // Копируем файл
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                inputStream.close()
                
                Result.success(file.absolutePath)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Получает оригинальное имя файла из URI
     */
    private fun getOriginalFileName(uri: Uri): String? {
        // Для content URI пытаемся получить Display Name
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val cursor = context.contentResolver.query(
                uri, null, null, null, null
            ) ?: return null
            
            val result = cursor.use {
                if (it.moveToFirst()) {
                    val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        return@use it.getString(displayNameIndex)
                    }
                }
                null
            }
            
            if (!result.isNullOrBlank()) {
                return result
            }
        }
        
        // Для file URI берем последнюю часть пути
        if (uri.scheme == ContentResolver.SCHEME_FILE) {
            return uri.path?.let { path ->
                val file = File(path)
                file.name
            }
        }
        
        // В крайнем случае, выделяем часть после последнего /
        val uriString = uri.toString()
        val lastSlashIndex = uriString.lastIndexOf('/')
        if (lastSlashIndex != -1 && lastSlashIndex < uriString.length - 1) {
            return uriString.substring(lastSlashIndex + 1)
        }
        
        return null
    }
    
    /**
     * Создает уникальное имя файла, добавляя суффикс, если файл с таким именем уже существует
     */
    private fun createUniqueFileName(originalName: String): String {
        val baseName = originalName.substringBeforeLast('.')
        val extension = originalName.substringAfterLast('.', "")
        val extensionWithDot = if (extension.isNotEmpty()) ".$extension" else ""
        
        var counter = 0
        var fileName = originalName
        var file = File(documentDir, fileName)
        
        while (file.exists()) {
            counter++
            fileName = "$baseName($counter)$extensionWithDot"
            file = File(documentDir, fileName)
        }
        
        return fileName
    }
    
    override suspend fun getDocumentUri(path: String): Result<Uri> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(path)
                if (!file.exists()) {
                    return@withContext Result.failure(IOException("Файл не найден: $path"))
                }
                
                // Используем FileProvider для получения URI
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                
                Result.success(uri)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    override suspend fun deleteDocument(path: String): Result<Unit> {
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
    
    override suspend fun isValidDocumentUri(uri: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Проверяем схему URI
                if (uri.scheme != ContentResolver.SCHEME_CONTENT && uri.scheme != ContentResolver.SCHEME_FILE) {
                    return@withContext false
                }
                
                // Для content URI пытаемся получить InputStream
                if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
                    context.contentResolver.openInputStream(uri)?.close()
                    return@withContext true
                }
                
                // Для file URI проверяем существование файла
                if (uri.scheme == ContentResolver.SCHEME_FILE) {
                    val path = uri.path ?: return@withContext false
                    return@withContext File(path).exists()
                }
                
                false
            } catch (e: Exception) {
                false
            }
        }
    }
    
    override suspend fun getDocumentMimeType(path: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(path)
                if (!file.exists()) {
                    return@withContext null
                }
                
                val extension = file.extension.lowercase(Locale.getDefault())
                return@withContext MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                    ?: "application/octet-stream" // Используем octet-stream как тип по умолчанию
            } catch (e: Exception) {
                null
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
} 