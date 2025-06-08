package com.realestateassistant.pro.di

import android.content.Context
import com.realestateassistant.pro.core.file.StorageHelper
import com.realestateassistant.pro.data.service.PdfExportService
import com.realestateassistant.pro.domain.repository.DocumentRepository
import com.realestateassistant.pro.domain.usecase.document.ExportPropertyToPdfUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DocumentModule {
    
    @Provides
    @Singleton
    fun providePdfExportService(
        @ApplicationContext context: Context
    ): PdfExportService {
        return PdfExportService(context)
    }
    
    @Provides
    @Singleton
    fun provideExportPropertyToPdfUseCase(
        @ApplicationContext context: Context,
        imageRepository: com.realestateassistant.pro.domain.repository.ImageRepository,
        documentRepository: DocumentRepository,
        pdfExportService: PdfExportService,
        storageHelper: StorageHelper
    ): ExportPropertyToPdfUseCase {
        return ExportPropertyToPdfUseCase(
            context = context,
            imageRepository = imageRepository,
            documentRepository = documentRepository,
            pdfExportService = pdfExportService,
            storageHelper = storageHelper
        )
    }
} 