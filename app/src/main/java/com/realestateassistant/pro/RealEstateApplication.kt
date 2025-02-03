package com.realestateassistant.pro

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Основной класс приложения.
 * Инициализирует Dagger Hilt для внедрения зависимостей.
 */
@HiltAndroidApp
class RealEstateApplication : Application() 