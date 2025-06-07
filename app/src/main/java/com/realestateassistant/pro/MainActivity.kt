package com.realestateassistant.pro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.realestateassistant.pro.navigation.components.AppScaffold
import com.realestateassistant.pro.navigation.routes.AppRoutes
import com.realestateassistant.pro.ui.theme.RealEstateAssistantTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Основная активность приложения
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Включаем edge-to-edge UI
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            // Используем нашу тему с синими цветами
            RealEstateAssistantTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppScaffold(startDestination = AppRoutes.DASHBOARD)
                }
            }
        }
    }
}