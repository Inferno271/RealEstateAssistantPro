package com.realestateassistant.pro.presentation.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.usecase.document.ExportPropertyToPdfUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для управления экспортом объекта недвижимости в PDF
 */
@HiltViewModel
class PdfExportViewModel @Inject constructor(
    private val exportPropertyToPdfUseCase: ExportPropertyToPdfUseCase
) : ViewModel() {
    
    /**
     * Состояния процесса экспорта PDF
     */
    sealed class ExportState {
        /**
         * Начальное состояние
         */
        object Initial : ExportState()
        
        /**
         * Процесс экспорта выполняется
         */
        object Loading : ExportState()
        
        /**
         * Экспорт успешно завершен
         * @param uri URI сгенерированного PDF
         */
        data class Success(val uri: Uri) : ExportState()
        
        /**
         * Произошла ошибка при экспорте
         * @param message Сообщение об ошибке
         */
        data class Error(val message: String) : ExportState()
    }
    
    // Состояние экспорта
    private val _exportState = MutableStateFlow<ExportState>(ExportState.Initial)
    val exportState: StateFlow<ExportState> = _exportState
    
    /**
     * Экспортирует информацию об объекте недвижимости в PDF
     *
     * @param property Объект недвижимости для экспорта
     */
    fun exportPropertyToPdf(property: Property) {
        viewModelScope.launch {
            _exportState.value = ExportState.Loading
            
            exportPropertyToPdfUseCase.invoke(property)
                .onSuccess { uri ->
                    _exportState.value = ExportState.Success(uri)
                }
                .onFailure { exception ->
                    _exportState.value = ExportState.Error(
                        exception.message ?: "Произошла ошибка при создании PDF"
                    )
                }
        }
    }
    
    /**
     * Открывает сгенерированный PDF файл, используя наиболее совместимый подход
     * 
     * @param context Контекст для запуска Intent
     * @param uri URI файла PDF
     */
    fun openPdf(context: Context, uri: Uri) {
        Log.d("PdfExportViewModel", "Пытаемся открыть PDF: $uri, content type: ${context.contentResolver.getType(uri)}")
        
        // Попробуем несколько подходов последовательно
        if (!tryOpenWithExplicitChooser(context, uri) && 
            !tryOpenWithGenericIntent(context, uri) &&
            !tryOpenWithSendAction(context, uri)) {
            
            // Если все методы не сработали, сообщаем пользователю
            Toast.makeText(
                context,
                "На устройстве не найдено приложение для просмотра PDF. Установите PDF-ридер.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    /**
     * Пытается открыть PDF с явным выбором приложения
     */
    private fun tryOpenWithExplicitChooser(context: Context, uri: Uri): Boolean {
        return try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            val chooserIntent = Intent.createChooser(intent, "Выберите приложение для просмотра PDF")
            
            // Если нет приложений, которые могут обработать этот intent
            if (intent.resolveActivity(context.packageManager) == null) {
                Log.d("PdfExportViewModel", "Не найдено приложений для обработки PDF через ACTION_VIEW + MIME")
                return false
            }
            
            context.startActivity(chooserIntent)
            Log.d("PdfExportViewModel", "Запущен выбор приложения через ACTION_VIEW + MIME")
            true
        } catch (e: Exception) {
            Log.e("PdfExportViewModel", "Ошибка при открытии PDF через ACTION_VIEW + MIME: ${e.message}", e)
            false
        }
    }
    
    /**
     * Пытается открыть PDF с общим интентом без указания MIME-типа
     */
    private fun tryOpenWithGenericIntent(context: Context, uri: Uri): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = uri
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
            val chooserIntent = Intent.createChooser(intent, "Выберите приложение")
            
            // Если нет приложений, которые могут обработать этот intent
            if (intent.resolveActivity(context.packageManager) == null) {
                Log.d("PdfExportViewModel", "Не найдено приложений для обработки PDF через ACTION_VIEW без MIME")
                return false
            }
            
            context.startActivity(chooserIntent)
            Log.d("PdfExportViewModel", "Запущен выбор приложения через ACTION_VIEW без MIME")
            true
        } catch (e: Exception) {
            Log.e("PdfExportViewModel", "Ошибка при открытии PDF через ACTION_VIEW без MIME: ${e.message}", e)
            false
        }
    }
    
    /**
     * Пытается поделиться PDF файлом, что может открыть больше опций для пользователя
     */
    private fun tryOpenWithSendAction(context: Context, uri: Uri): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            val chooserIntent = Intent.createChooser(intent, "Открыть PDF с помощью")
            
            // Если нет приложений, которые могут обработать этот intent
            if (intent.resolveActivity(context.packageManager) == null) {
                Log.d("PdfExportViewModel", "Не найдено приложений для обработки PDF через ACTION_SEND")
                return false
            }
            
            context.startActivity(chooserIntent)
            Log.d("PdfExportViewModel", "Запущен выбор приложения через ACTION_SEND")
            true
        } catch (e: Exception) {
            Log.e("PdfExportViewModel", "Ошибка при открытии PDF через ACTION_SEND: ${e.message}", e)
            false
        }
    }
    
    /**
     * Поделиться сгенерированным PDF файлом
     *
     * @param context Контекст для запуска Intent
     * @param uri URI файла PDF
     * @param property Объект недвижимости для формирования заголовка
     */
    fun sharePdf(context: Context, uri: Uri, property: Property) {
        val roomsText = if (property.isStudio) "Студия" else "${property.roomsCount}-комнатная ${property.propertyType.lowercase()}"
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Объект недвижимости: $roomsText")
            putExtra(Intent.EXTRA_TEXT, "Информация об объекте недвижимости: $roomsText, ${property.area} м²")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        
        val chooserIntent = Intent.createChooser(
            shareIntent, 
            "Поделиться информацией об объекте"
        )
        
        ContextCompat.startActivity(context, chooserIntent, null)
    }
    
    /**
     * Сбрасывает состояние экспорта до исходного
     */
    fun resetState() {
        _exportState.value = ExportState.Initial
    }
} 