package com.realestateassistant.pro.navigation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
    navController: NavHostController = rememberNavController()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Получаем текущий маршрут для отображения заголовка
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route
    
    // Заголовок экрана, по умолчанию - "Объекты недвижимости"
    val screenTitle = when (currentDestination) {
        AppRoutes.PROPERTIES -> "Объекты недвижимости"
        AppRoutes.ADD_PROPERTY -> "Добавление объекта"
        AppRoutes.CLIENTS -> "Клиенты"
        AppRoutes.APPOINTMENTS -> "Встречи"
        AppRoutes.PROFILE -> "Профиль"
        AppRoutes.SETTINGS -> "Настройки"
        AppRoutes.HELP -> "Помощь"
        AppRoutes.ABOUT -> "О приложении"
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
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = screenTitle)
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Открыть меню"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { paddingValues ->
            // Основное содержимое приложения с навигацией
            AppNavHost(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }
} 