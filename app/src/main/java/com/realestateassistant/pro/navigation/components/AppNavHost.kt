package com.realestateassistant.pro.navigation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.realestateassistant.pro.navigation.routes.AppRoutes
import com.realestateassistant.pro.presentation.screens.*
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
        // Основные экраны объектов недвижимости
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
        
        // Экраны для работы с клиентами
        composable(route = AppRoutes.CLIENTS) {
            ClientListScreen(
                onNavigateToAddClient = {
                    navController.navigate(AppRoutes.ADD_CLIENT)
                },
                onNavigateToClientDetail = { clientId ->
                    navController.navigate(AppRoutes.clientDetail(clientId))
                }
            )
        }
        
        composable(route = AppRoutes.ADD_CLIENT) {
            AddClientScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Детальный просмотр клиента
        composable(
            route = AppRoutes.CLIENT_DETAIL,
            arguments = listOf(
                navArgument("clientId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getString("clientId") ?: ""
            ClientDetailScreen(
                clientId = clientId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { clientId ->
                    navController.navigate(AppRoutes.editClient(clientId))
                }
            )
        }
        
        // Редактирование клиента
        composable(
            route = AppRoutes.EDIT_CLIENT,
            arguments = listOf(
                navArgument("clientId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getString("clientId") ?: ""
            EditClientScreen(
                clientId = clientId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
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