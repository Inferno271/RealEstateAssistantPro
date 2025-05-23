package com.realestateassistant.pro.presentation.components.property

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.presentation.components.property.details.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PropertyTabSection(property: Property) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Характеристики", "Детали", "Окружение")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    
    // Синхронизируем selectedTabIndex с pagerState
    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }
    
    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    
    Column {
        Text(
            text = "Информация об объекте",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Вкладки
        CustomTabRow(
            selectedTabIndex = selectedTabIndex,
            tabs = tabs,
            onTabSelected = { selectedTabIndex = it }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Контент для выбранной вкладки с поддержкой свайпа
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> {
                    Column {
                        PropertyFeatures(property)
                        Spacer(modifier = Modifier.height(24.dp))
                        PropertyTypeSpecificDetails(property)
                        Spacer(modifier = Modifier.height(24.dp))
                        PropertyAdditionalFeatures(property)
                        Spacer(modifier = Modifier.height(24.dp))
                        PropertyLivingConditions(property)
                    }
                }
                1 -> {
                    Column {
                        val hasShortTerm = property.dailyPrice != null
                        val hasLongTerm = property.monthlyRent != null
                        val hasBoth = hasShortTerm && hasLongTerm
                        
                        // Отображаем информацию о посуточной аренде
                        if (hasShortTerm) {
                            if (hasBoth) {
                                RentalTypeHeader(
                                    text = "Посуточная аренда",
                                    icon = Icons.Outlined.CalendarMonth
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            
                            PropertyShortTermDetails(property)
                            
                            if (hasBoth) {
                                Spacer(modifier = Modifier.height(24.dp))
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                    thickness = 1.dp
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                RentalTypeHeader(
                                    text = "Долгосрочная аренда",
                                    icon = Icons.Outlined.Home
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                        
                        // Отображаем информацию о долгосрочной аренде
                        if (hasLongTerm) {
                            PropertyLongTermDetails(property)
                        }
                    }
                }
                2 -> {
                    Column {
                        PropertyLocationDetails(property)
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        if (property.documents.isNotEmpty()) {
                            PropertyDocuments(property)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomTabRow(
    selectedTabIndex: Int,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        edgePadding = 0.dp,
        divider = {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 1.dp
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

@Composable
private fun RentalTypeHeader(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
} 