package com.realestateassistant.pro.di

import com.realestateassistant.pro.data.local.validation.AppointmentValidator
import com.realestateassistant.pro.data.local.validation.ClientValidator
import com.realestateassistant.pro.data.local.validation.PropertyValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Модуль Dagger Hilt для предоставления валидаторов
 */
@Module
@InstallIn(SingletonComponent::class)
object ValidationModule {
    
    @Provides
    @Singleton
    fun providePropertyValidator(): PropertyValidator {
        return PropertyValidator()
    }
    
    @Provides
    @Singleton
    fun provideClientValidator(): ClientValidator {
        return ClientValidator()
    }
    
    @Provides
    @Singleton
    fun provideAppointmentValidator(): AppointmentValidator {
        return AppointmentValidator()
    }
} 