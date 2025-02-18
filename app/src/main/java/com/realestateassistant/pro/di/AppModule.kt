package com.realestateassistant.pro.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.realestateassistant.pro.data.remote.FirebaseAuthManager
import com.realestateassistant.pro.data.remote.FirebaseDatabaseManager
import com.realestateassistant.pro.data.repository.AppointmentRepositoryImpl
import com.realestateassistant.pro.data.repository.AuthRepositoryImpl
import com.realestateassistant.pro.data.repository.ClientRepositoryImpl
import com.realestateassistant.pro.data.repository.PropertyRepositoryImpl
import com.realestateassistant.pro.domain.repository.AppointmentRepository
import com.realestateassistant.pro.domain.repository.AuthRepository
import com.realestateassistant.pro.domain.repository.ClientRepository
import com.realestateassistant.pro.domain.repository.PropertyRepository
import com.realestateassistant.pro.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Модуль Dagger Hilt для предоставления зависимостей на уровне приложения.
 * Содержит методы для создания синглтон-объектов Firebase и менеджеров.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Предоставляет экземпляр FirebaseAuth
     * @return FirebaseAuth instance
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Предоставляет экземпляр FirebaseDatabase
     * @return FirebaseDatabase instance
     */
    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    /**
     * Предоставляет экземпляр FirebaseAuthManager
     * @return FirebaseAuthManager instance
     */
    @Provides
    @Singleton
    fun provideFirebaseAuthManager(): FirebaseAuthManager = FirebaseAuthManager()

    /**
     * Предоставляет экземпляр FirebaseDatabaseManager
     * @return FirebaseDatabaseManager instance
     */
    @Provides
    @Singleton
    fun provideFirebaseDatabaseManager(): FirebaseDatabaseManager = FirebaseDatabaseManager()

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuthManager: FirebaseAuthManager
    ): AuthRepository = AuthRepositoryImpl(firebaseAuthManager)

    @Provides
    @Singleton
    fun providePropertyRepository(
        firebaseDatabaseManager: FirebaseDatabaseManager
    ): PropertyRepository = PropertyRepositoryImpl(firebaseDatabaseManager)

    @Provides
    @Singleton
    fun provideClientRepository(
        firebaseDatabaseManager: FirebaseDatabaseManager
    ): ClientRepository = ClientRepositoryImpl(firebaseDatabaseManager)

    @Provides
    @Singleton
    fun provideAppointmentRepository(
        firebaseDatabaseManager: FirebaseDatabaseManager
    ): AppointmentRepository = AppointmentRepositoryImpl(firebaseDatabaseManager)

    @Provides
    @Singleton
    fun providePropertyUseCases(repository: PropertyRepository): PropertyUseCases {
        return PropertyUseCases(
            addProperty = AddProperty(repository),
            updateProperty = UpdateProperty(repository),
            deleteProperty = DeleteProperty(repository),
            getProperty = GetProperty(repository),
            getAllProperties = GetAllProperties(repository)
        )
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
} 