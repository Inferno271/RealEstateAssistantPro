package com.realestateassistant.pro.di

import com.realestateassistant.pro.domain.repository.PropertyRepository
import com.realestateassistant.pro.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    @Provides
    @Singleton
    fun providePropertyUseCases(
        repository: PropertyRepository
    ): PropertyUseCases {
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