package com.realestateassistant.pro.domain.repository

import android.net.Uri

/**
 * Репозиторий для работы с документами.
 * Отвечает за сохранение, загрузку и удаление документов.
 */
interface DocumentRepository {
    /**
     * Сохраняет документ из переданного URI.
     * @param uri URI документа
     * @return Result с путем к сохраненному документу при успехе
     */
    suspend fun saveDocument(uri: Uri): Result<String>
    
    /**
     * Открывает документ по указанному пути с помощью внешнего приложения.
     * @param path Путь к документу
     * @return Result с Uri для открытия документа при успехе
     */
    suspend fun getDocumentUri(path: String): Result<Uri>
    
    /**
     * Удаляет документ по указанному пути.
     * @param path Путь к документу
     * @return Result с Unit при успехе
     */
    suspend fun deleteDocument(path: String): Result<Unit>
    
    /**
     * Проверяет валидность URI документа.
     * @param uri URI для проверки
     * @return true, если URI валидный и указывает на документ
     */
    suspend fun isValidDocumentUri(uri: Uri): Boolean
    
    /**
     * Получает MIME-тип документа по пути.
     * @param path Путь к документу
     * @return MIME-тип документа или null, если не удалось определить
     */
    suspend fun getDocumentMimeType(path: String): String?
} 