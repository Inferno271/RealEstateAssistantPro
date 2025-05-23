package com.realestateassistant.pro.navigation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.realestateassistant.pro.R
import com.realestateassistant.pro.navigation.model.NavigationItem
import com.realestateassistant.pro.navigation.model.NavigationItems
import com.realestateassistant.pro.navigation.routes.AppRoutes

/**
 * Компонент бокового меню приложения
 *
 * @param navController NavController для управления навигацией
 * @param onCloseDrawer Лямбда для закрытия меню
 */
@Composable
fun AppDrawer(
    navController: NavController,
    onCloseDrawer: () -> Unit
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Шапка drawer с логотипом и информацией
        DrawerHeader()
        
        // Разделитель
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        
        // Основные пункты навигации
        NavigationItems.mainItems.forEach { item ->
            DrawerItem(
                item = item,
                isSelected = currentRoute == item.route,
                onItemClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(AppRoutes.PROPERTIES) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    onCloseDrawer()
                }
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Разделитель
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        
        // Дополнительные пункты навигации (в нижней части drawer)
        NavigationItems.otherItems.forEach { item ->
            DrawerItem(
                item = item,
                isSelected = currentRoute == item.route,
                onItemClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route)
                    }
                    onCloseDrawer()
                }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * Шапка бокового меню с профилем пользователя
 */
@Composable
private fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Логотип приложения",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Real Estate Assistant Pro",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Инструмент для профессионалов",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Элемент меню навигации
 *
 * @param item Элемент навигации
 * @param isSelected Выбран ли текущий элемент
 * @param onItemClick Обработчик клика по элементу
 */
@Composable
private fun DrawerItem(
    item: NavigationItem,
    isSelected: Boolean,
    onItemClick: () -> Unit
) {
    val background = if (isSelected) {
        Modifier.background(MaterialTheme.colorScheme.primaryContainer)
    } else {
        Modifier
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .then(background)
            .padding(horizontal = 16.dp)
            .clickable(onClick = onItemClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.contentDescription,
            tint = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        
        // Отображение бейджа, если есть
        item.badgeCount?.let { count ->
            if (count > 0) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
} 