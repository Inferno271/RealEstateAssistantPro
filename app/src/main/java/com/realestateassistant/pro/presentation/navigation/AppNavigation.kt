package com.realestateassistant.pro.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.realestateassistant.pro.presentation.screens.PropertyDetailScreen
import com.realestateassistant.pro.presentation.screens.booking.BookingCalendarScreen
import com.realestateassistant.pro.presentation.screens.recommendation.PropertyRecommendationsScreen

/**
 * Маршруты навигации в приложении
 */
object AppRoutes {
    const val PROPERTY_DETAILS = "property_details/{propertyId}"
    const val BOOKING_CALENDAR = "booking_calendar/{propertyId}"
    const val PROPERTY_RECOMMENDATIONS = "property_recommendations/{clientId}"
    
    // Функции для создания маршрута с параметрами
    fun propertyDetails(propertyId: String) = "property_details/$propertyId"
    fun bookingCalendar(propertyId: String) = "booking_calendar/$propertyId"
    fun propertyRecommendations(clientId: String) = "property_recommendations/$clientId"
}

/**
 * Основной навигационный граф приложения
 * 
 * @param navController контроллер навигации
 * @param startDestination начальный маршрут
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Экран детальной информации об объекте недвижимости
        composable(
            route = AppRoutes.PROPERTY_DETAILS,
            arguments = listOf(
                navArgument("propertyId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            PropertyDetailScreen(
                propertyId = propertyId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { /* Добавьте навигацию к экрану редактирования */ },
                onNavigateToBookingCalendar = { propertyId -> 
                    navController.navigate(AppRoutes.bookingCalendar(propertyId)) 
                }
            )
        }
        
        // Экран календаря бронирований
        composable(
            route = AppRoutes.BOOKING_CALENDAR,
            arguments = listOf(
                navArgument("propertyId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            BookingCalendarScreen(
                propertyId = propertyId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Экран рекомендаций объектов недвижимости
        composable(
            route = AppRoutes.PROPERTY_RECOMMENDATIONS,
            arguments = listOf(
                navArgument("clientId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getString("clientId") ?: ""
            PropertyRecommendationsScreen(
                clientId = clientId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPropertyDetail = { propertyId ->
                    navController.navigate(AppRoutes.propertyDetails(propertyId))
                }
            )
        }
    }
} 