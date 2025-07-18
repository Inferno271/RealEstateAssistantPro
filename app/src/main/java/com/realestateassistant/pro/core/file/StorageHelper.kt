package com.realestateassistant.pro.core.file

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import android.provider.MediaStore
import android.util.Log
import android.content.ContentUris

/**
 * Вспомогательный класс для безопасной работы с хранилищем файлов.
 * Предоставляет методы для работы с внутренним и внешним хранилищем с учетом возможных ошибок доступа.
 */
@Singleton
class StorageHelper @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "StorageHelper"
    }

    /**
     * Создает и возвращает директорию для сохранения файлов в приложении
     * Сначала пытается использовать внешнее хранилище приложения, при ошибке - внутреннее
     *
     * @param type Тип директории (например, "pdf", "images", "documents")
     * @param preferInternal True, если предпочтительнее использовать внутреннее хранилище
     * @return Директория для сохранения файлов
     */
    fun getAppFileDirectory(type: String, preferInternal: Boolean = false): File {
        return try {
            if (preferInternal) {
                // Используем внутреннее хранилище (всегда доступно)
                getInternalFileDirectory(type)
            } else {
                // Пробуем внешнее хранилище
                getExternalFileDirectory(type)
            }
        } catch (e: Exception) {
            // При любой ошибке с внешним хранилищем используем внутреннее
            getInternalFileDirectory(type)
        }
    }
    
    /**
     * Возвращает директорию во внутреннем хранилище приложения
     *
     * @param type Тип директории
     * @return Директория для файлов
     */
    private fun getInternalFileDirectory(type: String): File {
        val directory = File(context.filesDir, type)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }
    
    /**
     * Пытается получить директорию во внешнем хранилище приложения
     *
     * @param type Тип директории
     * @return Директория для файлов или ошибка, если внешнее хранилище недоступно
     */
    @Throws(IOException::class)
    private fun getExternalFileDirectory(type: String): File {
        val externalDir = when (type) {
            "pdf" -> context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            "images" -> context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            "temp" -> context.externalCacheDir
            else -> context.getExternalFilesDir(type)
        }
        
        if (externalDir == null || !isExternalStorageWritable()) {
            throw IOException("Внешнее хранилище недоступно")
        }
        
        val directory = File(externalDir, type)
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw IOException("Не удалось создать директорию: ${directory.absolutePath}")
            }
        }
        
        return directory
    }
    
    /**
     * Возвращает директорию "Загрузки" на внешнем хранилище
     *
     * @return Директория "Загрузки" или null, если недоступна
     */
    fun getDownloadsDirectory(): File? {
        if (!isExternalStorageWritable()) {
            return null
        }
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Для Android 10+ используем специальный путь для загрузок
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath)
        } else {
            // Для более старых версий
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        }
    }
    
    /**
     * Проверяет, доступно ли внешнее хранилище для записи
     *
     * @return true если доступно
     */
    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }
    
    /**
     * Создает файл в указанной директории, безопасно обрабатывая ошибки
     *
     * @param directory Директория для создания файла
     * @param fileName Имя файла
     * @param overwrite Перезаписать, если файл существует
     * @return Созданный файл или null в случае ошибки
     */
    fun createFile(directory: File, fileName: String, overwrite: Boolean = true): File? {
        return try {
            val file = File(directory, fileName)
            
            if (file.exists()) {
                if (overwrite) {
                    file.delete()
                } else {
                    return file
                }
            }
            
            file.createNewFile()
            return file
        } catch (e: IOException) {
            null
        }
    }
    
    /**
     * Получает URI для файла через FileProvider
     *
     * @param file Файл
     * @return URI или null в случае ошибки
     */
    fun getUriForFile(file: File): Uri? {
        return try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Получает URI для файла из публичной директории Downloads
     * Использует FileProvider для Android 7.0+ для соответствия политике безопасности
     * 
     * @param file Файл из публичной директории
     * @return URI или null в случае ошибки
     */
    fun getUriForPublicFile(file: File): Uri? {
        return try {
            if (!file.exists()) {
                Log.e(TAG, "Файл не существует: ${file.absolutePath}")
                return null
            }
            
            Log.d(TAG, "Получаем URI для файла: ${file.absolutePath}, размер: ${file.length()} байт")
            
            // Для Android 7.0+ (API 24+) нужно использовать FileProvider
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val providerAuthority = "${context.packageName}.fileprovider"
                Log.d(TAG, "Используем FileProvider с authority: $providerAuthority")
                
                try {
                    FileProvider.getUriForFile(context, providerAuthority, file)
                } catch (e: IllegalArgumentException) {
                    Log.e(TAG, "Ошибка FileProvider: ${e.message}", e)
                    // Используем альтернативный подход в зависимости от версии API
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // Для Android 10+ (API 29+) используем Downloads URI
                        ContentUris.withAppendedId(
                            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                            file.name.hashCode().toLong()
                        )
                    } else {
                        // Для более старых версий используем общий метод
                        Uri.parse("content://downloads/public_downloads/${file.name.hashCode()}")
                    }
                }
            } else {
                Uri.fromFile(file)
            }
            
            // Проверяем, что URI не пустой и есть доступ на чтение
            if (uri != null) {
                try {
                    context.contentResolver.openInputStream(uri)?.close()
                    Log.d(TAG, "URI создан успешно и доступен для чтения: $uri")
                } catch (e: Exception) {
                    Log.e(TAG, "URI создан, но недоступен для чтения: $uri", e)
                }
            }
            
            uri
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении URI для файла: ${e.message}", e)
            null
        }
    }
    
    /**
     * Удаляет файл, безопасно обрабатывая ошибки
     *
     * @param file Файл для удаления
     * @return true если удаление успешно
     */
    fun deleteFile(file: File): Boolean {
        return try {
            if (file.exists()) {
                file.delete()
            } else {
                true
            }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Получает путь для сохранения PDF файлов
     *
     * @param preferInternal True, если предпочтительнее использовать внутреннее хранилище
     * @return Директория для сохранения PDF
     */
    fun getPdfDirectory(preferInternal: Boolean = true): File {
        return getAppFileDirectory("pdf_exports", preferInternal)
    }
    
    /**
     * Получает путь для сохранения изображений
     *
     * @param preferInternal True, если предпочтительнее использовать внутреннее хранилище
     * @return Директория для сохранения изображений
     */
    fun getImagesDirectory(preferInternal: Boolean = false): File {
        return getAppFileDirectory("images", preferInternal)
    }
    
    /**
     * Получает путь для сохранения документов
     *
     * @param preferInternal True, если предпочтительнее использовать внутреннее хранилище
     * @return Директория для сохранения документов
     */
    fun getDocumentsDirectory(preferInternal: Boolean = false): File {
        return getAppFileDirectory("documents", preferInternal)
    }
} 