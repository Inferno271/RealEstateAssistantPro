package com.realestateassistant.pro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.realestateassistant.pro.navigation.components.AppScaffold
import com.realestateassistant.pro.ui.theme.RealEstateAssistantProTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Устанавливаем UI как можно быстрее
        setContent {
            RealEstateAssistantProTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppScaffold()
                }
            }
        }
        
        // Откладываем тяжелые операции до после отображения UI
        lifecycleScope.launch(Dispatchers.IO) {
            // Здесь можно выполнить дополнительные инициализации,
            // которые не критичны для отображения UI
        }
    }
}