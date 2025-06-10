package com.realestateassistant.pro.domain.usecase.document

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat
import com.realestateassistant.pro.core.file.StorageHelper
import com.realestateassistant.pro.data.service.PdfExportService
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.repository.DocumentRepository
import com.realestateassistant.pro.domain.repository.ImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import android.util.Log
import android.media.MediaScannerConnection

/**
 * UseCase для экспорта информации об объекте недвижимости в PDF файл.
 * 
 * @param context Контекст приложения для доступа к ресурсам
 * @param imageRepository Репозиторий для загрузки изображений объектов недвижимости
 * @param documentRepository Репозиторий для работы с документами
 * @param pdfExportService Сервис для отрисовки PDF
 * @param storageHelper Вспомогательный класс для работы с хранилищем
 */
class ExportPropertyToPdfUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageRepository: ImageRepository,
    private val documentRepository: DocumentRepository,
    private val pdfExportService: PdfExportService,
    private val storageHelper: StorageHelper
) {
    /**
     * Генерирует PDF-документ с информацией о объекте недвижимости.
     *
     * @param property Объект недвижимости
     * @return Result, содержащий Uri для доступа к сгенерированному PDF файлу, либо ошибку
     */
    suspend fun invoke(property: Property): Result<Uri> = withContext(Dispatchers.IO) {
        var document: PdfDocument? = null
        var outputStream: FileOutputStream? = null
        
        try {
            // Проверяем разрешения перед сохранением
            val hasPermission = checkStoragePermission()
            
            val fileName = generateFileName(property)
            Log.d("ExportPropertyToPdfUseCase", "Генерируем PDF с именем: $fileName")
            
            val pdfFile = createPdfFile(fileName, hasPermission)
                ?: return@withContext Result.failure(IOException("Не удалось создать файл PDF"))
            
            Log.d("ExportPropertyToPdfUseCase", "Файл PDF создан по пути: ${pdfFile.absolutePath}")
            
            // Создаем PDF документ
            document = PdfDocument()
            
            // Загружаем изображения для объекта недвижимости
            val images = loadPropertyImages(property)
            Log.d("ExportPropertyToPdfUseCase", "Загружено ${images.size} изображений")
            
            // Генерируем содержимое PDF
            // Передаем весь document в сервис для отрисовки содержимого,
            // сервис самостоятельно создаст необходимые страницы
            pdfExportService.drawPropertyContent(document, property, images)
            
            // Сохраняем документ в файл
            outputStream = FileOutputStream(pdfFile)
            document.writeTo(outputStream)
            outputStream.flush()
            
            // Убедимся, что файл был успешно создан и доступен
            if (!pdfFile.exists() || pdfFile.length() == 0L) {
                Log.e("ExportPropertyToPdfUseCase", "Файл не был создан или пустой: ${pdfFile.absolutePath}")
                return@withContext Result.failure(IOException("Не удалось создать PDF файл"))
            }
            
            Log.d("ExportPropertyToPdfUseCase", "PDF файл создан успешно, размер: ${pdfFile.length()} байт")
            
            // Получаем URI для созданного файла
            val uri = storageHelper.getUriForPublicFile(pdfFile)
            if (uri == null) {
                Log.e("ExportPropertyToPdfUseCase", "Не удалось получить URI для файла: ${pdfFile.absolutePath}")
                return@withContext Result.failure(IOException("Не удалось получить URI для файла"))
            }
            
            // Проверяем доступность URI
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val available = inputStream.available()
                    Log.d("ExportPropertyToPdfUseCase", "URI доступен, можно прочитать $available байт")
                }
            } catch (e: Exception) {
                Log.e("ExportPropertyToPdfUseCase", "Ошибка при проверке доступности URI: ${e.message}", e)
                // Продолжаем выполнение, возможно, другие приложения все равно смогут открыть файл
            }
            
            // Вызываем MediaScanner для обновления галереи
            MediaScannerConnection.scanFile(
                context,
                arrayOf(pdfFile.absolutePath),
                arrayOf("application/pdf"),
                null
            )
            
            Log.d("ExportPropertyToPdfUseCase", "Возвращаем URI: $uri")
            
            return@withContext Result.success(uri)
        } catch (e: Exception) {
            Log.e("ExportPropertyToPdfUseCase", "Ошибка при генерации PDF: ${e.message}", e)
            Result.failure(IOException("Не удалось сгенерировать PDF: ${e.message}", e))
        } finally {
            // Закрываем ресурсы
            try {
                outputStream?.close()
                document?.close()
            } catch (e: IOException) {
                // Игнорируем ошибки при закрытии ресурсов
                Log.w("ExportPropertyToPdfUseCase", "Ошибка при закрытии ресурсов: ${e.message}")
            }
        }
    }
    
    /**
     * Проверяет наличие разрешения на запись во внешнее хранилище
     * @return true если разрешение есть или не требуется (Android 10+)
     */
    private fun checkStoragePermission(): Boolean {
        // На Android 10 (API 29) и выше разрешение не требуется для записи
        // в директорию Download/RealEstateAssistant
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true
        }
        
        // Для более старых версий проверяем наличие разрешения WRITE_EXTERNAL_STORAGE
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Генерирует имя файла на основе характеристик объекта
     */
    private fun generateFileName(property: Property): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        
        val roomsText = if (property.isStudio) "студия" else "${property.roomsCount}к"
        val areaText = "${property.area.toInt()}м2"
        val districtText = property.district.take(10).replace(" ", "_")
        
        return "${roomsText}_${areaText}_${districtText}_$currentDate.pdf"
    }
    
    /**
     * Создает файл для сохранения PDF в директории загрузок
     * 
     * @param fileName Имя файла
     * @param hasPermission Имеется ли разрешение на запись во внешнее хранилище
     * @return Созданный файл или null в случае ошибки
     */
    private fun createPdfFile(fileName: String, hasPermission: Boolean): File? {
        // Если есть разрешение, пытаемся использовать директорию "Загрузки"
        if (hasPermission) {
            val downloadsDir = storageHelper.getDownloadsDirectory()
            
            if (downloadsDir != null && downloadsDir.exists()) {
                // Создаем подпапку для экспортов в директории загрузок
                val exportDir = File(downloadsDir, "RealEstateAssistant")
                if (!exportDir.exists()) {
                    exportDir.mkdirs()
                }
                
                if (exportDir.exists() && exportDir.canWrite()) {
                    // Создаем файл с помощью StorageHelper
                    return storageHelper.createFile(exportDir, fileName)
                } else {
                    Log.w("ExportPropertyToPdfUseCase", "Директория $exportDir недоступна для записи")
                }
            }
        }
        
        // Если нет доступа к "Загрузки", используем внутреннее хранилище
        val pdfDir = storageHelper.getPdfDirectory(preferInternal = true)
        return storageHelper.createFile(pdfDir, fileName)
    }
    
    /**
     * Загружает изображения объекта недвижимости
     */
    private suspend fun loadPropertyImages(property: Property): List<Bitmap> {
        val images = mutableListOf<Bitmap>()
        
        // Загружаем все фотографии объекта
        for (photoPath in property.photos) {
            try {
                imageRepository.loadImage(photoPath).onSuccess { bitmap ->
                    images.add(bitmap)
                }
            } catch (e: Exception) {
                // Пропускаем неудачные загрузки изображений
            }
        }
        
        return images
    }
} 