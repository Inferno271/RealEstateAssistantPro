package com.realestateassistant.pro.di

import com.realestateassistant.pro.data.local.dao.PropertyDao
import com.realestateassistant.pro.data.local.dao.ClientDao
import com.realestateassistant.pro.data.local.dao.AppointmentDao
import com.realestateassistant.pro.data.repository.PropertyRepositoryImpl
import com.realestateassistant.pro.data.repository.ClientRepositoryImpl
import com.realestateassistant.pro.data.repository.AppointmentRepositoryImpl
import com.realestateassistant.pro.domain.repository.AppointmentRepository
import com.realestateassistant.pro.domain.repository.ClientRepository
import com.realestateassistant.pro.domain.repository.PropertyRepository
import com.realestateassistant.pro.domain.usecase.*
import com.realestateassistant.pro.data.repository.ImageRepositoryImpl
import com.realestateassistant.pro.domain.repository.ImageRepository
import com.realestateassistant.pro.domain.usecase.ImageUseCases
import com.realestateassistant.pro.data.repository.DocumentRepositoryImpl
import com.realestateassistant.pro.domain.repository.DocumentRepository
import com.realestateassistant.pro.domain.usecase.document.*
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

    /**
     * Предоставляет репозиторий для работы с клиентами
     */
    @Provides
    @Singleton
    fun provideClientRepository(
        clientDao: ClientDao
    ): ClientRepository {
        return ClientRepositoryImpl(clientDao)
    }

    /**
     * Предоставляет репозиторий для работы с встречами
     */
    @Provides
    @Singleton
    fun provideAppointmentRepository(
        appointmentDao: AppointmentDao
    ): AppointmentRepository {
        return AppointmentRepositoryImpl(appointmentDao)
    }

    @Provides
    @Singleton
    fun provideClientUseCases(repository: ClientRepository): ClientUseCases {
        return ClientUseCases(
            addClient = AddClient(repository),
            updateClient = UpdateClient(repository),
            deleteClient = DeleteClient(repository),
            getClient = GetClient(repository),
            getAllClients = GetAllClients(repository),
            getClientsByRentalType = GetClientsByRentalType(repository)
        )
    }

    @Provides
    @Singleton
    fun provideAppointmentUseCases(repository: AppointmentRepository): AppointmentUseCases {
        return AppointmentUseCases(
            createAppointment = CreateAppointment(repository),
            updateAppointment = UpdateAppointment(repository),
            deleteAppointment = DeleteAppointment(repository),
            getAppointment = GetAppointment(repository),
            getAllAppointments = GetAllAppointments(repository),
            getAppointmentsByProperty = GetAppointmentsByProperty(repository),
            getAppointmentsByClient = GetAppointmentsByClient(repository),
            getAppointmentsByDate = GetAppointmentsByDate(repository),
            getAppointmentsByDateRange = GetAppointmentsByDateRange(repository)
        )
    }

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
} 