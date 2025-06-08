package com.realestateassistant.pro.di

import com.realestateassistant.pro.data.local.dao.PropertyDao
import com.realestateassistant.pro.data.local.dao.ClientDao
import com.realestateassistant.pro.data.local.dao.AppointmentDao
import com.realestateassistant.pro.data.repository.PropertyRepositoryImpl
import com.realestateassistant.pro.data.repository.ClientRepositoryImpl
import com.realestateassistant.pro.domain.repository.ClientRepository
import com.realestateassistant.pro.domain.repository.PropertyRepository
import com.realestateassistant.pro.domain.usecase.*
import com.realestateassistant.pro.data.repository.ImageRepositoryImpl
import com.realestateassistant.pro.domain.repository.ImageRepository
import com.realestateassistant.pro.domain.usecase.ImageUseCases
import com.realestateassistant.pro.data.repository.DocumentRepositoryImpl
import com.realestateassistant.pro.domain.repository.DocumentRepository
import com.realestateassistant.pro.domain.usecase.document.*
import com.realestateassistant.pro.core.file.StorageHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 * Модуль Dagger Hilt для предоставления зависимостей на уровне приложения.
 * Содержит методы для создания синглтон-объектов.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideImageRepository(
        @ApplicationContext context: Context
    ): ImageRepository {
        return ImageRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideImageUseCases(repository: ImageRepository): ImageUseCases {
        return ImageUseCases(
            saveImage = SaveImage(repository),
            saveImages = SaveImages(repository),
            loadImage = LoadImage(repository),
            deleteImage = DeleteImage(repository),
            validateImageUri = ValidateImageUri(repository),
            clearImageCache = ClearImageCache(repository)
        )
    }
    
    /**
     * Предоставляет репозиторий для работы с документами
     */
    @Provides
    @Singleton
    fun provideDocumentRepository(
        @ApplicationContext context: Context
    ): DocumentRepository {
        return DocumentRepositoryImpl(context)
    }
    
    /**
     * Предоставляет юзкейсы для работы с документами
     */
    @Provides
    @Singleton
    fun provideDocumentUseCases(repository: DocumentRepository): DocumentUseCases {
        return DocumentUseCases(
            saveDocument = SaveDocument(repository),
            getDocumentUri = GetDocumentUri(repository),
            deleteDocument = DeleteDocument(repository),
            openDocument = OpenDocument(repository)
        )
    }
    
    /**
     * Предоставляет вспомогательный класс для работы с файловым хранилищем
     */
    @Provides
    @Singleton
    fun provideStorageHelper(
        @ApplicationContext context: Context
    ): StorageHelper {
        return StorageHelper(context)
    }
} 