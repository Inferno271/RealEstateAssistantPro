package com.realestateassistant.pro.di

import com.realestateassistant.pro.data.local.dao.AppointmentDao
import com.realestateassistant.pro.data.repository.AppointmentRepositoryImpl
import com.realestateassistant.pro.domain.repository.AppointmentRepository
import com.realestateassistant.pro.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Модуль Hilt для внедрения зависимостей, связанных с встречами
 */
@Module
@InstallIn(SingletonComponent::class)
object AppointmentModule {
    
    /**
     * Предоставляет репозиторий для работы с встречами
     */
    @Provides
    @Singleton
    fun provideAppointmentRepository(appointmentDao: AppointmentDao): AppointmentRepository {
        return AppointmentRepositoryImpl(appointmentDao)
    }
    
    /**
     * Предоставляет набор юз-кейсов для работы с встречами
     */
    @Provides
    @Singleton
    fun provideAppointmentUseCases(repository: AppointmentRepository): AppointmentUseCases {
        return AppointmentUseCases(
            createAppointment = CreateAppointment(repository),
            updateAppointment = UpdateAppointment(repository),
            deleteAppointment = DeleteAppointment(repository),
            getAppointment = GetAppointment(repository),
            getAllAppointments = GetAllAppointments(repository),
            observeAllAppointments = ObserveAllAppointments(repository),
            getAppointmentsByProperty = GetAppointmentsByProperty(repository),
            observeAppointmentsByProperty = ObserveAppointmentsByProperty(repository),
            getAppointmentsByClient = GetAppointmentsByClient(repository),
            observeAppointmentsByClient = ObserveAppointmentsByClient(repository),
            getAppointmentsByDate = GetAppointmentsByDate(repository),
            observeAppointmentsByDate = ObserveAppointmentsByDate(repository),
            getAppointmentsByDateRange = GetAppointmentsByDateRange(repository),
            observeAppointmentsByDateRange = ObserveAppointmentsByDateRange(repository),
            getAppointmentsByStatus = GetAppointmentsByStatus(repository),
            observeAppointmentsByStatus = ObserveAppointmentsByStatus(repository),
            getAppointmentsByType = GetAppointmentsByType(repository),
            observeAppointmentsByType = ObserveAppointmentsByType(repository),
            getAppointmentsCountForDay = GetAppointmentsCountForDay(repository),
            getOverlappingAppointments = GetOverlappingAppointments(repository),
            searchAppointments = SearchAppointments(repository),
            observeUpcomingAppointments = ObserveUpcomingAppointments(repository),
            observeAppointmentsForMonth = ObserveAppointmentsForMonth(repository),
            observeAppointmentsForWeek = ObserveAppointmentsForWeek(repository)
        )
    }
} 