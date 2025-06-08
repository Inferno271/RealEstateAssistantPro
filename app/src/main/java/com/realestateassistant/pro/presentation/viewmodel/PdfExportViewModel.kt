package com.realestateassistant.pro.presentation.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
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
     * Открывает сгенерированный PDF файл
     * 
     * @param context Контекст для запуска Intent
     * @param uri URI файла PDF
     */
    fun openPdf(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY or 
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        
        val chooserIntent = Intent.createChooser(intent, "Открыть PDF")
        
        if (intent.resolveActivity(context.packageManager) != null) {
            ContextCompat.startActivity(context, chooserIntent, null)
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