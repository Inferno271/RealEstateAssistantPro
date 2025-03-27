package com.realestateassistant.pro.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.realestateassistant.pro.presentation.screens.AddPropertyScreen
import com.realestateassistant.pro.presentation.screens.AppointmentListScreen
import com.realestateassistant.pro.presentation.screens.ClientListScreen
import com.realestateassistant.pro.presentation.screens.PropertyListScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "properties",
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = "properties") {
            PropertyListScreen(
                onNavigateToAddProperty = {
                    navController.navigate("add_property")
                }
            )
        }
        composable(route = "add_property") {
            AddPropertyScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = "clients") {
            ClientListScreen()
        }
        composable(route = "appointments") {
            AppointmentListScreen()
        }
    }
} 