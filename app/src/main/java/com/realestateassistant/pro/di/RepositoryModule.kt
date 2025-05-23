package com.realestateassistant.pro.di

import com.realestateassistant.pro.data.local.dao.PropertyDao
import com.realestateassistant.pro.data.mapper.PropertyMapper
import com.realestateassistant.pro.data.repository.PropertyRepositoryImpl
import com.realestateassistant.pro.domain.repository.PropertyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun providePropertyRepository(
        propertyDao: PropertyDao,
        propertyMapper: PropertyMapper
    ): PropertyRepository {
        return PropertyRepositoryImpl(propertyDao, propertyMapper)
    }
} 