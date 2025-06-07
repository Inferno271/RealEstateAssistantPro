package com.realestateassistant.pro.di

import com.realestateassistant.pro.data.local.dao.PropertyDao
import com.realestateassistant.pro.data.mapper.PropertyMapper
import com.realestateassistant.pro.data.repository.PropertyRepositoryImpl
import com.realestateassistant.pro.domain.repository.PropertyRepository
import com.realestateassistant.pro.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Модуль Hilt для внедрения зависимостей, связанных с объектами недвижимости
 */
@Module
@InstallIn(SingletonComponent::class)
object PropertyModule {
    
    /**
     * Предоставляет маппер для преобразования между моделями и сущностями
     */
    @Provides
    @Singleton
    fun providePropertyMapper(): PropertyMapper {
        return PropertyMapper()
    }
    
    /**
     * Предоставляет репозиторий для работы с объектами недвижимости
     */
    @Provides
    @Singleton
    fun providePropertyRepository(
        propertyDao: PropertyDao,
        propertyMapper: PropertyMapper
    ): PropertyRepository {
        return PropertyRepositoryImpl(propertyDao, propertyMapper)
    }
    
    /**
     * Предоставляет набор юз-кейсов для работы с объектами недвижимости
     */
    @Provides
    @Singleton
    fun providePropertyUseCases(repository: PropertyRepository): PropertyUseCases {
        return PropertyUseCases(
            addProperty = AddProperty(repository),
            updateProperty = UpdateProperty(repository),
            deleteProperty = DeleteProperty(repository),
            getProperty = GetProperty(repository),
            getAllProperties = GetAllProperties(repository),
            observeAllProperties = ObserveAllProperties(repository),
            observePropertiesByType = ObservePropertiesByType(repository)
        )
    }
} 