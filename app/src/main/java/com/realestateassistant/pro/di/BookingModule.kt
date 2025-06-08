package com.realestateassistant.pro.di

import com.realestateassistant.pro.data.repository.BookingRepositoryImpl
import com.realestateassistant.pro.domain.repository.BookingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Модуль Hilt для внедрения зависимостей, связанных с бронированиями
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class BookingModule {
    
    /**
     * Связывает интерфейс BookingRepository с его реализацией BookingRepositoryImpl
     */
    @Binds
    @Singleton
    abstract fun bindBookingRepository(
        bookingRepositoryImpl: BookingRepositoryImpl
    ): BookingRepository
} 