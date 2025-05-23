package com.realestateassistant.pro.domain.usecase.document

import android.net.Uri
import com.realestateassistant.pro.domain.repository.DocumentRepository
import javax.inject.Inject

/**
 * Набор юзкейсов для работы с документами
 */
data class DocumentUseCases @Inject constructor(
    val saveDocument: SaveDocument,
    val getDocumentUri: GetDocumentUri,
    val deleteDocument: DeleteDocument,
    val openDocument: OpenDocument
)

/**
 * Юзкейс для сохранения документа
 */
class SaveDocument @Inject constructor(
    private val repository: DocumentRepository
) {
    suspend operator fun invoke(uri: Uri): Result<String> {
        return repository.saveDocument(uri)
    }
}

/**
 * Юзкейс для получения URI документа
 */
class GetDocumentUri @Inject constructor(
    private val repository: DocumentRepository
) {
    suspend operator fun invoke(path: String): Result<Uri> {
        return repository.getDocumentUri(path)
    }
}

/**
 * Юзкейс для удаления документа
 */
class DeleteDocument @Inject constructor(
    private val repository: DocumentRepository
) {
    suspend operator fun invoke(path: String): Result<Unit> {
        return repository.deleteDocument(path)
    }
}

/**
 * Юзкейс для открытия документа во внешнем приложении
 */
class OpenDocument @Inject constructor(
    private val repository: DocumentRepository
) {
    suspend operator fun invoke(path: String): Result<Pair<Uri, String?>> {
        val uriResult = repository.getDocumentUri(path)
        if (uriResult.isFailure) {
            return Result.failure(uriResult.exceptionOrNull() ?: Exception("Не удалось получить URI документа"))
        }
        
        val mimeType = repository.getDocumentMimeType(path)
        return Result.success(Pair(uriResult.getOrThrow(), mimeType))
    }
} 