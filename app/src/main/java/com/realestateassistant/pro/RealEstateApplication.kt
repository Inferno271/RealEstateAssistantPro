package com.realestateassistant.pro

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

/**
 * Основной класс приложения.
 * Инициализирует Dagger Hilt для внедрения зависимостей.
 */
@HiltAndroidApp
class RealEstateApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Инициализация Yandex MapKit
        MapKitFactory.setApiKey("80b06c04-6156-4dfd-a086-2cee0b05fdb8")
    }
} 