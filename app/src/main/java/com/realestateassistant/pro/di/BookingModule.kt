package com.realestateassistant.pro.di

import com.realestateassistant.pro.data.local.dao.BookingDao
import com.realestateassistant.pro.data.repository.BookingRepositoryImpl
import com.realestateassistant.pro.domain.repository.BookingRepository
import com.realestateassistant.pro.domain.usecase.property.UpdatePropertyStatusesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Модуль Hilt для внедрения зависимостей, связанных с бронированиями
 */
@Module
@InstallIn(SingletonComponent::class)
object BookingModule {
    
    /**
     * Предоставляет репозиторий для работы с бронированиями
     */
    @Provides
    @Singleton
    fun provideBookingRepository(
        bookingDao: BookingDao,
        updatePropertyStatusesUseCase: UpdatePropertyStatusesUseCase
    ): BookingRepository {
        return BookingRepositoryImpl(bookingDao, updatePropertyStatusesUseCase)
    }
} 