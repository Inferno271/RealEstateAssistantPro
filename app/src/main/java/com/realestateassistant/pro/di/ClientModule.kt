package com.realestateassistant.pro.di

import com.realestateassistant.pro.data.local.dao.ClientDao
import com.realestateassistant.pro.data.repository.ClientRepositoryImpl
import com.realestateassistant.pro.domain.repository.ClientRepository
import com.realestateassistant.pro.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Модуль Hilt для внедрения зависимостей, связанных с клиентами
 */
@Module
@InstallIn(SingletonComponent::class)
object ClientModule {
    
    /**
     * Предоставляет репозиторий для работы с клиентами
     */
    @Provides
    @Singleton
    fun provideClientRepository(clientDao: ClientDao): ClientRepository {
        return ClientRepositoryImpl(clientDao)
    }
    
    /**
     * Предоставляет набор юз-кейсов для работы с клиентами
     */
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
} 