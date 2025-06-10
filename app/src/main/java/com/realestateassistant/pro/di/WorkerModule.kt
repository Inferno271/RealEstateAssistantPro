package com.realestateassistant.pro.di

import android.content.Context
import androidx.work.WorkManager
import com.realestateassistant.pro.data.worker.WorkManagerHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Модуль Hilt для внедрения зависимостей, связанных с WorkManager
 */
@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {
    
    /**
     * Предоставляет экземпляр WorkManager
     */
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
    
    /**
     * Предоставляет хелпер для работы с WorkManager
     */
    @Provides
    @Singleton
    fun provideWorkManagerHelper(): WorkManagerHelper {
        return WorkManagerHelper()
    }
} 