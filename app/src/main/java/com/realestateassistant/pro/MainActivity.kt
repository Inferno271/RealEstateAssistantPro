package com.realestateassistant.pro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.realestateassistant.pro.presentation.components.BottomNavBar
import com.realestateassistant.pro.presentation.navigation.AppNavHost
import com.realestateassistant.pro.ui.theme.RealEstateAssistantProTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RealEstateAssistantProTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    Scaffold(
                        bottomBar = { BottomNavBar(navController = navController) }
                    ) { paddingValues ->
                        AppNavHost(
                            navController = navController,
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }
            }
        }
    }
}