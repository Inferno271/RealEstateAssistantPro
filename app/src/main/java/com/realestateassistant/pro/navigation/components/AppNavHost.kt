package com.realestateassistant.pro.navigation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.realestateassistant.pro.navigation.routes.AppRoutes
import com.realestateassistant.pro.presentation.screens.*
import com.realestateassistant.pro.presentation.screens.about.AboutScreen
import com.realestateassistant.pro.presentation.screens.appointment.AddAppointmentScreen
import com.realestateassistant.pro.presentation.screens.appointment.AppointmentDetailScreen
import com.realestateassistant.pro.presentation.screens.appointment.AppointmentScreen
import com.realestateassistant.pro.presentation.screens.appointment.EditAppointmentScreen
import com.realestateassistant.pro.presentation.screens.booking.BookingCalendarScreen
import com.realestateassistant.pro.presentation.screens.dashboard.DashboardScreen
import com.realestateassistant.pro.presentation.screens.help.HelpScreen
import com.realestateassistant.pro.presentation.screens.recommendation.PropertyRecommendationsScreen
import com.realestateassistant.pro.presentation.screens.settings.SettingsScreen
import com.realestateassistant.pro.presentation.screens.booking.BookingListScreen

/**
 * Основной компонент навигации приложения
 * 
 * @param navController Контроллер навигации
 * @param startDestination Начальный пункт назначения
 * @param modifier Модификатор для настройки внешнего вида
 * @param drawerState Состояние ящика навигации, чтобы экраны могли его открывать
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = AppRoutes.DASHBOARD,
    modifier: Modifier = Modifier,
    drawerState: DrawerState? = null
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Панель управления
        composable(route = AppRoutes.DASHBOARD) {
            DashboardScreen(
                navController = navController,
                drawerState = drawerState
            )
        }
        
        // Основные экраны объектов недвижимости
        composable(route = AppRoutes.PROPERTIES) {
            PropertyListScreen(
                onNavigateToAddProperty = {
                    navController.navigate(AppRoutes.ADD_PROPERTY)
                },
                onNavigateToPropertyDetail = { propertyId ->
                    navController.navigate(AppRoutes.propertyDetail(propertyId))
                },
                drawerState = drawerState
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
                },
                onNavigateToBookingCalendar = { propertyId ->
                    navController.navigate(AppRoutes.bookingCalendar(propertyId))
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
        
        // Экран календаря бронирований
        composable(
            route = AppRoutes.BOOKING_CALENDAR,
            arguments = listOf(
                navArgument("propertyId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            BookingCalendarScreen(
                propertyId = propertyId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Экран списка всех бронирований
        composable(route = AppRoutes.BOOKINGS) {
            BookingListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCreateBooking = { propertyId ->
                    navController.navigate(AppRoutes.bookingCalendar(propertyId))
                },
                onNavigateToBookingDetails = { bookingId ->
                    // В будущем можно добавить экран детального просмотра бронирования
                    // navController.navigate(AppRoutes.bookingDetail(bookingId))
                },
                onNavigateToPropertyDetails = { propertyId ->
                    navController.navigate(AppRoutes.propertyDetail(propertyId))
                },
                onNavigateToClientDetails = { clientId ->
                    navController.navigate(AppRoutes.clientDetail(clientId))
                },
                onNavigateToCalendar = { propertyId ->
                    navController.navigate(AppRoutes.bookingCalendar(propertyId))
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
                },
                drawerState = drawerState
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
                },
                onNavigateToRecommendations = { clientId ->
                    navController.navigate(AppRoutes.propertyRecommendations(clientId))
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
        
        // Экраны для работы с встречами
        composable(route = AppRoutes.APPOINTMENTS) {
            AppointmentScreen(
                onNavigateToAppointmentDetail = { appointmentId ->
                    navController.navigate(AppRoutes.appointmentDetail(appointmentId))
                },
                onNavigateToAddAppointment = {
                    navController.navigate(AppRoutes.ADD_APPOINTMENT)
                },
                drawerState = drawerState
            )
        }
        
        // Добавление новой встречи
        composable(route = AppRoutes.ADD_APPOINTMENT) {
            AddAppointmentScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Детальный просмотр встречи
        composable(
            route = AppRoutes.APPOINTMENT_DETAIL,
            arguments = listOf(
                navArgument("appointmentId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
            AppointmentDetailScreen(
                appointmentId = appointmentId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { 
                    navController.navigate(AppRoutes.appointmentEdit(appointmentId))
                }
            )
        }
        
        // Редактирование встречи
        composable(
            route = AppRoutes.APPOINTMENT_EDIT,
            arguments = listOf(
                navArgument("appointmentId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
            EditAppointmentScreen(
                appointmentId = appointmentId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Экраны для работы с уведомлениями
        composable(route = AppRoutes.NOTIFICATIONS) {
            PlaceholderScreen("Уведомления")
        }
        
        // Дополнительные экраны
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
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToPropertyDetail = { propertyId ->
                    navController.navigate(AppRoutes.propertyDetail(propertyId))
                }
            )
        }
    }
}

/**
 * Заглушка для экранов, которые еще не реализованы
 */
@Composable
fun PlaceholderScreen(screenName: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Экран \"$screenName\" в разработке")
    }
} 