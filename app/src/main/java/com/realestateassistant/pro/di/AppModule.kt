package com.realestateassistant.pro.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.realestateassistant.pro.data.remote.FirebaseAuthManager
import com.realestateassistant.pro.data.remote.FirebaseDatabaseManager
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
     * @param auth экземпляр FirebaseAuth
     * @return FirebaseAuthManager instance
     */
    @Provides
    @Singleton
    fun provideFirebaseAuthManager(auth: FirebaseAuth): FirebaseAuthManager = 
        FirebaseAuthManager()

    /**
     * Предоставляет экземпляр FirebaseDatabaseManager
     * @param database экземпляр FirebaseDatabase
     * @return FirebaseDatabaseManager instance
     */
    @Provides
    @Singleton
    fun provideFirebaseDatabaseManager(database: FirebaseDatabase): FirebaseDatabaseManager = 
        FirebaseDatabaseManager()
} 