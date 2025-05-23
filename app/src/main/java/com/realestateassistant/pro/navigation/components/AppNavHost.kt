package com.realestateassistant.pro.navigation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.realestateassistant.pro.navigation.routes.AppRoutes
import com.realestateassistant.pro.presentation.screens.AddPropertyScreen
import com.realestateassistant.pro.presentation.screens.AppointmentListScreen
import com.realestateassistant.pro.presentation.screens.ClientListScreen
import com.realestateassistant.pro.presentation.screens.EditPropertyScreen
import com.realestateassistant.pro.presentation.screens.PropertyDetailScreen
import com.realestateassistant.pro.presentation.screens.PropertyListScreen
import com.realestateassistant.pro.presentation.screens.about.AboutScreen
import com.realestateassistant.pro.presentation.screens.help.HelpScreen
import com.realestateassistant.pro.presentation.screens.profile.ProfileScreen
import com.realestateassistant.pro.presentation.screens.settings.SettingsScreen

/**
 * Основной компонент навигации приложения
 * 
 * @param navController Контроллер навигации
 * @param startDestination Начальный пункт назначения
 * @param modifier Модификатор для настройки внешнего вида
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = AppRoutes.PROPERTIES,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Основные экраны
        composable(route = AppRoutes.PROPERTIES) {
            PropertyListScreen(
                onNavigateToAddProperty = {
                    navController.navigate(AppRoutes.ADD_PROPERTY)
                },
                onNavigateToPropertyDetail = { propertyId ->
                    navController.navigate(AppRoutes.propertyDetail(propertyId))
                }
            )
        }
        
        composable(route = AppRoutes.ADD_PROPERTY) {
            AddPropertyScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Детальный просмотр объекта недвижимости
        composable(
            route = AppRoutes.PROPERTY_DETAIL,
            arguments = listOf(
                navArgument("propertyId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            PropertyDetailScreen(
                propertyId = propertyId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { propertyId ->
                    navController.navigate(AppRoutes.editProperty(propertyId))
                }
            )
        }
        
        // Редактирование объекта недвижимости
        composable(
            route = AppRoutes.EDIT_PROPERTY,
            arguments = listOf(
                navArgument("propertyId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            EditPropertyScreen(
                propertyId = propertyId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(route = AppRoutes.CLIENTS) {
            ClientListScreen()
        }
        
        composable(route = AppRoutes.APPOINTMENTS) {
            AppointmentListScreen()
        }
        
        // Дополнительные экраны
        composable(route = AppRoutes.PROFILE) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(route = AppRoutes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(route = AppRoutes.HELP) {
            HelpScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(route = AppRoutes.ABOUT) {
            AboutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
} 