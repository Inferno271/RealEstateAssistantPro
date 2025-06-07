package com.realestateassistant.pro.navigation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.realestateassistant.pro.navigation.routes.AppRoutes
import kotlinx.coroutines.launch

/**
 * Основной компонент приложения, содержащий Drawer и навигационные элементы
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppRoutes.DASHBOARD
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Получаем текущий маршрут для отображения заголовка
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Заголовок экрана
    val screenTitle = when {
        currentRoute == AppRoutes.DASHBOARD -> "Панель управления"
        currentRoute == AppRoutes.PROPERTIES -> "Объекты недвижимости"
        currentRoute == AppRoutes.ADD_PROPERTY -> "Добавление объекта"
        currentRoute?.startsWith("property_detail") == true -> "Детали объекта"
        currentRoute?.startsWith("edit_property") == true -> "Редактирование объекта"
        currentRoute == AppRoutes.CLIENTS -> "Клиенты"
        currentRoute == AppRoutes.ADD_CLIENT -> "Добавление клиента"
        currentRoute?.startsWith("client_detail") == true -> "Детали клиента"
        currentRoute?.startsWith("edit_client") == true -> "Редактирование клиента"
        currentRoute == AppRoutes.APPOINTMENTS -> "Встречи"
        currentRoute?.startsWith("appointment_detail") == true -> "Детали встречи"
        currentRoute?.startsWith("appointment_edit") == true -> "Редактирование встречи"
        currentRoute == AppRoutes.NOTIFICATIONS -> "Уведомления"
        currentRoute == AppRoutes.SETTINGS -> "Настройки"
        currentRoute == AppRoutes.HELP -> "Помощь"
        currentRoute == AppRoutes.ABOUT -> "О приложении"
        else -> "Real Estate Assistant Pro"
    }
    
    // Material 3 Drawer с обычным Scaffold внутри
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                AppDrawer(
                    navController = navController,
                    onCloseDrawer = {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
            }
        }
    ) {
        // Навигационный хост без дополнительного Scaffold, чтобы избежать
        // дублирования заголовков. Каждый экран теперь отвечает за создание
        // своего собственного Scaffold и TopAppBar
            AppNavHost(
                navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize(),
            drawerState = drawerState  // Передаем состояние drawer, чтобы экраны могли его открывать
        )
    }
} 