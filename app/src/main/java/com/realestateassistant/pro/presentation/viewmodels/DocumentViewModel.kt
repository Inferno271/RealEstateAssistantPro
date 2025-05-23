package com.realestateassistant.pro.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.realestateassistant.pro.domain.usecase.document.DocumentUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel для работы с документами
 */
@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val documentUseCases: DocumentUseCases
) : ViewModel() {
    
    /**
     * Сохраняет документ
     * 
     * @param uri URI документа
     * @return Result с путем к сохраненному документу при успехе
     */
    suspend fun saveDocument(uri: Uri): Result<String> {
        return documentUseCases.saveDocument(uri)
    }
    
    /**
     * Открывает документ во внешнем приложении
     * 
     * @param path Путь к документу
     * @return Result с парой (Uri, MimeType) для открытия документа при успехе
     */
    suspend fun openDocument(path: String): Result<Pair<Uri, String?>> {
        return documentUseCases.openDocument(path)
    }
    
    /**
     * Удаляет документ
     * 
     * @param path Путь к документу
     * @return Result с Unit при успехе
     */
    suspend fun deleteDocument(path: String): Result<Unit> {
        return documentUseCases.deleteDocument(path)
    }
} 